package ar.edu.itba.pod.client;

import com.hazelcast.core.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import com.hazelcast.mapreduce.*;

public class TripsBetweenStationsQuery {

    //TODO may extend from abstract class for all 3 queries
    private final HazelcastInstance hazelcast;
    private final IMap<String, Integer> stationTripsMap;

    public TripsBetweenStationsQuery(HazelcastInstance hazelcastInstance, IMap<String, Integer> stationTripsMap) {
        this.hazelcast = hazelcastInstance;
        this.stationTripsMap = stationTripsMap;
    }

    public void execute(Map<String, String> stations, String outPath) {
        executeMapReduceQuery(stations);
        List<String> results = generateTripResults();
        saveResultsToFile(results, outPath);
    }

    private void executeMapReduceQuery(Map<String, String> stations) {
        JobTracker jobTracker = hazelcast.getJobTracker("tripQueryTracker");
        KeyValueSource<String, String> source = KeyValueSource.fromMap(stations);

        Job<String, String> job = jobTracker.newJob(source);

        job.mapper((entry, context) -> {
            String stationA = entry.getKey();
            String stationB = entry.getValue();

            if (!stationA.equals(stationB)) {
                context.emit(stationA + ";" + stationB, 1);
            }
        });

        job.combiner((CombinerFactory<String, Integer, Integer>) Combiners.integerSumCombinerFactory());

        job.reducer((ReducerFactory<String, Integer, Integer>) Reducers.integerSumReducerFactory());

        ICompletableFuture<List<Map.Entry<String, Integer>>> future = job.submit();

        List<Map.Entry<String, Integer>> results = future.get();

        for (Map.Entry<String, Integer> entry : results) {
            stationTripsMap.put(entry.getKey(), entry.getValue());
        }
    }

    private List<String> generateTripResults() {
        List<String> results = new ArrayList<>();
        stationTripsMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted((entry1, entry2) -> {
                    if (entry1.getValue().equals(entry2.getValue())) {
                        int stationCompare = entry1.getKey().compareTo(entry2.getKey());
                        if (stationCompare == 0) {
                            return entry1.getKey().compareTo(entry2.getKey());
                        }
                        return stationCompare;
                    }
                    return entry2.getValue().compareTo(entry1.getValue());
                })
                .forEach(entry -> {
                    String[] stations = entry.getKey().split(";");
                    results.add(stations[0] + ";" + stations[1] + ";" + entry.getValue());
                });
        return results;
    }

    private void saveResultsToFile(List<String> results, String filePath) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println("station_a;station_b;trips_between_a_b");
            results.forEach(writer::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        hazelcast.shutdown();
    }
}
