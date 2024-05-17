package com.example.managebudget;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.managebudget.bottomnav.IncomeFragment;
import com.example.managebudget.bottomnav.OutlayFragment;
import com.example.managebudget.bottomnav.ProfileFragment;
import com.example.managebudget.budget.Budget;
import com.example.managebudget.budget.BudgetViewModel;
import com.example.managebudget.user.User;
import com.example.managebudget.user.UserViewModel;
import com.example.managebudget.users.Users;
import com.example.managebudget.users.UsersViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private UsersViewModel usersViewModel;
    FirebaseDatabase database;
    FirebaseStorage storage;
    private BudgetViewModel budgetViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        storage = FirebaseStorage.getInstance("gs://manage-budget-41977.appspot.com");
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);


        // Начальная загрузка данных о пользователе

        if (userId != null)
        {
            loadUserInfo(userId.getUid());
            loadBudgetInfo(userId.getUid());
            loadUsersInfo();
        }
        else
        {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IncomeFragment()).commit();
        bottomNav.setSelectedItemId(R.id.income);

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.profile, new ProfileFragment());
        fragmentMap.put(R.id.income, new IncomeFragment());
        fragmentMap.put(R.id.outlay, new OutlayFragment());

        bottomNav.setOnItemSelectedListener(item ->
        {
            Fragment fragment = fragmentMap.get(item.getItemId());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        });

    }

    private void loadUserInfo( String userId)
    {
        DatabaseReference userRef = database.getReference().child("users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String profileImage = snapshot.child("profileImage").getValue(String.class);
                String userEmail = snapshot.child("email").getValue(String.class);
                User user = new User(username, userEmail, profileImage);
                userViewModel.setUser(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadBudgetInfo(String userId)
    {
        DatabaseReference budgetRef = database.getReference("budget");
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Budget> userBudgets = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Budget budget = dataSnapshot.getValue(Budget.class);
                    if (budget.getCreatorId().equals(userId) || budget.getParticipatnIds().contains(userId))
                    {
                        userBudgets.add(budget);
                    }
                }
                if (userBudgets.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "У вас нет бюджетов", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    budgetViewModel.setBudgets(userBudgets);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Ошибка при загрузке бюджетов пользователя из Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsersInfo()
    {
        ArrayList<Users> users = new ArrayList<>();

        database.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot :snapshot.getChildren())
                {
                    if (userSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        continue;
                    }

                    String userId = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue().toString();
                    String userEmail = userSnapshot.child("email").getValue().toString();
                    String profileImage = userSnapshot.child("profileImage").getValue().toString();

                    users.add(new Users(userId,username, userEmail, profileImage));

                }

                usersViewModel.setUsers(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}