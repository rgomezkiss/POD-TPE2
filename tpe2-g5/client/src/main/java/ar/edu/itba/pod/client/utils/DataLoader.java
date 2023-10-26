package ar.edu.itba.pod.client.utils;

import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataLoader {
    private final static Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private final static String STATIONS_CSV = "/stations.csv";
    private final static String BIKES_CSV = "/bikes.csv";
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<Station> readStations(String path) {
        List<Station> stations = null;

        final String stationsPath = path + STATIONS_CSV;

        try (Stream<String> lines = Files.lines(Paths.get(stationsPath))) {
            stations = lines
                    .skip(1)
                    .map(line -> line.split(";"))
                    .map(data ->
                            new Station(
                                    Integer.parseInt(data[0]),
                                    data[1],
                                    Double.parseDouble(data[2]),
                                    Double.parseDouble(data[3])
                            )
                    )
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error while reading file: {}", e.getMessage());
        }

        return stations;
    }

    public static void readBikes(final String path, final IMap<Integer, Trip> tripsIMap, int maxSize) {
        Map<Integer, Trip> trips = new HashMap<>();
        AtomicInteger count = new AtomicInteger();

        final String bikesPath = path + BIKES_CSV;

        try (Stream<String> lines = Files.lines(Paths.get(bikesPath))) {
            trips = lines
                    .skip(1)
                    .map(line -> line.split(";"))
                    .limit(maxSize)
                    .collect(Collectors.toMap(
                            data -> count.getAndIncrement(),
                            data -> new Trip(
                                    LocalDateTime.parse(data[0], FORMATTER),
                                    LocalDateTime.parse(data[2], FORMATTER),
                                    Integer.parseInt(data[1]),
                                    Integer.parseInt(data[3]),
                                    Integer.parseInt(data[4])
                            )
                    ));
        } catch (IOException e) {
            logger.error("Error while reading file: {}", e.getMessage());
        }

        tripsIMap.putAll(trips);
    }
}
