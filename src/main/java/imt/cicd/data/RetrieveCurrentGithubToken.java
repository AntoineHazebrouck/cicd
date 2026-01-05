package imt.cicd.data;

import imt.cicd.config.StaticIoc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Slf4j
public class RetrieveCurrentGithubToken {

    public static String run() {
        Authentication auth = SecurityContextHolder.getContext()
            .getAuthentication();
        if (!(auth instanceof OAuth2AuthenticationToken oauthToken)) {
            // Log if auth is null - this means the ThreadLocal is empty
            log.error("Authentication is null or not OAuth2: {}", auth);
            return null;
        }

        var clientManager = StaticIoc.getBean(
            OAuth2AuthorizedClientManager.class
        );

        OAuth2AuthorizeRequest request =
            OAuth2AuthorizeRequest.withClientRegistrationId(
                oauthToken.getAuthorizedClientRegistrationId()
            )
                .principal(auth)
                .build();

        try {
            OAuth2AuthorizedClient client = clientManager.authorize(request);
            if (client != null) {
                return client.getAccessToken().getTokenValue();
            }
        } catch (Exception e) {
            log.error("Failed to authorize/refresh GitHub token", e);
        }

        return null;
    }
}
