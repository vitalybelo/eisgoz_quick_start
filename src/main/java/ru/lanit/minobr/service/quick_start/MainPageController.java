package ru.lanit.minobr.service.quick_start;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lanit.minobr.service.quick_start.models.MacTable;
import ru.lanit.minobr.service.quick_start.repository.MacTableRepository;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@Transactional
@RestController("/")
@RequiredArgsConstructor
public class MainPageController {

    protected String auth_user;
    protected String user_name;
    protected String mac_label;
    protected String auth_pass;
    protected String level;
    protected String category;
    protected List<MacTable> macTables;

    private final static String NOT_FOUND = "<p><h1>NOT FOUND any records ...</h1></p>";
    private final static String ERROR_PRE = "<p><h2>ERROR OCCURRED: ";
    private final static String ERROR_SUF = "</h2></p>";

    private final RealmResource realmResource;
    private final MacTableRepository repository;



    @RequestMapping("")
    public String index(HttpServletRequest request)
    {
        auth_user = request.getHeader("auth_user");
        if (auth_user == null) {
            return "<p><h1>Доступ закрыт</h1></p>";
        }
        user_name = auth_user.substring(0, auth_user.indexOf("@"));
        mac_label = request.getHeader("mymaclabel");
        auth_pass = request.getHeader("Authorization");
        String[] words = mac_label.split(":");
        level = words[0];
        category = words[1];

        macTables = repository.findAll();
        StringBuilder sb;
        sb = new StringBuilder("<p><h1>Index started listening at: ").append(new Date()).append("</h1></p>");

        sb.append("<p><h1>База данных: mocluster --> таблица: mactable</h1><h2>");
        macTables.forEach(m -> sb.append(m.getUserName()).append(" :: ").append(m.getLevel()).append(" :: ").append(m.getCategory()).append("<br>"));
        sb.append("</h2></p>");

        sb.append("<p><h1>");
        sb.append("Пользователь: ").append(auth_user).append("<br>");
        sb.append("Имя: ").append(user_name).append("<br>");
        sb.append("Метка: ").append(mac_label).append("<br>");
        sb.append("</h1></p>");

        sb.append("<p><a href=\"create\"><h2>Добавить новую запись</h2></a></p>");
        sb.append("<p><a href=\"delete\"><h2>Удалить последнюю запись</h2></a></p>");
        sb.append("<p><a href=\"update\"><h2>Изменить последнюю запись</h2></a></p>");
        sb.append("<p><a href=\"hello\"><h2>KEYCLOAK HELLO PAGE</h2></a></p>");

        sb.append("<br><br><p><h2>");
        sb.append("Authorization: ").append(auth_pass).append("<br><br>");
        sb.append("</h2></p>");

        return sb.toString();
    }


    @RequestMapping(value = "hello", method = {GET, POST, PUT, DELETE})
    public String hello()
    {
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


    @RequestMapping(value = "create", method = {GET, POST, DELETE, PUT})
    public String createRecord() {

        MacTable mac = new MacTable();
        mac.setDate(new Timestamp(System.currentTimeMillis()));
        mac.setUserName(user_name);
        mac.setLevel("CREATE LVL = " + level);
        mac.setCategory("CREATE CAT = " + category);
        try {
            repository.save(mac);
        } catch (Exception e) {
            return ERROR_PRE + e.getMessage()+ ERROR_SUF;
        }
        return "<p><h1>NEW RECORD SAVED ... done ...</h1></p>";
    }


    @RequestMapping(value = "delete", method = {GET, POST, DELETE, PUT})
    public String deleteLastRecord() {

        if (CollectionUtils.isNotEmpty(macTables)) {
            int i = macTables.size() - 1;
            try {
                repository.deleteById(macTables.get(i).getUuid());
            } catch (Exception e) {
                return ERROR_PRE + e.getMessage()+ ERROR_SUF;
            }
            return "<p><h1>RECORD DELETED ... done ...</h1></p>";
        }
        return NOT_FOUND;
    }


    @RequestMapping(value = "update", method = {GET, POST, PUT, DELETE})
    public String updateLastRecord() {

        if (CollectionUtils.isNotEmpty(macTables)) {
            int i = macTables.size() - 1;
            String uuid = String.valueOf(macTables.get(i).getUuid());
            String updateLevel = "UPDATE LVL = " + level;
            String updateCategory = "UPDATE CAT = " + category;
            try {
                repository.updateById(user_name, updateLevel, updateCategory, uuid);
            } catch (Exception e) {
                return ERROR_PRE + e.getMessage()+ ERROR_SUF;
            }
            return "<p><h1>RECORD UPDATED ... done ...</h1></p>";
        }
        return NOT_FOUND;
    }

}
