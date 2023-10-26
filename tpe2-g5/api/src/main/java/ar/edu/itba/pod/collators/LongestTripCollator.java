package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Collator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("deprecation")
public class LongestTripCollator implements Collator<
        Map.Entry<Pair<Integer, Integer>, Pair<LocalDateTime, Integer>>,
        List<Map.Entry<String, Pair<LocalDateTime, Integer>>>> {
    private final Map<Integer, Station> stationMap = new HashMap<>();

    public LongestTripCollator(IMap<Integer, Station> stations) {
        stationMap.putAll(stations);
    }

    @Override
    public List<Map.Entry<String, Pair<LocalDateTime, Integer>>> collate(Iterable<Map.Entry<Pair<Integer, Integer>, Pair<LocalDateTime, Integer>>> iterable) {
        final Map<String, Pair<LocalDateTime, Integer>> tripsCount = new HashMap<>();

        for (Map.Entry<Pair<Integer, Integer>, Pair<LocalDateTime, Integer>> entry : iterable) {
            final Pair<Integer, Integer> pair = entry.getKey();
            final Pair<LocalDateTime, Integer> longestTrip = entry.getValue();

            final String stationA = stationMap.get(pair.getOne()).getName();
            final String stationB = stationMap.get(pair.getOther()).getName();

            if (stationA != null && stationB != null) {
                String stationKey = stationA + ";" + stationB;
                tripsCount.put(stationKey, longestTrip);
            }
        }

        final List<Map.Entry<String, Pair<LocalDateTime, Integer>>> sortedList = new ArrayList<>(tripsCount.entrySet());
        sortedList.sort((entry1, entry2) -> {
            int cmp = entry2.getValue().getOther().compareTo(entry1.getValue().getOther());
            if (cmp == 0) {
                cmp = entry1.getKey().toLowerCase().compareTo(entry2.getKey().toLowerCase());
            }
            return cmp;
        });

        return sortedList;
    }
}
