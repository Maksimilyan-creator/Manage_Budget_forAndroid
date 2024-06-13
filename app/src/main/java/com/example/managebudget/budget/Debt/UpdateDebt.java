package com.example.managebudget.budget.Debt;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.managebudget.R;
import com.example.managebudget.budget.Budget;
import com.example.managebudget.budget.BudgetViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UpdateDebt extends DialogFragment
{
    FirebaseDatabase database;
    FirebaseUser currentUser;
    ImageButton closeBt;
    EditText descriptionET;
    EditText AmountET;
    EditText DateET;
    FloatingActionButton saveBt;
    BudgetViewModel budgetViewModel;
    Budget budget;
    TextView title;
    Debt debt;
    int positionDebt;

    public UpdateDebt(Debt debt, int positionDebt)
    {
        this.debt = debt;
        this.positionDebt = positionDebt;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_debt, container, false);

        title = rootView.findViewById(R.id.textView);
        title.setText("Изменение долга");
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        closeBt = rootView.findViewById(R.id.closeBt);
        descriptionET = rootView.findViewById(R.id.descriptionET);
        descriptionET.setText(debt.getDescription());
        AmountET = rootView.findViewById(R.id.AmountET);
        AmountET.setText(String.valueOf(debt.getAmount()));
        DateET = rootView.findViewById(R.id.DateET);
        DateET.setText(debt.getDeadline());
        saveBt = rootView.findViewById(R.id.saveBt);
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        budgetViewModel.getSelectedBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget_) {
                budget = budget_;
            }
        });

        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { UpdateDebtVoid(); }});

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public void UpdateDebtVoid()
    {
        String description = descriptionET.getText().toString();
        String AmountText = AmountET.getText().toString();
        String Date = DateET.getText().toString();

        if(!description.trim().isEmpty() && !AmountText.trim().isEmpty() && !Date.trim().isEmpty()) {
            Double Amount = Double.parseDouble(AmountText);
            debt.setDescription(description);
            debt.setAmount(Amount);
            debt.setDeadline(Date);

            DatabaseReference budgetRef = database.getReference("budget").child(budget.getId());
            budgetRef.child("debts").child(String.valueOf(positionDebt)).setValue(debt).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), "Задолжность обновлена", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Ошибка обновления задолжности: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(getContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
        }
    }
}
