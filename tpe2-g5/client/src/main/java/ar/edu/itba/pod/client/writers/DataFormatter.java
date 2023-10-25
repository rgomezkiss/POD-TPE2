package ar.edu.itba.pod.client.writers;

public interface DataFormatter<K, V> {
    String getHeader();
    String formatEntry(K key, V value);
    String getOutput();
}
