package ar.edu.itba.pod.reducers;


import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.net.Inet4Address;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings("deprecation")
public class NetAffluenceReducerFactory implements ReducerFactory<String, Pair<Integer, LocalDateTime>, List<Integer>> {

    @Override
    public Reducer<Pair<Integer, LocalDateTime>, List<Integer>> newReducer(String s) {
        return new QueryReducer();
    }

    private static class QueryReducer extends Reducer<Pair<Integer, LocalDateTime>, List<Integer>> {
        private List<Integer> netAffluenceList;
        private Map<LocalDate, Integer> affluenceMapPerDay;

        @Override
        public void beginReduce() {
            netAffluenceList = new ArrayList<>();
            affluenceMapPerDay = new HashMap<>();
        }

        @Override
        public void reduce(Pair<Integer, LocalDateTime> pair) {
            LocalDate date = pair.getOther().toLocalDate();
            affluenceMapPerDay.putIfAbsent(date, 0);
            affluenceMapPerDay.merge(date, pair.getOne(), Integer::sum);
        }

        @Override
        public List<Integer> finalizeReduce() {
            int positiveAffluence = 0;
            int neutralAffluence = 0;
            int negativeAffluence = 0;

            for (Integer affluence : affluenceMapPerDay.values()) {
                if (affluence > 0) {
                    positiveAffluence++;
                } else if (affluence < 0) {
                    negativeAffluence++;
                } else {
                    neutralAffluence++;
                }
            }

            netAffluenceList.add(positiveAffluence);
            netAffluenceList.add(neutralAffluence);
            netAffluenceList.add(negativeAffluence);
            return netAffluenceList;
        }
    }
}
