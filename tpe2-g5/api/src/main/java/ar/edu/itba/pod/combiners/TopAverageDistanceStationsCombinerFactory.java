package ar.edu.itba.pod.combiners;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

@SuppressWarnings("deprecation")
public class TopAverageDistanceStationsCombinerFactory implements CombinerFactory<Integer, Pair<Integer, Double>, Pair<Integer, Double>> {

    @Override
    public Combiner<Pair<Integer, Double>, Pair<Integer, Double>> newCombiner(Integer key) {
        return new DistanceCombiner();
    }

    private static class DistanceCombiner extends Combiner<Pair<Integer, Double>, Pair<Integer, Double>> {
        private Double averageDistance;
        private int count;

        @Override
        public void combine(Pair<Integer, Double> pair) {
            count += pair.getOne();
            averageDistance += pair.getOther();
        }

        @Override
        public Pair<Integer, Double> finalizeChunk() {
            return new Pair<>(count, averageDistance);
        }

        @Override
        public void reset() {
            averageDistance = 0.0;
            count = 0;
        }
    }
}



