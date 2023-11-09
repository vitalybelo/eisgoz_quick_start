package ru.lanit.minobr.service.quick_start.authorization;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.*;

/**
 * Содержит набор методов возвращающих, после аутентификации, учётные данные пользователя, извлеченные из id токена.
 * @Author Vitali Belotserkovskii 11.05.2023<br><br>
 */
@Slf4j
@Component
@NoArgsConstructor
public class AuthorizationService {

    public static final String ATTRIBUTE_PHONE = "phone";
    public static final String ATTRIBUTE_MIDDLE_NAME = "middle_name";
    public static final String ATTRIBUTE_DEPARTMENT = "department";
    public static final String ATTRIBUTE_POSITION = "position";
    public static final String ATTRIBUTE_NUMBER_VP = "number_vp";
    public static final String ATTRIBUTE_ROLE_NAME = "role_name";
    public static final String ATTRIBUTE_IP_ADDRESS = "ip_address";
    public static final String ATTRIBUTE_MAX_SESSIONS = "max_sessions";
    public static final String ATTRIBUTE_MAX_IDLE = "max_idle_time";
    public static final String RETURN_EMPTY_STRING = "";

    public static final String CLIENT_ACCESS = "resource_access";
    public static final String REALM_ACCESS = "realm_access";
    public static final String ROLES_CLAIMS = "roles";
    public static final String CLAIM_ID = "sub";
    public static final String CLAIM_CLIENT = "azp";

    public static final String SESSION_ID = "sid";


