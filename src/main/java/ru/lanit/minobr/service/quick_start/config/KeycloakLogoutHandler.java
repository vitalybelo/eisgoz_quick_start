package ru.lanit.minobr.service.quick_start.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Класс KeycloakLogoutHandler реализует интерфейс LogoutHandler и отправляет запрос на выход в Keycloak.
 * @author Vitali Belotserkovskii 11.05.2023<br>
 */
@Slf4j
@Component
public class KeycloakLogoutHandler implements LogoutHandler {

    private final RestTemplate restTemplate;

    public KeycloakLogoutHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       @NotNull Authentication auth) {
        logoutFromKeycloak((OidcUser) auth.getPrincipal(), false);
        SecurityContextHolder.clearContext();
    }

    public void logout(@NotNull Authentication auth, boolean silent) {
        logoutFromKeycloak((OidcUser) auth.getPrincipal(), silent);
        SecurityContextHolder.clearContext();
    }

    /**
     * Метод реализует выход из keycloak запросом по back channel
     * @param user - информация о пользователе, id_token + утверждения из jwt токена
     * @param silent - true отключает вывод log сообщения
     */
    public void logoutFromKeycloak(@NotNull OidcUser user, boolean silent)
    {
        String endSessionEndpoint = user.getIssuer() + "/protocol/openid-connect/logout";
        String clientId = user.getClaimAsString("azp");
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(endSessionEndpoint)
                .userInfo(user.getUserInfo().toString())
                .queryParam("client_id", clientId)
                .queryParam("logout_hint", user.getName())
                .queryParam("id_token_hint", user.getIdToken().getTokenValue());

        ResponseEntity<String> logoutResponse = restTemplate.getForEntity(builder.toUriString(), String.class);

        if (!silent) {
            if (logoutResponse.getStatusCode().is2xxSuccessful()) {
                log.info("User: {} :: successfully logged out from Keycloak", user.getName());
            } else {
                log.error("User: {} :: could not propagate logout to Keycloak", user.getName());
            }
        }
    }

}