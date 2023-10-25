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

        @Override
        public void combine(Double distance) {
        }

        @Override
        public Double finalizeChunk() {
            return 0.0;
        }

        @Override
        public void reset() {
        }
    }
}



