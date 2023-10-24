package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.Station;
import com.hazelcast.mapreduce.Collator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class NetAffluenceCollator implements Collator<Map.Entry<String, List<Long>>, List<Map.Entry<String, List<Long>>>> {

    private final Map<Integer, Station> stationMap = new HashMap<>();
    private final LocalDate startDate;
    private final LocalDate endDate;

    public NetAffluenceCollator(List<Station> stations, LocalDate startDate, LocalDate endDate) {
        for (Station s : stations) {
            stationMap.put(s.getPk(), s);
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public List<Map.Entry<String, List<Long>>> collate(Iterable<Map.Entry<String, List<Long>>> iterable) {
        final Map<String, List<Long>> map = new HashMap<>();

        final long totalDays =  ChronoUnit.DAYS.between(startDate, endDate);

        for (Map.Entry<String, List<Long>> entry : iterable) {
            final String stationName = entry.getKey();
            final List<Long> affluencesList = entry.getValue();
            final long totalDayPerStation = affluencesList.get(0) + affluencesList.get(1) + affluencesList.get(2);
            if (totalDayPerStation < totalDays) {
                long missedDays = totalDays - totalDayPerStation;
                affluencesList.set(1, affluencesList.get(1) + missedDays);
            }
            map.put(stationName, affluencesList);
        }

        final List<Map.Entry<String, List<Long>>> sortedList = new ArrayList<>(map.entrySet());
        sortedList.sort((entry1, entry2) -> {
            int cmp = entry2.getValue().get(0).compareTo(entry1.getValue().get(0));
            if (cmp == 0) {
                cmp = entry1.getKey().compareTo(entry2.getKey());
            }
            return cmp;
        });

        return sortedList;
    }
}
