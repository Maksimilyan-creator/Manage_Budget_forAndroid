package com.example.managebudget.budget;

import com.example.managebudget.budget.Debt.Debt;
import com.example.managebudget.budget.Goal.Goal;
import com.example.managebudget.budget.Payments.Payments;
import com.google.firebase.database.PropertyName;

import java.util.List;
import java.util.stream.Collectors;

public class Budget {
    @PropertyName("id")
    private String id;
    @PropertyName("name")
    private String name;
    @PropertyName("description")
    private String description;
    @PropertyName("creatorId")
    private String creatorId;
    @PropertyName("participantIds")
    private List<String> participantIds;

    @PropertyName("incomeCategories")
    private List<Category> incomeCategories;
    @PropertyName("expenceCategories")
    private List<Category> expenceCategories;
    @PropertyName("incomeTransactions")
    private List<Transaction> incomeTransactions;
    @PropertyName("expenseTransactions")
    private List<Transaction> expenseTransactions;
    @PropertyName("goals")
    private List<Goal> goals;
    @PropertyName("debts")
    private List<Debt> debts;

    // Конструктор

    public Budget() {}

    public Budget(String id, String name, String description, String creatorId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public List<Category> getIncomeCategories() {
        return incomeCategories;
    }

    public List<Category> getExpenceCategories() {
        return expenceCategories;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public void setIncomeCategories(List<Category> incomeCategories) {
        this.incomeCategories = incomeCategories;
    }

    public void setExpenceCategories(List<Category> expenceCategories) {
        this.expenceCategories = expenceCategories;
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

    public double balance ()
    {
        Double totalIncome = 0.0;
        Double totalExpenses = 0.0;
        Double totalDebtPayments = 0.0;
        Double totalGoalPayments = 0.0;

        if (incomeTransactions != null)
        {
             totalIncome = incomeTransactions.stream().mapToDouble(Transaction::getAmount).sum();
        }

        if (expenseTransactions != null)
        {
            totalExpenses = expenseTransactions.stream().mapToDouble(Transaction::getAmount).sum();
        }

        if (debts != null) {
            List<Payments> allPayments = debts.stream()
                    .filter(debt -> debt.getPayments() != null && !debt.getPayments().isEmpty())
                    .flatMap(debt -> debt.getPayments().stream())
                    .collect(Collectors.toList());

            if (!allPayments.isEmpty()) {
                totalDebtPayments = allPayments.stream()
                        .mapToDouble(Payments::getAmount)
                        .sum();

            }
        }

        if (goals != null) {
            List<Payments> allPaymentss = goals.stream()
                    .filter(goal -> goal.getPayments() != null && !goal.getPayments().isEmpty())
                    .flatMap(goal -> goal.getPayments().stream())
                    .collect(Collectors.toList());

            if (!allPaymentss.isEmpty()) {
                totalGoalPayments = allPaymentss.stream()
                        .mapToDouble(Payments::getAmount)
                        .sum();
            }
        }


        Double balance = totalIncome - totalExpenses - totalGoalPayments - totalDebtPayments;

        return  balance;
    }
}

