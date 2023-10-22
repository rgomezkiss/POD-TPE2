package ar.edu.itba.pod.models;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class Station implements DataSerializable {
    private int pk;
    private String name;
    private double latitude;
    private double longitude;

    public Station(int pk, String name, double latitude, double longitude) {
        this.pk = pk;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Station() {
    }

    public int getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int hashCode(){
        return Objects.hash(pk);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station station)) {
            return false;
        }
        return this.pk == station.pk;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(pk);
        out.writeUTF(name);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        pk = in.readInt();
        name = in.readUTF();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }
}
