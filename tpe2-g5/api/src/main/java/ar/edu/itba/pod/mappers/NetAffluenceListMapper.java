package ar.edu.itba.pod.mappers;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;

@SuppressWarnings("deprecation")
public class NetAffluenceListMapper implements Mapper<Pair<Integer, LocalDate>, Long, Integer, Long> {
    @Override
    public void map(Pair<Integer, LocalDate> pair, Long sum, Context<Integer, Long> context) {
        context.emit(pair.getOne(), sum);
    }
}
