package ar.edu.itba.pod.client.writers;

import java.util.Locale;

public class Query2Writer implements DataFormatter<String, Double> {
    @Override
    public String getHeader() {
        return "station;avg_distance";
    }

    @Override
    public String formatEntry(String key, Double value) {
        String formattedValue = String.format(Locale.US, "%.2f", value); // Formato con dos decimales
        return key + ";" + formattedValue;
    }

    @Override
    public String getOuput() {
        return "/query2.csv";
    }
}
