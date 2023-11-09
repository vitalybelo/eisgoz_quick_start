Пример для использования класса авторизации AccessTokenService.

1. Класс читает из контекста безопасности java и spring security ID или Bearer токен
2. ID токен формируется как правило после того, как выполнена аутентификация через браузер
3. Bearer токен формируется при аутентификации в http запросах (например такие, какие выполняет Postman)
4. Класс AccessTokenService извлекает из токена учетные данные пользователя
5. Учётные данные доступны через public методы класса

В проекте есть MainPageController, который в примитивном виде отображает учетные данный пользователя под которым был выполнен вход через порт 8080

Методы класса AccessTokenService для чтение ролей и авторизации

1. Метод getRealmRoles() возвращает роли, назначенные пользователю как Realm роли, или роли области. В этих ролях можно найти "типы" аккаунта, например MAIN_ADMIN или USER
2. Метод getClientRoles() возвращает роли, назначенные пользователю как Client роли, или роли сервисов. Именно эти роли используются как набор действий по информационным объектам.
3. Метод isAllowed(String roleString) проверяет наличия требуемой роли у пользователя

Файл настроек application.properties

в нем есть блок настроек keycloak, ниже приведены пояснения по строкам

##--------------------- SECURITY KEYCLOAK ----------------------------------------------------------------------------
keycloak.server.url=http://172.29.92.91:8443 ---> это адрес расположения сервера keycloak
keycloak.realm=IntegrationNewModel ---> это название области используемой для работы
keycloak.admin=login-admin
keycloak.client=login-web
keycloak.client_secret=b6Nuopf9gp6jRRFJ0TTFpD0yaVSZQAJf

spring.security.oauth2.client.registration.keycloak.client-id=${keycloak.client}
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.scope=openid
spring.security.oauth2.client.registration.keycloak.provider=keycloak
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
spring.security.oauth2.client.provider.keycloak.issuer-uri=${keycloak.server.url}/realms/${keycloak.realm}

spring.security.oauth2.resourceserver.jwt.issuer-uri=${keycloak.server.url}/realms/${keycloak.realm}
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${keycloak.server.url}/realms/${keycloak.realm}/protocol/openid-connect/certs
##--------------------- SECURITY KEYCLOAK ----------------------------------------------------------------------------

