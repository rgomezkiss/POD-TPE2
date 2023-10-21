package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.utils.QueryParams;
import ar.edu.itba.pod.client.utils.QueryParser;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.*;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;

@SuppressWarnings("deprecation")
public class QueryClient {
    private static Logger logger = LoggerFactory.getLogger(QueryClient.class);

    public static void main(String[] args) throws Exception {
        logger.info("Client Starting ...");

        ClientConfig clientConfig = new ClientConfig();

        GroupConfig groupConfig = new GroupConfig().setName("g5").setPassword("g5-pass");

        clientConfig.setGroupConfig(groupConfig);

        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();

        QueryParams params = new QueryParser().parse(args);

        try {
            switch(params.getQuery()) {
                case 1 -> {
                }

                case 2 -> {
                }

                case 3 -> {
                }

                case 4 -> {
                }
            }
        } catch (Exception e) {
            logger.info("Error");
        }
    }

}
