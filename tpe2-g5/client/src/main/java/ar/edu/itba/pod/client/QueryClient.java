package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.utils.DataLoader;
import ar.edu.itba.pod.client.utils.QueryParams;
import ar.edu.itba.pod.client.utils.QueryParser;
import ar.edu.itba.pod.client.utils.ResultWriter;
import ar.edu.itba.pod.collators.LongestTripCollator;
import ar.edu.itba.pod.collators.TripsBetweenStationsCollator;
import ar.edu.itba.pod.mappers.LongestTripMapper;
import ar.edu.itba.pod.mappers.TripsBetweenStationsMapper;
import ar.edu.itba.pod.models.Pair;
import ar.edu.itba.pod.models.Station;
import ar.edu.itba.pod.models.Trip;
import ar.edu.itba.pod.reducers.LongestTripReducerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(QueryClient.class);

    public static void main(String[] args) throws Exception {
        logger.info("Client Starting ...");

        logger.info("Argument Parsing ...");
        QueryParams params = new QueryParser().parse(args);
        logger.info("Finished Argument Parsing");

        HazelcastInstance hazelcastInstance = getHazelClientInstance(params.getServerAddresses());

        logger.info("Data Parsing ...");

        Map<Integer, Station> stationMap = DataLoader.readStations(params.getInPath());

        IList<Trip> tripIList = hazelcastInstance.getList("g5-bikes-list");
        DataLoader.readBikes(params.getInPath(), tripIList);

        logger.info("Finished Data Parsing");


        try {
            switch(params.getQuery()) {
                case 1 -> {
                    KeyValueSource<String, Trip> keyValueSource = KeyValueSource.fromList(tripIList);
                    Job<String, Trip> job = hazelcastInstance.getJobTracker("query1-g5").newJob(keyValueSource);

                    List<Map.Entry<String, Integer>> result = job
                            .mapper(new TripsBetweenStationsMapper(stationMap))
                            .reducer(new TripsBetweenStationsReducerFactory())
                            .submit(new TripsBetweenStationsCollator(stationMap))
                            .get();

//                    ResultWriter.writeResult(params.getOutPath(), "station_a;station_b;trips_between_a_b", result);
                }

                case 2 -> {
                }

                case 3 -> {
                    KeyValueSource<String, Trip> keyValueSource = KeyValueSource.fromList(tripIList);
                    Job<String, Trip> job = hazelcastInstance.getJobTracker("query1-g5").newJob(keyValueSource);

                    List<Map.Entry<String, Pair<LocalDateTime, Integer>>> result = job
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

    private static HazelcastInstance getHazelClientInstance(List<String> addresses) {
        String name = "g5";
        String pass = "g5-pass";

        ClientConfig clientConfig = new ClientConfig();

        GroupConfig groupConfig = new GroupConfig().setName(name).setPassword(pass);
        clientConfig.setGroupConfig(groupConfig);

        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig().setAddresses(addresses);
        clientConfig.setNetworkConfig(clientNetworkConfig);

        return HazelcastClient.newHazelcastClient(clientConfig);
    }

}
