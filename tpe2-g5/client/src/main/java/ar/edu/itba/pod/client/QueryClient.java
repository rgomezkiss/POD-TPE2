package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.utils.*;
import ar.edu.itba.pod.client.writers.*;
import ar.edu.itba.pod.collators.*;
import ar.edu.itba.pod.combiners.*;
import ar.edu.itba.pod.mappers.*;
import ar.edu.itba.pod.models.*;
import ar.edu.itba.pod.reducers.*;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.*;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class QueryClient {
    private static Logger logger;
    private static final String HZ_CLIENT_NAME = "g5";
    private static final String HZ_CLIENT_PASS = "g5-pass";
    private static final String BIKES_MAP = "g5-bikes-map";
    private static final String STATIONS_MAP = "g5-stations-map";
    private static final String QUERY1 = "query1-g5";
    private static final String QUERY2 = "query2-g5";
    private static final String QUERY3 = "query3-g5";
    private static final String QUERY4_1 = "query4-g5-1";
    private static final String QUERY4_2 = "query4-g5-2";
    private static final int MAX_SIZE = 200000;
    private static final boolean WITH_COMBINER = false;

    public static void main(String[] args) {
        final QueryParams params = new QueryParser().parse(args);

        setUpLogger(params.getOutPath() + "/time" + params.getQuery() + ".txt");

        final HazelcastInstance hazelcastInstance = getHazelClientInstance(params.getServerAddresses());

        logger.info("Inicio de la lectura del archivo");

        final IMap<Integer, Station> stationIMap = hazelcastInstance.getMap(STATIONS_MAP);
        stationIMap.clear();
        DataLoader.readStations(params.getInPath(), stationIMap);

        final IMap<Integer, Trip> tripIMap = hazelcastInstance.getMap(BIKES_MAP);
        tripIMap.clear();
        DataLoader.readBikes(params.getInPath(), tripIMap, MAX_SIZE);

        logger.info("Fin de lectura del archivo");

        logger.info("Inicio del trabajo map/reduce");

        try {
            switch(params.getQuery()) {
                case 1 -> {
                    final KeyValueSource<Integer, Trip> keyValueSource = KeyValueSource.fromMap(tripIMap);
                    final Job<Integer, Trip> job = hazelcastInstance.getJobTracker(QUERY1).newJob(keyValueSource);

                    List<Map.Entry<String, Integer>> result;
                    if (WITH_COMBINER) {
                        result = job
                                .mapper(new TripsBetweenStationsMapper(stationIMap))
                                .combiner(new TripsBetweenStationsCombinerFactory())
                                .reducer(new TripsBetweenStationsReducerFactory())
                                .submit(new TripsBetweenStationsCollator(stationIMap))
                                .get();
                    } else {
                       result = job
                                .mapper(new TripsBetweenStationsMapper(stationIMap))
                                .reducer(new TripsBetweenStationsReducerFactory())
                                .submit(new TripsBetweenStationsCollator(stationIMap))
                                .get();
                    }

                    logger.info("Fin del trabajo map/reduce");

                    ResultWriter<String, Integer> query1Writer = new ResultWriter<>();
                    query1Writer.writeResult(params.getOutPath(), result, new Query1Writer());
                }

                case 2 -> {
                    final KeyValueSource<Integer, Trip> keyValueSource = KeyValueSource.fromMap(tripIMap);
                    final Job<Integer, Trip> job = hazelcastInstance.getJobTracker(QUERY2).newJob(keyValueSource);

                    List<Map.Entry<String, Double>> result;

                    if (WITH_COMBINER) {
                        result = job
                                .mapper(new TopAverageDistanceStationsMapper(stationIMap))
                                .combiner(new TopAverageDistanceStationsCombinerFactory())
                                .reducer(new TopAverageDistanceStationsReducerFactory())
                                .submit(new TopAverageDistanceStationsCollator(stationIMap, params.getN()))
                                .get();
                    } else {
                        result = job
                                .mapper(new TopAverageDistanceStationsMapper(stationIMap))
                                .reducer(new TopAverageDistanceStationsReducerFactory())
                                .submit(new TopAverageDistanceStationsCollator(stationIMap, params.getN()))
                                .get();
                    }

                    logger.info("Fin del trabajo map/reduce");

                    ResultWriter<String, Double> query2Writer = new ResultWriter<>();
                    query2Writer.writeResult(params.getOutPath(), result, new Query2Writer());
                }

                case 3 -> {
                    final KeyValueSource<Integer, Trip> keyValueSource = KeyValueSource.fromMap(tripIMap);
                    final Job<Integer, Trip> job = hazelcastInstance.getJobTracker(QUERY3).newJob(keyValueSource);

                    List<Map.Entry<String, Pair<LocalDateTime, Integer>>> result;
                    if (WITH_COMBINER) {
                        result = job
                                .mapper(new LongestTripMapper(stationIMap))
                                .combiner(new LongestTripCombinerFactory())
                                .reducer(new LongestTripReducerFactory())
                                .submit(new LongestTripCollator(stationIMap))
                                .get();
                    } else {
                        result = job
                                .mapper(new LongestTripMapper(stationIMap))
                                .reducer(new LongestTripReducerFactory())
                                .submit(new LongestTripCollator(stationIMap))
                                .get();
                    }

                    logger.info("Fin del trabajo map/reduce");

                    ResultWriter<String, Pair<LocalDateTime, Integer>> query3Writer = new ResultWriter<>();
                    query3Writer.writeResult(params.getOutPath(), result, new Query3Writer());

                }

                case 4 -> {
                    final KeyValueSource<Integer, Trip> firstKeyValueSource = KeyValueSource.fromMap(tripIMap);
                    final Job<Integer, Trip> firstJob = hazelcastInstance.getJobTracker(QUERY4_1).newJob(firstKeyValueSource);

                    final KeyValueSource<Pair<Integer, LocalDate>, Long> secondKeyValueSource;
                    final Job<Pair<Integer, LocalDate>, Long> secondJob;

                    IMap<Pair<Integer, LocalDate>, Long> temporaryIMap = hazelcastInstance.getMap("g5-temp-map");
                    temporaryIMap.clear();
                    List<Map.Entry<String, List<Long>>> result;

                    if (WITH_COMBINER) {
                        temporaryIMap.putAll(firstJob
                                .mapper(new NetAffluenceMapper(stationIMap, params.getStartDate(), params.getEndDate()))
                                .combiner(new NetAffluenceCombinerFactory())
                                .reducer(new NetAffluenceReducerFactory()).submit().get());

                        secondKeyValueSource = KeyValueSource.fromMap(temporaryIMap);
                        secondJob = hazelcastInstance.getJobTracker(QUERY4_2).newJob(secondKeyValueSource);

                        result = secondJob
                                .mapper(new NetAffluenceListMapper())
                                .reducer(new NetAffluenceListReducerFactory())
                                .submit(new NetAffluenceCollator(stationIMap, params.getStartDate(), params.getEndDate()))
                                .get();
                    } else {
                        temporaryIMap.putAll(firstJob
                                .mapper(new NetAffluenceMapper(stationIMap, params.getStartDate(), params.getEndDate()))
                                .reducer(new NetAffluenceReducerFactory()).submit().get());

                        secondKeyValueSource = KeyValueSource.fromMap(temporaryIMap);
                        secondJob = hazelcastInstance.getJobTracker(QUERY4_2).newJob(secondKeyValueSource);

                        result = secondJob
                                .mapper(new NetAffluenceListMapper())
                                .reducer(new NetAffluenceListReducerFactory())
                                .submit(new NetAffluenceCollator(stationIMap, params.getStartDate(), params.getEndDate()))
                                .get();
                    }
                    logger.info("Fin del trabajo map/reduce");
                    temporaryIMap.clear();

                    ResultWriter<String, List<Long>> query4Writer = new ResultWriter<>();
                    query4Writer.writeResult(params.getOutPath(), result, new Query4Writer());
                }
            }
        } catch (Exception e) {
            //TODO: start adding validations
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            tripIMap.clear();
            stationIMap.clear();
            hazelcastInstance.getDistributedObjects().forEach(DistributedObject::destroy);
            HazelcastClient.shutdownAll();
        }
    }

    private static void setUpLogger(String pathname) {
        System.out.println(pathname);
        System.setProperty("pathname", pathname);
        logger = LoggerFactory.getLogger(QueryClient.class);
    }

    private static HazelcastInstance getHazelClientInstance(final List<String> addresses) {
        final ClientConfig clientConfig = new ClientConfig();

        final GroupConfig groupConfig = new GroupConfig().setName(HZ_CLIENT_NAME).setPassword(HZ_CLIENT_PASS);
        clientConfig.setGroupConfig(groupConfig);

        final ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig().setAddresses(addresses);
        clientConfig.setNetworkConfig(clientNetworkConfig);

        return HazelcastClient.newHazelcastClient(clientConfig);
    }

}
