package imt.cicd.sonarqube;

public class QualityGateService {

    private final SonarQubeApiClient apiClient;
    private final int timeoutSeconds;

    public QualityGateService(SonarQubeApiClient apiClient, int timeoutSeconds) {
        this.apiClient = apiClient;
        this.timeoutSeconds = timeoutSeconds;
    }

    public String waitForQualityGate(String projectKey)
            throws SonarQubeAnalysisException {

        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;

        try {
            while (System.currentTimeMillis() < deadline) {

                String status = apiClient.getQualityGateStatus(projectKey);

                if ("OK".equals(status) || "ERROR".equals(status)) {
                    return status;
                }

                Thread.sleep(3000);
            }
        } catch (Exception e) {
            throw new SonarQubeAnalysisException(
                    "Failed while waiting for Quality Gate", e);
        }

        throw new SonarQubeAnalysisException("Quality Gate timeout exceeded");
    }
}
