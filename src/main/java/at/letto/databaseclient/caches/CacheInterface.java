package at.letto.databaseclient.caches;

import at.letto.globalinterfaces.IdEntity;
import at.letto.security.LettoToken;

public interface CacheInterface<T extends IdEntity> {

    /**
     * Laden eines DTOs mit ID (implements IdIdentity) aus einem Cache.<br>
     * @param id    ID, nach der gesucht werden soll
     * @param token LettoToken zur Ermittlung der Schule
     * @return Objekt, das im Cache abgelegt wurde
     */
    T load(int id, LettoToken token);

    /**
     * Löschen eines DTOs mit ID aus einem Cache.<br>
     * @param id    ID des DTOs
     * @param token LeTTo-Token zur Ermittlung der Schule
     */
    void remove(int id, LettoToken token);
    /**
     * Löschen eines DTOs mit ID aus einem Cache.<br>
     * @param id    ID des DTOs
     * @param school    Schul-Kurzbezeichner
     * @param token Token in Stringform (Bearer-Token)
     */
    void remove(int id, String school, String token);

    /**
     * Speichern eines DTOs mit ID in einem Cache.<br>
     * @param data  DTO mit ID
     * @param token LeTTo-Token zur Ermittlung der Schule
     */
    void put(T data, LettoToken token);

    /**
     * Löschen von allen Cache-Einträgen dieses Typs von DTOs
     * @param token LeTTo-Token zur Schulidentifikation
     */
    void clear(LettoToken token);

    /**
     * Löschen von allen Cache-Einträgen dieses Typs von DTOs
     * @param school Schulidentifikation (Kurzbezeichnung)
     */
    void clear(String school);

}
