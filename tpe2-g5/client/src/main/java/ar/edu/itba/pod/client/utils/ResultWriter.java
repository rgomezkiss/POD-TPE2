package ar.edu.itba.pod.client.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ResultWriter<K,V> {

    public static void writeResult(String outPath, List<Map.Entry<String, Integer>> results) {
        try (PrintWriter writer = new PrintWriter(outPath)) {
            writer.println("station_a;station_b;trips_between_a_b");
            results.forEach(writer::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
