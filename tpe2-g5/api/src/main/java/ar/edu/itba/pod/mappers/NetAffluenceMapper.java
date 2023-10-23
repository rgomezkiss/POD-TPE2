package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class NetAffluenceMapper implements Mapper<String, Trip, String, Pair<Integer, LocalDateTime>> {

    private final Map<Integer, Station> stationMap = new HashMap<>();
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public NetAffluenceMapper(List<Station> stations, LocalDateTime startDate, LocalDateTime endDate) {
        for (Station s : stations) {
            stationMap.put(s.getPk(), s);
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void map(String s, Trip trip, Context<String, Pair<Integer, LocalDateTime>> context) {
        if (stationMap.containsKey(trip.getStartStation()) && stationMap.containsKey(trip.getEndStation())) {
            if (trip.getStartDate().isAfter(startDate) && trip.getEndDate().isBefore(endDate)) {
                final Station startStation = stationMap.get(trip.getStartStation());
                final Station endStation = stationMap.get(trip.getEndStation());
                context.emit(startStation.getName(), new Pair<>(-1, trip.getStartDate()));
                context.emit(endStation.getName(), new Pair<>(1, trip.getEndDate()));
            }
        }
    }
}
