package at.letto.databaseclient.service;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service @Getter @Setter
public class DatabaseConnectionService {

    private Logger logger = LoggerFactory.getLogger(DatabaseConnectionService.class);

    /** Root-Passwort vom MySQL-Server */
    private String mysqlPassword;
    /** Root-Passwort vom MongoDB-Server */
    private String mongoPassword;
    /** Passwort vom Redis-Server */
    private String redisPassword;
    /** MySQL-Host-Adresse */
    private String lettoMySQLHost;
    /** Mongo-DB-Host-Adresse */
    private String lettoMongoHost;
    /** Redos-Host-Adresse */
    private String lettoRedisHost;
    /** Datenbankname der secondary Mongo-Datenbank */
    private String mongoDefaultDatabase;
    /** Datenbanknummer der default-Datebank des Services */
    private int    redisDefaultDatabase;
    /** Port des MySQL-Servers */
    private int    mySQLPort=3306;
    /** Port der Mongo-DB */
    private int    mongoPort=27017;
    /** Port der Redis-Datebank */
    private int    redisPort=6379;

    private MongoClient      mongoClient;
    private MongoTemplate    lettoMongoTemplate;
    private MongoTemplate    secondaryMongoTemplate;
    private HashMap<Integer, LettuceConnectionFactory> redisFactories      = new HashMap<>();
    private HashMap<Integer, RedisTemplate<String, Object>> redisTemplates = new HashMap<>();

    public void init(String mysqlPassword, String mongoPassword, String redisPassword,
                     String lettoMySQLHost,String lettoMongoHost,String lettoRedisHost,
                     String mongoDefaultDatabase, int redisDefaultDatabase,
                     int mySQLPort, int mongoPort, int redisPort) {
        init();
        this.mysqlPassword = mysqlPassword;
        this.mongoPassword = mongoPassword;
        this.redisPassword = redisPassword;
        this.lettoMySQLHost = lettoMySQLHost;
        this.lettoMongoHost = lettoMongoHost;
        this.lettoRedisHost = lettoRedisHost;
        this.mongoDefaultDatabase = mongoDefaultDatabase;
        this.redisDefaultDatabase = redisDefaultDatabase;
        this.mySQLPort      = mySQLPort;
        this.mongoPort      = mongoPort;
        this.redisPort      = redisPort;
    }

    public void init() {
        mongoClient = null;
        lettoMongoTemplate = null;
        secondaryMongoTemplate = null;
        redisFactories= new HashMap<>();
        redisTemplates = new HashMap<>();
    }

    public MongoClient mongoClient() {
        if (mongoClient != null) return mongoClient;
        try {
            if (lettoMongoHost == null || lettoMongoHost.isEmpty())
                return null;
            mongoClient = createMongoClient(mongoPassword,lettoMongoHost);
            return mongoClient;
        } catch (Throwable t) {}
        return null;
    }

    public MongoTemplate lettoMongoTemplate() {
        if (lettoMongoTemplate != null) return lettoMongoTemplate;
        String db="letto";
        try {
            lettoMongoTemplate = new MongoTemplate(mongoClient(), db);
            logger.info("Mongo-Template created for Database: " + db);
            return lettoMongoTemplate;
        } catch (Throwable t) {}
        logger.info("cannot create Mongo-Template for Database: " + db);
        return null;
    }

    public MongoTemplate secondaryMongoTemplate() {
        if (secondaryMongoTemplate != null) return secondaryMongoTemplate;
        try {
            secondaryMongoTemplate = new MongoTemplate(mongoClient(), mongoDefaultDatabase);
            logger.info("Mongo-Template created for Database: " + mongoDefaultDatabase);
            return secondaryMongoTemplate;
        } catch (Throwable t) {}
        logger.info("cannot create Mongo-Template for Database: " + mongoDefaultDatabase);
        return null;
    }

    public MongoClient createMongoClient(String password, String host) {
        logger.info("instantiate MongoClient ");

        //String url = "mongodb://root:"+password+"@"+host+":"+mongoPort;
        try {
            MongoCredential cred = MongoCredential.createCredential(
                    "root",
                    "admin",
                    mongoPassword.toCharArray()
            );

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyToClusterSettings(b -> b.hosts(List.of(new ServerAddress(host, mongoPort)))
                            .serverSelectionTimeout(1000, TimeUnit.MILLISECONDS))
                    .applyToSocketSettings(b -> b.connectTimeout(1000, TimeUnit.MILLISECONDS))
                    .credential(cred)
                    .build();

            /*
            ConnectionString connectionString = new ConnectionString(url);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)

                    .applyToClusterSettings(builder ->
                            builder.serverSelectionTimeout(1000, TimeUnit.MILLISECONDS) // 2 Sekunden Timeout für Verbindungsaufbau
                    )
                    .applyToSocketSettings(builder ->
                            builder.connectTimeout(1000, TimeUnit.MILLISECONDS) // 2 Sekunden Timeout für Socket-Verbindung
                    )
                    .build(); */
            MongoClient mongoClient = MongoClients.create(settings);
            if (mongoClient != null) {
                return mongoClient;
            } else logger.error("no Connection to Mongo Server root@"+host+":"+mongoPort);
        } catch (Throwable e) {
            logger.error("Mongo connect failed to {}:{} - {}", host, mongoPort, e.toString());
        }
        return null;
    }

