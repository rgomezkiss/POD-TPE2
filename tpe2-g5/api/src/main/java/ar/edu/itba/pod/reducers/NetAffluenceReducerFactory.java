package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings("deprecation")
public class NetAffluenceReducerFactory implements ReducerFactory<String, Pair<Long, LocalDateTime>, List<Long>> {

    @Override
    public Reducer<Pair<Long, LocalDateTime>, List<Long>> newReducer(String s) {
        return new QueryReducer();
    }

    private static class QueryReducer extends Reducer<Pair<Long, LocalDateTime>, List<Long>> {
        private List<Long> netAffluenceList;
        private Map<LocalDate, Long> affluenceMapPerDay;

        @Override
        public void beginReduce() {
            netAffluenceList = new ArrayList<>();
            affluenceMapPerDay = new HashMap<>();
        }

        @Override
        public void reduce(Pair<Long, LocalDateTime> pair) {
            LocalDate date = pair.getOther().toLocalDate();
            affluenceMapPerDay.putIfAbsent(date, 0L);
            affluenceMapPerDay.merge(date, pair.getOne(), Long::sum);
        }

        @Override
        public List<Long> finalizeReduce() {
            long positiveAffluence = 0;
            long neutralAffluence = 0;
            long negativeAffluence = 0;

            for (Long affluence : affluenceMapPerDay.values()) {
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
