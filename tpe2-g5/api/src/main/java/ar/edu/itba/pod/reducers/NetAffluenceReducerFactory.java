package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.time.LocalDateTime;

@SuppressWarnings("deprecation")
public class NetAffluenceReducerFactory implements ReducerFactory<Pair<Integer, LocalDateTime>, Long, Long> {

    @Override
    public Reducer<Long, Long> newReducer(Pair<Integer, LocalDateTime> integerLocalDateTimePair) {
        return new QueryReducer();
    }

    private static class QueryReducer extends Reducer<Long, Long> {
        private Long netAffluence;

        @Override
        public void beginReduce() {
            netAffluence = 0L;
        }

        @Override
        public void reduce(Long value) {
            netAffluence += value;
        }

        @Override
        public Long finalizeReduce() {
            return netAffluence;
        }
    }
}
