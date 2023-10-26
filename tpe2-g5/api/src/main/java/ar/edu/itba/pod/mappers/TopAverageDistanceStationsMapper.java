package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class TopAverageDistanceStationsMapper implements Mapper<Integer, Trip, Integer, Double> {

    private final Map<Integer, Station> stationMap = new HashMap<>();

    public TopAverageDistanceStationsMapper(IMap<Integer, Station> stations) {
        stationMap.putAll(stations);
    }


    @Override
    public void map(Integer key, Trip trip, Context<Integer, Double> context) {
        if (stationMap.containsKey(trip.getStartStation()) && stationMap.containsKey(trip.getEndStation())) {
            if (trip.getEndStation() != trip.getStartStation() && trip.getIsMember() == 1) {
                final Station startStation = stationMap.get(trip.getStartStation());
                final Station endStation = stationMap.get(trip.getEndStation());
                context.emit(trip.getStartStation(), startStation.haversineDistance(endStation));
            }
        }
    }
}
