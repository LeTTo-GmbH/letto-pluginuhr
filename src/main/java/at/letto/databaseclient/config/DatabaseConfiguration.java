package at.letto.databaseclient.config;

import at.letto.databaseclient.service.DatabaseConnectionService;
import com.mongodb.client.MongoClient;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class DatabaseConfiguration {

    @Value("${MYSQL_ROOT_PASSWORD:}") @Getter @Setter
    private String mysqlPassword;

    @Value("${MONGO_ROOT_PASSWORD:}") @Getter @Setter
    private String mongoPassword;

    @Value("${REDIS_PASSWORD:}") @Getter @Setter
    private String redisPassword;

    @Value("${MYSQL_HOST:letto-mysql.nw-letto}") @Getter @Setter
    private String lettoMysqlHost;

    @Value("${MONGO_HOST:letto-mongo.nw-letto}") @Getter @Setter
    private String lettoMongoHost;

    @Value("${REDIS_HOST:letto-redis.nw-letto}") @Getter @Setter
    private String lettoRedisHost;

    @Value("${MONGO_DEFAULT_DATABASE:letto}") @Getter @Setter
    private String mongoDefaultDatabase;

    @Value("${REDIS_DEFAULT_DATABASE:0}") @Getter @Setter
    private int redisDefaultDatabase;

    @Value("${MYSQL_PORT:3306}") @Getter @Setter
    private int mySQLPort;

    @Value("${MONGO_PORT:27017}") @Getter @Setter
    private int mongoPort;

    @Value("${REDIS_PORT:6379}") @Getter @Setter
    private int redisPort;

    private Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Autowired private DatabaseConnectionService databaseConnectionService;

    @PostConstruct
    public void init() {
        databaseConnectionService.init(
            mysqlPassword,mongoPassword,redisPassword,
            lettoMysqlHost,lettoMongoHost,lettoRedisHost,
            mongoDefaultDatabase,redisDefaultDatabase,
            mySQLPort,mongoPort,redisPort
        );
    }

    @Bean
    public MongoClient mongoClient() {
        return databaseConnectionService.mongoClient();
    }

    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate() {
        //return new MongoTemplate(mongoClient(), "meineDatenbank");  // Datenbank setzen
        return databaseConnectionService.lettoMongoTemplate();
    }

    @Bean(name = "lettoMongoTemplate")
    public MongoTemplate lettoMongoTemplate() {
        //return new MongoTemplate(mongoClient(), "meineDatenbank");  // Datenbank setzen
        return databaseConnectionService.lettoMongoTemplate();
    }

    @Bean(name = "secondaryMongoTemplate")
    public MongoTemplate secondaryMongoTemplate() {
        return databaseConnectionService.secondaryMongoTemplate();
        //return new MongoTemplate(databaseConnectionService().mongoClient(), "secondaryDatabase");
    }

    /** Konfiguration für die erste Redis-Datenbank */
    @Bean public LettuceConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = databaseConnectionService.redisConnectionFactory(redisDefaultDatabase);
        return factory;
    }

    /** Redis-Template für die erste Redis-Datenbank */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return databaseConnectionService.redisTemplate(redisDefaultDatabase);
    }

}
