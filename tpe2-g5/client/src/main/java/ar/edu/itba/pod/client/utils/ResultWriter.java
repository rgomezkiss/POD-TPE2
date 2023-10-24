package ar.edu.itba.pod.client.utils;

import ar.edu.itba.pod.models.Pair;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


//TODO: try to implement generic
public class ResultWriter {

    public static void writeQuery1Result(String outPath, List<Map.Entry<String, Integer>> result) {
        try (PrintWriter writer = new PrintWriter(outPath + "/query1.csv")) {
            writer.println( "station_a;station_b;trips_between_a_b");
            for (Map.Entry<String, Integer> r: result) {
                writer.println(r.getKey() + ";" + r.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeQuery2Result(String outPath, List<Map.Entry<String, Double>> result) {
        try (PrintWriter writer = new PrintWriter(outPath + "/query2.csv")) {
            writer.println( "station;avg_distance");
            for (Map.Entry<String, Double> r: result) {
                writer.println(r.getKey() + ";" + r.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeQuery3Result(String outPath, List<Map.Entry<String, Pair<LocalDateTime, Integer>>> result) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        try (PrintWriter writer = new PrintWriter(outPath + "/query3.csv")) {
            writer.println( "start_station;end_station;start_date;minutes");
            for (Map.Entry<String, Pair<LocalDateTime, Integer>> r: result) {
                writer.println(r.getKey() + ";" + r.getValue().getOne().format(formatter) + ";" + r.getValue().getOther());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeQuery4Result(String outPath, List<Map.Entry<String, List<Long>>> result) {
        try (PrintWriter writer = new PrintWriter(outPath + "/query4.csv")) {
            writer.println( "station;pos_afflux;neutral_afflux;negative_afflux");
            for (Map.Entry<String, List<Long>> r: result) {
                writer.println(r.getKey() + ";" + r.getValue().get(0)  + ";" + r.getValue().get(1) + ";" + r.getValue().get(2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
