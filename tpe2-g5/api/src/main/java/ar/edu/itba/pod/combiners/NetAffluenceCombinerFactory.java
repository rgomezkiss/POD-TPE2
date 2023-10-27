package ar.edu.itba.pod.combiners;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SuppressWarnings("deprecation")
public class NetAffluenceCombinerFactory implements CombinerFactory<Pair<Integer, LocalDate>, Long, Long> {

    @Override
    public Combiner<Long, Long> newCombiner(Pair<Integer, LocalDate> key) {
        return new NetAffluenceCombiner();
    }

    private static class NetAffluenceCombiner extends Combiner<Long, Long> {

        private Long auxNetAffluence = 0L;

        @Override
        public void combine(Long value) {
            auxNetAffluence += value;
        }

        @Override
        public Long finalizeChunk() {
            return auxNetAffluence;
        }

        @Override
        public void reset() {
            auxNetAffluence = 0L;
        }
    }
}

