package com.example.managebudget.DashBoard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.managebudget.budget.Debt.Debt;
import com.example.managebudget.budget.Debt.DebtAdapter;
import com.example.managebudget.budget.Goal.CreateGoal;
import com.example.managebudget.budget.Goal.Goal;
import com.example.managebudget.budget.Goal.GoalAdapter;
import com.example.managebudget.budget.Goal.UpdateGoal;
import com.example.managebudget.budget.Payments.addPaymentsGoal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends Fragment
{
    RecyclerView RecyclerViewGoal;
    GoalAdapter goalAdapter;
    FloatingActionButton GoalSaveBt;
    private BudgetViewModel budgetViewModel;
    FirebaseDatabase database;
    DatabaseReference usersRef;
    FirebaseUser currentUser;
    Budget budget;
    List<Goal> goalList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goals, container, false);

        RecyclerViewGoal = rootView.findViewById(R.id.RecyclerViewGoal);
        GoalSaveBt = rootView.findViewById(R.id.GoalSaveBt);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        GoalAdapter.OnGoalClickListener onGoalClickListener = new GoalAdapter.OnGoalClickListener() {
            @Override
            public void onGoalClick(Goal goal, int position) {
                OpenUpdateGoal(goal, position);
            }
        };

        GoalAdapter.OnGoalLongClickListener onGoalLongClickListener = new GoalAdapter.OnGoalLongClickListener() {
            @Override
            public void onGoalLongClick(Goal goal, int position) {
                RemoveGoal(goal);
            }
        };

        GoalAdapter.OnButtonCliclListener onButtonCliclListener = new GoalAdapter.OnButtonCliclListener() {
            @Override
            public void onButtonClick(Goal goal, int position) {
                OpenAddPayments(position, goal);
            }
        };

        goalAdapter = new GoalAdapter(getContext(), new ArrayList<>(), usersRef, onGoalClickListener, onGoalLongClickListener, onButtonCliclListener);
        RecyclerViewGoal.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewGoal.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RecyclerViewGoal.setAdapter(goalAdapter);

        budgetViewModel.getSelectedBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget_) {
                if (budget_ !=null)
                {
                    budget = budget_;
                    goalList = budget_.getGoals();
                    if (goalList !=null)
                    {
                        goalAdapter.UpdateAdapter(goalList);
                    }
                    else
                    {
                        goalAdapter.UpdateAdapter(new ArrayList<>());
                    }
                }
            }
        });

        GoalSaveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCreateGoal();
            }
        });

        return rootView;
    }

    private void OpenCreateGoal()
    {
        CreateGoal createGoal = new CreateGoal();
        createGoal.show(getParentFragmentManager(), "CreateGoal");
    }

    private void RemoveGoal(Goal goal)
    {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить цель")
                .setMessage("Вы уверены, что хотите удалить цель с описанием " + goal.getDescription() + ", на сумму " + goal.getAmount() + " ₽" + "?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        budget.getGoals().remove(goal);

                        DatabaseReference budgetRef = database.getReference("budget").child(budget.getId());
                        budgetRef.child("goals").setValue(budget.getGoals()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getContext(), "Цель удалена", Toast.LENGTH_SHORT).show();
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

    private void OpenUpdateGoal(Goal goal, int positionGoal)
    {
        UpdateGoal updateGoal = new UpdateGoal(goal, positionGoal);
        updateGoal.show(getParentFragmentManager(), "UpdateGoal");
    }

    private void OpenAddPayments(int position, Goal goal)
    {
        addPaymentsGoal addPaymentsGoal = new addPaymentsGoal(position, goal);
        addPaymentsGoal.show(getParentFragmentManager(), "addPaymentsGoal");

    }
}
