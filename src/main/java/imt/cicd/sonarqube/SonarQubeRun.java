package imt.cicd.sonarqube;

import imt.cicd.data.HasStatus;
import java.io.File;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SonarQubeRun {

    @Builder
    @Getter
    public static class SonarQubeResult implements HasStatus {

        private final Boolean status;
        private final String qualityGateStatus;
        private final Map<String, String> measures;
    }

    public static SonarQubeRun.SonarQubeResult run(String repoUrl) {
        String folderName = repoUrl.replace("https://", "").replace(".git", "");
        String pathString = "./temp/repositories/" + folderName;
        File localPath = new File(pathString);

        String sonarHostUrl = System.getenv("SONAR_HOST_URL");
        String sonarToken = System.getenv("SONAR_TOKEN");

        try {
            if (!localPath.exists()) {
                log.warn(
                    "Le dossier {} n'existe pas encore. Assurez-vous que le repo a été cloné.",
                    pathString
                );
            }

            log.info("Début de l'analyse SonarQube pour {}", repoUrl);

            SonarQubeConfig sonarConfig = new SonarQubeConfig(
                sonarHostUrl,
                sonarToken,
                120
            );

            SonarQubeService sonarService = new SonarQubeService(sonarConfig);

            String orgNameBrut = SonarUtils.getOrgFromUrl(repoUrl);
            String projectKey = SonarUtils.generateProjectKey(orgNameBrut);
            String projectName = folderName;

            log.debug(
                "projectKey = {}, projectName = {}",
                projectKey,
                projectName
            );

            log.info("Compilation du projet avant analyse...");
            ProcessBuilder compilePb = new ProcessBuilder("mvn", "clean", "compile");
            compilePb.directory(localPath);

            compilePb.inheritIO();

            int compileExit = compilePb.start().waitFor();

            if (compileExit == 0) {
                sonarService.analyze(localPath.toPath(), projectKey, projectName);
            } else {
                log.error("La compilation a échoué, impossible de lancer SonarQube");
            }

            sonarService.analyze(localPath.toPath(), projectKey, projectName);
            SonarQubeApiClient apiClient = new SonarQubeApiClient(
                sonarHostUrl,
                sonarToken
            );

            String qualityGateStatus = apiClient.getQualityGateStatus(
                projectKey
            );
            Map<String, String> measures = apiClient.getMeasures(
                projectKey,
                "coverage",
                "bugs",
                "code_smells",
                "vulnerabilities",
                "duplicated_lines_density"
            );

            log.info("Analyse SonarQube terminée pour {}.", repoUrl);

            return SonarQubeResult.builder()
                .status(true)
                .qualityGateStatus(qualityGateStatus)
                .measures(measures)
                .build();
        } catch (Exception e) {
            log.error(
                "Échec de l'analyse SonarQube pour {} : {}",
                repoUrl,
                e.getMessage()
            );

            return SonarQubeResult.builder()
                .status(false)
                .qualityGateStatus(null)
                .measures(null)
                .build();
        }
    }
}
