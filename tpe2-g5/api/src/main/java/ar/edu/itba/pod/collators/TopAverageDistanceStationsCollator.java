package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.Station;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Collator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class TopAverageDistanceStationsCollator implements Collator<Map.Entry<Integer, Double>, List<Map.Entry<String, Double>>> {

    private final Map<Integer, Station> stationMap = new HashMap<>();
    private final int N;

    public TopAverageDistanceStationsCollator(IMap<Integer, Station> stations, int N) {
        stationMap.putAll(stations);
        this.N = N;
    }


    @Override
    public List<Map.Entry<String, Double>> collate(Iterable<Map.Entry<Integer, Double>> iterable) {
        final Map<String, Double> stationsWithAverage = new HashMap<>();

        for (Map.Entry<Integer, Double> entry : iterable) {
            final Station station = stationMap.get(entry.getKey());
            final Double averageDistance = entry.getValue();
            stationsWithAverage.put(station.getName(), averageDistance);
        }

        // Descendente por el promedio de distancia aproximada y luego alfabético
        // por nombre de la estación de inicio del viaje.

        final List<Map.Entry<String, Double>> sortedList = new ArrayList<>(stationsWithAverage.entrySet());
        sortedList.sort((entry1, entry2) -> {
            int cmp = entry2.getValue().compareTo(entry1.getValue());
            if (cmp == 0) {
                cmp = entry1.getKey().toLowerCase().compareTo(entry2.getKey().toLowerCase());
            }
            return cmp;
        });

        return sortedList.subList(0, this.N - 1);
    }
}
