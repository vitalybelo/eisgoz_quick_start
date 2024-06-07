package ru.lanit.minobr.service.quick_start.models;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MacTable {

    private String uuid = UUID.randomUUID().toString();
    private Timestamp date;
    private String userName;
    private String level;
    private String category;

}
