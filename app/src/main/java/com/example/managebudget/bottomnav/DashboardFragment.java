package com.example.managebudget.bottomnav;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.managebudget.DashBoard.DashboardPagerAdapter;
import com.example.managebudget.R;
import com.example.managebudget.budget.Budget;
import com.example.managebudget.budget.BudgetViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DashboardFragment extends Fragment
{
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private DashboardPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.viewPager);

        pagerAdapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Доходы");
                            break;
                        case 1:
                            tab.setText("Расходы");
                            break;
                        case 2:
                            tab.setText("Цели");
                            break;
                        case 3:
                            tab.setText("Долги");
                            break;
                    }
                }).attach();

        return rootView;
    }

}
