package com.george.meterdatareader;

import java.sql.Date;

public class MeterData {
    private String nmi;
    private Date timestamp;
    private double consumption;

    public MeterData(String nmi, Date timestamp, double consumption) {
        this.nmi = nmi;
        this.timestamp = timestamp;
        this.consumption = consumption;
    }

    public String getNmi() {
        return nmi;
    }
    
    public void setNmi(String nmi) {
        this.nmi = nmi;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    @Override
    public String toString() {
        return "MeterData{" +
                "nmi='" + nmi + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", consumption=" + consumption +
                '}';
    }
}