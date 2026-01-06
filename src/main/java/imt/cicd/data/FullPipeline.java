package imt.cicd.data;

import com.vaadin.flow.component.notification.Notification;
import imt.cicd.data.BuildHistory.BuildRecap;
import imt.cicd.sonarqube.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FullPipeline {

    public static List<BuildRecap> run(String githubRepoUrl) throws SonarQubeAnalysisException {
        var cloneResult = CloneRepository.run(githubRepoUrl);
        if (cloneResult.isStatus()) Notification.show(
            "Cloned " + githubRepoUrl
        );
        else Notification.show("Failed to clone " + githubRepoUrl);

        var sonarResult = SonarQubeRun.run(githubRepoUrl);
        if (sonarResult.isStatus()) Notification.show(
                "SonarQube Scan Validate"
        );
        else Notification.show("Failed to pass SonarQube Scan ");


        var buildResult = BuildDockerImage.run(cloneResult.getFolder());
        if (buildResult.isStatus()) Notification.show("Built " + githubRepoUrl);
        else Notification.show("Failed to build " + githubRepoUrl);

        var failures = new ArrayList<String>();
        if (!cloneResult.isStatus()) failures.add("CLONE_FAILED");
        if (!sonarResult.isStatus()) failures.add("SONAR_FAILED");
        if (!buildResult.isStatus()) failures.add("BUILD_FAIL");

        return BuildHistory.add(
            BuildRecap.builder()
                .status(
                    failures.isEmpty()
                        ? "SUCCESS"
                        : failures.stream().collect(Collectors.joining(", "))
                )
                .image(buildResult.getImage())
                .imageTag(buildResult.getImageTag())
                .time(LocalDateTime.now())
                .build()
        );
    }
}
