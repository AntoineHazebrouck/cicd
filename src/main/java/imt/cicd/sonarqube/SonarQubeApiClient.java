package imt.cicd.sonarqube;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SonarQubeApiClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final String hostUrl;
    private final String token;

    public SonarQubeApiClient(String hostUrl, String token) {
        this.hostUrl = hostUrl;
        this.token = token;
    }

    public String getQualityGateStatus(String projectKey) throws Exception {

        String auth = Base64.getEncoder()
                .encodeToString((token + ":").getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        hostUrl + "/api/qualitygates/project_status?projectKey=" + projectKey))
                .header("Authorization", "Basic " + auth)
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode root = mapper.readTree(response.body());

        return root.path("projectStatus").path("status").asText();
    }

    public Map<String, String> getMeasures(String projectKey, String coverage, String bugs, String codeSmells, String vulnerabilities, String duplicatedLinesDensity) throws IOException, InterruptedException {
        StringBuilder keysBuilder = new StringBuilder();
        for (String k : new String[]{coverage, bugs, codeSmells, vulnerabilities, duplicatedLinesDensity}) {
            if (k != null && !k.isEmpty()) {
                if (keysBuilder.length() > 0) keysBuilder.append(',');
                keysBuilder.append(k);
            }
        }

        Map<String, String> measures = new HashMap<>();
        if (keysBuilder.length() == 0) {
            return measures; // nothing requested
        }

        String metricKeys = keysBuilder.toString();
        String uri = hostUrl + "/api/measures/component?component=" +
                URLEncoder.encode(projectKey, StandardCharsets.UTF_8) +
                "&metricKeys=" + URLEncoder.encode(metricKeys, StandardCharsets.UTF_8);

        String auth = Base64.getEncoder().encodeToString((token + ":").getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Basic " + auth)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(response.body());
        JsonNode measuresNode = root.path("component").path("measures");
        if (measuresNode.isArray()) {
            for (JsonNode m : measuresNode) {
                String metric = m.path("metric").asText(null);
                String value = m.path("value").asText(null);
                if (metric != null && value != null) {
                    measures.put(metric, value);
                }
            }
        }

        return measures;

    }
}
