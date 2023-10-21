package ar.edu.itba.pod.models;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDateTime;

public class Trip implements DataSerializable {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Station startStation;
    private Station endStation;

    private int isMember;

    public Trip(LocalDateTime startDate, LocalDateTime endDate, Station startStation, Station endStation, int isMember) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startStation = startStation;
        this.endStation = endStation;
        this.isMember = isMember;
    }

    public Trip() {

    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public int getIsMember() {
        return isMember;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(startDate);
        out.writeObject(endDate);
        startStation.writeData(out);
        endStation.writeData(out);
        out.writeInt(isMember);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        startDate = in.readObject();
        endDate = in.readObject();
        startStation = new Station();
        startStation.readData(in);
        endStation = new Station();
        endStation.readData(in);
        isMember = in.readInt();
    }
}
