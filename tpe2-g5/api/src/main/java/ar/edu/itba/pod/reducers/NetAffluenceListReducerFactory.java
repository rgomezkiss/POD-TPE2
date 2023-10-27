package ar.edu.itba.pod.reducers;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("deprecation")
public class NetAffluenceListReducerFactory implements ReducerFactory<Integer, Long, List<Long>> {
    @Override
    public Reducer<Long, List<Long>> newReducer(Integer stationId) {
        return new QueryReducer();
    }

    private static class QueryReducer extends Reducer<Long, List<Long>> {
        long positiveAffluence;
        long neutralAffluence;
        long negativeAffluence;

        @Override
        public void beginReduce() {
            positiveAffluence = 0;
            neutralAffluence = 0;
            negativeAffluence = 0;
        }

        @Override
        public void reduce(Long affluence) {
            if (affluence > 0) {
                positiveAffluence++;
            } else if (affluence < 0) {
                negativeAffluence++;
            } else {
                neutralAffluence++;
            }
        }

        @Override
        public List<Long> finalizeReduce() {
            List<Long> netAffluenceList = new ArrayList<>();
            netAffluenceList.add(positiveAffluence);
            netAffluenceList.add(neutralAffluence);
            netAffluenceList.add(negativeAffluence);
            return netAffluenceList;
        }
    }
}
