package imt.cicd.data;

import imt.cicd.config.StaticIoc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Slf4j
public class RetrieveCurrentGithubToken {

    public static String run() {
        var authorizedClientService = StaticIoc.getBean(
            OAuth2AuthorizedClientService.class
        );

        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            // "github" should match the registrationId in your application.yml
            OAuth2AuthorizedClient client =
                authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
                );
            log.info("Retrieved oauth client : {}", client);

            if (client != null && client.getAccessToken() != null) {
                var token = client.getAccessToken().getTokenValue();
                log.info("Retrieved github token : {}", token);
                return token;
            }
        }
        log.error("Error retrieving github token : {}", authentication);

        return null;
    }
}
