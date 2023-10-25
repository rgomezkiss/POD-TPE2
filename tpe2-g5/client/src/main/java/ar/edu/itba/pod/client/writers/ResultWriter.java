package ar.edu.itba.pod.client.writers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ResultWriter<K,V> {

    public void writeResult(String outPath, List<Map.Entry<K, V>> result, DataFormatter<K, V> formatter) {
        try (PrintWriter writer = new PrintWriter(outPath + formatter.getOuput())) {
            writer.println(formatter.getHeader());
            for (Map.Entry<K, V> r : result) {
                writer.println(formatter.formatEntry(r.getKey(), r.getValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


