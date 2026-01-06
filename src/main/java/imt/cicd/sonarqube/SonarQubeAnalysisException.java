package imt.cicd.sonarqube;

public class SonarQubeAnalysisException extends Exception {

    public SonarQubeAnalysisException(String message) {
        super(message);
    }

    public SonarQubeAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
