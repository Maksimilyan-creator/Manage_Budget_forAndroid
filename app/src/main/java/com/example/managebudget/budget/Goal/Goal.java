package com.example.managebudget.budget.Goal;

import com.example.managebudget.budget.Payments.Payments;

import java.util.List;

public class Goal
{
    private String userId;
    private String description;
    private String deadline;
    private double amount;
    private List<Payments> payments;

    public Goal () {}

    public Goal(String userId, String description, String deadline, double amount, List<Payments> payments) {
        this.userId = userId;
        this.description = description;
        this.deadline = deadline;
        this.amount = amount;
        this.payments = payments;
    }

    public Goal(String userId, String description, double amount, String deadline) {
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.deadline = deadline;
    }

    public String getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public String getDeadline() {
        return deadline;
    }

    public double getAmount() {
        return amount;
    }

    public List<Payments> getPayments() {
        return payments;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPayments(List<Payments> payments) {
        this.payments = payments;
    }
}
