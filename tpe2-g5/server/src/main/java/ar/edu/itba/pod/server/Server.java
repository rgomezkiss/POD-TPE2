package ar.edu.itba.pod.server;

import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;

import java.io.IOException;
import java.lang.invoke.MutableCallSite;
import java.util.Collections;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        logger.info("Server Starting ...");
        Config config = new Config();

        GroupConfig groupConfig = new GroupConfig().setName("g5").setPassword("g5-pass");
        config.setGroupConfig(groupConfig);

        MulticastConfig multicastConfig = new MulticastConfig();

        JoinConfig joinConfig = new JoinConfig().setMulticastConfig(multicastConfig);

        InterfacesConfig interfacesConfig = new InterfacesConfig()
                .setInterfaces(Collections.singletonList("192.168.1.*"))
                .setEnabled(false);

        NetworkConfig networkConfig = new NetworkConfig()
                .setInterfaces(interfacesConfig)
                .setJoin(joinConfig);
        config.setNetworkConfig(networkConfig);

//        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig()
//                .setUrl("http://localhost:8080/mancenter/")
//                .setEnabled(true);
//        config.setManagementCenterConfig(managementCenterConfig);

        Hazelcast.newHazelcastInstance(config);
    }
}