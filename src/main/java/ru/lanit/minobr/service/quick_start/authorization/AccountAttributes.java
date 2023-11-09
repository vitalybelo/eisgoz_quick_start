package ru.lanit.minobr.service.quick_start.authorization;


import lombok.Getter;

/**
 * Класс перечисления атрибутов пользователя keycloak
 * @Author Vitalii Belotserkovskii, 17.10.2023
 */
@Getter
public enum AccountAttributes {

    PHONE("phone"),
    POSITION("position"),
    DEPARTMENT("department"),
    IP_ADDRESS("ip_address"),
    MIDDLE_NAME("middle_name"),
    MAX_SESSIONS("max_sessions"),
    MAX_IDLE_TIME("max_idle_time"),
    TECHNICAL_PASSWORD("is_technical_password");


    final String name;

    AccountAttributes(String name) {
        this.name = name;
    }

}
