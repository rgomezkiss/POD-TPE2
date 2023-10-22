package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.time.LocalDateTime;

@SuppressWarnings("deprecation")
public class LongestTripReducerFactory implements ReducerFactory<
        Pair<Integer, Integer>, Pair<LocalDateTime, Integer>, Pair<LocalDateTime, Integer>> {

    @Override
    public Reducer<Pair<LocalDateTime, Integer>, Pair<LocalDateTime, Integer>> newReducer(Pair<Integer, Integer> integerIntegerPair) {
        return new QueryReducer();
    }

    private static class QueryReducer extends Reducer<Pair<LocalDateTime, Integer>, Pair<LocalDateTime, Integer>> {
        private Pair<LocalDateTime, Integer> longestTrip;

        @Override
        public void beginReduce() {
            longestTrip = null;
        }

        @Override
        public void reduce(Pair<LocalDateTime, Integer> pair) {
            int cmp = pair.getOther().compareTo(longestTrip.getOther());
            if (longestTrip == null || cmp < 0) {
                longestTrip = pair;
            } else if (cmp == 0) {
                if (longestTrip.getOne().isAfter(pair.getOne())) {
                    longestTrip = pair;
                }
            }
        }

        @Override
        public Pair<LocalDateTime, Integer> finalizeReduce() {
            return longestTrip;
        }
    }
}
