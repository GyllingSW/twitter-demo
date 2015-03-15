package dk.software.gylling;

import com.mongodb.ServerAddress;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * TwitterStream @Component is used to demonstrate the
 * connection to the streaming api exposed by Twitter.
 *
 * Be aware, that you will have to obtain your own API key
 * to make this example work.
 *
 * This is still basically a copy from:
 * https://github.com/twitter/hbc/blob/master/hbc-example/src/main/java/com/twitter/hbc/example/SampleStreamExample.java
 *
 * User: GyllingSW
 * Date: 3/15/15
 * Time: 12:31
 */
@Component
@PropertySource(value = "file:/home/peg/.twitter/access.properties", ignoreResourceNotFound = false)
public class TwitterStream implements CommandLineRunner {

    /**
     * Standard logging facade using SLF4J
     */
    private final static Logger log = LoggerFactory.getLogger(TwitterStream.class);

    @Value("${consumer.key}")
    String consumerKey;

    @Value("${consumer.secret}")
    String consumerSecret;

    @Value("${access.token}")
    String token;

    @Value("${access.secret}")
    String secret;

    @Autowired
    MongoTemplate mongoTemplate;

    @Value("${mongodb.twitter.collection}")
    String mongoCollectionName;

    /**
     *
     * This is basically a copy from:
     * https://github.com/twitter/hbc/blob/master/hbc-example/src/main/java/com/twitter/hbc/example/SampleStreamExample.java
     *
     * @return BasicClient ready to connect
     */
    public BasicClient setupClient(BlockingQueue<String> queue) {

        // Define our endpoint: By default, delimited=length is set (we need this for our processor)
        // and stall warnings are on.
        StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
        endpoint.stallWarnings(false);

        log.warn("consumerKey : {}",consumerKey);
        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);

        // Create a new BasicClient. By default gzip is enabled.
        BasicClient client = new ClientBuilder()
                .name("SenseMaker")
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        return client;
    }

    /**
     * This is basically a copy from:
     * https://github.com/twitter/hbc/blob/master/hbc-example/src/main/java/com/twitter/hbc/example/SampleStreamExample.java
     *
     * Just to make sure my twitter credentials work.
     *
     * @throws InterruptedException
     */
    @Override
    public void run(String... strings) throws Exception {

        // Let's initialize our MongoDB connection
        ServerAddress serverAddress = mongoTemplate.getDb().getMongo().getAddress();
        log.info("Connected to mongodb running at {}:{}", serverAddress.getHost(), serverAddress.getPort());
        if(!mongoTemplate.collectionExists(mongoCollectionName)) {
            log.info("Initializing MongoDB collection: "+mongoCollectionName);
            mongoTemplate.createCollection(mongoCollectionName);
        }

        // Create an appropriately sized blocking queue
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);

        BasicClient client = this.setupClient(queue);

        // Establish a connection
        client.connect();

        // Do whatever needs to be done with messages
        for (int msgRead = 0; msgRead < 1000; msgRead++) {
            if (client.isDone()) {
                log.info("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
                break;
            }

            String msg = queue.poll(5, TimeUnit.SECONDS);
            if (msg == null) {
                log.warn("Did not receive a message in 5 seconds");
            } else {
                mongoTemplate.insert(msg, mongoCollectionName);
            }
        }

        client.stop();

        // Print some stats
        log.info("The client read {} messages!\n", client.getStatsTracker().getNumMessages());
    }
}
