package com.example.managebudget.DashBoard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.managebudget.budget.CategoriesIncome;
import com.example.managebudget.budget.Category;
import com.example.managebudget.budget.CreateTransactionIncome;
import com.example.managebudget.budget.Transaction;
import com.example.managebudget.budget.TransactionAdapter;
import com.example.managebudget.budget.UpdateTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class IncomeFragment extends Fragment
{
    private BudgetViewModel budgetViewModel;
    FirebaseDatabase database;
    DatabaseReference usersRef;
    FirebaseUser currentUser;
    RecyclerView RecyclerViewTransaction;
    TransactionAdapter transactionAdapter;
    FloatingActionButton IncomeTransactionAdd;
    Button categoryAddBt;
    Budget budget1;
    List<Transaction> incomeTransactionList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_income, container, false);

        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");
        RecyclerViewTransaction = rootView.findViewById(R.id.RecyclerViewTransaction);
        IncomeTransactionAdd = rootView.findViewById(R.id.IncomeSaveBt);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        categoryAddBt = rootView.findViewById(R.id.categoryAddBt);


        TransactionAdapter.OnTransactionClickLisneter onTransactionClickLisneter = new TransactionAdapter.OnTransactionClickLisneter() {
            @Override
            public void onTransactionClick(Transaction transaction, int position) {
                OpenUpdateTransaction(transaction, position);
            }
        };

        TransactionAdapter.OnTransactionLongClickListener onTransactionLongClickListener = new TransactionAdapter.OnTransactionLongClickListener() {
            @Override
            public void onTransactionLongClick(Transaction transaction, int position) {
                RemoveTransaction(transaction);
            }
        };

        transactionAdapter = new TransactionAdapter(getContext(), new ArrayList<>(), usersRef, onTransactionClickLisneter, onTransactionLongClickListener);

        RecyclerViewTransaction.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewTransaction.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RecyclerViewTransaction.setAdapter(transactionAdapter);

        budgetViewModel.getSelectedBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget)
            {
                if (budget !=null)
                {
                    budget1 = budget;
                    incomeTransactionList = budget.getIncomeTransactions();
                    if (incomeTransactionList != null)
                    {
                        transactionAdapter.UpdateAdapter(incomeTransactionList);
                    }
                    else
                    {
                        transactionAdapter.UpdateAdapter(new ArrayList<>());
                    }
                }
            }
        });

        IncomeTransactionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { OpenTransacrionIncome(); }});

        categoryAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { OpenCategoriesIncome(); }});



        return rootView;
    }

    public void OpenCategoriesIncome()
    {
        CategoriesIncome categoriesIncome = new CategoriesIncome();
        categoriesIncome.show(getParentFragmentManager(), "CategoriesIncome");
    }

    public void OpenTransacrionIncome()
    {
        CreateTransactionIncome createTransactionIncome = new CreateTransactionIncome();
        createTransactionIncome.show(getParentFragmentManager(), "CreateTransactionIncome");
    }

    public void OpenUpdateTransaction(Transaction selectedTransaction, int position)
    {
        UpdateTransaction updateTransaction = new UpdateTransaction(selectedTransaction, position);
        updateTransaction.show(getParentFragmentManager(), "UpdateTransaction");
    }

    public void RemoveTransaction(Transaction transaction)
    {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить транзакцию")
                .setMessage("Вы уверены, что хотите удалить транзакцию категории " + transaction.getCategory() + ", на сумму " + transaction.getAmount() + " рублей" + "?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        budget1.getIncomeTransactions().remove(transaction);

                        DatabaseReference budgetRef = database.getReference("budget").child(budget1.getId());
                        budgetRef.child("incomeTransactions").setValue(budget1.getIncomeTransactions()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getContext(), "Транзакция удалена", Toast.LENGTH_SHORT).show();
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

}

