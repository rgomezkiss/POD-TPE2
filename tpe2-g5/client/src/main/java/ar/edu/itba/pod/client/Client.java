package ar.edu.itba.pod.client;

import ar.edu.itba.pod.TokenizerMapper;
import ar.edu.itba.pod.WordCountCollator;
import ar.edu.itba.pod.WordCountCombinerFactory;
import ar.edu.itba.pod.WordCountReducerFactory;
import com.hazelcast.core.*;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args)
            throws Exception {

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        try {
            fillMapWithData(hazelcastInstance);

            Map<String, Long> countsPerWord = mapReduce(hazelcastInstance);
            System.out.println("Counts per words over " + DATA_RESOURCES_TO_LOAD.length + " files:");
            for (Map.Entry<String, Long> entry : countsPerWord.entrySet()) {
                System.out.println("\tWord '" + entry.getKey() + "' occured " + entry.getValue() + " times");
            }

            long wordCount = mapReduceCollate(hazelcastInstance);
            System.out.println("All content sums up to " + wordCount + " words.");

        } finally {
            Hazelcast.shutdownAll();
        }
    }

    private static Map<String, Long> mapReduce(HazelcastInstance hazelcastInstance)
            throws Exception {

        // Retrieving the JobTracker by name
        JobTracker jobTracker = hazelcastInstance.getJobTracker("default");

        // Creating the KeyValueSource for a Hazelcast IMap
        IMap<String, String> map = hazelcastInstance.getMap("articles");
        KeyValueSource<String, String> source = KeyValueSource.fromMap(map);

        Job<String, String> job = jobTracker.newJob(source);

        // Creating a new Job
        ICompletableFuture<Map<String, Long>> future = job // returned future
                .mapper(new TokenizerMapper())             // adding a mapper
                .combiner(new WordCountCombinerFactory())  // adding a combiner through the factory
                .reducer(new WordCountReducerFactory())    // adding a reducer through the factory
                .submit();                                 // submit the task

        // Attach a callback listener
        future.andThen(buildCallback());

        // Wait and retrieve the result
        return future.get();
    }

    private static long mapReduceCollate(HazelcastInstance hazelcastInstance)
            throws Exception {

        // Retrieving the JobTracker by name
        JobTracker jobTracker = hazelcastInstance.getJobTracker("default");

        // Creating the KeyValueSource for a Hazelcast IMap
        IMap<String, String> map = hazelcastInstance.getMap("articles");
        KeyValueSource<String, String> source = KeyValueSource.fromMap(map);

        // Creating a new Job
        Job<String, String> job = jobTracker.newJob(source);

        ICompletableFuture<Long> future = job // returned future
                .mapper(new TokenizerMapper())             // adding a mapper
                .combiner(new WordCountCombinerFactory())  // adding a combiner through the factory
                .reducer(new WordCountReducerFactory())    // adding a reducer through the factory
                .submit(new WordCountCollator());          // submit the task and supply a collator

        // Wait and retrieve the result
        return future.get();
    }

    private static ExecutionCallback<Map<String, Long>> buildCallback() {
        return new ExecutionCallback<Map<String, Long>>() {
            @Override
            public void onResponse(Map<String, Long> stringLongMap) {
                System.out.println("Calculation finished! :)");
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }
        };
    }
}
