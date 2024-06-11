package com.example.managebudget.DashBoard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managebudget.R;
import com.example.managebudget.budget.Budget;
import com.example.managebudget.budget.BudgetViewModel;
import com.example.managebudget.budget.Debt.CreateDebt;
import com.example.managebudget.budget.Debt.Debt;
import com.example.managebudget.budget.Debt.DebtAdapter;
import com.example.managebudget.budget.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DebtsFragment extends Fragment
{
    RecyclerView RecyclerViewDebt;
    DebtAdapter debtAdapter;
    FloatingActionButton DebtSaveBt;
    private BudgetViewModel budgetViewModel;
    FirebaseDatabase database;
    DatabaseReference usersRef;
    FirebaseUser currentUser;
    Budget budget;
    List<Debt> debtList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_debts, container, false);

        RecyclerViewDebt = rootView.findViewById(R.id.RecyclerViewDebt);
        DebtSaveBt = rootView.findViewById(R.id.DebtSaveBt);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        DebtAdapter.OnDebtClickListener onDebtClickListener = new DebtAdapter.OnDebtClickListener() {
            @Override
            public void onDebtClick(Debt debt, int position) {
                Toast.makeText(getContext(), "Ну давай еще потыкай че", Toast.LENGTH_SHORT).show();
                UpdateDebt(debt);
            }
        };

        DebtAdapter.OnDebtLongClickListener onDebtLongClickListener = new DebtAdapter.OnDebtLongClickListener() {
            @Override
            public void onDebtLongClick(Debt debt, int position) {
                Toast.makeText(getContext(), "Продави экран", Toast.LENGTH_SHORT).show();
                RemoveDebt(debt);
            }
        };

        debtAdapter = new DebtAdapter(getContext(), new ArrayList<>(), usersRef, onDebtClickListener, onDebtLongClickListener);
        RecyclerViewDebt.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewDebt.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RecyclerViewDebt.setAdapter(debtAdapter);

        budgetViewModel.getSelectedBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget_) {
                if (budget_ != null)
                {
                    budget = budget_;
                    debtList = budget_.getDebts();
                    if (debtList != null)
                    {
                        debtAdapter.UpdateAdapter(debtList);
                    }
                    else
                    {
                        debtAdapter.UpdateAdapter(new ArrayList<>());
                    }
                }
            }
        });

        DebtSaveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { OpenCreateDebt(); }});

        return rootView;
    }

    private void OpenCreateDebt()
    {
        CreateDebt createDebt = new CreateDebt();
        createDebt.show(getParentFragmentManager(), "CreateDebt");
    }

    private void RemoveDebt(Debt debt)
    {
        return;
    }

    private void UpdateDebt(Debt debt)
    {
        return;
    }
}
