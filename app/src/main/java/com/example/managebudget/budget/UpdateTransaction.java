package com.example.managebudget.budget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.managebudget.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class UpdateTransaction extends DialogFragment
{
    FirebaseDatabase database;
    FirebaseUser currentUser;
    ImageButton closeBt;
    EditText TransactionAmountEt;
    Spinner TransactionCategorySpinner;
    EditText DateET;
    List<Category> incomeCategories = new ArrayList<>();
    BudgetViewModel budgetViewModel2;
    Budget budget2;
    Transaction selectedTransaction;
    int positionTransaction;

    FloatingActionButton BudgerUpdate;



    public UpdateTransaction (Transaction  currentTransaction, int positionTransaction)
    {
        selectedTransaction = currentTransaction;
        this.positionTransaction = positionTransaction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_transaction, container, false);

        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        closeBt = rootView.findViewById(R.id.closeBt);
        TransactionAmountEt = rootView.findViewById(R.id.TransactionAmountEt);
        TransactionCategorySpinner = rootView.findViewById(R.id.TransactionCategorySpinner);
        DateET = rootView.findViewById(R.id.DateET);
        BudgerUpdate = rootView.findViewById(R.id.BudgetCreateBt);

        budgetViewModel2 = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        budgetViewModel2.getSelectedBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget) {
                budget2 = budget;
                if (budget.getIncomeCategories() !=null)
                {
                    incomeCategories = budget.getIncomeCategories();
                    setupSpinner();
                }
            }
        });

        TransactionAmountEt.setText(String.valueOf(selectedTransaction.getAmount()));
        DateET.setText(selectedTransaction.getDate());

        TransactionCategorySpinner.post(new Runnable() {
            @Override
            public void run() {
                setSelectedCategory(selectedTransaction.getCategory());
            }
        });

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        BudgerUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateTransaction(selectedTransaction, positionTransaction);
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
        for (Category category : incomeCategories)
        {
            categoryNames.add(category.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TransactionCategorySpinner.setAdapter(adapter);

        setSelectedCategory(selectedTransaction.getCategory());
    }

    private void  setSelectedCategory(String category)
    {
        if (!category.isEmpty())
        {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) TransactionCategorySpinner.getAdapter();
            if (adapter !=null)
            {
                int position = adapter.getPosition(category);
                if (position >=0)
                {
                    TransactionCategorySpinner.setSelection(position);
                }
            }

        }
    }

    @SuppressLint("NotConstructor")
    private  void UpdateTransaction (Transaction selectedTransaction, int positionTransaction)
    {
        String newAmountText = TransactionAmountEt.getText().toString();
        String newDate = DateET.getText().toString();
        String newCategory = TransactionCategorySpinner.getSelectedItem().toString();
        if (!newAmountText.trim().isEmpty() && !newCategory.isEmpty() && !newDate.trim().isEmpty())
        {
            Double newAmount = Double.parseDouble(newAmountText);
            selectedTransaction.setAmount(newAmount);
            selectedTransaction.setCategory(newCategory);
            selectedTransaction.setDate(newDate);

            DatabaseReference budgetRef = database.getReference("budget").child(budget2.getId());
            budgetRef.child("incomeTransactions").child(String.valueOf(positionTransaction)).setValue(selectedTransaction)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Транзакция обновлена", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Ошибка обновления транзакции: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
        {
           Toast.makeText(getContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
        }
    }

}


