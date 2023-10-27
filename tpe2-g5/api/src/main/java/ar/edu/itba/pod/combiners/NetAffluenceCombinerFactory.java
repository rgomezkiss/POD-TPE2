package ar.edu.itba.pod.combiners;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.time.LocalDateTime;

@SuppressWarnings("deprecation")
public class NetAffluenceCombinerFactory implements CombinerFactory<Pair<Integer, LocalDateTime>, Long, Long> {

    @Override
    public Combiner<Long, Long> newCombiner(Pair<Integer, LocalDateTime> key) {
        return new NetAffluenceCombiner();
    }

    private static class NetAffluenceCombiner extends Combiner<Long, Long> {

        private Long auxNetAffluence;

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

