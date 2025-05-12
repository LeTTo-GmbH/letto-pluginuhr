package at.letto.databaseclient.config.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages     = "at.letto.databaseclient.repository.mongo.secondary", // Repositories für secondaryDatabase
        mongoTemplateRef = "secondaryMongoTemplate"
)
public class SecondaryMongoConfiguration {
}
