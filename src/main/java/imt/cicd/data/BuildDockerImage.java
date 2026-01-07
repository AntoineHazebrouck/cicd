package imt.cicd.data;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import java.io.File;
import java.util.Collections;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuildDockerImage {

    @Builder
    @Getter
    public static class BuildDockerImageResult implements HasStatus {

        private final String imageId;
        private final String imageName;
        private final String imageTag;
        private final Boolean status;
    }

    public static BuildDockerImageResult run(String folder) {
        var imageName = folder.split("/")[folder.split("/").length - 1];
        var imageTag = "latest";

        log.info("Starting to build {} as {}:{}", folder, imageName, imageTag);

        try {
            DockerClient dockerClient = DockerClientFactory.create();

            String imageId = dockerClient
                .buildImageCmd()
                .withDockerfile(new File(folder + "/" + "Dockerfile"))
                .withPull(true) // Pull base images if missing
                .withTags(Collections.singleton(imageName + ":" + imageTag))
                .exec(new BuildImageResultCallback())
                .awaitImageId();

            log.info(
                "Successfully built image {}:{} with image id {}",
                imageName,
                imageTag,
                imageId
            );

            return BuildDockerImageResult.builder()
                .status(true)
                .imageId(imageId)
                .imageTag(imageTag)
                .imageName(imageName)
                .build();
        } catch (Exception e) {
            log.info("Error building {}", folder, e);

            return BuildDockerImageResult.builder().status(false).build();
        }
    }
}
