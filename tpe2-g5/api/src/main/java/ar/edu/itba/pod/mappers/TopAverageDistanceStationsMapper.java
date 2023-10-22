package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.Map;

@SuppressWarnings("deprecation")
public class TopAverageDistanceStationsMapper implements Mapper<String, Trip, Integer, Double> {

    private final Map<Integer, Station> stations;

    public TopAverageDistanceStationsMapper(Map<Integer, Station> stations) {
        this.stations = stations;
    }

    @Override
    public void map(String s, Trip trip, Context<Integer, Double> context) {
        if (stations.containsKey(trip.getStartStation()) && stations.containsKey(trip.getEndStation())) {
            if (trip.getEndStation() != trip.getStartStation() && trip.getIsMember() == 1) {
                final Station startStation = stations.get(trip.getStartStation());
                final Station endStation = stations.get(trip.getEndStation());
                context.emit(trip.getStartStation(), startStation.haversineDistance(endStation));
            }
        }
    }
}
