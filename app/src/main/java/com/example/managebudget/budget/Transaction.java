package com.example.managebudget.budget;

public class Transaction
{
    private String userId;
    private double amount;
    private String category;
    private String date;

    public Transaction() {}

    public Transaction( String userId, double amount, String category, String date) {

        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }


    public String getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
