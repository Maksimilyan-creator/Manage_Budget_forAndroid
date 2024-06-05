package com.example.managebudget.budget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managebudget.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BudgetsAdapter extends BaseAdapter
{
    private ArrayList<Budget> budgets = new ArrayList<>();
    private DatabaseReference userRef;
    private LayoutInflater inflater;

    public BudgetsAdapter(ArrayList<Budget> budgets, DatabaseReference userRef, Context context)
    {
        this.budgets = budgets;
        this.userRef = userRef;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return budgets.size();
    }

    @Override
    public Object getItem(int position)
    {
        return budgets.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.budget_item_rv, parent, false);
            holder = new ViewHolder();
            holder.budgetNameTV = convertView.findViewById(R.id.budget_name_tv);
            holder.descriptionTV = convertView.findViewById(R.id.budget_description_tv);
            holder.creatorTV = convertView.findViewById(R.id.creator_name_tv);
            holder.emailTV = convertView.findViewById(R.id.email_tv);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Budget budget = (Budget) getItem(position);
        holder.budgetNameTV.setText(budget.getName());
        holder.descriptionTV.setText(budget.getDescription());

        userRef.child(budget.getCreatorId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String creatorName = snapshot.child("username").getValue(String.class);
                    String creatorEmail = snapshot.child("email").getValue(String.class);
                    holder.creatorTV.setText(creatorName);
                    holder.emailTV.setText(creatorEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BudgetAdapter", "Ошибка при получении информации об авторе: " + error.getMessage());
            }
        });

        return convertView;
    }

    public void updateBudgets(List<Budget> newBudgets)
    {
        budgets.clear();
        budgets.addAll(newBudgets);
        notifyDataSetChanged();
    }

    static class ViewHolder
    {
        TextView budgetNameTV;
        TextView creatorTV;
        TextView descriptionTV;
        TextView emailTV;
    }

}
