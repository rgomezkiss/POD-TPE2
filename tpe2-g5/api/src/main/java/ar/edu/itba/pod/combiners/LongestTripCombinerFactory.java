package ar.edu.itba.pod.combiners;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.time.LocalDateTime;

@SuppressWarnings("deprecation")
public class LongestTripCombinerFactory implements CombinerFactory<Pair<Integer, Integer>, Pair<LocalDateTime, Integer>, Pair<LocalDateTime, Integer>> {

    @Override
    public Combiner<Pair<LocalDateTime, Integer>, Pair<LocalDateTime, Integer>> newCombiner(Pair<Integer, Integer> integerIntegerPair) {
        return new LongestTripCombiner();
    }

    private static class LongestTripCombiner extends Combiner<Pair<LocalDateTime, Integer>, Pair<LocalDateTime, Integer>> {


        @Override
        public void combine(Pair<LocalDateTime, Integer> localDateTimeIntegerPair) {

        }

        @Override
        public Pair<LocalDateTime, Integer> finalizeChunk() {
            return new Pair<>();
        }

        @Override
        public void reset() {
        }
    }

}
