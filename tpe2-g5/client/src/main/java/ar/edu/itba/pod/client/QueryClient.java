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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class QueryClient {
    private static Logger logger;
    private static final String HZ_CLIENT_NAME = "g5";
    private static final String HZ_CLIENT_PASS = "g5-pass";
    private static final String BIKES_LIST = "g5-bikes-map";
    private static final String QUERY1 = "query1-g5";
    private static final String QUERY2 = "query2-g5";
    private static final String QUERY3 = "query3-g5";
    private static final String QUERY4 = "query4-g5";

    private static final int MAX_SIZE = 100000;

//    private static final boolean WITH_COMBINER = true;
    private static final boolean WITH_COMBINER = false;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    public static void main(String[] args) {
        final QueryParams params = new QueryParser().parse(args);

        setUpLogger(params.getOutPath() + "time" + params.getQuery() + ".txt");

        final HazelcastInstance hazelcastInstance = getHazelClientInstance(params.getServerAddresses());

        logger.info("Inicio de la lectura del archivo");

        // Todo: change distributed
        final List<Station> stations = DataLoader.readStations(params.getInPath());


        final IMap<Integer, Trip> tripIMap = hazelcastInstance.getMap(BIKES_LIST);
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
                                .mapper(new TripsBetweenStationsMapper(stations))
                                .combiner(new TripsBetweenStationsCombinerFactory())
                                .reducer(new TripsBetweenStationsReducerFactory())
                                .submit(new TripsBetweenStationsCollator(stations))
                                .get();
                    } else {
                       result = job
                                .mapper(new TripsBetweenStationsMapper(stations))
                                .reducer(new TripsBetweenStationsReducerFactory())
                                .submit(new TripsBetweenStationsCollator(stations))
                                .get();
                    }

                    ResultWriter<String, Integer> query1Writer = new ResultWriter<>();
                    query1Writer.writeResult(params.getOutPath(), result, new Query1Writer());
                }

                case 2 -> {
                    final KeyValueSource<Integer, Trip> keyValueSource = KeyValueSource.fromMap(tripIMap);
                    final Job<Integer, Trip> job = hazelcastInstance.getJobTracker(QUERY2).newJob(keyValueSource);

                    List<Map.Entry<String, Double>> result;

                    if (WITH_COMBINER) {
                        result = job
                                .mapper(new TopAverageDistanceStationsMapper(stations))
                                .combiner(new TopAverageDistanceStationsCombinerFactory())
                                .reducer(new TopAverageDistanceStationsReducerFactory())
                                .submit(new TopAverageDistanceStationsCollator(stations, params.getN()))
                                .get();
                    } else {
                        result = job
                                .mapper(new TopAverageDistanceStationsMapper(stations))
                                .reducer(new TopAverageDistanceStationsReducerFactory())
                                .submit(new TopAverageDistanceStationsCollator(stations, params.getN()))
                                .get();
                    }

                    ResultWriter<String, Double> query2Writer = new ResultWriter<>();
                    query2Writer.writeResult(params.getOutPath(), result, new Query2Writer());
                }

                case 3 -> {
                    final KeyValueSource<Integer, Trip> keyValueSource = KeyValueSource.fromMap(tripIMap);
                    final Job<Integer, Trip> job = hazelcastInstance.getJobTracker(QUERY3).newJob(keyValueSource);

                    List<Map.Entry<String, Pair<LocalDateTime, Integer>>> result;
                    if (WITH_COMBINER) {
                        result = job
                                .mapper(new LongestTripMapper(stations))
                                .combiner(new LongestTripCombinerFactory())
                                .reducer(new LongestTripReducerFactory())
                                .submit(new LongestTripCollator(stations))
                                .get();
                    } else {
                        result = job
                                .mapper(new LongestTripMapper(stations))
                                .reducer(new LongestTripReducerFactory())
                                .submit(new LongestTripCollator(stations))
                                .get();
                    }

                    ResultWriter<String, Pair<LocalDateTime, Integer>> query3Writer = new ResultWriter<>();
                    query3Writer.writeResult(params.getOutPath(), result, new Query3Writer());

                }

                case 4 -> {
                    final KeyValueSource<Integer, Trip> keyValueSource = KeyValueSource.fromMap(tripIMap);
                    final Job<Integer, Trip> job = hazelcastInstance.getJobTracker(QUERY4).newJob(keyValueSource);

                    List<Map.Entry<String, List<Long>>> result;
                    if (WITH_COMBINER) {
                        result = job
                                .mapper(new NetAffluenceMapper(stations, params.getStartDate(), params.getEndDate()))
//                                .combiner(new NetAffluenceCombinerFactory())
                                .reducer(new NetAffluenceReducerFactory())
                                .submit(new NetAffluenceCollator(stations, params.getStartDate(), params.getEndDate()))
                                .get();
                    } else {
                        result = job
                                .mapper(new NetAffluenceMapper(stations, params.getStartDate(), params.getEndDate()))
                                .reducer(new NetAffluenceReducerFactory())
                                .submit(new NetAffluenceCollator(stations, params.getStartDate(), params.getEndDate()))
                                .get();
                    }

                    ResultWriter<String, List<Long>> query4Writer = new ResultWriter<>();
                    query4Writer.writeResult(params.getOutPath(), result, new Query4Writer());
                }
            }
        } catch (Exception e) {
            //TODO: start adding validations
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.info("Error");
        } finally {
            logger.info("Fin del trabajo map/reduce");
            tripIMap.clear();
            stations.clear();
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
