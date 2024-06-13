package com.example.managebudget.budget.Payments;

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

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder>
{
    private final LayoutInflater inflater;
    private final List<Payments> paymentsList;

    private final DatabaseReference userRef;

    public PaymentAdapter(Context context, List<Payments> paymentsList, DatabaseReference userRef )
    {
        this.inflater = LayoutInflater.from(context);
        this.paymentsList = paymentsList;
        this.userRef = userRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.payment_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payments payments = paymentsList.get(position);

        holder.AmountTV.setText(String.valueOf(payments.getAmount()));
        holder.DateTV.setText(payments.getDate());

        userRef.child(payments.getPayerId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        return paymentsList.size();
    }

    public void UpdateAdapter(List<Payments> newPayments)
    {
        paymentsList.clear();
        paymentsList.addAll(newPayments);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView AmountTV;
        TextView DateTV;
        TextView UserNameTV;
        TextView UserEmailTV;

        ViewHolder(View view)
        {
            super(view);
            AmountTV = view.findViewById(R.id.AmountTV);
            DateTV = view.findViewById(R.id.DateTV);
            UserNameTV = view.findViewById(R.id.UserNameTV);
            UserEmailTV = view.findViewById(R.id.UserEmailTV);
        }
    }
}
