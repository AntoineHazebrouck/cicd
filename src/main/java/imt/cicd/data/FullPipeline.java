package imt.cicd.data;

import com.vaadin.flow.component.UI;
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
        return run(githubRepoUrl, null,(idx, res) -> {});
    }

    public static List<BuildRecap> run(String githubRepoUrl, UI ui, StepCallback callback) {
        var cloneResult = runStep(ui,
            () -> CloneRepository.run(githubRepoUrl),
            "Cloned " + githubRepoUrl,
            "Failed to clone " + githubRepoUrl
        );
        callback.onUpdate(0, cloneResult.getStatus());

        var sonarResult = runStep(ui,
            () -> SonarQubeRun.run(githubRepoUrl),
            "SonarQube Scan Validate",
            "Failed to pass SonarQube Scan "
        );
        callback.onUpdate(1, sonarResult.getStatus());

        var buildResult = runStep(ui,
            () -> BuildDockerImage.run(cloneResult.getFolder()),
            "Built " + githubRepoUrl,
            "Failed to build " + githubRepoUrl
        );
        callback.onUpdate(2, buildResult.getStatus());

        var startResult = runStep(ui,
            () ->
                StartDockerContainer.run(
                    buildResult.getImageName(),
                    buildResult.getImageTag()
                ),
            "Started in prod " + githubRepoUrl,
            "Failed to start in prod " + githubRepoUrl
        );
        callback.onUpdate(3, startResult.getStatus());

        var healthCheckResult = runStep(ui,
            () -> CheckAppHealth.run(),
            "Health check was ok for " + githubRepoUrl,
            "Health check failed for " + githubRepoUrl
        );
        callback.onUpdate(4, healthCheckResult.getStatus());


        var failures = Stream.of(
            cloneResult.getStatus() ? "CLONE_OK" : "CLONE_FAILED",
            sonarResult.getStatus() ? "SONAR_OK" : "SONAR_FAILED",
            buildResult.getStatus() ? "BUILD_OK" : "BUILD_FAILED",
            startResult.getStatus() ? "START_OK" : "START_FAILED",
            healthCheckResult.getStatus() ? "HEALTH_OK" : "HEALTH_FAILED"
        ).collect(Collectors.joining(", "));

        var measures = sonarResult.getMeasures();

        return BuildHistory.add(
            BuildRecap.builder()
                .status(failures)
                .imageId(buildResult.getImageId())
                .imageName(buildResult.getImageName())
                .imageTag(buildResult.getImageName())
                .containerId(startResult.getContainerId())
                .containerName(startResult.getContainerName())
                .security(measures.getOrDefault("security_rating", "0"))
                .reliability(measures.getOrDefault("reliability_rating", "0"))
                .maintainability(measures.getOrDefault("sqale_rating", "0"))
                .hotspots(measures.getOrDefault("security_review_rating", "0"))
                .coverage(measures.getOrDefault("coverage", "0.0") + "%")
                .duplications(measures.getOrDefault("duplicated_lines_density", "0.0") + "%")
                .time(LocalDateTime.now())
                .build()
        );
    }

    private static <T extends HasStatus> T runStep(
        UI ui,
        Supplier<T> code,
        String successMessage,
        String failureMessage

    ) {
        var result = code.get();

        if (ui != null) {
            ui.access(() -> {
                Notification.show(result.getStatus() ? successMessage : failureMessage);
            });
        }

        return result;
    }

    public interface StepCallback {
        void onUpdate(int stepIndex, boolean success);
    }
}
