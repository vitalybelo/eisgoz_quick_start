package ru.lanit.minobr.service.quick_start.repository;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.lanit.minobr.service.quick_start.models.MacTable;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MacTableJdbcRepository {

    private final static String SELECT_MAC_TABLE = "SELECT maclabel, * FROM mactable WHERE maclabel IN (%s)";

    private final JdbcTemplate jdbcTemplate;
    private final HttpServletRequest request;


    public List<MacTable> findAll() {

        List<Map<String, Object>> list = jdbcTemplate.queryForList(String.format(SELECT_MAC_TABLE, getMacLabels()));
        return list.stream().map(this::getMacTable).filter(Objects::nonNull).collect(Collectors.toList());
    }


    private @Nullable MacTable getMacTable(@NotNull Map<String, Object> map) {

        Object uuid = map.get("uuid");
        if (uuid instanceof String) {
            Object date = map.get("date");
            if (date instanceof Timestamp) {
                String username = (String) map.get("username");
                String level = (String) map.get("level");
                String category = (String) map.get("category");
                return new MacTable((String) uuid, (Timestamp) date, username, level, category);
            }
        }
        return null;
    }


    private @NotNull String getMacLabels() {

        String macLabel = request.getHeader("mymaclabel");
        if (macLabel == null) {
            macLabel = "0:0";
        }

        String[] words = macLabel.split(":");
        try {
            int userLVL = Integer.parseInt(words[0]);
            int userCAT = Integer.parseInt(words[1]);
            StringBuilder sb = new StringBuilder();
            for (int l = 0; l <= userLVL; ++l) {
                for (int c = 0; c <= userCAT; ++c) {
                    sb.append("'{").append(l).append(",").append(c).append("}',");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (Exception ignored) {
        }
        return "{0.0}";
    }


}
