package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Collator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class NetAffluenceCollator implements Collator<Map.Entry<Pair<Integer, LocalDateTime>, Long>, List<Map.Entry<String, List<Long>>>> {

    private final Map<Integer, Station> stationMap = new HashMap<>();
    private final LocalDate startDate;
    private final LocalDate endDate;

    public NetAffluenceCollator(IMap<Integer, Station> stations, LocalDate startDate, LocalDate endDate) {
        this.stationMap.putAll(stations);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public List<Map.Entry<String, List<Long>>> collate(Iterable<Map.Entry<Pair<Integer, LocalDateTime>, Long>> iterable) {
        final Map<String, List<Long>> map = new HashMap<>();

        final Map<Integer, Pair<Long, LocalDateTime>> netAffluencePerDay = new HashMap<>();

        final long totalDays =  ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));

        for (Map.Entry<Pair<Integer, LocalDateTime>, Long> entry : iterable) {
            Pair<Integer, LocalDateTime> key = entry.getKey();
            Long value = entry.getValue();

            netAffluencePerDay.putIfAbsent(key.getOne(), new Pair<>(value, key.getOther()));




            final Station station = stationMap.get(entry.getKey());
            final List<Long> affluencesList = entry.getValue();
            final long totalDayPerStation = affluencesList.get(0) + affluencesList.get(1) + affluencesList.get(2);
            if (totalDayPerStation < totalDays) {
                long missedDays = totalDays - totalDayPerStation;
                affluencesList.set(1, affluencesList.get(1) + missedDays);
            }
            map.put(station.getName(), affluencesList);
        }

        final List<Map.Entry<String, List<Long>>> sortedList = new ArrayList<>(map.entrySet());
        sortedList.sort((entry1, entry2) -> {
            int cmp = entry2.getValue().get(0).compareTo(entry1.getValue().get(0));
            if (cmp == 0) {
                cmp = entry1.getKey().toLowerCase().compareTo(entry2.getKey().toLowerCase());
            }
            return cmp;
        });

        return sortedList;

        return null;
    }

}