    /**
     * Проверяет доступность класса аутентификации spring security для чтения данных пользователя из памяти
     * @return true если security context доступен
     */
    public static boolean isSpringContext() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .isPresent();
    }

    /**
     * Возвращает информацию о пользователе: id токен, утверждения, стандартную учётку, кастомные аттрибуты.
     * @return экземпляр класса аутентификации Authentication, или null если не читается
     */
    public static Authentication getAuthentication() {
        if (isSpringContext()) {
            return SecurityContextHolder.getContext().getAuthentication();
        }
        return null;
    }

    /**
     * Возвращает информацию о пользователе: id токен, утверждения, стандартную учётку, кастомные аттрибуты.
     * @param principal java.security класс аутентифицированного запроса
     * @return экземпляр класса аутентификации Oauth2 Open Id Connect :: OidcUser с обширным набором методов,
     * или null если не читается
     */
    public static OidcUser getOidcUser(Principal principal) throws NullPointerException {
        if (principal instanceof Authentication) {
            Authentication authentication = (Authentication) principal; // не убирать
            if (authentication.getPrincipal() instanceof OidcUser) {
                return (OidcUser) authentication.getPrincipal();
            }
        }
        return null;
    }

    /**
     * Возвращает информацию о пользователе: id токен, утверждения, стандартную учётку, кастомные аттрибуты.
     * @return экземпляр класса аутентификации Oauth2 Open Id Connect :: OidcUser с обширным набором методов,
     * или null если не читается
     */
    public static OidcUser getOidcUser() throws NullPointerException {
        if (isSpringContext()) {
            return (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }

    /**
     * Возвращает имя пользователя = логин (login name)
     * @return строка имени пользователя login,<br> пустая строка - признак ошибки
     */
    public static String getUserName() {
        if (isSpringContext()) {
            OidcUser user = getOidcUser();
            String userName = Objects.requireNonNull(user).getPreferredUsername();
            return (userName != null ? user.getPreferredUsername() : RETURN_EMPTY_STRING);
        }
        return RETURN_EMPTY_STRING;
    }

    /**
     * Возвращает имя пользователя как Given Name.<br> Это не username, который используется для login.
     * @return строка с данным родителями именем
     */
    public static String getFirstName() {
        if (isSpringContext()) {
            OidcUser user = getOidcUser();
            String givenName = Objects.requireNonNull(user).getGivenName();
            return (givenName != null ? user.getGivenName() : RETURN_EMPTY_STRING);
        }
        return RETURN_EMPTY_STRING;
    }

    /**
     * Метод извлекает кастомный аттрибут из id токена middle_name = "отчество". Передается в наборе claims.
     * @return строка "отчество" пользователя, или пустая строка если поле не было задано
     */
    public static String getMiddleName() {
        return getClaimValue(ATTRIBUTE_MIDDLE_NAME);
    }

    /**
     * Возвращает фамилию пользователя из стандартных учётных данных keycloak
     * @return строка с фамилией пользователя, или пустая строка если поле не было задано
     */
    public static String getLastName() {
        if (isSpringContext()) {
            OidcUser user = getOidcUser();
            String familyName = Objects.requireNonNull(user).getFamilyName();
            return (familyName != null ? familyName : RETURN_EMPTY_STRING);
        }
        return RETURN_EMPTY_STRING;
    }

    /**
     * Возвращает полное имя пользователя = First Name + "отчество" + Last Name
     * @return строка полного имени пользователя, или username если эти данные не заданы
     */
    public static String getFullName() {
        if (isSpringContext()) {
            OidcUser user = getOidcUser();
            String fullName = getFirstName() + " " + getMiddleName() + " " + getLastName();
            return (StringUtils.isBlank(fullName) ? Objects.requireNonNull(user).getPreferredUsername() : fullName);
        }
        return RETURN_EMPTY_STRING;
    }

    /**
     * Возвращает "электронную почту" пользователя из стандартной учётки keycloak если она была задана.
     * @return строка с "электронной почтой", или пустая строка если поле не было задано
     */
    public static String getUserEmail() {
        if (isSpringContext()) {
            OidcUser user = getOidcUser();
            String email = Objects.requireNonNull(user).getEmail();
            return (email != null ? user.getEmail() : RETURN_EMPTY_STRING);
        }
        return RETURN_EMPTY_STRING;
    }

    /**
     * Метод извлекает кастомный аттрибут из id токена phone = "телефон". Передается в наборе claims.
     * @return строка с номером телефона, или пустая строка если ключ атрибута не задан
     */
    public static String getPhoneNumber() {
        return getClaimValue(ATTRIBUTE_PHONE);
    }

    /**
     * Метод извлекает кастомный аттрибут из id токена department = "подразделение". Передается в наборе утверждений claims.
     * @return строка с "подразделением", или пустая строка если ключ атрибута не задан
     */
    public static String getDepartment() {
        return getClaimValue(ATTRIBUTE_DEPARTMENT);
    }

    /**
     * Метод извлекает кастомный аттрибут из id токена position = "должность". Передается в наборе claims.
     * @return строка с "должностью", или пустая строка если ключ атрибута не задан
     */
    public static String getPosition() {
        return getClaimValue(ATTRIBUTE_POSITION);
    }

    /**
     * Метод извлекает кастомный аттрибут из id токена "number_vp" = "номер представительства". Передается в наборе claims.
     * @return строка с "номер представительства", или пустая строка если ключ атрибута не задан
     */
    public static String getNumberMilitaryDepartment() {
        return getClaimValue(ATTRIBUTE_NUMBER_VP);
    }

    /**
     * Метод извлекает кастомный аттрибут "role_name" = "название роли". Передается в наборе claims.
     * @return строка с "именем роли", или пустая строка если ключ атрибута не задан
     */
    public static String getRoleName() {
        return getClaimValue(ATTRIBUTE_ROLE_NAME);
    }

    /**
     * Ищет в атрибутах пользователя разрешенные ip адреса, и сливает их в одну строку (если они есть)
     * @return строку из ip адресов, разделенные запятой
     */
    public static String getIpAddress() {
        String ipString = "";
        List<String> ips = getClaimValueList(ATTRIBUTE_IP_ADDRESS);
        if (ips.size() > 1) {
            ipString = String.join(",", ips);
        }
        return ipString;
    }

    /**
     * Ищет в атрибутах пользователя "максимальное количество параллельных сессий" и конвертирует его в целое значение
     * @return "максимальное количество параллельных сессий", или ноль если не удалось прочитать
     */
    public static int getMaxSession() {
        Integer result = getClaimValueInteger(ATTRIBUTE_MAX_SESSIONS);
        return (result == null ? 0 : result);
    }

    /**
     * Ищет в атрибутах пользователя "ограничение времени простоя" и конвертирует его в целое значение
     * @return "ограничение времени простоя", или ноль если не удалось прочитать
     */
    public static int getMaxIdleTime() {
        Integer result = getClaimValueInteger(ATTRIBUTE_MAX_IDLE);
        return (result == null ? 0 : result);
    }

    /**
     * Ищет атрибут по ключу и пробует выполнить конвертацию строки со значением в целое число
     * @param claimName ключ для поиска строки значения атрибута
     * @return целое значение атрибута, или null при ошибке
     */
    public static Integer getClaimValueInteger(String claimName) {
        Integer result = null;
        String maxSession = getClaimValue(claimName);
        if (!StringUtils.isBlank(maxSession)) {
            try {
                result = Integer.parseInt(maxSession);
            } catch (NumberFormatException e) {
                log.info("Ошибка чтения целого значения атрибута {}", claimName);
            }
        }
        return result;
    }

    /**
     * Метод извлекает значение аттрибута пользователя из токена доступа
     * @param claimName ключ искомого атрибута
     * @param error_result значение, возвращаемая в случае ошибки чтения по ключу атрибута
     * @return строка со значением атрибута, или заданный error_result
     */
    public static String getClaimValue(String claimName, String error_result) {
        if (!StringUtils.isBlank(claimName)) {
            OidcUser user = getOidcUser();
            if (user != null) {
                String value = user.getClaimAsString(claimName);
                if (!StringUtils.isBlank(value)) return value;
            }
        }
        return error_result;
    }

    /**
     * Метод извлекает значение аттрибута пользователя из токена доступа
     * @param claimName ключ искомого атрибута
     * @return строка со значением атрибута, или пустая строка если ключ атрибута не задан
     */
    public static String getClaimValue(String claimName) {
        return getClaimValue(claimName, RETURN_EMPTY_STRING);
    }

    /**
     * Метод извлекает список значений аттрибута пользователя из токена доступа.
     * Можно применять только к атрибутам, у которых в админке keycloak задана multivalued опция, иначе
     * метод будет возвращать всегда пустой список.
     * @param claimName ключ атрибута
     * @return список значений атрибута, или emptyList() если ключ атрибута не задан или ошибка
     */
    public static List<String> getClaimValueList(String claimName) {
        if (!StringUtils.isBlank(claimName))
        {
            OidcUser user = getOidcUser();
            if (user != null) {
                List<String> values = user.getClaimAsStringList(claimName);
                if (values != null) return values;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Извлекает из id token список realm ролей.<br><br>
     * <b>Внимание: обязательно в client scopes, для roles и roles_list - маппер должен иметь multivalued</b><br><br>
     * @return Список realm ролей<br><br>
     */
    private static List<String> extractRealmRolesList() {

        List<String> realmRolesList = new ArrayList<>();
        if (isSpringContext()) {
            OidcUser user = getOidcUser();
            assert user != null;
            if (user.containsClaim(REALM_ACCESS)) {
                Map<String, Object> rolesClaimMap = new HashMap<>(user.getClaimAsMap(REALM_ACCESS));
                JSONArray jsonArray = new JSONArray();
                if (rolesClaimMap.containsKey(ROLES_CLAIMS)) {
                    if (rolesClaimMap.get(ROLES_CLAIMS) instanceof List) {
                        jsonArray.addAll((List) rolesClaimMap.get(ROLES_CLAIMS));
                        for (Object o : jsonArray)
                            realmRolesList.add(o.toString());
                    }
                }
            }
        }
        return realmRolesList;
    }

    /**
     * Извлекает из id token список client ролей.<br><br>
     * <b>Внимание: обязательно в client scopes, для roles и roles_list - маппер должен иметь multivalued</b><br><br>
     * @return Список client ролей<br><br>
     */
    private static List<String> extractClientRolesList() {

        List<String> clientRolesList = new ArrayList<>();
        if (isSpringContext()) {
            OidcUser user = getOidcUser();
            // извлекаем название службы (client_id)
            String clientName = getClientName();
            if (!StringUtils.isBlank(clientName)) {
                assert user != null;
                if (user.containsClaim(CLIENT_ACCESS)) {
                    // извлекаем карту с утверждениями клиентских ролей
                    Map<String, Object> o = new HashMap<>(user.getClaimAsMap(CLIENT_ACCESS));
                    Object optionalMap = o.get(clientName);
                    if (optionalMap instanceof Map) {
                        if (((Map<?, ?>) optionalMap).containsKey(ROLES_CLAIMS)) {
                            if (((Map<?, ?>) optionalMap).get(ROLES_CLAIMS) instanceof List) {
                                JSONArray jsonArray = new JSONArray();
                                jsonArray.addAll((List) ((Map<?, ?>) optionalMap).get(ROLES_CLAIMS));
                                for (Object object : jsonArray)
                                    clientRolesList.add(object.toString());
                            }
                        }
                    }
                }
            }
        }
        return clientRolesList;
    }

    /**
     * Возвращает realm список ролей пользователя.
     * @return List коллекция - список realm ролей, или пустой список если роли не назначены
     */
    public static List<String> getRealmRolesList() {
        return extractRealmRolesList();
    }

    /**
     * Возвращает client список ролей пользователя.
     * @return List коллекция - список client ролей, или пустой список если роли не назначены
     */
    public static List<String> getClientRolesList() {
        return extractClientRolesList();
    }

    /**
     * Возвращает id федеративного пользователя.<br>
     * Этот id является частью id пользователя keycloak, и содержится в id токене, в утверждении claims "sub".<br>
     * Формат keycloak id для федеративного пользователя:<br>
     * f:{keycloak_id}:{id в jdbc хранилище}
     * @return id аккаунта пользователя в базе КОИ<br><br>
     */
    public static Long getFederationUserId()
    {
        long userId = 1L;
        OidcUser user = getOidcUser();

        assert user != null;
        if (user.containsClaim("sub")) {
            String keycloakId = user.getClaimAsString("sub");
            if (keycloakId != null && !keycloakId.isEmpty()) {
                int finishIndex = keycloakId.length();
                int startIndex = keycloakId.lastIndexOf(":");
                if (startIndex != -1) {
                    ++startIndex;
                    if (finishIndex > startIndex) {
                        try {
                            userId = Long.parseLong(keycloakId.substring(startIndex, finishIndex));
                        } catch (NumberFormatException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        }
        return userId;
    }

    /**
     * Возвращает строку id пользователя из базы keycloak
     * @return uuid пользователя keycloak, или пустую строку
     */
    public static String getKeycloakId() {
        return getClaimValue(CLAIM_ID);
    }

    /**
     * Возвращает строку id пользователя из базы keycloak
     * @param principal java.security класс аутентифицированного запроса
     * @return uuid пользователя keycloak, или пустую строку
     */
    public static String getKeycloakId(Principal principal) {
        OidcUser user = getOidcUser(principal);
        if (user != null) return user.getClaimAsString(CLAIM_ID);
        return null;
    }

    /**
     * Возвращает строку с именем клиента keycloak
     * @return строку с именем клиента client, в случае ошибки - пустую строку
     */
    public static String getClientName() {
        return getClaimValue(CLAIM_CLIENT);
    }


}
