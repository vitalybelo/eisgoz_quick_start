package ru.lanit.minobr.service.quick_start;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController("/")
@RequiredArgsConstructor
public class MainPageController {

    protected String auth_user;
    protected String user_name;
    protected String mac_label;
    protected String auth_pass;
    protected String level;
    protected String category;

    private final RealmResource realmResource;


    @RequestMapping("")
    public String index(HttpServletRequest request) {

        auth_user = request.getHeader("auth_user");
        if (auth_user == null) {
            return "<p><h1>Доступ закрыт</h1></p>";
        }

        //Properties props = new Properties();
        //props.setProperty("java.security.krb5.conf", "/etc/krb5.conf");
        //props.setProperty("java.security.krb5.realm", "ASTRA.DOMAIN");
        //props.setProperty("java.security.krb5.kdc", "freeipa.astra.domain");
        //props.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
        //props.setProperty("java.security.auth.login.config", "/etc/jaas.conf");

        Connection conn;
        String url = "jdbc:postgresql://client.astra.domain:5440/demoprimer";
        String env = System.getenv("DEMOPRIMER_DB_CONNECT");
        System.out.printf("Environment parameter DEMOPRIMER_DB_CONNECT = %s\n", env);
        System.out.printf("DB CONNECTION URL = %s\n", url);
        try {
            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(url);
            DatabaseMetaData data = conn.getMetaData();
            System.out.println(data.getMaxColumnsInIndex());

            conn.close();
        } catch (Exception e) {
            log.info("CONNECTION >>>> {}", e.getMessage());
        }

        user_name = auth_user.substring(0, auth_user.indexOf("@"));
        mac_label = request.getHeader("mymaclabel");
        auth_pass = request.getHeader("Authorization");
        String[] words = mac_label.split(":");
        level = words[0];
        category = words[1];


        StringBuilder sb;
        sb = new StringBuilder("<p><h1>Index started listening at: ").append(new Date()).append("</h1></p>");

        sb.append("<p><h1>");
        sb.append("Пользователь: ").append(auth_user).append("<br>");
        sb.append("Имя: ").append(user_name).append("<br>");
        sb.append("Метка: ").append(mac_label).append("<br>");
        sb.append("</h1></p>");

        sb.append("<br><p><h2>");
        sb.append("Authorization: ").append(auth_pass).append("<br><br>");
        sb.append("</h2></p>");

        return sb.toString();
    }


    @RequestMapping(value = "hello", method = {GET, POST, PUT, DELETE})
    public String hello() {
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
