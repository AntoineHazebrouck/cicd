package imt.cicd.data;

import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@Slf4j
public class CheckAppHealth {

    @Builder
    @Getter
    public static class CheckAppHealthResult implements HasStatus {

        private final Boolean status;
    }

    public static CheckAppHealthResult run() {
        var failure = CheckAppHealthResult.builder().status(false).build();
        try {
            waitForApplicationToBeUp();

            var vmForwardedPort = "18080";
            var healthUrl =
                "http://%s:%s/clients".formatted(
                        Constants.PHYSICAL_MACHINE_HOST,
                        vmForwardedPort
                    );

            var rest = RestClient.builder().build();
            var response = rest
                .get()
                .uri(healthUrl)
                .retrieve()
                .toEntity(String.class);

            if (
                response.getStatusCode().is2xxSuccessful() &&
                response.getBody().startsWith("[") &&
                response.getBody().endsWith("]")
            ) {
                log.info("health check was successful at {}", healthUrl);

                return CheckAppHealthResult.builder().status(true).build();
            } else {
                log.info("health check failed {}", response);

                return failure;
            }
        } catch (Exception e) {
            log.error("failed while checking health", e);
            return failure;
        }
    }

    private static void waitForApplicationToBeUp() throws InterruptedException {
        log.info("sleeping for one minute");
        TimeUnit.MINUTES.sleep(1);
    }
}
