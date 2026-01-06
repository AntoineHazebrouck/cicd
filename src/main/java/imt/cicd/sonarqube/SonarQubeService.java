package imt.cicd.sonarqube;

import java.nio.file.Path;

public class SonarQubeService {

    private final SonarQubeScannerService scannerService;
    private final QualityGateService qualityGateService;

    public SonarQubeService(SonarQubeConfig config) {

        SonarQubeApiClient apiClient =
                new SonarQubeApiClient(config.getHostUrl(), config.getToken());

        this.scannerService = new SonarQubeScannerService(config);
        this.qualityGateService =
                new QualityGateService(apiClient, config.getQualityGateTimeoutSeconds());
    }

    public void analyze(Path projectDir, String projectKey, String projectName)
            throws SonarQubeAnalysisException {

        scannerService.runScan(projectDir, projectKey, projectName);

        String gateStatus =
                qualityGateService.waitForQualityGate(projectKey);

        if ("ERROR".equals(gateStatus)) {
            throw new SonarQubeAnalysisException(
                    "SonarQube Quality Gate FAILED");
        }
    }
}
