package com.example.managebudget.budget;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.managebudget.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BudgetDetailsFragment extends DialogFragment
{
    private Budget budget;
    private TextView budgetNameTextView;
    private TextView budgetDescriptionTextView;
    private TextView budgetCreatorTextView;
    private String budgetId;
    FirebaseDatabase database;
    DatabaseReference usersRef;
    private BudgetViewModel budgetViewModel;


    public static BudgetDetailsFragment newInstance(String budgetId)
    {
        Bundle args = new Bundle();
        args.putString("budgetId", budgetId);
        BudgetDetailsFragment fragment = new BudgetDetailsFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget_details, container, false);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");

        budgetNameTextView = rootView.findViewById(R.id.text_budget_name);
        budgetDescriptionTextView = rootView.findViewById(R.id.text_budget_description);
        budgetCreatorTextView = rootView.findViewById(R.id.text_budget_creator);

        if (getArguments() != null)
        {
            budgetId = getArguments().getString("budgetId");
        }

        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        budgetViewModel.getBudgetById(budgetId).observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget) {
                if (budget !=null)
                {
                    budgetNameTextView.setText(budget.getName());
                    budgetDescriptionTextView.setText(budget.getDesription());
                    usersRef.child(budget.getCreatorId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                String creatorName = snapshot.child("username").getValue(String.class);
                                budgetCreatorTextView.setText(creatorName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("BudgetAdapter", "Ошибка при получении информации об авторе: " + error.getMessage());
                        }
                    });

                }
            }
        });

        return rootView;
    }
}
