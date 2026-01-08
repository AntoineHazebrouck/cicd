package imt.cicd.data;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

public class DockerClientFactory {

    public static DockerClient create() {
        var vmForwardedPort = "12375";
        DockerClientConfig config =
            DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(
                    "tcp://%s:%s".formatted(
                            Constants.PHYSICAL_MACHINE_HOST,
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
