package com.example.managebudget.budget;

import com.google.firebase.database.PropertyName;

import java.util.List;

public class Budget {
    @PropertyName("id")
    private String id;
    @PropertyName("name")
    private String name;
    @PropertyName("description")
    private String description;
    @PropertyName("creatorId")
    private String creatorId;
    private List<String> participantIds;
    private List<Transaction> incomeTransactions;
    private List<Transaction> expenseTransactions;
    private List<Goal> goals;
    private List<Debt> debts;

    // Конструктор

    public Budget() {}

    public Budget(String id, String name, String description, String creatorId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
    }



    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public List<Transaction> getIncomeTransactions() {
        return incomeTransactions;
    }

    public List<Transaction> getExpenseTransactions() {
        return expenseTransactions;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public List<Debt> getDebts() {
        return debts;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public void setIncomeTransactions(List<Transaction> incomeTransactions) {
        this.incomeTransactions = incomeTransactions;
    }

    public void setExpenseTransactions(List<Transaction> expenseTransactions) {
        this.expenseTransactions = expenseTransactions;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }
}

