//package ar.edu.itba.pod.client.jobs;
//
//import ar.edu.itba.pod.client.utils.QueryParams;
//import ar.edu.itba.pod.client.writers.*;
//import ar.edu.itba.pod.collators.LongestTripCollator;
//import ar.edu.itba.pod.collators.NetAffluenceCollator;
//import ar.edu.itba.pod.collators.TopAverageDistanceStationsCollator;
//import ar.edu.itba.pod.collators.TripsBetweenStationsCollator;
//import ar.edu.itba.pod.combiners.LongestTripCombinerFactory;
//import ar.edu.itba.pod.combiners.NetAffluenceCombinerFactory;
//import ar.edu.itba.pod.combiners.TopAverageDistanceStationsCombinerFactory;
//import ar.edu.itba.pod.combiners.TripsBetweenStationsCombinerFactory;
//import ar.edu.itba.pod.mappers.LongestTripMapper;
//import ar.edu.itba.pod.mappers.NetAffluenceMapper;
//import ar.edu.itba.pod.mappers.TopAverageDistanceStationsMapper;
//import ar.edu.itba.pod.mappers.TripsBetweenStationsMapper;
//import ar.edu.itba.pod.models.Station;
//import ar.edu.itba.pod.models.Trip;
//import ar.edu.itba.pod.reducers.LongestTripReducerFactory;
//import ar.edu.itba.pod.reducers.NetAffluenceReducerFactory;
//import ar.edu.itba.pod.reducers.TopAverageDistanceStationsReducerFactory;
//import ar.edu.itba.pod.reducers.TripsBetweenStationsReducerFactory;
//import com.hazelcast.client.HazelcastClient;
//import com.hazelcast.core.DistributedObject;
//import com.hazelcast.core.IMap;
//import com.hazelcast.mapreduce.*;
//
//import java.util.List;
//import java.util.Map;
//
//@SuppressWarnings("deprecation")
//public class JobInfo {
//
//    public static Mapper<Integer, Trip, ?, ?> getMapperForQuery(int query, IMap<Integer, Station> stationIMap, QueryParams params) {
//        return switch (query) {
//            case 1 -> new TripsBetweenStationsMapper(stationIMap);
//            case 2 -> new TopAverageDistanceStationsMapper(stationIMap);
//            case 3 -> new LongestTripMapper(stationIMap);
//            case 4 -> new NetAffluenceMapper(stationIMap, params.getStartDate(), params.getEndDate());
//            default -> throw new IllegalArgumentException("Consulta no válida: " + query);
//        };
//    }
//
//    public static CombinerFactory<?, ?, ?> getCombinerForQuery(int query) {
//        return switch (query) {
//            case 1 -> new TripsBetweenStationsCombinerFactory();
//            case 2 -> new TopAverageDistanceStationsCombinerFactory();
//            case 3 -> new LongestTripCombinerFactory();
//            case 4 -> new NetAffluenceCombinerFactory();
//            default -> throw new IllegalArgumentException("Consulta no válida: " + query);
//        };
//    }
//
//    public static ReducerFactory<?, ?, ?> getReducerForQuery(int query) {
//        return switch (query) {
//            case 1 -> new TripsBetweenStationsReducerFactory();
//            case 2 -> new TopAverageDistanceStationsReducerFactory();
//            case 3 -> new LongestTripReducerFactory();
//            case 4 -> new NetAffluenceReducerFactory():
//            default -> throw new IllegalArgumentException("Consulta no válida: " + query);
//        };
//    }
//
//    public static Collator<Map.Entry<?, ?>, List<Map.Entry<?, ?>>> getCollatorForQuery(int query, IMap<Integer, Station> stationIMap, QueryParams params) {
//        return switch (query) {
//            case 1 -> new TripsBetweenStationsCollator(stationIMap);
//            case 2 -> new TopAverageDistanceStationsCollator(stationIMap, params.getN());
//            case 3 -> new LongestTripCollator(stationIMap);
//            case 4 -> new NetAffluenceCollator(stationIMap, params.getStartDate(), params.getEndDate());
//            default -> throw new IllegalArgumentException("Consulta no válida: " + query);
//        };
//    }
//
//    public static ResultWriter<String, ?> getWriterForQuery(int query) {
//        return switch (query) {
//            case 1, 2, 3, 4 -> new ResultWriter<>();
//            default -> throw new IllegalArgumentException("Consulta no válida: " + query);
//        };
//    }
//
//    public static DataFormatter<String, ?> getQueryWriterForQuery(int query) {
//        return switch (query) {
//            case 1 -> new Query1Writer();
//            case 2 -> new Query2Writer();
//            case 3 -> new Query3Writer();
//            case 4 -> new Query4Writer();
//            default -> throw new IllegalArgumentException("Consulta no válida: " + query);
//        };
//    }
//
//    public static String getQueryTracker(int query){
//        return switch (query) {
//            case 1 -> "query1-g5";
//            case 2 -> "query2-g5";
//            case 3 -> "query3-g5";
//            case 4 -> "query4-g5";
//            default -> throw new IllegalArgumentException("Consulta no válida: " + query);
//        };
//    }
//
//
//    try {
//        final KeyValueSource<Integer, Trip> keyValueSource = KeyValueSource.fromMap(tripIMap);
//        final Job<Integer, Trip> job = hazelcastInstance.getJobTracker(JobInfo.getQueryTracker(params.getQuery())).newJob(keyValueSource);
//
//        List<?> result;
//        if (WITH_COMBINER) {
//            result = job
//                    .mapper(JobInfo.getMapperForQuery(params.getQuery(), stationIMap, params))
//                    .combiner(JobInfo.getCombinerForQuery(params.getQuery()))
//                    .reducer(JobInfo.getReducerForQuery(params.getQuery()))
//                    .submit(JobInfo.getCollatorForQuery(params.getQuery(), stationIMap, params))
//                    .get();
//        } else {
//            result = job
//                    .mapper(JobInfo.getMapperForQuery(params.getQuery(), stationIMap, params))
//                    .reducer(JobInfo.getReducerForQuery(params.getQuery()))
//                    .submit(JobInfo.getCollatorForQuery(params.getQuery(), stationIMap, params))
//                    .get();
//        }
//
//        ResultWriter<?, ?> writer = JobInfo.getWriterForQuery(params.getQuery());
//        writer.writeResult(params.getOutPath(), result, JobInfo.getQueryWriterForQuery(params.getQuery()));
//
//    } catch (Exception e) {
//        // Manejar otras excepciones generales
//        logger.error(e.getMessage());
//        e.printStackTrace();
//    } finally {
//        tripIMap.clear();
//        stationIMap.clear();
//        hazelcastInstance.getDistributedObjects().forEach(DistributedObject::destroy);
//        HazelcastClient.shutdownAll();
//    }
//
//}
