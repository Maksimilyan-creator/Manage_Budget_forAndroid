package com.example.managebudget.budget;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BudgetViewModel extends ViewModel {
    private final MutableLiveData<List<Budget>> budgets = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Budget> selectedBudget = new MutableLiveData<>();

    public LiveData<List<Budget>> getBudgets() {
        return budgets;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets.setValue(budgets);
    }

    public LiveData<Budget> getSelectedBudget() {
        return selectedBudget;
    }

    public void setSelectedBudget(Budget selectedBudget) {
        Budget previousBudget = this.selectedBudget.getValue();
        if (previousBudget == null || !previousBudget.equals(selectedBudget)) {
            this.selectedBudget.setValue(selectedBudget);
        }
    }

    public void loadBudgetData(String userId, DatabaseReference databaseReference, OnBudgetsLoadedListener listener) {
        databaseReference.child("budget").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Budget> userBudgets = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Budget budget = dataSnapshot.getValue(Budget.class);
                    if (budget != null && (budget.getCreatorId().equals(userId) || (budget.getParticipantIds() != null && budget.getParticipantIds().contains(userId)))) {
                        userBudgets.add(budget);
                    }
                }
                setBudgets(userBudgets);
                if (listener !=null)
                {
                    listener.onBudgetsLoaded();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BudgetViewModel", "Ошибка загрузки бюджетов: " + error.getMessage());
            }
        });
    }

    public interface OnBudgetsLoadedListener
    {
        void onBudgetsLoaded();
    }
}



