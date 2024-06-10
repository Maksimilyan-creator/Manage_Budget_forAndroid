package com.example.managebudget.budget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managebudget.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>
{

    private final LayoutInflater inflater;
    private final List<Transaction> transactionIncomeList;
    private final DatabaseReference usersRef;

    private OnTransactionClickLisneter onTransactionClickLisneter;
    private OnTransactionLongClickListener onTransactionLongClickListener;


    public TransactionAdapter (Context context, List<Transaction> transactionIncomeList, DatabaseReference usersRef, OnTransactionClickLisneter onTransactionClickLisneter, OnTransactionLongClickListener onTransactionLongClickListener)
    {
        this.inflater = LayoutInflater.from(context);
        this.transactionIncomeList = transactionIncomeList;
        this.usersRef = usersRef;
        this.onTransactionClickLisneter = onTransactionClickLisneter;
        this.onTransactionLongClickListener = onTransactionLongClickListener;
    }


    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.transaction_item_rv, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionIncomeList.get(position);
        holder.AmountTV.setText(String.valueOf(transaction.getAmount() + " рублей"));
        holder.DateTV.setText(transaction.getDate());
        holder.CategoryTV.setText(transaction.getCategory());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTransactionClickLisneter.onTransactionClick(transaction, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onTransactionLongClickListener.onTransactionLongClick(transaction, position);
                return true;
            }
        });

        usersRef.child(transaction.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String creatorName = snapshot.child("username").getValue(String.class);
                String creatorEmail = snapshot.child("email").getValue(String.class);
                holder.UserNameTV.setText(creatorName);
                holder.UserEmailTV.setText(creatorEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return transactionIncomeList.size();
    }

    public void UpdateAdapter(List<Transaction> newTransaction)
    {
        transactionIncomeList.clear();
        transactionIncomeList.addAll(newTransaction);

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView CategoryTV;
        final TextView AmountTV;
        final TextView DateTV;
        final TextView UserNameTV;
        final TextView UserEmailTV;

        ViewHolder(View view)
        {
            super(view);
            CategoryTV = view.findViewById(R.id.CategoryTV);
            AmountTV = view.findViewById(R.id.AmountTV);
            DateTV = view.findViewById(R.id.DateTV);
            UserNameTV = view.findViewById(R.id.UserNameTV);
            UserEmailTV = view.findViewById(R.id.UserEmailTV);
        }

    }

   public interface OnTransactionClickLisneter
    {
        void onTransactionClick(Transaction transaction, int position);
    }

   public interface OnTransactionLongClickListener
    {
        void onTransactionLongClick (Transaction transaction, int position);
    }
}
