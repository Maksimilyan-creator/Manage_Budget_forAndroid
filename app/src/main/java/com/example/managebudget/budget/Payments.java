package com.example.managebudget.budget;

public class Payments {
    private String payerId;
    private double amount;
    private String date;

    public Payments () {}

    public Payments(String payerId, double amount, String date) {
        this.payerId = payerId;
        this.amount = amount;
        this.date = date;
    }

    public String getPayerId() {
        return payerId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
