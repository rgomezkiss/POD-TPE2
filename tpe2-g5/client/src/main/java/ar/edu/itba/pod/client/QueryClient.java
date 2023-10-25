package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.utils.*;
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
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class QueryClient {
    private static final Logger logger = LoggerFactory.getLogger(QueryClient.class);
    private static final String HZ_CLIENT_NAME = "g5";
    private static final String HZ_CLIENT_PASS = "g5-pass";
    private static final String BIKES_LIST = "g5-bikes-list";
    private static final String QUERY1 = "query1-g5";
    private static final String QUERY2 = "query2-g5";
    private static final String QUERY3 = "query3-g5";
    private static final String QUERY4 = "query4-g5";
    private static final boolean WITH_COMBINER = false;

    public static void main(String[] args) {
        logger.info("Client Starting ...");

        logger.info("Argument Parsing ...");
        final QueryParams params = new QueryParser().parse(args);
        logger.info("Finished Argument Parsing");

        final HazelcastInstance hazelcastInstance = getHazelClientInstance(params.getServerAddresses());

        logger.info("Data Parsing ...");
        final List<Station> stations = DataLoader.readStations(params.getInPath());
        final IList<Trip> tripIList = hazelcastInstance.getList(BIKES_LIST);
        tripIList.clear();
        DataLoader.readBikes(params.getInPath(), tripIList);
        logger.info("Finished Data Parsing");

        try {
            switch(params.getQuery()) {
                case 1 -> {
                    final KeyValueSource<String, Trip> keyValueSource = KeyValueSource.fromList(tripIList);
                    final Job<String, Trip> job = hazelcastInstance.getJobTracker(QUERY1).newJob(keyValueSource);

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

                    ResultWriter.writeQuery1Result(params.getOutPath() , result);
                }

                case 2 -> {
                    final KeyValueSource<String, Trip> keyValueSource = KeyValueSource.fromList(tripIList);
                    final Job<String, Trip> job = hazelcastInstance.getJobTracker(QUERY2).newJob(keyValueSource);

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

                    ResultWriter.writeQuery2Result(params.getOutPath(), result);
                }

                case 3 -> {
                    final KeyValueSource<String, Trip> keyValueSource = KeyValueSource.fromList(tripIList);
                    final Job<String, Trip> job = hazelcastInstance.getJobTracker(QUERY3).newJob(keyValueSource);

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

                    ResultWriter.writeQuery3Result(params.getOutPath(), result);

                }

                case 4 -> {
                    final KeyValueSource<String, Trip> keyValueSource = KeyValueSource.fromList(tripIList);
                    final Job<String, Trip> job = hazelcastInstance.getJobTracker(QUERY4).newJob(keyValueSource);

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

                    ResultWriter.writeQuery4Result(params.getOutPath(), result);
                }
            }
        } catch (Exception e) {
            //TODO: start adding validations
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.info("Error");
        } finally {
            tripIList.clear();
            stations.clear();
            hazelcastInstance.getDistributedObjects().forEach(DistributedObject::destroy);
            HazelcastClient.shutdownAll();
            logger.info("Finished query");
        }
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
