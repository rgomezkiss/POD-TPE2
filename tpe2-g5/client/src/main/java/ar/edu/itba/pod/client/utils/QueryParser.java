package ar.edu.itba.pod.client.utils;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class QueryParser implements Parser<QueryParams> {

    private final Logger logger = LoggerFactory.getLogger(QueryParser.class);
    private final CommandLineParser parser = new DefaultParser();
    private final Options options = new Options();
    private final static String SERVER_ADDRESSES = "Daddresses";
    private final static String IN_PATH = "DinPath";
    private final static String OUT_PATH = "DoutPath";
    private final static String START_DATE = "DstartDate";
    private final static String END_DATE = "DendDate";
    private final static String N = "Dn";
    private final static String QUERY = "Dquery";

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    public QueryParser() {
        options.addRequiredOption(SERVER_ADDRESSES, SERVER_ADDRESSES, true, "Addresses");
        options.addRequiredOption(IN_PATH, IN_PATH, true, "Input file path");
        options.addRequiredOption(OUT_PATH, OUT_PATH, true, "Output file path");
        options.addOption(START_DATE, START_DATE, true, "Start date");
        options.addOption(END_DATE, END_DATE, true, "End date");
        options.addOption(N, N, true, "N");
        options.addOption(QUERY, QUERY, true, "Query requested");
    }

    // -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -DinPath=filename | -Dride=rideName | -Dday=dayOfYear | -Dcapacity=amount ]
    @Override
    public QueryParams parse(String[] args) {
        try {
            final CommandLine cmd = parser.parse(options, args);

            //TODO: check how to do multiple
            final String[] addresses = null;

            String inPath = cmd.getOptionValue(IN_PATH);
            String outPath = cmd.getOptionValue(OUT_PATH);

            int queryRequested = Integer.valueOf(cmd.getOptionValue(QUERY));

            switch(queryRequested) {
                case 1,3 -> {
                    return new QueryParams(addresses, inPath, outPath, queryRequested);
                }

                case 2 -> {
                    int numberN = Integer.valueOf(cmd.getOptionValue(N));
                    return new QueryParams(addresses, inPath, outPath, numberN, queryRequested);
                }

                case 4 -> {
                    LocalDateTime startTime = LocalDateTime.parse(cmd.getOptionValue(START_DATE), formatter);
                    LocalDateTime endTime = LocalDateTime.parse(cmd.getOptionValue(END_DATE), formatter);
                    return new QueryParams(addresses, inPath, outPath, startTime, endTime, queryRequested);
                }
            }

        } catch (ParseException e) {
            System.out.println("Error parsing command line arguments");
            logger.error("Error parsing command line arguments");
            return null;
        }
        return null;
    }
}