Пример для использования класса авторизации AccessTokenService.

1. Класс читает из контекста безопасности java и spring security ID или Bearer токен
2. ID токен формируется как правило после того, как выполнена аутентификация через браузер
3. Bearer токен формируется при аутентификации в http запросах (например такие, какие выполняет Postman)
4. Класс AccessTokenService извлекает из токена учетные данные пользователя
5. Учётные данные доступны через public методы класса

В проекте есть MainPageController, который в примитивном виде отображает учетные данный пользователя под которым был выполнен вход через порт 8080

Класс AccessTokenService имеет 2 метода для чтение ролей

1. Метод getRealmRoles() возвращает роли, назначенные пользователю как Realm роли, или роли области. В этих ролях можно найти "типы" аккаунта, например MAIN_ADMIN или USER
2. Метод getClientRoles() возвращает роли, назначенные пользователю как Client роли, или роли сервисов. Именно эти роли используются как набор действий по информационным объектам.
