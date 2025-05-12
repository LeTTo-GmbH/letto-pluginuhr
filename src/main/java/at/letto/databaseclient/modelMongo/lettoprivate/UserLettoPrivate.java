package at.letto.databaseclient.modelMongo.lettoprivate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import at.letto.question.dto.lettoprivate.Abo;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "usersprivate") // Name der Collection in MongoDB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLettoPrivate {
    /** EMAIL-Adresse des Benutzuers */
    @Id private String email;
    @Indexed(unique=true)
    private String nickname;
    /** Salted Password */
    private String password;
    /** Vorname */
    private String vorname;
    /** Nachname */
    private String nachname;
    /** Adressse */
    private String adresse;
    /** Sprache */
    private String sprache;
    /** Liste mit allen gekauften oder zugeordneten Büchern */
    private List<Abo> abos = new ArrayList<>();

}
