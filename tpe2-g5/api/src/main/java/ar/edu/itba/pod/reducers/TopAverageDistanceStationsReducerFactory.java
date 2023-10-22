package ar.edu.itba.pod.reducers;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

@SuppressWarnings("deprecation")
public class TopAverageDistanceStationsReducerFactory implements ReducerFactory<Integer, Double, Double> {

    @Override
    public Reducer<Double, Double> newReducer(Integer integer) {
        return new QueryReducer();
    }

    private static class QueryReducer extends Reducer<Double, Double> {

        private Double averageDistance;
        private int count;

        @Override
        public void beginReduce() {
            averageDistance = 0.0;
            count = 0;
        }

        @Override
        public void reduce(Double distance) {
            averageDistance += distance;
            count++;
        }

        @Override
        public Double finalizeReduce() {
            return averageDistance / count;
        }


    }
}
