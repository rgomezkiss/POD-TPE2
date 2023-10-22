package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

@SuppressWarnings("deprecation")
public class TripsBetweenStationsMapper implements Mapper<String, Trip, Pair<Integer, Integer>, Integer> {
    private final Map<Integer, Station> stations;

    public TripsBetweenStationsMapper(Map<Integer,Station> stations) {
        this.stations = stations;
    }

//    Sólo se deben listar las estaciones presentes en el archivo CSV de estaciones.
//    No se deben listar los viajes circulares, es decir, salidas que contengan el mismo nombre de estación para las dos primeras columnas.
    @Override
    public void map(String string, Trip trip, Context<Pair<Integer, Integer>, Integer> context) {
        if (stations.containsKey(trip.getStartStation()) && stations.containsKey(trip.getEndStation())) {
            if(trip.getEndStation() != trip.getStartStation()) {
                context.emit(new Pair<>(trip.getStartStation(), trip.getEndStation()), 1);
            }
        }
    }
}
