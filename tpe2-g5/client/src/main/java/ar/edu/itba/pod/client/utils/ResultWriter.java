package ar.edu.itba.pod.client.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ResultWriter<K,V> {
    //TODO: fix generic
    public void writeResult(String outPath, String header, List<Map.Entry<K, V>> results) {
        try (PrintWriter writer = new PrintWriter(outPath)) {
            writer.println(header);
            results.forEach(writer::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
