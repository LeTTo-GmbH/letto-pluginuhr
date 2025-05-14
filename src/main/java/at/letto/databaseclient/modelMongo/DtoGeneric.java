package at.letto.databaseclient.modelMongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dto_storage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoGeneric<T> {

    @Id
    private String idTemp;

    @Indexed(unique = false)
    private String classInfo;

    private T dtoData;
}
