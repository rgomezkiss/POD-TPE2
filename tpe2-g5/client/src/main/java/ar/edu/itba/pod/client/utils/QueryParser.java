package ar.edu.itba.pod.client.utils;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryParser implements Parser<QueryParams> {

//    private final Logger logger = LoggerFactory.getLogger(QueryParser.class);
    private final CommandLineParser parser = new DefaultParser();
    private final Options options = new Options();
    private final static String SERVER_ADDRESSES = "Daddresses";
    private final static String IN_PATH = "DinPath";
    private final static String OUT_PATH = "DoutPath";
    private final static String START_DATE = "DstartDate";
    private final static String END_DATE = "DendDate";
    private final static String N = "Dn";
    private final static String QUERY = "Dquery";
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QueryParser() {
        options.addRequiredOption(SERVER_ADDRESSES, SERVER_ADDRESSES, true, "Addresses");
        options.addRequiredOption(IN_PATH, IN_PATH, true, "Input file path");
        options.addRequiredOption(OUT_PATH, OUT_PATH, true, "Output file path");
        options.addOption(START_DATE, START_DATE, true, "Start date");
        options.addOption(END_DATE, END_DATE, true, "End date");
        options.addOption(N, N, true, "N");
        options.addRequiredOption(QUERY, QUERY, true, "Query requested");
    }

    // -Daddresses='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' -DinPath=XX -DoutPath=YY -Dn=n -DstartDate=... -DendDate=... -git Dquery=x
    @Override
    public QueryParams parse(String[] args) {
        try {
            final CommandLine cmd = parser.parse(options, args);

            final String[] addressArray = cmd.getOptionValue(SERVER_ADDRESSES).split(";");
            final List<String> addresses = new ArrayList<>(Arrays.asList(addressArray));

            final String inPath = cmd.getOptionValue(IN_PATH);
            final String outPath = cmd.getOptionValue(OUT_PATH);

            int queryRequested = Integer.parseInt(cmd.getOptionValue(QUERY));

            switch(queryRequested) {
                case 1,3 -> {
                    return new QueryParams(addresses, inPath, outPath, queryRequested);
                }

                case 2 -> {
                    final int numberN = Integer.parseInt(cmd.getOptionValue(N));
                    return new QueryParams(addresses, inPath, outPath, numberN, queryRequested);
                }

                case 4 -> {
                    final LocalDate startTime = LocalDate.parse(cmd.getOptionValue(START_DATE), FORMATTER);
                    final LocalDate endTime = LocalDate.parse(cmd.getOptionValue(END_DATE), FORMATTER);
                    return new QueryParams(addresses, inPath, outPath, startTime, endTime, queryRequested);
                }
            }

        } catch (ParseException e) {
            System.out.println("Error parsing command line arguments");
//            logger.error("Error parsing command line arguments");
            return null;
        }
        return null;
    }
}