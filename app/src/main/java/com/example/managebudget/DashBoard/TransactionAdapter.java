package com.example.managebudget.DashBoard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.managebudget.R;
import com.example.managebudget.budget.BudgetsAdapter;
import com.example.managebudget.budget.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends BaseAdapter
{
    private ArrayList<Transaction> transactionIncome = new ArrayList<>();
    private DatabaseReference userRef;
    private LayoutInflater inflater;

    public TransactionAdapter(ArrayList<Transaction> transactionIncome, DatabaseReference userRef, Context context)
    {
        this.transactionIncome = transactionIncome;
        this.userRef = userRef;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return transactionIncome.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionIncome.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.transaction_item_rv, parent, false);
            holder = new ViewHolder();
            holder.CategoryTV = convertView.findViewById(R.id.CategoryTV);
            holder.AmountTV = convertView.findViewById(R.id.AmountTV);
            holder.DateTV = convertView.findViewById(R.id.DateTV);
            holder.UserNameTV = convertView.findViewById(R.id.UserNameTV);
            holder.UserEmailTV = convertView.findViewById(R.id.UserEmailTV);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Transaction transaction = (Transaction) getItem(position);
        holder.CategoryTV.setText(transaction.getCategory());
        holder.AmountTV.setText(String.valueOf(transaction.getAmount()));
        holder.DateTV.setText(transaction.getDate());

        userRef.child(transaction.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String userName = snapshot.child("username").getValue(String.class);
                    String userEmail = snapshot.child("email").getValue(String.class);
                    holder.UserNameTV.setText(userName);
                    holder.UserEmailTV.setText(userEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Log.e("BudgetAdapter", "Ошибка при получении информации об авторе: " + error.getMessage());
            }
        });

        return convertView;
    }

    public void updateTransaction(List<Transaction> transactionsList)
    {
        transactionIncome.clear();
        transactionIncome.addAll(transactionsList);
        notifyDataSetChanged();
    }

    static class ViewHolder
    {
        TextView CategoryTV;
        TextView AmountTV;
        TextView DateTV;
        TextView UserNameTV;
        TextView UserEmailTV;
    }
}
