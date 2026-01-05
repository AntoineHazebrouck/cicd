package imt.cicd;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Apply Vaadin's default security rules (CSRF, Request Cache, etc.)
        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
            configurer.loginView(LoginView.class);
        });

        // Add GitHub OAuth2 login
        http.oauth2Login(oauth2 ->
            oauth2.loginPage("/login").defaultSuccessUrl("/", true)
        );

        return http.build();
    }

    @Bean
    GrantedAuthoritiesMapper userAuthoritiesMapper() {
        var admins = List.of("AntoineHazebrouck");
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                mappedAuthorities.add(authority);

                if (authority instanceof OAuth2UserAuthority oauth2User) {
                    String githubLogin = (String) oauth2User
                        .getAttributes()
                        .get("login");

                    if (admins.contains(githubLogin)) {
                        mappedAuthorities.add(
                            new SimpleGrantedAuthority("ROLE_ADMIN")
                        );
                    }
                }
            });

            return mappedAuthorities;
        };
    }
}