    public LettuceConnectionFactory redisConnectionFactory(int database) {
        if (redisFactories.containsKey(database)) { return redisFactories.get(database); }
        logger.info("instantiate RedisClient "+database);
        try {
            if (lettoRedisHost == null || lettoRedisHost.isEmpty())
                return null;
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(lettoRedisHost);
            config.setPort(redisPort);
            config.setPassword(redisPassword);
            config.setDatabase(database); // Datenbank-Index

            // ✅ Timeout-Settings für Redis setzen
            LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                    .commandTimeout(Duration.ofSeconds(3)) // 1 Sekunden Timeout für Redis-Befehle
                    .clientOptions(ClientOptions.builder()
                            .socketOptions(SocketOptions.builder()
                                    .connectTimeout(Duration.ofSeconds(3)) // 1 Sekunden Timeout für Verbindung
                                    .build())
                            .build())
                    .build();

            LettuceConnectionFactory factory =  new LettuceConnectionFactory(config, clientConfig);
            factory.afterPropertiesSet();
            if (factory!=null) {
                logger.info("Redis Client connected to "+lettoRedisHost+":"+redisPort+"/"+database);
                redisFactories.put(database, factory);
                return factory;
            }
        } catch (Error e) {
        } catch (Exception e) {
        }
        logger.error("no Connection to Redis Server "+lettoRedisHost+":"+redisPort+"/"+database);
        return null;
    }

    public RedisTemplate<String, Object> redisTemplate(int database) {
        try {
            if (redisTemplates.containsKey(database)) { return redisTemplates.get(database); }
            logger.info("instantiate RedisTemplate "+database);
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory(database));
            // ✅ String als Schlüssel und Wert verwenden
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new StringRedisSerializer());

            template.afterPropertiesSet();
            //Verbindungscheck verhindert einen Start des Servers - wird weggelassen
            //template.opsForValue().set("temp:check:connection", Datum.formatDateTime(Datum.nowLocalDateTime()));
            redisTemplates.put(database, template);
            logger.info("Redis Template connected to "+lettoRedisHost+":"+redisPort+"/"+database);
            return template;
        } catch (Throwable e) { }
        logger.error("no Redis Template connection to "+lettoRedisHost+":"+redisPort+"/"+database+" possible!");
        return null;
    }

    /** @return liefert die MySQL-URL ohne Datenbank am LeTTo-SQL-Server */
    public String mysqlUrl() {
        return "jdbc:mysql://"+lettoMySQLHost+":"+mySQLPort;
    }

    /** @return liefert die MySQL-URL einer Datenbank am LeTTo-SQL-Server */
    public String mysqlUrl(String database) {
        return "jdbc:mysql://"+lettoMySQLHost+":"+mySQLPort+"/"+database;
    }

    /**
     * Liefert eine JDBC-Verbindung zu einer MySQL-Datebank am Letto-Mysql-Server
     * @param database   Datenbankname
     * @param user       Benutzername
     * @param password   Klartextpasswort
     * @return           SQL-Connection
     * @throws SQLException Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public Connection mysqlConnection(String database, String user, String password) throws SQLException {
        Connection con = DriverManager.getConnection(mysqlUrl(database),user,password);
        return con;
    }

    /**
     * Liefert eine JDBC-Verbindung zu einer MySQL-Datebank am Letto-Mysql-Server
     * @param database   Datenbankname
     * @return           SQL-Connection
     * @throws SQLException Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public Connection mysqlRootConnection(String database) throws SQLException {
        Connection con = DriverManager.getConnection(mysqlUrl(database),"root",mysqlPassword);
        return con;
    }

    /**
     * Liefert eine JDBC-Verbindung zu einer MySQL-Datebank am Letto-Mysql-Server
     * @param url        Datenbank URL : jdbc:mysql://adresse:port/datebank
     * @param user       Benutzername
     * @param password   Klartextpasswort
     * @return           SQL-Connection
     * @throws SQLException Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public Connection mysqlUrlConnection(String url, String user, String password) throws SQLException {
        Connection con = DriverManager.getConnection(url,user,password);
        return con;
    }

    /**
     * Liefert eine JDBC-Verbindung zu einer MySQL-Datebank am Letto-Mysql-Server
     * @param url        Datenbank URL : jdbc:mysql://adresse:port/datebank
     * @return           SQL-Connection
     * @throws SQLException Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public Connection mysqlRootUrlConnection(String url) throws SQLException {
        Connection con = DriverManager.getConnection(url,"root",mysqlPassword);
        return con;
    }

    /**
     * Liefert eine JDBC-Verbindung zur 'mysql'-Datebank für die Benutzerverwaltung am Letto-Mysql-Server
     * @return           SQL-Connection
     * @throws SQLException Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public Connection mysqlAdminMysqlConnection() throws SQLException {
        Connection con = DriverManager.getConnection(mysqlUrl("mysql"),"root",mysqlPassword);
        return con;
    }



}
