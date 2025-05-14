package at.letto.databaseclient.service;

import at.letto.databaseclient.config.DatabaseConfiguration;
import at.letto.security.LettoToken;
import at.letto.tools.Datum;
import at.letto.tools.JSON;
import at.letto.tools.dto.LeTToServiceInfoDto;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Service für die Zugriff auf die REDIS-Datenbank in LeTTo<br>
 * Es sind die Datenbanken 0 bis 15 verfügbar<br>
 * In der Umgebungsvariable REDIS_DEFAULT_DATABASE kann eine Standard-Datebank
 * definiert sein ohne Angabe ist 0 die Standard-Datenbank<br>
 *
 */
@Service
public class BaseLettoRedisDBService {

    public static final int REDIS_DATABASE_SETUP     = 0;
    public static final int REDIS_DATABASE_LOGIN     = 1;
    public static final int REDIS_DATABASE_QUESTION  = 2;
    public static final int REDIS_DATABASE_EXPORT    = 3;
    public static final int REDIS_DATABASE_EDIT      = 4;

    @Autowired private DatabaseConnectionService databaseConnectionService;
    // @Autowired DatabaseConfiguration databaseConfiguration;

    protected Logger logger = LoggerFactory.getLogger(BaseLettoRedisDBService.class);

    /** Zähler für Fehlzugriffe auf Redis-DB */
    private int errorCount = 0;

    /** Indikator, ob Redis-DB zugreifbar */
    @Getter
    private boolean redisOk = false;

    /** Test der Funktionsfähigkeit der Redis-Datenbank, startet erstmals nach 10 Sek. und dann alle 5 Minuten */
    @Scheduled(initialDelay = 10000, fixedRate = 300000)
    public void checkRedisService(){
        logger.info("checkRedisService");
        if (redisOk) return;
        try {
            redisTemplate(databaseConnectionService.getRedisDefaultDatabase())
                    .opsForValue()
                    .set("temp:check:connection", Datum.formatDateTime(Datum.nowLocalDateTime()));
            errorCount = 0;
            this.redisOk = true;
            logger.info("Redis OK");
        } catch (Exception e) {}
    }


    /**
     * Liefert einen Redis-Client auf die Standard-Redis-Datenbank des Services
     * @return LettuceConnectionFactory für den Datenbankzugriff
     */
    public LettuceConnectionFactory redisClient() {
        return redisClient(databaseConnectionService.getRedisDefaultDatabase());
    }

    /**
     * Liefert einen Redis-Client auf angegebene Datenbank
     * @param database Datenbank auf die verbunden wird
     * @return LettuceConnectionFactory für den Datenbankzugriff
     */
    public LettuceConnectionFactory redisClient(int database) {
        return databaseConnectionService.redisConnectionFactory(database);
    }

    /**
     * Liefert ein Redis-Template auf die Standard-Redis-Datenbank des Services
     * @return RedisTemplate<String, Object> für den Datenbankzugriff
     */
    public RedisTemplate<String, Object> redisTemplate() {
        return redisTemplate(databaseConnectionService.getRedisDefaultDatabase());
    }

    /**
     * Liefert ein Redis-Template auf angegebene Datenbank
     * @param database Datenbank auf die verbunden wird
     * @return RedisTemplate<String, Object> für den Datenbankzugriff
     */
    public RedisTemplate<String, Object> redisTemplate(int database) {
        return databaseConnectionService.redisTemplate(database);
    }

    //----------------------------------------------------------------------------------------

    /** @return Sucht in der Standard-Datenbank des Services nach einem key */
    public boolean hasKey(String key) {
        return hasKey(databaseConnectionService.getRedisDefaultDatabase(), key);
    }
    /** @return Sucht in der angegebenen Datenbank nach einem key */
    public boolean hasKey(int database, String key) {
        return redisTemplate(database).hasKey(key);
    }

    /** @return liefert alle Keys der Standard-Datenbank */
    public Set<String> keySet() {
        return keySet(databaseConnectionService.getRedisDefaultDatabase(), "");
    }
    /** @return liefert alle Keys der Standard-Datebank welche einem Suchkriterium entsprechen */
    public Set<String> keySet(String pattern) {
        return keySet(databaseConnectionService.getRedisDefaultDatabase(), pattern);
    }
    /** @return liefert alle Keys einer Datenbank */
    public Set<String>  keySet(int database) {
        return redisTemplate(database).keys("");
    }
    /** @return liefert alle Keys einer Datebank welche einem Suchkriterium entsprechen */
    public Set<String>  keySet(int database, String pattern) {
        return redisTemplate(database).keys(pattern);
    }

    //----------------------------------------------------------------------------------------

