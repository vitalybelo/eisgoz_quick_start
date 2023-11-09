package ru.lanit.minobr.service.quick_start.authorization;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Класс описывающий сущность токена доступа пользователя keycloak. Поля по необходимости можно добавлять.
 * @Author Vitalii Belotserkovskii, 16.10.2023
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

    private String exp;
    private String iat;
    private String jti;
    private String iss;
    private Object aud;

    @JsonProperty("sub")
    private String userId;

    private String typ;

    @JsonProperty("azp")
    private String clientId;

    @JsonProperty("sid")
    private String sessionId;

    @JsonProperty("session_state")
    private String sessionState;

    @JsonProperty("realm_access")
    private LinkedHashMap<String, List<String>> realmRolesMap;

    @JsonProperty("resource_access")
    private LinkedHashMap<String, LinkedHashMap<String, List<String>>> clientRolesMap;

    @JsonProperty("given_name")
    private String firstName;

    @JsonProperty("middle_name")
    private String middleName;

    @JsonProperty("family_name")
    private String familyName;

    private String name;

    @JsonProperty("preferred_username")
    private String login;

    private String email;
    private String phone;
    private String department;
    private String position;

    @JsonProperty("email_verified")
    private boolean emailVerified;

    @JsonProperty("ip_address")
    private String ipAddress;

    @JsonProperty("max_sessions")
    private Integer maxSessions;

    @JsonProperty("max_idle_time")
    private Integer maxIdleTime;


    @JsonSetter("max_sessions")
    public void setMaxSessions(String maxSessions) {
        if (StringUtils.isNotBlank(maxSessions)) {
            try {
                this.maxSessions = Integer.parseInt(maxSessions);
                return;
            } catch (NumberFormatException ignored) {}
        }
        this.maxSessions = 0;
    }

    @JsonSetter("max_idle_time")
    public void setMaxIdleTime(String maxIdleTime) {
        if (StringUtils.isNotBlank(maxIdleTime)) {
            try {
                this.maxIdleTime = Integer.parseInt(maxIdleTime);
                return;
            } catch (NumberFormatException ignored) {}
        }
        this.maxIdleTime = 0;
    }

    @JsonGetter("preferred_username")
    public String getLogin() {
        return getParameter(login);
    }

    @JsonGetter("given_name")
    public String getFirstName() {
        return getParameter(firstName);
    }

    @JsonGetter("middle_name")
    public String getMiddleName() {
        return getParameter(middleName);
    }

    @JsonGetter("family_name")
    public String getFamilyName() {
        return getParameter(familyName);
    }

    @JsonGetter("email")
    public String getEmail() {
        return getParameter(email);
    }

    @JsonGetter("phone")
    public String getPhone() {
        return getParameter(phone);
    }

    @JsonGetter("department")
    public String getDepartment() {
        return getParameter(department);
    }

    @JsonGetter("position")
    public String getPosition() {
        return getParameter(position);
    }

    @JsonGetter("ip_address")
    public String getIpAddress() {
        return getParameter(ipAddress);
    }





    private String getParameter(String string) {
        if (StringUtils.isBlank(string)) return "";
        return string;
    }

}
