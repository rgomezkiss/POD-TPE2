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
public class NetAffluenceMapper implements Mapper<Integer, Trip, String, Pair<Long, LocalDateTime>> {
    private final Map<Integer, Station> stationMap = new HashMap<>();
    private final LocalDate startDate;
    private final LocalDate endDate;

    public NetAffluenceMapper(IMap<Integer, Station> stations, LocalDate startDate, LocalDate endDate) {
        this.stationMap.putAll(stations);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void map(Integer key, Trip trip, Context<String, Pair<Long, LocalDateTime>> context) {
        if (stationMap.containsKey(trip.getStartStation()) && stationMap.containsKey(trip.getEndStation())) {
            if (trip.getStartDate().isAfter(startDate.atStartOfDay()) && trip.getEndDate().isBefore(endDate.atTime(23, 59))) {
                final Station startStation = stationMap.get(trip.getStartStation());
                final Station endStation = stationMap.get(trip.getEndStation());
                context.emit(startStation.getName(), new Pair<>(-1L, trip.getStartDate()));
                context.emit(endStation.getName(), new Pair<>(1L, trip.getEndDate()));
            }
        }
    }
}
