package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Sale {
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime timestamp;
    private double total;
    private double discount;
    private double netTotal;

    public Sale() {
    }

    public Sale(LocalDate date, LocalTime time, double total, double discount, double netTotal) {
        this.date = date;
        this.time = time;
        this.timestamp = LocalDateTime.of(date, time);
        this.total = total;
        this.discount = discount;
        this.netTotal = netTotal;
    }

    public Sale(LocalDateTime timestamp, double total, double discount, double netTotal) {
        this.timestamp = timestamp;
        this.date = timestamp.toLocalDate();
        this.time = timestamp.toLocalTime();
        this.total = total;
        this.discount = discount;
        this.netTotal = netTotal;
    }

    // Getters
    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getTotal() {
        return total;
    }

    public double getDiscount() {
        return discount;
    }

    public double getNetTotal() {
        return netTotal;
    }

    // Setters
    public void setDate(LocalDate date) {
        this.date = date;
        if (this.time != null) {
            this.timestamp = LocalDateTime.of(date, this.time);
        }
    }

    public void setTime(LocalTime time) {
        this.time = time;
        if (this.date != null) {
            this.timestamp = LocalDateTime.of(this.date, time);
        }
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        this.date = timestamp.toLocalDate();
        this.time = timestamp.toLocalTime();
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "date=" + date +
                ", time=" + time +
                ", total=" + total +
                ", discount=" + discount +
                ", netTotal=" + netTotal +
                '}';
    }
}
