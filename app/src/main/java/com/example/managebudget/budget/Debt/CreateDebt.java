package com.example.managebudget.budget.Debt;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateDebt extends DialogFragment
{
    FirebaseDatabase database;
    FirebaseUser currentUser;
    ImageButton closeBt;
    EditText descriptionET;
    EditText AmountET;
    EditText DateET;
    FloatingActionButton saveBt;
    BudgetViewModel budgetViewModel;

    List<Debt> debtList;

    Budget budget;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_debt, container, false);

        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        closeBt = rootView.findViewById(R.id.closeBt);
        descriptionET = rootView.findViewById(R.id.descriptionET);
        AmountET = rootView.findViewById(R.id.AmountET);
        DateET = rootView.findViewById(R.id.DateET);
        DateET.setText(getCurrentDateTime());
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
            public void onClick(View v) { CreateDebt(); }});


        return rootView ;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    @SuppressLint("NotConstructor")
    private void CreateDebt()
    {
        String userId = currentUser.getUid();
        String description = descriptionET.getText().toString();
        String AmountText = AmountET.getText().toString();
        String Date = DateET.getText().toString();

        if(!description.trim().isEmpty() && !AmountText.trim().isEmpty() && !Date.trim().isEmpty())
        {
            Double Amount = Double.parseDouble(AmountText);
            Debt newDebt = new Debt(userId, description, Amount, Date);
            if (budget.getDebts() == null)
            {
                budget.setDebts(new ArrayList<>());
            }

            budget.getDebts().add(newDebt);
            DatabaseReference budgetRef = database.getReference("budget").child(budget.getId());
            budgetRef.child("debts").setValue(budget.getDebts()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getContext(), "Задолженость добавлена", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Ошибка добавления", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(getContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
        }
    }

    String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

}
