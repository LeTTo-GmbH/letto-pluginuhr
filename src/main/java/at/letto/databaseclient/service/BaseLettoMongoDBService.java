package at.letto.databaseclient.service;

import at.letto.databaseclient.config.DatabaseConfiguration;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class BaseLettoMongoDBService {

    //@Autowired MongoClient mongoClient;
    @Autowired private DatabaseConnectionService databaseConnectionService;

    protected Logger logger = LoggerFactory.getLogger(BaseLettoMongoDBService.class);

    /** @return Liefert einen MongoClient für den Zugriff auf alle Datenbanken des Mongo-DB-Servers */
    public MongoClient mongoClient() {
        return databaseConnectionService.mongoClient();
    }

    /** @return liefert eine Liste aller Datenbanknamen welche am Datenbankserver zur Verfügung stehen */
    public List<String> databaseNames() {
        try {
            List<String> result = new ArrayList<String>();
            for (String n:mongoClient().listDatabaseNames()) {
                result.add(n);
            }
            return result;
        } catch (Throwable e) { }
        return null;
    }

    /** @return liefert eine Liste aller Datebanken als MongoDatabase-Objekt welche am Datenbankserver zur Verfügung stehen */
    public List<MongoDatabase> databases() {
        try {
            List<MongoDatabase> result = new ArrayList<>();
            for (String n:mongoClient().listDatabaseNames()) {
                MongoDatabase db = mongoClient().getDatabase(n);
                result.add(db);
            }
            return result;
        } catch (Throwable e) { }
        return null;
    }

    /** @return liefert ein MongoDatabase-Objekt der LeTTo-Datenbank des Services */
    public MongoDatabase database() {
        return mongoClient().getDatabase("letto");
    }

    /** @return liefert ein MongoDatabase-Objekt der Datenbank mit dem angegebenen Namen */
    public MongoDatabase database(String databaseName) {
        return mongoClient().getDatabase(databaseName);
    }

    /** @return Liefert alle Collections der Default-Datenbank */
    public List<String> collectionNames() {
        try {
            List<String> result = new ArrayList<>();
            database().listCollectionNames().iterator().forEachRemaining(result::add);
            return result;
        } catch (Throwable e) { }
        return null;
    }

    /** @return Liefert alle Collections einer Datenbank */
    public List<String> collectionNames(String databaseName) {
        try {
            List<String> result = new ArrayList<>();
           database(databaseName).listCollectionNames().iterator().forEachRemaining(result::add);
            return result;
        } catch (Throwable e) { }
        return null;
    }

    /** erzeugt eine Collection in der Default-Datenbank, Exisitert die Datenbank noch nicht dann wird sie angelegt */
    public void mongoCreateCollection(String collectionName) {
        // Erstelle eine Collection (notwendig, um die DB zu persistieren)
        database().createCollection(collectionName);
    }

    /** erzeugt eine Collection in der angegebenen Datenbank, Exisitert die Datenbank noch nicht dann wird sie angelegt */
    public void mongoCreateCollection(String databaseName, String collectionName) {
        // Erstelle eine Collection (notwendig, um die DB zu persistieren)
        database(databaseName).createCollection(collectionName);
    }

    /** Liefert den Zugriff auf eine Collection der Default-Datenbank */
    public MongoCollection<Document> getCollection(String collectionName) {
        MongoCollection<Document> collection = database().getCollection(collectionName);
        return collection;
    }

    /** Liefert den Zugriff auf eine Collection der angegebenen Datebank */
    public MongoCollection<Document> getCollection(String databaseName, String collectionName) {
        MongoCollection<Document> collection = database(databaseName).getCollection(collectionName);
        return collection;
    }

    /** Fügt einen Eintrag in eine Collection ein */
    public void insert(MongoCollection<Document> collection, Document document) {
        collection.insertOne(document);
    }

    /** sucht nach dem ersten Dokument welches das attribute gleich value hat */
    public Document findFirst(MongoCollection<Document> collection, String fieldName, String value) {
        Document foundDocument = collection.find(Filters.eq(fieldName, value)).first();
        return foundDocument;
    }

    /** sucht nach allen Dokumenten welches das attribute gleich value haben */
    public FindIterable<Document> find(MongoCollection<Document> collection, String fieldName, String value) {
        FindIterable<Document> foundDocuments = collection.find(Filters.eq(fieldName, value));
        return foundDocuments;
    }

    /** sucht nach allen Dokumenten einer Collection*/
    public FindIterable<Document> find(MongoCollection<Document> collection) {
        FindIterable<Document> foundDocuments = collection.find();
        return foundDocuments;
    }

    /** aktualisiert ein Dokument old mit update */
    public UpdateResult update(MongoCollection<Document> collection, Document old, Document update) {
        UpdateResult result = collection.updateOne(old,update);
        return result;
    }

    /** aktualisiert einen Wert in einem Dokument */
    public UpdateResult update(MongoCollection<Document> collection, Document old, String fieldName, String value) {
        UpdateResult result = collection.updateOne(old, Updates.set(fieldName,value));
        return result;
    }

}
