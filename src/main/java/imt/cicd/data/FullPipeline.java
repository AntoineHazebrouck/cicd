package imt.cicd.data;

import com.vaadin.flow.component.notification.Notification;
import imt.cicd.data.BuildHistory.BuildRecap;
import imt.cicd.sonarqube.SonarQubeRun;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

        var failures = new ArrayList<String>();
        if (!cloneResult.getStatus()) failures.add("CLONE_FAILED");
        if (!sonarResult.getStatus()) failures.add("SONAR_FAILED");
        if (!buildResult.getStatus()) failures.add("BUILD_FAIL");

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
