package com.example.managebudget.DashBoard;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.managebudget.R;
import com.example.managebudget.budget.Budget;
import com.example.managebudget.budget.BudgetViewModel;
import com.example.managebudget.budget.BudgetsAdapter;
import com.example.managebudget.budget.Category;
import com.example.managebudget.budget.CategoryAdapter;
import com.example.managebudget.budget.CreateBudgetFragment;
import com.example.managebudget.budget.CreateCategoriesIncome;
import com.example.managebudget.budget.CreateTransactionIncome;
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

public class IncomeFragment extends Fragment
{
    private BudgetViewModel budgetViewModel;
    FirebaseDatabase database;
    DatabaseReference usersRef;
    FirebaseUser currentUser;
    ListView IncomeListView;
    TransactionAdapter transactionAdapter;
    FloatingActionButton IncomeSaveBt;
    Button categoryAddBt;
    Budget budget1;
    CategoryAdapter categoryAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_income, container, false);

        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");
        IncomeListView = rootView.findViewById(R.id.listViewIncome);
        IncomeSaveBt = rootView.findViewById(R.id.IncomeSaveBt);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        categoryAddBt = rootView.findViewById(R.id.categoryAddBt);

        budget1 = new Budget();


        /*transactionAdapter = new TransactionAdapter(new ArrayList<>(), usersRef, requireContext());
        IncomeListView.setAdapter(transactionAdapter);*/
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), requireContext());
        IncomeListView.setAdapter(categoryAdapter);

        budgetViewModel.getSelectedBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget)
            {
                if (budget !=null)
                {
                   /*List<Transaction> incomeTransactionList = budget.getIncomeTransactions();
                    if (incomeTransactionList != null)
                    {
                        transactionAdapter.updateTransaction(incomeTransactionList);
                    }*/
                    List<Category> categoryIncomeList = budget.getIncomeCategories();
                    if (categoryIncomeList != null)
                    {
                        categoryAdapter.updateCategories(categoryIncomeList);
                    }

                    budget1 = budget;
                }

            }
        });

        IncomeSaveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {OpenCreateTransactionIncome();}});

        categoryAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {OpenAddCategories(v,budget1);}});



        return rootView;
    }

    public void OpenCreateTransactionIncome()
    {
        CreateTransactionIncome createTransactionIncome = new CreateTransactionIncome();
        createTransactionIncome.show(getParentFragmentManager(), "CreateTransactionIncome");
    }


    public void OpenAddCategories(View v, Budget budget1)
    {
        final Dialog dialog = new Dialog(v.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.fragment_add_categories);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final EditText CategoryET = dialog.findViewById(R.id.CategoryET);
        final FloatingActionButton CategoryAdd = dialog.findViewById(R.id.CategoryAdd);
        final ImageButton closeBt = dialog.findViewById(R.id.closeBt);

        CategoryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = CategoryET.getText().toString();
                if (!category.trim().isEmpty())
                {
                    Category category1 = new Category(category);
                    if (budget1.getIncomeCategories() == null)
                    {
                        budget1.setIncomeCategories(new ArrayList<>());
                    }
                    budget1.getIncomeCategories().add(category1);
                    DatabaseReference budgetRef = database.getReference("budget").child(budget1.getId());
                    budgetRef.child("incomeCategories").setValue(budget1.getIncomeCategories()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(getContext(), "Категория добавлена", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
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
                    Toast.makeText(requireContext(), "Поле не может быть пустым", Toast.LENGTH_SHORT).show();
                }
            }
        });

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dialog.dismiss();}});

        dialog.show();

    }


    }

