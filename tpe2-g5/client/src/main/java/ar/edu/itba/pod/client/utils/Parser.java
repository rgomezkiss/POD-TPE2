package ar.edu.itba.pod.client.utils;

@FunctionalInterface
public interface Parser<T extends QueryParams> {
    T parse(String[] args);
}