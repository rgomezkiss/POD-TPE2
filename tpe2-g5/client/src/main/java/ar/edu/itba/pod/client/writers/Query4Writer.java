package ar.edu.itba.pod.client.writers;

import java.util.List;

public class Query4Writer implements DataFormatter<String, List<Long>> {
    @Override
    public String getHeader() {
        return "station;pos_afflux;neutral_afflux;negative_afflux";
    }

    @Override
    public String formatEntry(String key, List<Long> value) {
        return key + ";" + value.get(0)  + ";" + value.get(1) + ";" + value.get(2);
    }

    @Override
    public String getOuput() {
        return "/query4.csv";
    }
}
