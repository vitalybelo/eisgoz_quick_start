package ru.lanit.minobr.service.quick_start;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lanit.minobr.service.quick_start.authorization.AccessTokenService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Date;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@Hidden
@RestController
@RequiredArgsConstructor
public class MainPageController {


    private final AccessTokenService service;


    @RequestMapping("/")
    public String index(Principal principal)
    {
        System.out.println("Класс инициализирован ? >> " + service.assign());
        System.out.println("Класс инициализирован ? >> " + service.assign(principal));

        StringBuilder builder;
        builder = new StringBuilder("<p><h1>Index started listening at: ").append(new Date()).append("</h1></p>");

        builder.append("<br><p><h2>");
        builder.append("Пользователь: ").append(service.getLogin()).append("<br><br>");
        builder.append("Имя: ").append(service.getFirstName()).append("<br>");
        builder.append("Отчество: ").append(service.getMiddleName()).append("<br>");
        builder.append("Фамилия: ").append(service.getFamilyName()).append("<br><br>");
        builder.append("Полное имя: ").append(service.getFullName()).append("<br><br>");
        builder.append("Подразделение: ").append(service.getDepartment()).append("<br>");
        builder.append("Должность: ").append(service.getPosition()).append("<br>");

        builder.append("Email: ").append(service.getEmail()).append("<br>");
        builder.append("Phone: ").append(service.getPhone()).append("<br>");
        builder.append("Разрешенные ip адреса: ").append(service.getIpAddress()).append("<br>");
        builder.append("Количество разрешенных сессий: ").append(service.getMaxSession()).append("<br>");
        builder.append("Максимальное время простоя (сек): ").append(service.getMaxIdleTime()).append("<br>");
        builder.append("Список realm ролей: ").append(service.getRealmRoles().size()).append("<br>");
        builder.append(service.getRealmRoles()).append("<br>");
        builder.append("Список client ролей: ").append(service.getClientRoles().size()).append("<br>");
        builder.append(service.getClientRoles()).append("<br>");
        builder.append("</h2></p><br>");

        builder.append("<br><p><a href=\"/custom_logout\"><h3>выход из приложения</h3></a></p>");
        return builder.toString();
    }


    @SneakyThrows
    @RequestMapping(value = "/custom_logout", method = {GET, POST})
    public void custom_logout(@NotNull HttpServletRequest request, HttpServletResponse response)
    {
        request.logout();
        String contextPath = request.getContextPath();
        if (contextPath == null || contextPath.isEmpty()) {
            contextPath = "/";
        }
        response.sendRedirect(contextPath);
    }


}
