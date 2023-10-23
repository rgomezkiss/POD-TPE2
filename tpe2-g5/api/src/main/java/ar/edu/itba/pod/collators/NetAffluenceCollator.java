package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.Station;
import com.hazelcast.mapreduce.Collator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class NetAffluenceCollator implements Collator<Map.Entry<String, List<Integer>>, List<Map.Entry<String, List<Integer>>>> {

    private final Map<Integer, Station> stationMap = new HashMap<>();
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public NetAffluenceCollator(List<Station> stations, LocalDateTime startDate, LocalDateTime endDate) {
        for (Station s : stations) {
            stationMap.put(s.getPk(), s);
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public List<Map.Entry<String, List<Integer>>> collate(Iterable<Map.Entry<String, List<Integer>>> iterable) {
        final Map<String, List<Integer>> map = new HashMap<>();
        final int totalDays = countTotalDayInRange(startDate, endDate);

        for (Map.Entry<String, List<Integer>> entry : iterable) {
            final String stationName = entry.getKey();
            final List<Integer> affluencesList = entry.getValue();
            final int totalDayPerStation = affluencesList.get(0) + affluencesList.get(1) + affluencesList.get(2);
            if (totalDayPerStation < totalDays) {
                int missedDays = totalDays - totalDayPerStation;
                affluencesList.set(1, affluencesList.get(1) + missedDays);
            }
            map.put(stationName, affluencesList);
        }

        final List<Map.Entry<String, List<Integer>>> sortedList = new ArrayList<>(map.entrySet());
        sortedList.sort((entry1, entry2) -> {
            int cmp = entry2.getValue().get(0).compareTo(entry1.getValue().get(0));
            if (cmp == 0) {
                cmp = entry1.getKey().compareTo(entry2.getKey());
            }
            return cmp;
        });

        return sortedList;
    }

    private int countTotalDayInRange(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDate startDateOnly = startDate.toLocalDate();
        LocalDate endDateOnly = endDate.toLocalDate();

        Period period = Period.between(startDateOnly, endDateOnly);
        int totalDays = period.getDays();

        // Incluir el último día en el rango si no está incluido
        if (!endDate.toLocalDate().isEqual(endDateOnly)) {
            totalDays++;
        }

        return totalDays;
    }
}
