package ar.edu.itba.pod.client.utils;

import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataLoader {
    private final static Logger logger = LoggerFactory.getLogger(DataLoader.class);

    public Map<Integer, Station> readStations(String path) {
        Map<Integer, Station> stations = null;

        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            stations = lines
                    .skip(1)
                    .map(line -> line.split(";"))
                    .collect(Collectors.toMap(
                            parts -> Integer.parseInt(parts[0]),
                            parts -> new Station(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]))
                    ));
        } catch (IOException e) {
            logger.error("Error while reading file: {}", e.getMessage());
        }

        return stations;
    }

    public List<Trip> readBikes(String path) {
        List<Trip> trips = null;

        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            trips = lines
                    .skip(1)    // Salteamos los encabezados
                    .map(line -> line.split(";"))
                    .map(data ->
                            new Trip()
                    )
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error while reading file: {}", e.getMessage());
        }

        return trips;
    }
}
