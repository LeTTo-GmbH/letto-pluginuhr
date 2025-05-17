package at.letto.databaseclient.caches;

import at.letto.databaseclient.service.BaseLettoRedisDBService;
import at.letto.globalinterfaces.IdEntity;
import at.letto.security.LettoToken;
import at.letto.tools.JSON;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public abstract class GenericRedisCache<T extends IdEntity> implements CacheInterface<T> {

    @Autowired
    BaseLettoRedisDBService baseLettoRedisDBService;

    @Setter private TimeUnit timeUnit = TimeUnit.HOURS;
    @Setter private int timeout = 1;

    /** Check, ob Redis-Datenbank verfügbar ist */
    public boolean checkRedis() {
        return baseLettoRedisDBService.isRedisOk();
    }

    /**
     * Öffentliche Methode zum Laden eines DTOs aus dem Cache.
     * @param id  ID des gesuchten Objekts (DTO muss IdEntity implementiert haben!)
     * @return    DTO aus dem Cache
     */
    @Override
    public T load(int id, LettoToken token) {
        return loadData(id, token);
    }

    /**
     * Methode zum Laden des DTOs aus der Datenbank. Muss im Kindelement überschrieben werden,
     * um den korrekten Typ an loadData(id, Class<T> type, ... ) übergeben zu können.
     * Wird intern von load(...) aufgerufen.
     * @param id    ID, nach der gesucht werden soll
     * @param token LettoToken zur Ermittlung des Schul-Namens
     * @return  DTO
     */
    public T loadData(int id, LettoToken token) {
        Class<T> x = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        return loadData(id, x, token);
    }

    /**
     * Methode wird aufgerufen, wenn im Cache kein Eintrag gefunden wurde.<br>
     * Dient zum Laden des jeweiligen DTOs aus der SQL-Datenbank über das Datenservice.
     * Wenn DTO korrekt geladen wurde, wird es im Redis-Cache gespeichert!
     * @param token LeTTo-Token
     * @param id    ID des DTOs in der SQL-Datenbank
     * @param key   Key für das Abfragen in der Redis-Datenbank
     * @return  DTO aus MySQL-Datenbank
     */
    protected abstract T loadExternalData(LettoToken token, int id, String key);
    @Override
    public void remove(int id, LettoToken token) {
        Class<T> x = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        remove(id, x, token.getSchool());
    }

    @Override
    public void remove(int id, String school, String token) {
        Class<T> x = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        remove(id, x, school);
    }


    /**
     * Laden eines DTOs aus der Redis-Datenbank
     * Ist Redis nicht funktionsfähig, dann Abbruch, null wird zurückgegeben
     * @param id    Id (Datenelement muss IdEntity implementiert haben)
     * @param type  Ziel-Type (Class) für JSON
     * @param token LeTTo-Token
     * @return  DTO oder null
     */
    protected T loadData(int id, Class<T> type, LettoToken token) {

        if (!checkRedis()) return null;
        String school = token.getSchool();
        String key = key(id, type, school);
        T data = null;
        try {
            String json = baseLettoRedisDBService.get(key);
            data = JSON.jsonToObj(json, type);
        } catch (Exception e) {}
        if (data==null) data = loadExternalData(token, id, key);
        return data;
    }

    /**
     * Speichern eines DTOs in der Datenbank.<br>
     * Ist Redis nicht funktionsfähig, dann Abbruch ohne Meldung.
     * @param data      Datenelement, muss IdEntity implementiert haben
     * @param token     LeTTo-Token zur Autentifizierung
     */
    @Override
    public void put(T data, LettoToken token) {
        if (!checkRedis()) return;

        String key = key(data.getId(), (Class<T>)data.getClass(), token.getSchool());
        baseLettoRedisDBService.put(key, JSON.objToJson(data));
        baseLettoRedisDBService.redisTemplate().expire(key, timeout, timeUnit);
    }

    /**
     * Erstellung eines Keys für das Speichern eines DTOs (IdEntity) in der Redis-Datenbank.<br>
     * Zusammensetzung des Keys: schule:class-name:id
     * @param id        ID des DTOs
     * @param type      Class des DTOs
     * @param school    Schulname
     * @return  Redis-Key
     */
    private String key(int id, Class<T> type, String school) {
        return school + ":" + type.getSimpleName() + ":" +id;
    }

    /**
     * Lösschen eines DTOs aus dem Redis-Cache.<br>
     * Ist Redis nicht funktionsfähig, dann Abbruch ohne Meldung
     * @param id    ID des DTOs (IdEntity)
     * @param type  class des DTOs
     * @param school Schulname (Kurzbezeichner)
     */
    public void remove(int id, Class<T> type, String school) {
        if (!checkRedis()) return;
        String key = key(id, type, school);
        baseLettoRedisDBService.redisTemplate().delete(key);
    }

    @Override
    public void clear(LettoToken token) {
        Class<T> x = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        String key = token.getSchool() + ":" + x.getSimpleName() + ":*" ;
        baseLettoRedisDBService.deleteKeysWithPrefix(key);
    }

}
