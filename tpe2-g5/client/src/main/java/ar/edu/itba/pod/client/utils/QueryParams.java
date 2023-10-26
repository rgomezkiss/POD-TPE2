package ar.edu.itba.pod.client.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class QueryParams {
    private final List<String> addresses;
    private final String inPath;
    private final String outPath;
    private final LocalDate startDate;
    private final LocalDate endDate;
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

    public QueryParams(List<String> addresses, String inPath, String outPath, LocalDate startDate, LocalDate endDate, int query) {
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
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public int getN() {
        return N;
    }
    public int getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return "QueryParams{"
                + "addresses=" + addresses + '\n'
                + ", inPath=" + inPath + '\n'
                + ", outPath=" + outPath + '\n'
                + ", startDate=" + startDate  + '\n'
                + ", endDate=" + endDate + '\n'
                + ", N=" + N + '\n'
                + ", query=" + query + '\n'
                + '}';
    }
}
