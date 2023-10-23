package ar.edu.itba.pod.models;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

//TODO: check if objects also have to be serializable
public class Pair<T, V> implements DataSerializable {
    private T value1;
    private V value2;

    public Pair(T value1, V value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public Pair() {
    }

    public T getOne() {
        return value1;
    }
    public V getOther() {
        return value2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair<?, ?> pair)) {
            return false;
        }
        return value1.equals(pair.value1) && value2.equals(pair.value2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value1.hashCode() + value2.hashCode());
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(value1);
        out.writeObject(value2);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        value1 = in.readObject();
        value2 = in.readObject();
    }
}