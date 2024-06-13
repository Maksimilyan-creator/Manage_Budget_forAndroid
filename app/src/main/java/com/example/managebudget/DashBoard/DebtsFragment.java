package com.example.managebudget.DashBoard;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.managebudget.budget.Debt.UpdateDebt;
import com.example.managebudget.budget.Payments.addPayments;
import com.example.managebudget.budget.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
            public void onDebtClick(Debt debt, int position)
            {
                OpenUpdateDebt(debt, position);
            }
        };

        DebtAdapter.OnDebtLongClickListener onDebtLongClickListener = new DebtAdapter.OnDebtLongClickListener() {
            @Override
            public void onDebtLongClick(Debt debt, int position)
            {
                RemoveDebt(debt);
            }
        };

        DebtAdapter.OnButtonCliclListener onButtonCliclListener = new DebtAdapter.OnButtonCliclListener() {
            @Override
            public void onButtonClick(Debt debt, int position) {
                OpenAddPayments(position, debt);
            }
        };

        debtAdapter = new DebtAdapter(getContext(), new ArrayList<>(), usersRef, onDebtClickListener, onDebtLongClickListener, onButtonCliclListener);
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
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить задолжность")
                .setMessage("Вы уверены, что хотите удалить задолжность с описанием " + debt.getDescription() + ", на сумму " + debt.getAmount() + " рублей" + "?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        budget.getDebts().remove(debt);

                        DatabaseReference budgetRef = database.getReference("budget").child(budget.getId());
                        budgetRef.child("debts").setValue(budget.getDebts()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getContext(), "Задолжность удалена", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Нет", null).show();
    }

    private void OpenUpdateDebt(Debt debt, int positionDebt)
    {
        UpdateDebt updateDebt = new UpdateDebt(debt, positionDebt);
        updateDebt.show(getParentFragmentManager(), "UpdateDebt");
    }

    private void OpenAddPayments(int position, Debt debt)
    {
        addPayments addPaymentss = new addPayments(position, debt);
        addPaymentss.show(getParentFragmentManager(), "addPayments");
    }
}
