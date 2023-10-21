package ar.edu.itba.pod.hazelcast.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("hazelcast Client Starting ...");
        logger.info("grpc-com-patterns Client Starting ...");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        IMap<String, String> map = hazelcastInstance.getMap( "articles" );
        KeyValueSource<String, String> source = KeyValueSource.fromMap( map );
        Job<String, String> job = jobTracker.newJob( source );

        ICompletableFuture<Map<String, Long>> future = job
                .mapper( new TokenizerMapper() )
                .combiner( new WordCountCombinerFactory() )
                .reducer( new WordCountReducerFactory() )
                .submit();

        // Attach a callback listener
        future.andThen( buildCallback() );

        // Wait and retrieve the result
        Map<String, Long> result = future.get();

    }
}
