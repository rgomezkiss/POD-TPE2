package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;

import java.util.HashMap;
import java.util.Map;

import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

@SuppressWarnings("deprecation")
public class TripsBetweenStationsMapper implements Mapper<Integer, Trip, Pair<Integer, Integer>, Integer> {
    private final Map<Integer, Station> stationMap = new HashMap<>();

    public TripsBetweenStationsMapper(IMap<Integer, Station> stations) {
        stationMap.putAll(stations);
    }

    @Override
    public void map(Integer key, Trip trip, Context<Pair<Integer, Integer>, Integer> context) {
        if (iMapContains(trip.getStartStation()) && iMapContains(trip.getEndStation())) {
            if (trip.getEndStation() != trip.getStartStation()) {
                context.emit(new Pair<>(trip.getStartStation(), trip.getEndStation()), 1);
            }
        }
    }

    private boolean iMapContains (Integer key) {
        return stationMap.containsKey(key);
    }
}
