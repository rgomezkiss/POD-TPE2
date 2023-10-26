package ar.edu.itba.pod.combiners;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

@SuppressWarnings("deprecation")
public class TopAverageDistanceStationsCombinerFactory implements CombinerFactory<Integer, Double, Double> {

    @Override
    public Combiner<Double, Double> newCombiner(Integer key) {
        return new DistanceCombiner();
    }

    private static class DistanceCombiner extends Combiner<Double, Double> {
        private Double averageDistance = 0.0;
        private int count = 0;

        @Override
        public void combine(Double distance) {
            averageDistance += distance;
            count++;
        }

        @Override
        public Double finalizeChunk() {
            return averageDistance / count;
        }

        @Override
        public void reset() {
            averageDistance = 0.0;
            count = 0;
        }
    }
}



