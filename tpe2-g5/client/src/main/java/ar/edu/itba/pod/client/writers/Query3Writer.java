package ar.edu.itba.pod.client.writers;

import ar.edu.itba.pod.models.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Query3Writer implements DataFormatter<String, Pair<LocalDateTime, Integer>> {
    @Override
    public String getHeader() {
        return "start_station;end_station;start_date;minutes";
    }

    @Override
    public String formatEntry(String key, Pair<LocalDateTime, Integer> value) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return key + ";" + value.getOne().format(formatter) + ";" + value.getOther();
    }

    @Override
    public String getOutput() {
        return "/query3.csv";
    }
}
