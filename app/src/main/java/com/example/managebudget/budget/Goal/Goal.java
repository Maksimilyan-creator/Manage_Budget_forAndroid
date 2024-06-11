package com.example.managebudget.budget.Goal;

import com.example.managebudget.budget.Payments;

import java.util.List;

public class Goal
{
    private String userId;
    private String description;
    private String deadline;
    private List<Payments> payments;

    public Goal () {}

    public Goal(String userId, String description, String deadline, List<Payments> payments) {
        this.userId = userId;
        this.description = description;
        this.deadline = deadline;
        this.payments = payments;
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

    public void setPayments(List<Payments> payments) {
        this.payments = payments;
    }
}
