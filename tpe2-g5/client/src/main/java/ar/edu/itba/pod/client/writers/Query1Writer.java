package ar.edu.itba.pod.client.writers;

public class Query1Writer implements DataFormatter<String, Integer> {

    @Override
    public String getHeader() {
        return "station_a;station_b;trips_between_a_b";
    }

    @Override
    public String formatEntry(String key, Integer value) {
        return key + ";" + value;
    }

    @Override
    public String getOuput() {
        return "/query1.csv";
    }
}
