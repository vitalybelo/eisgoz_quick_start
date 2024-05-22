package ru.lanit.minobr.service.quick_start;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController("/")
@RequiredArgsConstructor
public class MainPageController {

    private final RealmResource realmResource;

    @RequestMapping("")
    public String index(HttpServletRequest request)
    {
        String auth_user = request.getHeader("auth_user");
        if (auth_user == null) {
            return "<p><h1>Доступ закрыт</h1></p>";
        }
        String user_name = auth_user.substring(0, auth_user.indexOf("@"));
        String auth_pass = request.getHeader("Authorization");
        String mac_label = request.getHeader("mymaclabel");

        StringBuilder builder;
        builder = new StringBuilder("<p><h1>Index started listening at: ").append(new Date()).append("</h1></p>");

        builder.append("<br><p><h2>");
        builder.append("Пользователь: ").append(auth_user).append("<br>");
        builder.append("Имя: ").append(user_name).append("<br><br>");
        builder.append("Mac Label: ").append(mac_label).append("<br><br>");
        builder.append("Authorization: ").append(auth_pass).append("<br><br>");
        builder.append("</h2></p>");

        builder.append("<p><a href=\"/api_8080/hello\"><h3>hello page</h3></a></p>");
        return builder.toString();
    }


    @RequestMapping(value = "hello", method = {GET, POST})
    public String hello(@NotNull HttpServletRequest request)
    {
        String auth_user = request.getHeader("auth_user");
        String user_name = auth_user.substring(0, auth_user.indexOf("@"));

        StringBuilder sb;
        sb = new StringBuilder("<p><h1>HELLO, " + user_name + " !<br><br>");
        List<UserRepresentation> userRepresentationList = realmResource.users().list();
        if (CollectionUtils.isNotEmpty(userRepresentationList)) {
            UserRepresentation user = userRepresentationList.stream()
                    .filter(u -> u.getUsername().equals(user_name))
                    .findFirst().orElse(null);
            if (user != null) {
                sb.append("First Name: ").append(user.getFirstName()).append("<br>");
                sb.append("Last Name: ").append(user.getLastName()).append("<br>");
                sb.append("Email: ").append(user.getEmail()).append("<br>");
            }
        }
        sb.append("</h1></p>");
        return sb.toString();
    }


}
