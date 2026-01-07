package imt.cicd.data;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartDockerContainer {

    @Builder
    @Getter
    public static class StartDockerContainerResult implements HasStatus {

        private final Boolean status;
        private final String containerId;
        private final String containerName;
    }

    public static StartDockerContainerResult run(
        String imageName,
        String imageTag
    ) {
        log.info("Starting docker container {}:{}", imageName, imageTag);

        try {
            var containerName = imageName + "_prod";

            var dockerClient = DockerClientFactory.create();

            dockerClient
                .removeContainerCmd(containerName)
                .withForce(true) // Kills it if running
                // .withRemoveVolumes(true) // Cleans up associated anonymous volumes
                .exec();
            log.info("Removed old container if it existed {}", containerName);

            var container = dockerClient
                .createContainerCmd("%s:%s".formatted(imageName, imageTag))
                .withName(containerName)
                .exec();

            dockerClient.startContainerCmd(container.getId()).exec();

            log.info(
                "Successfully started docker container {}:{} with id {} and name {}",
                imageName,
                imageTag,
                container.getId(),
                containerName
            );

            return StartDockerContainerResult.builder()
                .status(true)
                .containerId(container.getId())
                .containerName(containerName)
                .build();
        } catch (Exception e) {
            log.info(
                "Failed to start docker container {}:{}",
                imageName,
                imageTag,
                e
            );
            return StartDockerContainerResult.builder().status(false).build();
        }
    }
}
