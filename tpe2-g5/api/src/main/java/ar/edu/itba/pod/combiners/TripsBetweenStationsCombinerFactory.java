package ar.edu.itba.pod.combiners;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

@SuppressWarnings("deprecation")
public class TripsBetweenStationsCombinerFactory implements CombinerFactory<Pair<Integer, Integer>, Integer, Integer> {

    @Override
    public Combiner<Integer, Integer> newCombiner(Pair<Integer, Integer> key) {
        return new TripsBetweenStationsCombiner();
    }

    private static class TripsBetweenStationsCombiner extends Combiner<Integer, Integer> {
        private int combinedTrips;

        @Override
        public void combine(Integer value) {
            combinedTrips += value;
        }

        @Override
        public Integer finalizeChunk() {
            return combinedTrips;
        }

        @Override
        public void reset() {
            combinedTrips = 0;
        }
    }
}
