package ar.edu.itba.pod.client.utils;

import java.time.LocalDateTime;
import java.util.List;

public class QueryParams {
    private final List<String> addresses;
    private final String inPath;
    private final String outPath;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final int N;
    private final int query;

    public QueryParams(List<String> addresses, String inPath, String outPath, int query) {
        this.addresses = addresses;
        this.inPath = inPath;
        this.outPath = outPath;
        this.startDate = null;
        this.endDate = null;
        this.N = 0;
        this.query = query;
    }

    public QueryParams(List<String> addresses, String inPath, String outPath, LocalDateTime startDate, LocalDateTime endDate, int query) {
        this.addresses = addresses;
        this.inPath = inPath;
        this.outPath = outPath;
        this.startDate = startDate;
        this.endDate = endDate;
        this.N = 0;
        this.query = query;
    }

    public QueryParams(List<String> addresses, String inPath, String outPath, int N, int query) {
        this.addresses = addresses;
        this.inPath = inPath;
        this.outPath = outPath;
        this.startDate = null;
        this.endDate = null;
        this.N = N;
        this.query = query;
    }

    public List<String> getServerAddresses() {
        return addresses;
    }
    public String getInPath() {
        return inPath;
    }
    public String getOutPath() {
        return outPath;
    }
    public LocalDateTime getStartDate() {
        return startDate;
    }
    public LocalDateTime getEndDate() {
        return endDate;
    }
    public int getN() {
        return N;
    }
    public int getQuery() {
        return query;
    }
}
