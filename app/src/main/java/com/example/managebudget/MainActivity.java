package com.example.managebudget;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managebudget.bottomnav.DashboardFragment;
import com.example.managebudget.bottomnav.Statisticsfragment;
import com.example.managebudget.bottomnav.ProfileFragment;
import com.example.managebudget.budget.Budget;
import com.example.managebudget.budget.BudgetViewModel;
import com.example.managebudget.user.UserViewModel;
import com.example.managebudget.users.UsersViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private FrameLayout fragment_container;
    private BottomNavigationView bottom_nav;
    private ConstraintLayout ProggresBar;
    private Spinner budgetSpinner;
    private SharedPreferences sharedPreferences;
    private UserViewModel userViewModel;
    private UsersViewModel usersViewModel;
    private BudgetViewModel budgetViewModel;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private TextView balanceTV;
    private ImageView balanceImage ;
    private String selecredBudgetId = "";
    int textColorRed;
    int textColorWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textColorRed = getResources().getColor(R.color.red, null);
        textColorWhite = getResources().getColor(R.color.white, null);

        toolbar = findViewById(R.id.toolbar);
        fragment_container = findViewById(R.id.fragment_container);
        bottom_nav = findViewById(R.id.bottom_nav);
        ProggresBar = findViewById(R.id.ProggresBar);
        budgetSpinner = findViewById(R.id.budget_spinner);
        balanceTV = findViewById(R.id.balanceTV);
        balanceImage = findViewById(R.id.blanceImage);

        // Натсройка Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Инициализация Firebase и ViewModel
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        storage = FirebaseStorage.getInstance("gs://manage-budget-41977.appspot.com");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        showLoadingAnimation();

        // Загрузка данных из базы

        if (currentUser != null)
        {
            userViewModel.loadUserData(currentUser.getUid(), database.getReference());
            usersViewModel.loadUsersData(database.getReference(), currentUser.getUid());
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
                            GetBalance();
                        }
                        hideLoadingAnimation();
                    });
                }
            });
        }
        else
        {
            navigateToLogin();
            hideLoadingAnimation();
        }

        setupBottomNavigationView();
    }

    private void GetBalance()
    {

        budgetViewModel.getSelectedBudget().observe( MainActivity.this, budget ->
        {
            if (budget != null)
            {
                Double balance = budget.balance();
                balance = Math.floor(balance * 100) / 100;

                if (balance > 0)
                {
                    balanceTV.setText(String.valueOf(balance + " ₽"));
                    balanceTV.setTextColor(textColorWhite);
                    balanceImage.setColorFilter(textColorWhite);
                }
                if (balance == 0)
                {
                    balanceTV.setText(String.valueOf(balance + " ₽"));
                    balanceTV.setTextColor(textColorWhite);
                    balanceImage.setColorFilter(textColorWhite);
                }
                if (balance < 0)
                {
                    balanceTV.setText(String.valueOf(balance + " ₽"));
                    balanceTV.setTextColor(textColorRed);
                    balanceImage.setColorFilter(textColorRed);
                }
            }
            else
            {
                balanceTV.setText("0 ₽");
                balanceTV.setTextColor(textColorWhite);
                balanceImage.setColorFilter(textColorWhite);
            }
        });
    }

    private void setupBottomNavigationView() {
        bottom_nav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            String tag = null;
            int itemId = item.getItemId();

            if (itemId == R.id.profile)
            {
                fragment = getSupportFragmentManager().findFragmentByTag("ProfileFragment");
                if (fragment == null)
                {
                    fragment = new ProfileFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, fragment, tag)
                            .commit();

                }
            }
            else if (itemId == R.id.dashboard)
            {
                fragment = getSupportFragmentManager().findFragmentByTag("DashboardFragment");
                if (fragment == null)
                {
                    fragment = new DashboardFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, fragment, tag)
                            .commit();
                }
            }
            else if (itemId == R.id.statistic)
            {
                fragment = getSupportFragmentManager().findFragmentByTag("Statisticsfragment");
                if (fragment == null)
                {
                    fragment = new Statisticsfragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, fragment, tag)
                            .commit();
                }
            }

            if (fragment != null) {
                for (Fragment frag : getSupportFragmentManager().getFragments()) {
                    getSupportFragmentManager().beginTransaction().hide(frag).commit();
                }
                getSupportFragmentManager().beginTransaction().show(fragment).commit();
            }

            return true;

        });

        bottom_nav.setSelectedItemId(R.id.dashboard);
        Fragment initialFragment = new DashboardFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, initialFragment, "DashboardFragment")
                .commit();
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
                        convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
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

        private void showLoadingAnimation()
        {
            toolbar.setVisibility(View.GONE);
            fragment_container.setVisibility(View.GONE);
            bottom_nav.setVisibility(View.GONE);
            ProggresBar.setVisibility(View.VISIBLE);
        }

        private void hideLoadingAnimation()
        {
            ProggresBar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            fragment_container.setVisibility(View.VISIBLE);
            bottom_nav.setVisibility(View.VISIBLE);
        }

    }



