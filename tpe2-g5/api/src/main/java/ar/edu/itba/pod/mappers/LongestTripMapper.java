package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDateTime;
import java.util.Map;

@SuppressWarnings("deprecation")
public class LongestTripMapper implements Mapper<String, Trip, Pair<Integer, Integer>, Pair<LocalDateTime, Integer>> {
    private final Map<Integer, Station> stations;

    public LongestTripMapper(Map<Integer, Station> stations) {
        this.stations = stations;
    }

    @Override
    public void map(String string, Trip trip, Context<Pair<Integer, Integer>, Pair<LocalDateTime, Integer>> context) {
        if (stations.containsKey(trip.getStartStation()) && stations.containsKey(trip.getEndStation())) {
            if (trip.getEndStation() != trip.getStartStation()) {
                context.emit(new Pair<>(trip.getStartStation(), trip.getEndStation()), new Pair<>(trip.getStartDate(), trip.getTripLength()));
            }
        }
    }
}
