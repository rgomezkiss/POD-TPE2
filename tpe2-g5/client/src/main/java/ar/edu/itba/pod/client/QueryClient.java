package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.utils.DataLoader;
import ar.edu.itba.pod.client.utils.QueryParams;
import ar.edu.itba.pod.client.utils.QueryParser;
import ar.edu.itba.pod.client.utils.ResultWriter;
import ar.edu.itba.pod.collators.LongestTripCollator;
import ar.edu.itba.pod.collators.TopAverageDistanceStationsCollator;
import ar.edu.itba.pod.collators.TripsBetweenStationsCollator;
import ar.edu.itba.pod.mappers.LongestTripMapper;
import ar.edu.itba.pod.mappers.TopAverageDistanceStationsMapper;
import ar.edu.itba.pod.mappers.TripsBetweenStationsMapper;
import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;
import ar.edu.itba.pod.reducers.LongestTripReducerFactory;
import ar.edu.itba.pod.reducers.TopAverageDistanceStationsReducerFactory;
import ar.edu.itba.pod.reducers.TripsBetweenStationsReducerFactory;
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

    public static void main(String[] args) {
        logger.info("Client Starting ...");

        logger.info("Argument Parsing ...");
        final QueryParams params = new QueryParser().parse(args);
        logger.info("Finished Argument Parsing");

        final HazelcastInstance hazelcastInstance = getHazelClientInstance(params.getServerAddresses());

        logger.info("Data Parsing ...");
        final Map<Integer, Station> stationMap = DataLoader.readStations(params.getInPath());
        final IList<Trip> tripIList = hazelcastInstance.getList(BIKES_LIST);
        DataLoader.readBikes(params.getInPath(), tripIList);
        logger.info("Finished Data Parsing");

        try {
            switch(params.getQuery()) {
                case 1 -> {
                    final KeyValueSource<String, Trip> keyValueSource = KeyValueSource.fromList(tripIList);
                    final Job<String, Trip> job = hazelcastInstance.getJobTracker(QUERY1).newJob(keyValueSource);

                    final List<Map.Entry<String, Integer>> result = job
                            .mapper(new TripsBetweenStationsMapper(stationMap))
                            .reducer(new TripsBetweenStationsReducerFactory())
                            .submit(new TripsBetweenStationsCollator(stationMap))
                            .get();

//                    ResultWriter.writeResult(params.getOutPath(), "station_a;station_b;trips_between_a_b", result);
                }

                case 2 -> {
                    final KeyValueSource<String, Trip> keyValueSource = KeyValueSource.fromList(tripIList);
                    final Job<String, Trip> job = hazelcastInstance.getJobTracker(QUERY2).newJob(keyValueSource);

                    final List<Map.Entry<String, Double>> result = job
                            .mapper(new TopAverageDistanceStationsMapper(stationMap))
                            .reducer(new TopAverageDistanceStationsReducerFactory())
                            .submit(new TopAverageDistanceStationsCollator(stationMap))
                            .get();

//                    ResultWriter.writeResult(params.getOutPath(), "station;avg_distance\n", result);
                }

                case 3 -> {
                    final KeyValueSource<String, Trip> keyValueSource = KeyValueSource.fromList(tripIList);
                    final Job<String, Trip> job = hazelcastInstance.getJobTracker(QUERY3).newJob(keyValueSource);

                    final List<Map.Entry<String, Pair<LocalDateTime, Integer>>> result = job
                            .mapper(new LongestTripMapper(stationMap))
                            .reducer(new LongestTripReducerFactory())
                            .submit(new LongestTripCollator(stationMap))
                            .get();

//                    ResultWriter.writeResult(params.getOutPath(), "start_station;end_station;start_date;minutes\n", result);
                }

                case 4 -> {
                }
            }
        } catch (Exception e) {
            logger.info("Error");
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
