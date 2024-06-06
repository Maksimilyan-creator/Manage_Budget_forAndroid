package com.example.managebudget.budget;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.managebudget.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CreateCategoriesIncome extends DialogFragment
{
    ListView CategoryIncomeLV;
    FloatingActionButton AddCategoryBt;
    FirebaseDatabase database;
     BudgetViewModel budgetViewModel;
     CategoryAdapter categoryAdapter;
     List<Category> categoryList;
    Budget budget1;

    ImageButton closeBtt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_income, container, false);

        CategoryIncomeLV = rootView.findViewById(R.id.CategoryIncomeLV);
        AddCategoryBt = rootView.findViewById(R.id.AddCategoryBt);

        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");

        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), requireContext());
        CategoryIncomeLV.setAdapter(categoryAdapter);
        closeBtt = rootView.findViewById(R.id.closeBtt);



        budget1 = new Budget();


        budgetViewModel.getSelectedBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget)
            {
                if (budget !=null)
                {
                    budget1 = budget;
                    List<Category> categoryIncomeList = budget.getIncomeCategories();
                    if (categoryIncomeList != null)
                    {
                        categoryAdapter.updateCategories(categoryIncomeList);
                    }

                }

            }
        });

        closeBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        AddCategoryBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {OpenAddCategories(v);}});

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public void OpenAddCategories(View v)
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
