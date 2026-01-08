package imt.cicd.data;

import imt.cicd.data.BuildDockerImage.BuildDockerImageResult;
import imt.cicd.data.BuildHistory.BuildRecap;
import imt.cicd.data.CheckAppHealth.CheckAppHealthResult;
import imt.cicd.data.CloneRepository.CloneRepositoryResult;
import imt.cicd.data.RollbackToLastContainer.RollbackToLastContainerResult;
import imt.cicd.data.StartDockerContainer.StartDockerContainerResult;
import imt.cicd.data.orchestration.ChainedOrchestrator;
import imt.cicd.data.orchestration.ChainedOrchestrator.StepsHandler;
import imt.cicd.data.orchestration.StepCallback;
import imt.cicd.sonarqube.SonarQubeRun;
import imt.cicd.sonarqube.SonarQubeRun.SonarQubeResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FullPipeline {

    public static List<BuildRecap> run(String githubRepoUrl) {
        return run(githubRepoUrl, (idx, res) -> {});
    }

    public static List<BuildRecap> run(
        String githubRepoUrl,
        StepCallback callback
    ) {
        log.info("Starting pipeline with repo {}", githubRepoUrl);
        var pipeline = ChainedOrchestrator.withStepCompletionCallback(callback)
            .step(
                () -> CloneRepository.run(githubRepoUrl),
                StepsHandler::stopThere
            )
            .step(
                steps -> SonarQubeRun.run(githubRepoUrl),
                StepsHandler::stopThere
            )
            .step(
                steps -> {
                    var clone = steps.find(CloneRepositoryResult.class);
                    return BuildDockerImage.run(clone.getFolder());
                },
                StepsHandler::stopThere
            )
            .step(
                steps -> {
                    var build = steps.find(BuildDockerImageResult.class);
                    return StartDockerContainer.run(
                        build.getImageName(),
                        build.getImageTag(),
                        build.getImageId()
                    );
                },
                StepsHandler::stopThere
            )
            .step(steps -> CheckAppHealth.run(), StepsHandler::keepGoing)
            .step(
                steps -> {
                    var health = steps.find(CheckAppHealthResult.class);

                    return RollbackToLastContainer.run(health.getStatus());
                },
                StepsHandler::stopThere
            )
            .finish();

        log.info(
            "Pipeline steps run : {}",
            pipeline
                .getAllSteps()
                .stream()
                .map(HasStatus::getStatus)
                .map(Object::toString)
                .collect(Collectors.joining(", "))
        );

        var failures = Stream.of(
            pipeline.formattedStepStatus(CloneRepositoryResult.class, "CLONE"),
            pipeline.formattedStepStatus(SonarQubeResult.class, "SONAR"),
            pipeline.formattedStepStatus(BuildDockerImageResult.class, "BUILD"),
            pipeline.formattedStepStatus(
                StartDockerContainerResult.class,
                "START"
            ),
            pipeline.formattedStepStatus(CheckAppHealthResult.class, "HEALTH"),
            pipeline.formattedStepStatus(
                RollbackToLastContainerResult.class,
                "ROLLBACK"
            )
        ).collect(Collectors.joining(", "));

        var measures = pipeline
            .findOptional(SonarQubeResult.class)
            .map(sonar -> sonar.getMeasures())
            .orElse(Map.of());

        var buildResult = pipeline.findOptional(BuildDockerImageResult.class);
        var startResult = pipeline.findOptional(
            StartDockerContainerResult.class
        );

        var history = BuildHistory.add(
            BuildRecap.builder()
                .status(failures)
                .imageId(
                    buildResult.map(build -> build.getImageId()).orElse(null)
                )
                .imageName(
                    buildResult.map(build -> build.getImageName()).orElse(null)
                )
                .imageTag(
                    buildResult.map(build -> build.getImageTag()).orElse(null)
                )
                .containerId(
                    startResult
                        .map(start -> start.getContainerId())
                        .orElse(null)
                )
                .containerName(
                    startResult
                        .map(start -> start.getContainerName())
                        .orElse(null)
                )
                .rollbackContainerId(
                    pipeline
                        .findOptional(RollbackToLastContainerResult.class)
                        .map(start -> start.getContainerId())
                        .orElse(null)
                )
                .security(measures.getOrDefault("security_rating", "0"))
                .reliability(measures.getOrDefault("reliability_rating", "0"))
                .maintainability(measures.getOrDefault("sqale_rating", "0"))
                .hotspots(measures.getOrDefault("security_review_rating", "0"))
                .coverage(measures.getOrDefault("coverage", "0.0") + "%")
                .duplications(
                    measures.getOrDefault("duplicated_lines_density", "0.0") +
                    "%"
                )
                .time(LocalDateTime.now())
                .build()
        );
        return history;
    }
}
