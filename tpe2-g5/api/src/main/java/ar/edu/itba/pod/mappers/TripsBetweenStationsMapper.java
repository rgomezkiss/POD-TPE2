package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

@SuppressWarnings("deprecation")
public class TripsBetweenStationsMapper implements Mapper<Integer, Trip, Pair<Integer, Integer>, Integer>, HazelcastInstanceAware {
    private final Map<Integer, Station> stationMap = new HashMap<>();
    HazelcastInstance hazelcastInstance;

    public TripsBetweenStationsMapper(List<Station> stations) {
        for (Station s : stations) {
            stationMap.put(s.getPk(), s);
        }
    }

    @Override
    public void map(Integer key, Trip trip, Context<Pair<Integer, Integer>, Integer> context) {
        if (stationMap.containsKey(trip.getStartStation()) && stationMap.containsKey(trip.getEndStation())) {
            if (trip.getEndStation() != trip.getStartStation()) {
                context.emit(new Pair<>(trip.getStartStation(), trip.getEndStation()), 1);
            }
        }
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
