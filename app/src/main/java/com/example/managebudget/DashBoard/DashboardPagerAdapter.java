package com.example.managebudget.DashBoard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardPagerAdapter extends FragmentStateAdapter
{
    public DashboardPagerAdapter(@NonNull Fragment fragment)
    {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        switch (position)
        {
            case 0:
                return new IncomeFragment();
            case 1:
                return new ExpenceFragment();
            case 2:
                return new GoalsFragment();
            case 3:
                return new DebtsFragment();
            default:
                return new IncomeFragment();
        }
    }

    @Override
    public int getItemCount()
    {
        return 4;
    }


}
