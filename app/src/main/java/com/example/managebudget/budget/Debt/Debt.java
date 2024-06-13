package com.example.managebudget.budget.Debt;

import com.example.managebudget.budget.Payments.Payments;

import java.util.List;

public class Debt
{
    private String userId;
    private String description;
    private double amount;
    private String deadline;
    private List<Payments> payments;

    public Debt() {}

    public Debt(String userId, String description, double amount, String deadline, List<Payments> payments) {
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.deadline = deadline;
        this.payments = payments;
    }

    public Debt(String userId, String description, double amount, String deadline) {
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

    public double getAmount() {
        return amount;
    }

    public String getDeadline() {
        return deadline;
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
