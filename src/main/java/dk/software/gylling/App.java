package dk.software.gylling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Hello world - Streaming from twitter to MongoDB as a Spring Boot application
 *
 * If you consider running this sample code - you must establish an API key using your twitter account.
 * See: https://apps.twitter.com/
 *
 * You will need a running instance of MongoDB
 * See: https://www.mongodb.org/ and https://www.mongodb.org/downloads
 * See: http://robomongo.org/ - in case you would like a GUI based tool to query your MongoDB
 *
 * I wrote this to evaluate if my twitter API Key is configured correct
 *
 * User: GyllingSW
 * Date: 3/15/15
 * Time: 12:31
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class App
{

    /**
     * Standard logging facade using SLF4J
     */
    private final static Logger log = LoggerFactory.getLogger(App.class);

    /**
     * Standard Spring Boot Application start.
     *
     * @param args
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception{
            SpringApplication.run(App.class,args);
    }
}
