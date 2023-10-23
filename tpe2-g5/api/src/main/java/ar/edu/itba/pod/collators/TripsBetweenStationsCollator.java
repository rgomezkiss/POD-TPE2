package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import com.hazelcast.mapreduce.Collator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class TripsBetweenStationsCollator implements Collator<
        Map.Entry<Pair<Integer, Integer>, Integer>,
        List<Map.Entry<String, Integer>>> {
    private final Map<Integer, Station> stationMap = new HashMap<>();

    public TripsBetweenStationsCollator(List<Station> stations) {
        for (Station s : stations) {
            stationMap.put(s.getPk(), s);
        }
    }

    @Override
    public List<Map.Entry<String, Integer>> collate(Iterable<Map.Entry<Pair<Integer, Integer>, Integer>> iterable) {
        final Map<String, Integer> tripsCount = new HashMap<>();

        for (Map.Entry<Pair<Integer, Integer>, Integer> entry : iterable) {
            final Pair<Integer, Integer> pair = entry.getKey();
            final Integer trips = entry.getValue();

            final String stationA = stationMap.get(pair.getOne()).getName();
            final String stationB = stationMap.get(pair.getOther()).getName();

            if (stationA != null && stationB != null && trips > 0) {
                final String stationKey = stationA + ";" + stationB;
                tripsCount.put(stationKey, trips);
            }
        }

        final List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(tripsCount.entrySet());
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

