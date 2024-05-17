package com.example.managebudget.budget;

import java.util.List;

public class Goal
{
    private String id;
    private String description;
    private double targetAmount;
    private String deadline;
    private List<Payments> payments;

    public Goal () {}

    public Goal(String id, String description, double targetAmount, String deadline, List<Payments> payments) {
        this.id = id;
        this.description = description;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.payments = payments;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getTargetAmount() {
        return targetAmount;
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

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setPayments(List<Payments> payments) {
        this.payments = payments;
    }
}
