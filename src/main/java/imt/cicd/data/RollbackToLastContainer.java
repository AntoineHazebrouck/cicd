package imt.cicd.data;

import imt.cicd.data.BuildHistory.BuildRecap;
import java.util.Comparator;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RollbackToLastContainer {

    @Builder
    @Getter
    public static class RollbackToLastContainerResult implements HasStatus {

        private final Boolean status;
        private final String containerId;
    }

    public static RollbackToLastContainerResult run(boolean healthy) {
        if (healthy) {
            log.info("Application is healthy, no rollback required");
            return RollbackToLastContainerResult.builder().status(true).build();
        }

        log.info("Starting to roll back");

        var lastFullySucessfulBuild = BuildHistory.history()
            .stream()
            .filter(build -> isFullySuccessful(build))
            .sorted(Comparator.comparing(BuildRecap::getTime).reversed())
            .findFirst();

        var failure = RollbackToLastContainerResult.builder()
            .status(false)
            .build();

        if (lastFullySucessfulBuild.isEmpty()) {
            log.info("Could not find an image to roll back to");
            return failure;
        }

        var build = lastFullySucessfulBuild.get();

        log.info("Rolling back to latest stable image : {}", build.getImageId());

        var start = StartDockerContainer.run(
            build.getImageName(),
            build.getImageTag(),
            build.getImageId()
        );

        if (!start.getStatus()) {
            log.info("Failed to roll back to {}", build.getImageId());
            return failure;
        }

        log.info(
            "Successfully rolled back to last stable image : {}",
            build.getImageId()
        );
        return RollbackToLastContainerResult.builder()
            .status(true)
            .containerId(start.getContainerId())
            .build();
    }

    private static boolean isFullySuccessful(BuildRecap build) {
        return build.getStatus().contains("FAIL") == false;
    }
}
