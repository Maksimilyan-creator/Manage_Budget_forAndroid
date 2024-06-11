package com.example.managebudget.budget;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.example.managebudget.budget.Category;
import com.example.managebudget.budget.Transaction;
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

public class CreateTransactionExpence extends DialogFragment
{
    FirebaseDatabase database;
    FirebaseUser currentUser;
    ImageButton closeBt;
    EditText TransactionAmountEt;
    Spinner TransactionCategorySpinner;
    EditText DateET;
    TextView title;

    FloatingActionButton BudgetCreateBt;
    List<Category> expenceCategories = new ArrayList<>();
    BudgetViewModel budgetViewModel2;
    Budget budget2;

    List<Transaction> expenceTransactionList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_transaction_income, container, false);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        closeBt = rootView.findViewById(R.id.closeBt);
        TransactionAmountEt = rootView.findViewById(R.id.TransactionAmountEt);
        TransactionCategorySpinner = rootView.findViewById(R.id.TransactionCategorySpinner);
        DateET = rootView.findViewById(R.id.DateET);
        BudgetCreateBt = rootView.findViewById(R.id.BudgetCreateBt);

        budgetViewModel2 = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        title = rootView.findViewById(R.id.textView);
        title.setText("Создание расхода");

        DateET.setText(getCurrentDateTime());

        budgetViewModel2.getSelectedBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget) {
                budget2 = budget;
                if (budget.getExpenceCategories() !=null)
                {
                    expenceCategories = budget.getExpenceCategories();
                    setupSpinner();
                }
                if (budget.getExpenseTransactions() !=null)
                {
                    expenceTransactionList = budget.getExpenseTransactions();
                }
            }
        });

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dismiss();}});

        BudgetCreateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransaction();
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

    String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void setupSpinner() {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : expenceCategories)
        {
            categoryNames.add(category.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TransactionCategorySpinner.setAdapter(adapter);
    }

    private void addTransaction()
    {
        String userId = currentUser.getUid();
        String amount = TransactionAmountEt.getText().toString();
        String category = TransactionCategorySpinner.getSelectedItem().toString();
        String date = DateET.getText().toString();

        if (!amount.trim().isEmpty() && !userId.isEmpty() && !category.isEmpty() && !date.trim().isEmpty())
        {
            Transaction newTransaction = new Transaction(userId, Double.parseDouble(amount), category, date);
            if (budget2.getExpenseTransactions() == null)
            {
                budget2.setExpenseTransactions(new ArrayList<>());
            }
            budget2.getExpenseTransactions().add(newTransaction);
            DatabaseReference budgetRef = database.getReference("budget").child(budget2.getId());
            budgetRef.child("expenseTransactions").setValue(budget2.getExpenseTransactions()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getContext(), "Транзакция добавлена", Toast.LENGTH_SHORT).show();
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
}
