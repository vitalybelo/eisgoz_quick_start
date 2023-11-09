package ru.lanit.minobr.service.quick_start.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.*;

/**
 * Класс для извлечения учетных данных и ролей пользователя из "Bearer" или "ID" токенов доступа.
 * @Author Vitalii Belotserkovskii, 05.11.2023
 */
@Slf4j
@Getter
@Component
public class AccessTokenService {

    private static final String EMPTY_STRING = "";
    private static final String IP_DELIMITER = "##";
    private AccessToken accessToken = null;


    /**
     * Проверяет доступность класса аутентификации spring security для чтения данных пользователя из токена доступа
     * @return true если security context доступен
     */
    private boolean isSpringContext() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .isPresent();
    }


    /**
     * В зависимости от состава контекста безопасности spring boot security, инициализирует авторизационный
     * класс AccessToken для чтения данных пользователя из токена аутентификации.
     * @return true если класс авторизации AccessToken инициализирован
     */
    public boolean assign() {
        // проверяем инициализацию
        if (accessToken == null) {
            if (isSpringContext()) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Object principal = authentication.getPrincipal();

                // Случай, когда аутентификация проходит по типу "Bearer"
                if (principal instanceof Jwt) {
                    Jwt jwt = (Jwt) principal;
                    accessToken = parseAccessToken(jwt.getTokenValue());
                    if (accessToken != null) return true;
                }

                // Случай, когда аутентификация проходит по типу "ID"
                if (principal instanceof DefaultOidcUser) {
                    DefaultOidcUser user = (DefaultOidcUser) principal;
                    accessToken = parseAccessToken(user.getIdToken().getTokenValue());
                    return accessToken != null;
                }
            }
            return false;
        }
        return true;
    }


    /**
     * Пытается инициализировать класс авторизации AccessToken из класса безопасности поступающего на вход контроллера.
     * @param principal класс java.security поступающий на вход контроллера spring boot
     * @return true если класс авторизации AccessToken инициализирован
     */
    public boolean assign(Principal principal) {

        if (principal != null) {
            JwtAuthenticationToken jwt = getJwtToken(principal);
            if (jwt != null) {
                accessToken = parseAccessToken(jwt.getToken().getTokenValue());
                if (accessToken != null) return true;
            }
            OidcUser oidc = getOidcUser(principal);
            if (oidc != null) {
                accessToken = parseAccessToken(oidc.getIdToken().getTokenValue());
                return accessToken != null;
            }
        }
        return false;
    }


    /**
     * @return Возвращает имя пользователя, использующееся как login для входа
     */
    public @NotNull String getLogin() {
        if (assign()) return accessToken.getLogin();
        return EMPTY_STRING;
    }


    /**
     * @return Возвращает имя пользователя
     */
    public @NotNull String getFirstName() {
        if (assign()) return accessToken.getFirstName();
        return EMPTY_STRING;
    }


    /**
     * @return Возвращает отчество пользователя
     */
    public @NotNull String getMiddleName() {
        if (assign()) return accessToken.getMiddleName();
        return EMPTY_STRING;
    }


    /**
     * @return Возвращает фамилию пользователя
     */
    public @NotNull String getFamilyName() {
        if (assign()) return accessToken.getFamilyName();
        return EMPTY_STRING;
    }


    /**
     * @return Возвращает полное имя пользователя: имя, отчество и фамилию
     */
    public @NotNull String getFullName() {
        if (assign()) {
            String space = " ";
            String firstName = accessToken.getFirstName();
            String middleName = accessToken.getMiddleName();
            String familyName = accessToken.getFamilyName();
            StringBuilder fio = new StringBuilder();
            if (StringUtils.isBlank(firstName)) fio.append(getLogin());
            else fio.append(getFirstName());
            if (StringUtils.isNotBlank(middleName)) fio.append(space).append(middleName);
            if (StringUtils.isNotBlank(familyName)) fio.append(space).append(familyName);
            return fio.toString();
        }
        return EMPTY_STRING;
    }


    /**
     * @return Возвращает строку с электронной почтой пользователя
     */
    public @NotNull String getEmail() {
        if (assign()) return accessToken.getEmail();
        return EMPTY_STRING;
    }


    /**
     * @return Возвращает строку с номером телефона пользователя
     */
    public @NotNull String getPhone() {
        if (assign()) return accessToken.getPhone();
        return EMPTY_STRING;
    }


    /**
     * @return Возвращает строку с названием департамента пользователя
     */
    public @NotNull String getDepartment() {
        if (assign()) return accessToken.getDepartment();
        return EMPTY_STRING;
    }


    /**
     * @return Возвращает строку с названием должности пользователя
     */
    public @NotNull String getPosition() {
        if (assign()) return accessToken.getPosition();
        return EMPTY_STRING;
    }


    /**
     * @return Возвращает список разрешенных ip адресов пользователя
     */
    public @NotNull List<String> getIpAddress() {
        if (assign()) {
            String ipString = accessToken.getIpAddress();
            if (StringUtils.isNotBlank(ipString)) {
                String[] ips = ipString.split(IP_DELIMITER);
                return List.of(ips);
            }
        }
        return Collections.emptyList();
    }


    /**
     * @return Возвращает количество разрешенных одновременно сессия для пользователя
     */
    public Integer getMaxSession() {
        if (assign()) return accessToken.getMaxSessions();
        return 0;
    }


    /**
     * @return Возвращает максимальное время простоя для пользователя
     */
    public Integer getMaxIdleTime() {
        if (assign()) return  accessToken.getMaxIdleTime();
        return 0;
    }


    /**
     * Извлекает все значения realm ролей, которые были назначены пользователю.
     * @return строковый список realm ролей
     */
    public List<String> getRealmRoles() {
        if (assign()) {
            return accessToken.getRealmRolesMap()
                    .entrySet()
                    .stream()
                    .collect(ArrayList::new, (list, element) -> list.addAll(element.getValue()), ArrayList::addAll);
        }
        return Collections.emptyList();
    }


    /**
     * Извлекает все значения client ролей, которые были назначены пользователю
     * @return строковый список всех client ролей
     */
    public List<String> getClientRoles() {
        if (assign()) {
            List<String> rolesList = new ArrayList<>();
            for (Map.Entry<String, LinkedHashMap<String, List<String>>> allKeys : accessToken.getClientRolesMap().entrySet()) {
                for (Map.Entry<String, List<String>> set : allKeys.getValue().entrySet()) {
                    rolesList.addAll(set.getValue());
                }
            }
            return rolesList;
        }
        return Collections.emptyList();
    }


    /**
     * Выполняет проверку на наличие у пользователя требуемой роли
     * @param roleString строка требуемой роли
     * @return true если роль подтверждена, иначе false
     */
    public boolean isAllowed(String roleString) {
        if (assign()) {
            return getClientRoles()
                    .stream()
                    .anyMatch(s -> s.equals(roleString));
        }
        return false;
    }


    /**
     * Метод извлекает экземпляр класса авторизации AccessToken из payload токена доступа keycloak.
     * @param tokenString строка с токеном доступа keycloak, без префикса Bearer
     * @return экземпляр класса AccessToken, или null в случае ошибки
     */
    private AccessToken parseAccessToken(String tokenString) {

        AccessToken accessToken = null;
        if (!StringUtils.isBlank(tokenString)) {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String[] chunks = tokenString.split("\\.");
            if (chunks.length > 1) {
                //String header = new String(decoder.decode(chunks[0]));
                String payload = new String(decoder.decode(chunks[1]));
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    accessToken = objectMapper.readValue(payload, AccessToken.class);
                } catch (Exception e) {
                    log.info(">>> Ошибка парсинга токена доступа: {}", e.getMessage());
                }
            }
        }
        return accessToken;
    }


    /**
     * Переопределяет экземпляр класса Principal в экземпляр класса токена доступа: JwtAuthenticationToken
     * @param principal класс java.security поступающий на вход контроллера spring boot frameworks
     * @return экземпляр класса JwtAuthenticationToken или null
     */
    private JwtAuthenticationToken getJwtToken(Principal principal) {
        if (principal instanceof JwtAuthenticationToken) {
            return (JwtAuthenticationToken) principal;
        }
        return null;
    }


    /**
     * Переопределяет экземпляр класса Principal в экземпляр класса токена доступа: OidcUser
     * @param principal класс java.security поступающий на вход контроллера spring boot frameworks
     * @return экземпляр класса OidcUser или null
     */
    private OidcUser getOidcUser(Principal principal) {
        if (principal instanceof Authentication) {
            Authentication authentication = (Authentication) principal; // не убирать
            if (authentication.getPrincipal() instanceof OidcUser) {
                return (OidcUser) authentication.getPrincipal();
            }
        }
        return null;
    }



}
