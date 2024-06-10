package com.example.managebudget;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managebudget.bottomnav.DashboardFragment;
import com.example.managebudget.bottomnav.Statisticsfragment;
import com.example.managebudget.bottomnav.ProfileFragment;
import com.example.managebudget.budget.Budget;
import com.example.managebudget.budget.BudgetViewModel;
import com.example.managebudget.budget.Category;
import com.example.managebudget.budget.Transaction;
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
    private Toolbar toolbar;
    private Spinner budgetSpinner;
    private SharedPreferences sharedPreferences;
    private UserViewModel userViewModel;
    private UsersViewModel usersViewModel;
    private BudgetViewModel budgetViewModel;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private String selecredBudgetId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Натсройка Toolbar
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        budgetSpinner = findViewById(R.id.budget_spinner);

        // Инициализация Firebase и ViewModel
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        storage = FirebaseStorage.getInstance("gs://manage-budget-41977.appspot.com");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        // Загрузка данных из базы

        if (currentUser != null)
        {
            userViewModel.loadUserData(currentUser.getUid(), database.getReference());
            budgetViewModel.loadBudgetData(currentUser.getUid(), database.getReference(), new BudgetViewModel.OnBudgetsLoadedListener() {
                @Override
                public void onBudgetsLoaded() {
                    budgetViewModel.getBudgets().observe(MainActivity.this, budgets -> {
                        if (budgets.isEmpty())
                        {
                            showToast("У вас нет бюджетов");
                        }
                        else
                        {
                            setupBudgetSpinner(budgets);
                        }
                    });
                }
            });
            usersViewModel.loadUsersData(database.getReference(), currentUser.getUid());
        }
        else
        {
            navigateToLogin();
        }

        setupBottomNavigationView();
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        // Установка первого фрагмента
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
        bottomNav.setSelectedItemId(R.id.income);

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.profile, new ProfileFragment());
        fragmentMap.put(R.id.income, new DashboardFragment());
        fragmentMap.put(R.id.outlay, new Statisticsfragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());
            if (fragment != null) {
                if (item.getItemId() == R.id.profile)
                {
                    getSupportActionBar().hide();
                }
                else
                {
                    getSupportActionBar().show();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
            return true;
        });
    }

    private void navigateToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void setupBudgetSpinner(List<Budget> budgets) {
            ArrayAdapter<Budget> adapter = new ArrayAdapter<Budget>(this, android.R.layout.simple_spinner_item, budgets)
            {
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
                {
                    if (convertView == null)
                    {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
                    }
                    TextView textView = convertView.findViewById(android.R.id.text1);
                    textView.setText(budgets.get(position).getName());
                    return convertView;
                }

                @Override
                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    if (convertView == null)
                    {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
                    }
                    TextView textView = convertView.findViewById(android.R.id.text1);
                    textView.setText(budgets.get(position).getName());
                    return convertView;
                }
            };
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            budgetSpinner.setAdapter(adapter);

            // Установка выбора бюджета
            String selectedBudgetId = sharedPreferences.getString("selectedBudgetId", null);
            if (selectedBudgetId != null) {
                for (int i = 0; i < budgets.size(); i++) {
                    if (budgets.get(i).getId().equals(selectedBudgetId)) {
                        budgetSpinner.setSelection(i);
                        // Установка в ViewModel
                        budgetViewModel.setSelectedBudget(budgets.get(i));
                        break;
                    }
                }
            }




        // Сохранение выбора бюджета
            budgetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Budget selectedBudget = (Budget) parent.getItemAtPosition(position);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("selectedBudgetId", selectedBudget.getId());
                    editor.apply();

                    //проверка, изменился ли выбранный бюджет
                    if (!selectedBudget.getId().equals(selectedBudgetId))
                    {
                        showToast("Выбран бюджет: " + selectedBudget.getName());
                        selecredBudgetId = selectedBudget.getId();
                    }

                    budgetViewModel.setSelectedBudget(selectedBudget);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }



