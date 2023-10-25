package ar.edu.itba.pod.combiners;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class NetAffluenceCombinerFactory implements CombinerFactory<String, Pair<Long, LocalDateTime>, List<Long>> {

    @Override
    public Combiner<Pair<Long, LocalDateTime>, List<Long>> newCombiner(String s) {
        return new NetAffluenceCombiner();
    }

    private static class NetAffluenceCombiner extends Combiner<Pair<Long, LocalDateTime>, List<Long>> {


        @Override
        public void combine(Pair<Long, LocalDateTime> longLocalDateTimePair) {

        }

        @Override
        public List<Long> finalizeChunk() {
            return new ArrayList<>();
        }

        @Override
        public void reset() {
        }
    }
}
