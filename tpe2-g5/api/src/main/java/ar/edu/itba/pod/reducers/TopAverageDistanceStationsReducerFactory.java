package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

@SuppressWarnings("deprecation")
public class TopAverageDistanceStationsReducerFactory implements ReducerFactory<Integer, Pair<Integer, Double>, Double> {

    @Override
    public Reducer<Pair<Integer, Double>, Double> newReducer(Integer integer) {
        return new QueryReducer();
    }

    private static class QueryReducer extends Reducer<Pair<Integer, Double>, Double> {
        private Double averageDistance;
        private int count;

        @Override
        public void beginReduce() {
            count = 0;
            averageDistance = 0.0;
        }

        @Override
        public void reduce(Pair<Integer, Double> pair) {
            count += pair.getOne();
            averageDistance += pair.getOther();
        }

        @Override
        public Double finalizeReduce() {
            return averageDistance / count;
        }

    }
}
