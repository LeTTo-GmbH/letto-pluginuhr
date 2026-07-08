package at.letto.databaseclient.dto;

import at.letto.databaseclient.modelMongo.login.LeTToUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionCountDto {
    private LeTToUser user;
    private long sessionCount;
}