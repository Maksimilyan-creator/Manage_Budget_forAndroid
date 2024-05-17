package com.example.managebudget.budget;

import java.util.List;

public class Debt
{
    private String id;
    private String description;
    private double amount;
    private String deadline;
    private List<Payments> payments;

    public Debt () {}

    public Debt(String id, String description, double amount, String deadline, List<Payments> payments) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.deadline = deadline;
        this.payments = payments;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getDeadline() {
        return deadline;
    }

    public List<Payments> getPayments() {
        return payments;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setPayments(List<Payments> payments) {
        this.payments = payments;
    }
}
