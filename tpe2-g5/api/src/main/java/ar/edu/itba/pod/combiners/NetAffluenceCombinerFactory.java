package ar.edu.itba.pod.combiners;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class NetAffluenceCombinerFactory implements CombinerFactory<String, Pair<Long, LocalDateTime>, List<Long>> {

    @Override
    public Combiner<Pair<Long, LocalDateTime>, List<Long>> newCombiner(String s) {
        return new NetAffluenceCombiner();
    }

    private static class NetAffluenceCombiner extends Combiner<Pair<Long, LocalDateTime>, List<Long>> {
        private List<Long> netAffluenceList;
        private Map<LocalDate, Long> affluenceMapPerDay;

        @Override
        public void combine(Pair<Long, LocalDateTime> pair) {
            LocalDate date = pair.getOther().toLocalDate();
            affluenceMapPerDay.putIfAbsent(date, 0L);
            affluenceMapPerDay.merge(date, pair.getOne(), Long::sum);
        }

        @Override
        public List<Long> finalizeChunk() {
            long positiveAffluence = 0;
            long neutralAffluence = 0;
            long negativeAffluence = 0;

            for (Long affluence : affluenceMapPerDay.values()) {
                if (affluence > 0) {
                    positiveAffluence++;
                } else if (affluence < 0) {
                    negativeAffluence++;
                } else {
                    neutralAffluence++;
                }
            }

            netAffluenceList.add(positiveAffluence);
            netAffluenceList.add(neutralAffluence);
            netAffluenceList.add(negativeAffluence);
            return netAffluenceList;
        }

        @Override
        public void reset() {
            netAffluenceList = new ArrayList<>();
            affluenceMapPerDay = new HashMap<>();
        }
    }
}
