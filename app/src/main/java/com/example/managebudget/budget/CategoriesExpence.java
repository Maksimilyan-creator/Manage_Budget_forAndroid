package com.example.managebudget.budget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managebudget.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoriesExpence extends DialogFragment
{
    EditText CategoryET;
    ImageButton CategoryAdd;
    ImageButton CategoryUpdate;
    ImageButton closeBt;
    BudgetViewModel budgetViewModel1;
    FirebaseDatabase database;
    DatabaseReference usersRef;
    FirebaseUser currentUser;
    RecyclerView CategoriesRecyclerView;
    Budget budget1;
    CategoryAdapter categoryAdapter;
    Category selectedCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories_income, container,false);

        CategoryET = rootView.findViewById(R.id.CategoryET);
        CategoryAdd = rootView.findViewById(R.id.CategorySave);
        CategoryUpdate = rootView.findViewById(R.id.CategoryUpdate);
        closeBt = rootView.findViewById(R.id.closeBtt);
        CategoriesRecyclerView = rootView.findViewById(R.id.CategoriesList);
        budgetViewModel1 = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        budget1 = new Budget();

        CategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CategoriesRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        CategoryAdapter.OnCategoryClickListener onCategoryClickListener = new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category, int position) {
                Toast.makeText(getContext(), "Был выбран пункт " + category.getName(), Toast.LENGTH_SHORT).show();
                CategoryAdd.setVisibility(View.GONE);
                CategoryUpdate.setVisibility(View.VISIBLE);
                CategoryET.setText(category.getName().toString());
                selectedCategory = category;
            }
        };

        CategoryAdapter.OnCategoryLongClickListener onCategoryLongClickListener = new CategoryAdapter.OnCategoryLongClickListener() {
            @Override
            public void onCategoryLongClick(Category category, int position) {
                RemoveCategory(category);
            }
        };

        categoryAdapter = new CategoryAdapter(getContext(), new ArrayList<>(), onCategoryClickListener, onCategoryLongClickListener);
        CategoriesRecyclerView.setAdapter(categoryAdapter);

        budgetViewModel1.getSelectedBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget)
            {
                if (budget !=null)
                {

                    List<Category> categoryExpenceList = budget.getExpenceCategories();
                    if (categoryExpenceList != null)
                    {

                        categoryAdapter.UpdateAdapter(categoryExpenceList);
                    }
                    else
                    {
                        categoryAdapter.UpdateAdapter(new ArrayList<>());
                    }

                    budget1 = budget;
                }

            }
        });

        CategoryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { AddCategories(); } });

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dismiss(); }});

        CategoryUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { UpdateCategories(); } });

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public void AddCategories()
    {
        String category = CategoryET.getText().toString();
        if (!category.trim().isEmpty())
        {
            Category category1 = new Category(category);
            if (budget1.getExpenceCategories() == null)
            {
                budget1.setExpenceCategories(new ArrayList<>());
            }
            budget1.getExpenceCategories().add(category1);
            DatabaseReference budgetRef = database.getReference("budget").child(budget1.getId());
            budgetRef.child("expenceCategories").setValue(budget1.getExpenceCategories()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getContext(), "Категория добавлена", Toast.LENGTH_SHORT).show();
                        CategoryET.setText("");
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

    public void UpdateCategories() {
        String updateCategory = CategoryET.getText().toString().trim();
        if (updateCategory.isEmpty()) {
            Toast.makeText(requireContext(), "Поле не может быть пустым", Toast.LENGTH_SHORT).show();
            CategoryET.setText("");
            CategoryUpdate.setVisibility(View.GONE);
            CategoryAdd.setVisibility(View.VISIBLE);
            selectedCategory = null;
            return;
        }
        if (updateCategory.equals(selectedCategory.getName())) {
            Toast.makeText(requireContext(), "Категория не изменилась", Toast.LENGTH_SHORT).show();
            CategoryET.setText("");
            CategoryUpdate.setVisibility(View.GONE);
            CategoryAdd.setVisibility(View.VISIBLE);
            selectedCategory = null;
            return;
        }
        selectedCategory.setName(updateCategory);

        DatabaseReference budgetRef = database.getReference("budget").child(budget1.getId());
        budgetRef.child("expenceCategories").setValue(budget1.getExpenceCategories()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Категория обновлена", Toast.LENGTH_SHORT).show();
                    CategoryET.setText("");
                    CategoryUpdate.setVisibility(View.GONE);
                    CategoryAdd.setVisibility(View.VISIBLE);
                    selectedCategory = null;
                } else {
                    Toast.makeText(getContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    public void RemoveCategory(Category category)
    {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить категорию")
                .setMessage("Вы уверены, что хотите удалить категорию " + category.getName() + "?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        budget1.getExpenceCategories().remove(category);

                        DatabaseReference budgetRef = database.getReference("budget").child(budget1.getId());
                        budgetRef.child("expenceCategories").setValue(budget1.getExpenceCategories()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getContext(), "Категория удалена", Toast.LENGTH_SHORT).show();
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
