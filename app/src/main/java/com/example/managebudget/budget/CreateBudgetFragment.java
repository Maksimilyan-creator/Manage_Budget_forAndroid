package com.example.managebudget.budget;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.managebudget.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class CreateBudgetFragment extends DialogFragment
{
    FloatingActionButton createButton;
    ImageButton close;
    FirebaseDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_budget, container, false);

        createButton = rootView.findViewById(R.id.BudgetCreateBt);
        close = rootView.findViewById(R.id.closeBt);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        createButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {
                    EditText nameEditText = rootView.findViewById(R.id.BudgetNameEt);
                    EditText descriptionEditText  = rootView.findViewById(R.id.BudgetDisriptionEt);
                    String name = nameEditText.getText().toString();
                    String description = descriptionEditText.getText().toString();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (name.isEmpty() || description.isEmpty())
                    {
                        Toast.makeText(getContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String budgetId = database.getReference("budget").push().getKey();
                        Budget budget = new Budget(budgetId, name, description, userId);
                        DatabaseReference budgetRef = database.getReference("budget").child(budgetId);

                        budgetRef.setValue(budget).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(getContext(), "Бюджет успешно создан", Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Ошибка при создании бюджета: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    Log.e("CreateBudgetFragment", "Произошла ошибка: " + ex.getMessage());
                    Toast.makeText(getContext(), "Произошла ошибка" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
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
}
