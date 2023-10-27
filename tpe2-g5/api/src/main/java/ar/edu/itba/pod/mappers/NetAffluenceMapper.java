package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class NetAffluenceMapper implements Mapper<Integer, Trip, Pair<Integer, LocalDateTime>, Long> {
    private final Map<Integer, Station> stationMap = new HashMap<>();
    private final LocalDate startDate;
    private final LocalDate endDate;

    public NetAffluenceMapper(IMap<Integer, Station> stations, LocalDate startDate, LocalDate endDate) {
        this.stationMap.putAll(stations);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void map(Integer key, Trip trip, Context<Pair<Integer, LocalDateTime>, Long> context) {
        if (stationMap.containsKey(trip.getStartStation()) && stationMap.containsKey(trip.getEndStation())) {
            if (trip.getStartDate().isAfter(startDate.atStartOfDay()) && trip.getEndDate().isBefore(endDate.atTime(23, 59))) {
                context.emit(new Pair<>(trip.getStartStation(), trip.getStartDate()), -1L);
                context.emit(new Pair<>(trip.getEndStation(), trip.getEndDate()), 1L);
            }
        }
    }
}
