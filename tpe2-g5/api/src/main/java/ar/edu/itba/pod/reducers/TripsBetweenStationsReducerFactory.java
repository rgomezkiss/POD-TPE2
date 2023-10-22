package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

@SuppressWarnings("deprecation")
public class TripsBetweenStationsReducerFactory implements ReducerFactory<Pair<Integer, Integer>, Integer, Integer> {
    @Override
    public Reducer<Integer, Integer> newReducer(Pair<Integer, Integer> pair) {
        return new QueryReducer();
    }

    private static class QueryReducer extends Reducer<Integer, Integer> {
        private Integer tripsBetweenStations;

        @Override
        public void beginReduce() {
            tripsBetweenStations = 0;
        }

        @Override
        public void reduce(Integer integer) {
            tripsBetweenStations += integer;
        }

        @Override
        public Integer finalizeReduce() {
            return tripsBetweenStations;
        }
    }
}
