package at.letto.databaseclient.repository.mongo.letto;

import at.letto.databaseclient.modelMongo.DtoGeneric;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoDtoRepository<T> extends MongoRepository<DtoGeneric<T>, ObjectId> {
    DtoGeneric<T> findByIdTemp(String idTemp);

    List<DtoGeneric<T>> findAllByClassInfo(String classInfo);
}