    /** Sucht nach einem Key und gibt den gefundenen Value zurück */
    public String get(String key) {
        return get(databaseConnectionService.getRedisDefaultDatabase(),key);
    }
    /** Sucht nach einem Key und gibt den gefundenen Value zurück */
    public String get(int database, String key) {
        try {
            String result = (String) redisTemplate(database).opsForValue().get(key);
            this.errorCount=0;
            return result;
        } catch (Exception e) {
            this.setError();
        }
        return null;
    }

    /** Sucht nach einem Key und gibt den gefundenen Value als String zurück */
    public String getString(String key) {
        return getString(databaseConnectionService.getRedisDefaultDatabase(),key);
    }
    /** Sucht nach einem Key und gibt den gefundenen Value als String zurück */
    public String getString(int database, String key) {
        try {
            String result = (String)redisTemplate(database).opsForValue().get(key);
            this.errorCount=0;
            return result;
        } catch (Exception e) {
            this.setError();
        }
        return null;
    }

    /** Sucht nach einem Key und gibt den gefundenen Value als Klasse mit entsprechendem Typ zurück oder null wenn der key nicht gefunden werden kann */
    public <T> T get(String key, Class<T> tClass) {
        return get(databaseConnectionService.getRedisDefaultDatabase(),key,tClass);
    }
    /** Sucht nach einem Key und gibt den gefundenen Value zurück oder null wenn der key nicht gefunden werden kann  */
    public <T> T get(int database, String key, Class<T> tClass) {
        try {
            String json = (String) redisTemplate(database).opsForValue().get(key);
            T result = JSON.jsonToObj(json,tClass);
            this.errorCount=0;
            return result;
        } catch (Exception e) {
            this.setError();
        }
        return null;
    }

    //----------------------------------------------------------------------------------------
    /** speichert ein Objekt mit einem key in der Standard-Datebank */
    public boolean put(String key, Object value) {
        return put(databaseConnectionService.getRedisDefaultDatabase(),key, value);
    }
    /** speichert ein Objekt mit einem key als JSON in einer Datenbank */
    public boolean put(int database, String key, Object value) {
        try {
            String json;
            if (value instanceof String) {
                json = (String) value;
            } else {
                json = JSON.objToJson(value);
            }
            redisTemplate(database).opsForValue().set(key, json);
            return true;
        } catch (Exception e) {
            this.setError();
        }
        return false;
    }

