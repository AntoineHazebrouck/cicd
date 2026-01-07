package imt.cicd.data;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import java.io.File;
import java.util.Collections;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuildDockerImage {

    @Builder
    @Getter
    public static class BuildDockerImageResult {

        private final String image;
        private final String imageTag;
        private final boolean status;
    }

    public static BuildDockerImageResult run(String folder) {
        try {
            DockerClient dockerClient = dockerClient();

            var imageName = folder.split("/")[folder.split("/").length - 1];

            log.info(
                "Starting to build {} with image name {}",
                folder,
                imageName
            );

            String imageId = dockerClient
                .buildImageCmd()
                .withDockerfile(new File(folder + "/" + "Dockerfile"))
                .withPull(true) // Pull base images if missing
                .withTags(Collections.singleton(imageName + ":latest"))
                .exec(new BuildImageResultCallback())
                .awaitImageId();

            log.info("Successfully built image: " + imageId);

            return BuildDockerImageResult.builder()
                .status(true)
                .image(imageId)
                .imageTag(imageName)
                .build();
        } catch (Exception e) {
            log.info("Error building {}", folder, e);

            return BuildDockerImageResult.builder().status(false).build();
        }
    }

    private static DockerClient dockerClient() {
        var physicalMachineHost = "host.docker.internal";
        var vmForwardedPort = "12375";
        DockerClientConfig config =
            DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(
                    "tcp://%s:%s".formatted(
                            physicalMachineHost,
                            vmForwardedPort
                        )
                )
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
}
