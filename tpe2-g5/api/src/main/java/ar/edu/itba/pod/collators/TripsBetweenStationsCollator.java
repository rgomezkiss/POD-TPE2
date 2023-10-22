package ar.edu.itba.pod.collators;

import ar.edu.itba.pod.models.Pair;
import com.hazelcast.mapreduce.Collator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("deprecation")
public class TripsBetweenStationsCollator implements Collator<Map.Entry<Pair<Integer, Integer>, Integer>, List<Map.Entry<String, Integer>>> {
    //    El orden de impresión es descendente por cantidad total de viajes y desempata  alfabético por el nombre de la estación A y luego alfabético por nombre de la estación B.
    //    Sólo se deben listar las estaciones presentes en el archivo CSV de estaciones.
    //    No se deben listar los pares de estaciones que no tengan viajes entre ellas, es decir, salidas que contengan 0 en la última columna.


    @Override
    public List<Map.Entry<String, Integer>> collate(Iterable<Map.Entry<Pair<Integer, Integer>, Integer>> iterable){
        return null;
    }
}

