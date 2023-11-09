package ru.lanit.minobr.service.quick_start.config;

import org.jetbrains.annotations.NotNull;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * Конфигурация класса безопасности, работает для аутентификации через Keycloak (под spring framework 2.18 RELEASE)
 * @Author Vitali Belotserkovskii, 09.10.2023<br><br>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${keycloak.admin}")
    private String clientName;
    @Value("${keycloak.client_secret}")
    private String clientSecret;
    @Value("${keycloak.server.url}")
    private String serverURL;
    @Value("${keycloak.realm}")
    private String realmName;

    private final KeycloakLogoutHandler keycloakLogoutHandler;

    SecurityConfig(KeycloakLogoutHandler keycloakLogoutHandler) {
        this.keycloakLogoutHandler = keycloakLogoutHandler;
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/your_api_loophole/**")
                .permitAll();

        http.authorizeRequests()
                .antMatchers("/**")
                .authenticated()
                .anyRequest()
                .permitAll();

        http.oauth2Login()
                .and()
                .logout()
                .addLogoutHandler(keycloakLogoutHandler)
                .logoutSuccessUrl("/");

        http.oauth2ResourceServer().jwt();
    }

    @Bean
    Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverURL)
                .realm(realmName)
                .clientId(clientName)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    @Bean
    public RealmResource realmResource() {
        return KeycloakBuilder.builder()
                .serverUrl(serverURL)
                .realm(realmName)
                .clientId(clientName)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build().realm(realmName);
    }

}
