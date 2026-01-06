package imt.cicd.sonarqube;

public class SonarUtils {

    public static String getOrgFromUrl(String githubRepoUrl) {
        String[] parts = githubRepoUrl.split("/");
        return parts[parts.length - 2];
    }

    public static String generateProjectKey(String orgName) {
        return (orgName).replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}

