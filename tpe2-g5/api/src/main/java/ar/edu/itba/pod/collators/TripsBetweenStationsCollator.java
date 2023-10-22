package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import com.hazelcast.mapreduce.Collator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class TripsBetweenStationsCollator implements Collator<Map.Entry<Pair<Integer, Integer>, Integer>, List<Map.Entry<String, Integer>>> {
    private Map<Integer, Station> stations;

    public TripsBetweenStationsCollator(Map<Integer, Station> stations) {
        this.stations = stations;
    }

    @Override
    public List<Map.Entry<String, Integer>> collate(Iterable<Map.Entry<Pair<Integer, Integer>, Integer>> iterable){
        Map<String, Integer> tripsCount = new HashMap<>();

        for (Map.Entry<Pair<Integer, Integer>, Integer> entry : iterable) {
            Pair<Integer, Integer> pair = entry.getKey();
            Integer trips = entry.getValue();

            String stationA = stations.get(pair.getOne()).getName();
            String stationB = stations.get(pair.getOther()).getName();

            if (stationA != null && stationB != null && trips > 0) {
                String stationKey = stationA + ";" + stationB;
                tripsCount.put(stationKey, trips);
            }
        }

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(tripsCount.entrySet());
        sortedList.sort((entry1, entry2) -> {
            int cmp = entry2.getValue().compareTo(entry1.getValue());
            if (cmp == 0) {
                cmp = entry1.getKey().compareTo(entry2.getKey());
            }
            return cmp;
        });

        return sortedList;
    }
}

