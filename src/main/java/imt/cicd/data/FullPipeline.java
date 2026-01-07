package imt.cicd.data;

import com.vaadin.flow.component.notification.Notification;
import imt.cicd.data.BuildHistory.BuildRecap;
import imt.cicd.sonarqube.SonarQubeRun;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FullPipeline {

    public static List<BuildRecap> run(String githubRepoUrl) {
        var cloneResult = runStep(
            () -> CloneRepository.run(githubRepoUrl),
            "Cloned " + githubRepoUrl,
            "Failed to clone " + githubRepoUrl
        );

        var sonarResult = runStep(
            () -> SonarQubeRun.run(githubRepoUrl),
            "SonarQube Scan Validate",
            "Failed to pass SonarQube Scan "
        );

        var buildResult = runStep(
            () -> BuildDockerImage.run(cloneResult.getFolder()),
            "Built " + githubRepoUrl,
            "Failed to build " + githubRepoUrl
        );

        var startResult = runStep(
            () ->
                StartDockerContainer.run(
                    buildResult.getImageName(),
                    buildResult.getImageTag()
                ),
            "Started in prod " + githubRepoUrl,
            "Failed to start in prod " + githubRepoUrl
        );

        var failures = Stream.of(
            cloneResult.getStatus() ? "CLONE_OK" : "CLONE_FAILED",
            sonarResult.getStatus() ? "SONAR_OK" : "SONAR_FAILED",
            buildResult.getStatus() ? "BUILD_OK" : "BUILD_FAILED",
            startResult.getStatus() ? "START_OK" : "START_FAILED"
        ).collect(Collectors.joining(", "));

        return BuildHistory.add(
            BuildRecap.builder()
                .status(failures)
                .imageId(buildResult.getImageId())
                .imageName(buildResult.getImageName())
                .imageTag(buildResult.getImageName())
                .containerId(startResult.getContainerId())
                .containerName(startResult.getContainerName())
                .time(LocalDateTime.now())
                .build()
        );
    }

    private static <T extends HasStatus> T runStep(
        Supplier<T> code,
        String successMessage,
        String failureMessage
    ) {
        var result = code.get();
        if (result.getStatus()) Notification.show(successMessage);
        else Notification.show(failureMessage);

        return result;
    }
}
