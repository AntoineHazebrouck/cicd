package imt.cicd.sonarqube;

public class SonarQubeConfig {

    private final String hostUrl;
    private final String token;
    private final int qualityGateTimeoutSeconds;

    public SonarQubeConfig(String hostUrl, String token, int qualityGateTimeoutSeconds) {
        this.hostUrl = hostUrl;
        this.token = token;
        this.qualityGateTimeoutSeconds = qualityGateTimeoutSeconds;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public String getToken() {
        return token;
    }

    public int getQualityGateTimeoutSeconds() {
        return qualityGateTimeoutSeconds;
    }
}
