package ar.edu.itba.pod.models;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Trip implements DataSerializable {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int startStation;
    private int endStation;
    private int isMember;

    public Trip(LocalDateTime startDate, LocalDateTime endDate, int startStation, int endStation, int isMember) {
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
    public int getStartStation() {
        return startStation;
    }
    public int getEndStation() {
        return endStation;
    }
    public int getIsMember() {
        return isMember;
    }

    public Integer getTripLength() {
        return Math.toIntExact(Duration.between(startDate, endDate).toMinutes());
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(startDate.toInstant(ZoneOffset.UTC).toEpochMilli());
        out.writeLong(endDate.toInstant(ZoneOffset.UTC).toEpochMilli());
        out.writeInt(startStation);
        out.writeInt(endStation);
        out.writeInt(isMember);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        long epochMillis = in.readLong();
        startDate = LocalDateTime.ofEpochSecond(epochMillis / 1000, 0, ZoneOffset.UTC);
        epochMillis = in.readLong();
        endDate = LocalDateTime.ofEpochSecond(epochMillis / 1000, 0, ZoneOffset.UTC);
        startStation = in.readInt();
        endStation = in.readInt();
        isMember = in.readInt();
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, startStation, endStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip trip)) {
            return false;
        }
        return this.startDate.equals(trip.startDate) && this.endDate.equals(trip.endDate)
                && this.startStation == trip.startStation && this.endStation == trip.endStation;
    }
}
