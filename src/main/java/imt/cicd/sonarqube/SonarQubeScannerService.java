package imt.cicd.sonarqube;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class SonarQubeScannerService {

    private final SonarQubeConfig config;

    public SonarQubeScannerService(SonarQubeConfig config) {
        this.config = config;
    }

    public void runScan(Path projectDir, String projectKey, String projectName)
            throws SonarQubeAnalysisException {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "mvn",
                    "clean",
                    "verify",
                    "org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar",
                    "-Dsonar.projectKey=" + projectKey,
                    "-Dsonar.projectName=" + projectName,
                    "-Dsonar.host.url=" + config.getHostUrl(),
                    "-Dsonar.login=" + config.getToken(),
                    "-Dsonar.java.binaries=target/classes",
                    "-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"
            );

            processBuilder.directory(projectDir.toFile());
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(System.out::println);
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new SonarQubeAnalysisException(
                        "SonarScanner failed (exit code " + exitCode + ")");
            }

        } catch (Exception e) {
            throw new SonarQubeAnalysisException("Error during SonarQube scan", e);
        }
    }
}
