package ru.lanit.minobr.service.quick_start.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {

    @Value("${keycloak.admin}")
    private String clientName;
    @Value("${keycloak.client_secret}")
    private String clientSecret;
    @Value("${keycloak.server.url}")
    private String serverURL;
    @Value("${keycloak.realm}")
    private String realmName;


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
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
