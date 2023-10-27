package ar.edu.itba.pod.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;

import java.util.Collections;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final String IP = "192.168.1.*";
//    private static final String IP = "10.16.1.*";
    private static final String HZ_CLIENT_NAME = "g5";
    private static final String HZ_CLIENT_PASS = "g5-pass";

    public static void main(String[] args) {
        logger.info("Server Starting ...");
        Config config = new Config();

        GroupConfig groupConfig = new GroupConfig().setName(HZ_CLIENT_NAME).setPassword(HZ_CLIENT_PASS);
        config.setGroupConfig(groupConfig);

        MulticastConfig multicastConfig = new MulticastConfig();
        JoinConfig joinConfig = new JoinConfig().setMulticastConfig(multicastConfig);
        InterfacesConfig interfacesConfig = new InterfacesConfig().setInterfaces(Collections.singletonList(IP)).setEnabled(false);
//        InterfacesConfig interfacesConfig = new InterfacesConfig().setInterfaces(Collections.singletonList(IP)).setEnabled(true);

        NetworkConfig networkConfig = new NetworkConfig().setInterfaces(interfacesConfig).setJoin(joinConfig);
        config.setNetworkConfig(networkConfig);

        Hazelcast.newHazelcastInstance(config);

        logger.info("Server started");
    }
}
