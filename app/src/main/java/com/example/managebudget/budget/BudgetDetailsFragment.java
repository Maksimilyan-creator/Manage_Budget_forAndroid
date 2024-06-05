package com.example.managebudget.budget;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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
import com.example.managebudget.user.User;
import com.example.managebudget.user.UserViewModel;
import com.example.managebudget.users.Users;
import com.example.managebudget.users.UsersAdapter;
import com.example.managebudget.users.UsersViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetDetailsFragment extends DialogFragment
{
    UserViewModel userViewModel1;
    UsersViewModel usersViewModel2;
    ImageButton closeBt;
    ImageButton Save_edit_details_budget_bt;
    RecyclerView participantsRecyclerView;
    FloatingActionButton add_participath;
    EditText budget_nameEt;
    EditText budget_description;
    TextView budget_creator;
    TextView budget_creator_Email;

    Budget budget;
    DatabaseReference userRef;
    FirebaseDatabase database;

    public BudgetDetailsFragment (Budget _budget, DatabaseReference _userRef, FirebaseDatabase _database)
    {
        budget = _budget;
        userRef = _userRef;
        database = _database;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget_details, container, false);

        closeBt = rootView.findViewById(R.id.closeBt);
        Save_edit_details_budget_bt = rootView.findViewById(R.id.Save_edit_details_budget_bt);
        participantsRecyclerView = rootView.findViewById(R.id.participantsRecyclerView);
        add_participath = rootView.findViewById(R.id.add_participath);
        budget_nameEt = rootView.findViewById(R.id.text_budget_name);
        budget_description = rootView.findViewById(R.id.text_budget_description);
        budget_creator = rootView.findViewById(R.id.text_budget_creator);
        budget_creator_Email = rootView.findViewById(R.id.text_budget_creator_Email);

        budget_nameEt.setText(budget.getName());
        budget_description.setText(budget.getDescription());
        String budgetId = budget.getId();




        userViewModel1 = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel1.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    budget_creator.setText(user.getUsername());
                    budget_creator_Email.setText(user.getUserEmail());

                }
            }
        });

        usersViewModel2 = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);
        loadUsersParticipant();



        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Save_edit_details_budget_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameBudget = budget.getName();
                String descriptionBudget = budget.getDescription();
                String newNameBudget = budget_nameEt.getText().toString();
                String newDescriptionBudget = budget_description.getText().toString();
                SaveDetails(nameBudget, descriptionBudget, newNameBudget, newDescriptionBudget, budgetId, database);

            }
        });

        add_participath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddPaticipath(v, userRef, database);
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

    private void SaveDetails(String nameBudget, String descriptionBudget, String newNameBudget, String newDescriptionBudget, String budgetId, FirebaseDatabase database) {
        if (newNameBudget.isEmpty() || newDescriptionBudget.isEmpty()) {
            Toast.makeText(getContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
        } else {
            {
                DatabaseReference budgetRef = database.getReference("budget").child(budgetId);

                // Создаем мапу с новыми значениями
                Map<String, Object> updates = new HashMap<>();
                if (!newNameBudget.equals(nameBudget)) {
                    updates.put("name", newNameBudget);
                }
                if (!newDescriptionBudget.equals(descriptionBudget)) {
                    updates.put("description", newDescriptionBudget);
                }

                // Проверяем, есть ли что обновлять
                if (updates.isEmpty()) {
                    Toast.makeText(getContext(), "Нет изменений для обновления", Toast.LENGTH_SHORT).show();
                } else {
                    // Выполняем обновление
                    budgetRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Обновлено", Toast.LENGTH_SHORT).show();
                            loadUsersParticipant();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Не удалось обновить данные: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
    private void openAddPaticipath(View v, DatabaseReference usersRef, FirebaseDatabase databas) {
        final Dialog dialog = new Dialog(v.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.fragment_all_users);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RecyclerView usersRecyclerView = dialog.findViewById(R.id.users_rv);
        ImageButton closeBt = dialog.findViewById(R.id.closeBt_users);

        UsersViewModel usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);

        usersViewModel.getUsersLiveData().observe(getViewLifecycleOwner(), new Observer<List<Users>>() {
            @Override
            public void onChanged(List<Users> users) {
                usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                usersRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                UsersAdapter adapter = new UsersAdapter(users, new UsersAdapter.OnUserClickListener() {
                    @Override
                    public void onUserClick(int position) {
                        String userId = users.get(position).getId();
                        addParticipathToBudget(userId);
                        loadUsersParticipant();
                        dialog.dismiss();
                    }
                });
                usersRecyclerView.setAdapter(adapter);
            }
        });

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addParticipathToBudget(String userId)
    {
        if (budget.getParticipantIds() == null)
        {
            budget.setParticipantIds(new ArrayList<>());
        }
        if (budget.getParticipantIds().contains(userId))
        {
            Toast.makeText(getContext(), "Пользователь уже добавлен", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Подождите", Toast.LENGTH_SHORT).show();

        budget.getParticipantIds().add(userId);

        DatabaseReference budgetRef = database.getReference("budget").child(budget.getId());
        budgetRef.child("participantIds").setValue(budget.getParticipantIds()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getContext(), "Участник добавлен", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getContext(), "Ошибка добавления участника", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadUsersParticipant()
    {
        usersViewModel2.getUsersLiveData().observe(getViewLifecycleOwner(), new Observer<List<Users>>() {
            @Override
            public void onChanged(List<Users> allusers)
            {
                List<Users> filteredUsers = new ArrayList<>();
                for (Users user : allusers)
                {
                    if (budget.getParticipantIds() != null && budget.getParticipantIds().contains(user.getId()))
                    {
                        filteredUsers.add(user);
                    }
                }
                participantsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                participantsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                UsersAdapter adapter1 = new UsersAdapter(filteredUsers, new UsersAdapter.OnUserClickListener() {
                    @Override
                    public void onUserClick(int position) {
                        return;
                    }
                });

                participantsRecyclerView.setAdapter(adapter1);
            }
        });
    }
}
