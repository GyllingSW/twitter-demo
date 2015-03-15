package dk.software.gylling.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * When using Spring Data Mongo make sure to return a MongoClient
 *
 * Extending AbstractMongoConfiguration is the way to do this
 * without having to bother with the XML configuration.
 *
 * User: GyllingSW
 * Date: 3/15/15
 * Time: 21:12
 */
@Configuration
@PropertySource(value = "mongo.properties", ignoreResourceNotFound = false)
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${mongodb.host}")
    String mongoHost;

    @Value("${mongodb.port}")
    int mongoPort;

    @Value("${mongodb.twitter.demodb}")
    String mongoDB;

    @Override
    protected String getDatabaseName() {
        return mongoDB;
    }

    @Override
    public Mongo mongo() throws Exception {
        MongoClient mongoClient = new MongoClient(mongoHost,mongoPort);
        return mongoClient;
    }
}
