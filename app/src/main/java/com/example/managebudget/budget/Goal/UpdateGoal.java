package com.example.managebudget.budget.Goal;

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
import com.example.managebudget.budget.Debt.Debt;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateGoal extends DialogFragment
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
    Goal goal;
    int positionGoal;

    public UpdateGoal(Goal goal, int positionGoal)
    {
        this.goal = goal;
        this.positionGoal = positionGoal;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_goal, container, false);

        title = rootView.findViewById(R.id.textView);
        title.setText("Изменение цели");
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        closeBt = rootView.findViewById(R.id.closeBt);
        descriptionET = rootView.findViewById(R.id.descriptionET);
        descriptionET.setText(goal.getDescription());
        AmountET = rootView.findViewById(R.id.AmountET);
        AmountET.setText(String.valueOf(goal.getAmount()));
        DateET = rootView.findViewById(R.id.DateET);
        DateET.setText(goal.getDeadline());
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
            public void onClick(View v) {
                UpdateGoalVoid();
            }
        });

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

    public void UpdateGoalVoid()
    {
        String description = descriptionET.getText().toString();
        String AmountText = AmountET.getText().toString();
        String Date = DateET.getText().toString();

        if(!description.trim().isEmpty() && !AmountText.trim().isEmpty() && !Date.trim().isEmpty()) {
            Double Amount = Double.parseDouble(AmountText);
            goal.setDescription(description);
            goal.setAmount(Amount);
            goal.setDeadline(Date);

            DatabaseReference budgetRef = database.getReference("budget").child(budget.getId());
            budgetRef.child("goals").child(String.valueOf(positionGoal)).setValue(goal).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), "Цель обновлена", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Ошибка обновления цели: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(getContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
        }
    }
}

