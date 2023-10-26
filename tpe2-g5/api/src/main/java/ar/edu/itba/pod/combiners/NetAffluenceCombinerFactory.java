package ar.edu.itba.pod.combiners;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.time.LocalDateTime;

@SuppressWarnings("deprecation")
public class NetAffluenceCombinerFactory implements CombinerFactory<String, Pair<Long, LocalDateTime>, Pair<Long, LocalDateTime>> {

    @Override
    public Combiner<Pair<Long, LocalDateTime>, Pair<Long, LocalDateTime>> newCombiner(String key) {
        return new NetAffluenceCombiner();
    }

    private static class NetAffluenceCombiner extends Combiner<Pair<Long, LocalDateTime>, Pair<Long, LocalDateTime>> {
        private Pair<Long, LocalDateTime> auxNetAffluence = null;

        @Override
        public void combine(Pair<Long, LocalDateTime> value) {
            if (auxNetAffluence == null){
                auxNetAffluence = new Pair<>(value.getOne(), value.getOther());
            } else {
                auxNetAffluence.setOne(auxNetAffluence.getOne() + value.getOne());
            }
        }

        @Override
        public Pair<Long, LocalDateTime> finalizeChunk() {
            return auxNetAffluence;
        }

        @Override
        public void reset() {
            auxNetAffluence = null;
        }
    }
}

