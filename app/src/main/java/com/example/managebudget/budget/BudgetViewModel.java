package com.example.managebudget.budget;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class BudgetViewModel extends AndroidViewModel
{
    private MutableLiveData<List<Budget>> budgets = new MutableLiveData<>();
    private MutableLiveData<Budget> selectedBudget = new MutableLiveData<>();

    public BudgetViewModel (Application application)
    {
        super(application);
    }

    public LiveData<List<Budget>> getBudgets()
    {
        return budgets;
    }

    public void setBudgets(List<Budget> budgetList)
    {
        budgets.setValue(budgetList);
    }

    public LiveData<Budget> getBudgetById(String budgetId)
    {
        MutableLiveData<Budget> budgetLiveData = new MutableLiveData<>();
        List<Budget> budgetList = budgets.getValue();
        if (budgetList != null)
        {
            for (Budget budget : budgetList)
            {
                if (budget.getId().equals(budgetId))
                {
                    budgetLiveData.setValue(budget);
                    break;
                }
            }
        }
        return budgetLiveData;
    }
    public LiveData<Budget> getSelectedBudget()
    {
        return selectedBudget;
    }
    public void setSelectedBudget(Budget budget)
    {
        selectedBudget.setValue(budget);
    }
}
