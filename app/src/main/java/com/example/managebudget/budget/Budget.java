package com.example.managebudget.budget;

import com.google.firebase.database.PropertyName;

import java.util.List;

public class Budget
{
    @PropertyName("id")
    private String id;
    @PropertyName("name")
    private String name;
    @PropertyName("desription")
    private String desription;
    @PropertyName("creatorId")
    private String creatorId;
    private List<String> participatnIds;
    private List<Transaction> incomeTransactions;
    private List<Transaction> expenseTransactions;
    private List<Goal> goals;
    private List<Debt> debts;

    public Budget() {}

    public Budget(String id, String name, String desription, String creatorId) {
        this.id = id;
        this.name = name;
        this.desription = desription;
        this.creatorId = creatorId;
    }

    public Budget(String id, String name, String desription, String creatorId, List<String> participatnIds, List<Transaction> incomeTransactions, List<Transaction> expenseTransactions, List<Goal> goals, List<Debt> debts) {
        this.id = id;
        this.name = name;
        this.desription = desription;
        this.creatorId = creatorId;
        this.participatnIds = participatnIds;
        this.incomeTransactions = incomeTransactions;
        this.expenseTransactions = expenseTransactions;
        this.goals = goals;
        this.debts = debts;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesription() {
        return desription;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public List<String> getParticipatnIds() {
        return participatnIds;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesription(String desription) {
        this.desription = desription;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public void setParticipatnIds(List<String> participatnIds) {
        this.participatnIds = participatnIds;
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
