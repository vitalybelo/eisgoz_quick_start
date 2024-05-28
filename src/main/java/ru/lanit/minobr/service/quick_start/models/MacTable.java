package ru.lanit.minobr.service.quick_start.models;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "MACTABLE", schema = "PUBLIC")
public class MacTable {

    @Id
    @Column(name = "UUID", length = 36, nullable = false)
    private String uuid = UUID.randomUUID().toString();

    @Column(name = "DATE", nullable = false)
    private Timestamp date;

    @Column(name = "USERNAME", length = 50)
    private String userName;

    @Column(name = "LEVEL", length = 50)
    private String level;

    @Column(name = "CATEGORY", length = 50)
    private String category;

}
