package at.letto.databaseclient.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionCountByUserId {
    private String id;       // Mongo-Aggregation: _id wird auf id gemappt
    private long count;
}