    /**
     *  speichert ein Objekt mit einem key in einer Datenbank
     * @param database
     * @param key
     * @param value
     * @param minutes   Anzahl an Minuten bis der Eintrag gelöscht wird
     */
    public boolean put(int database, String key, Object value, int minutes) {
        try {
            String json;
            if (value instanceof String) {
                json = (String) value;
            } else {
                json = JSON.objToJson(value);
            }
            redisTemplate(database).opsForValue().set(key, json, minutes, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            this.setError();
        }
        return false;
    }

    /** Erhöht der Fehlerzähler und setzt redisOK auf false, wenn zu viele Fehler auftreten */
    private void setError() {
        errorCount++;
        if (errorCount >= 5) {
            this.redisOk = false;
            logger.info("Redis FEHLERHAFT");
        }
    }


    //----------------------------------------------------------------------------------------
    /**
     * registriert ein Service in der Datenbank 0 (REDIS_DATABASE_SETUP) mit den notwendigen Daten<br>
     * sollte von jedem Service regelmäßig aufgerufen werden (z.B. jede Minute und beim Systemstart)
     * und speichert alle Daten eines Docker-Containers (Services) in der Redis-Datenbank<br>
     * Als Key wird die Adresse innerhalb des Docker-Netzwerkes verwendet - die ja eindeutig sein muss!
     */
    public boolean registerService(LeTToServiceInfoDto serviceInfoDto){
        LeTToServiceInfoDto oldDto = getServiceInfo(serviceInfoDto.getServiceAddress());
        if (oldDto != null){
            List<Long> lastStarts = oldDto.getLastServiceStarts();
            if (oldDto.getStartTime()<serviceInfoDto.getStartTime()){
                // Service wurde neu gestartet
                lastStarts.add(oldDto.getStartTime());
                if (lastStarts.size()>20){lastStarts.remove(0);}
                serviceInfoDto.setLastRunningTime(oldDto.getRunningTime());
            } else {
                serviceInfoDto.setLastRunningTime(oldDto.getLastRunningTime());
            }
            serviceInfoDto.setLastServiceStarts(lastStarts);
        }
        try {
            String json = JSON.objToJson(serviceInfoDto);
            put(REDIS_DATABASE_SETUP,"service:info:"+serviceInfoDto.getServiceAddress(),json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LeTToServiceInfoDto getServiceInfo(String serviceAddress){
        try {
            LeTToServiceInfoDto serviceInfoDto = get(REDIS_DATABASE_SETUP, "service:info:" + serviceAddress, LeTToServiceInfoDto.class);
            return serviceInfoDto;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Löschen van allen Datenbankeinträgen aus der STANDARD-Redis-DB mit einem Such-Pattern.<br>
     * Alle DB-Einträge, die diesem Such-Pattern entsprechen, werden gelöscht.
     * @param pattern   Suchmuster (zB: htlstp:tests:*) 
     */
    public void deleteKeysWithPrefix(String pattern) {
        deleteKeysWithPrefix(databaseConnectionService.getRedisDefaultDatabase(),pattern);
    }

    /**
     * Löschen van allen Datenbankeinträgen in der Redis-DB mit einem Such-Pattern.<br>
     * Alle DB-Einträge, die diesem Such-Pattern entsprechen, werden gelöscht.
     * @param database  Ziel-Redis-Datenbank
     * @param pattern   Suchmuster (zB: htlstp:tests:*)
     */
    public void deleteKeysWithPrefix(int database,String pattern) {
        RedisTemplate redisTemplate = redisTemplate(database);
        // SCAN mit einem Muster für die Schlüssel, die mit dem Präfix anfangen
        ScanOptions options = ScanOptions.scanOptions().match(pattern).build();

        RedisConnection conn = redisTemplate.getConnectionFactory().getConnection();
        Cursor<byte[]> cursor = conn.scan(options);
        while (cursor.hasNext()) {
            byte[] key = cursor.next();
            String keyString = new String(key);
            redisTemplate.delete(keyString);
        }
    }

    /**
     * Laden von allen Redis-Einträgen des gleichen Datentyps mit einem Such-Pattern aus der Default-Redis-DB
     * @param typ       Class des Ziel-Typs
     * @param pattern   Suchmuster (zB: htlstp:tests:*)
     * @return  HshMap mit Key in Redis-DB und Objekten
     * @param <T>
     */
    public <T> Map<String, T> loadKeysWithPrefix( Class<T> typ, String pattern) {
        return loadKeysWithPrefix(databaseConnectionService.getRedisDefaultDatabase(), typ, pattern);
    }

    /**
     * Laden von allen Redis-Einträgen des gleichen Datentyps mit einem Such-Pattern
     * @param database  Redis-Datenbanknummer
     * @param typ       Class des Ziel-Typs
     * @param pattern   Suchmuster (zB: htlstp:tests:*)
     * @return  HshMap mit Key in Redis-DB und Objekten
     * @param <T>
     */
    public <T> Map<String, T> loadKeysWithPrefix(int database, Class<T> typ, String pattern) {
        Map<String, String> jsonHash = loadKeysWithPrefix(database, pattern);
        Map<String, T> data = new HashMap<String, T>();

        for (String keys : jsonHash.keySet())
            data.put(keys, JSON.jsonToObj(jsonHash.get(keys), typ));
        return data;
    }

    /**
     * Laden von allen Redis-Einträgen von beliebigen Daten mit einem Such-Pattern.
     * @param database  Redis-Datenbanknummer
     * @param pattern   Suchmuster (zB: htlstp:tests:*)
     * @return  HashMap mit Keys in Redis-DB und JSON-Strings
     */
    public Map<String, String> loadKeysWithPrefix(int database, String pattern) {
        Map<String, String> data = new HashMap<>();
        RedisTemplate redisTemplate = redisTemplate(database);
        // SCAN mit einem Muster für die Schlüssel, die mit dem Präfix anfangen
        ScanOptions options = ScanOptions.scanOptions().match(pattern).build();

        RedisConnection conn = redisTemplate.getConnectionFactory().getConnection();
        Cursor<byte[]> cursor = conn.scan(options);
        while (cursor.hasNext()) {
            byte[] key = cursor.next();
            String keyString = new String(key);
            data.put(keyString, get(database, keyString));
        }
        return data;
    }

    /**
     * Lädt einen Token aus der Redis-DB
     * @param token Tokenstring
     * @return      LettoToken-Objekt
     */
    public LettoToken getToken(String token) {
        LettoToken lettoToken = get(BaseLettoRedisDBService.REDIS_DATABASE_LOGIN,"login.token." + token, LettoToken.class);
        return lettoToken;
    }

    /**
     * Speichert einen Token in der Redis-DB für eine Zeit von maximal 2 Minuten
     * @param lettoToken LettoToken-Objekt
     */
    public boolean putToken(LettoToken lettoToken) {
        String token = lettoToken.getToken();
        return put(BaseLettoRedisDBService.REDIS_DATABASE_LOGIN,"login.token." + token, lettoToken,2);
    }

}
