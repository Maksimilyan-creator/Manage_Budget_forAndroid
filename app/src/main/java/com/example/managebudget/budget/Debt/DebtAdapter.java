package com.example.managebudget.budget.Debt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managebudget.R;
import com.example.managebudget.budget.Payments;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.ViewHolder>
{
    private final LayoutInflater inflater;
    private final List<Debt> debtList;
    private final DatabaseReference usersRef;

    private OnDebtClickListener onDebtClickListener;
    private OnDebtLongClickListener onDebtLongClickListener;

    public DebtAdapter (Context context, List<Debt> debtList, DatabaseReference usersRef, OnDebtClickListener onDebtClickListener, OnDebtLongClickListener onDebtLongClickListener )
    {
        this.inflater = LayoutInflater.from(context);
        this.debtList = debtList;
        this.usersRef = usersRef;
        this.onDebtClickListener = onDebtClickListener;
        this.onDebtLongClickListener = onDebtLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.debt_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Debt debt = debtList.get(position);

        double totalPayments = 0;

        if (debt.getPayments() != null)
        {
            for (Payments payment : debt.getPayments())
            {
                totalPayments += payment.getAmount();
            }

            if(totalPayments >= debt.getAmount())
            {
                holder.status.setText("Оплачено");
            }
            else
            {
                holder.status.setText("Не оплачено");
            }
        }
        else
        {
            holder.status.setText("Не оплачено");
        }


        holder.DescriptionTv.setText(debt.getDescription());
        holder.Amount.setText(String.valueOf(debt.getAmount() + " ₽"));
        holder.PaymentAmount.setText(String.valueOf(totalPayments));
        holder.Deadline.setText(debt.getDeadline());

        usersRef.child(debt.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
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

        holder.addPaymentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(inflater.getContext(), "Ну молодец хули", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDebtClickListener.onDebtClick(debt, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onDebtLongClickListener.onDebtLongClick(debt, position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return debtList.size();
    }

    public void UpdateAdapter(List<Debt> newDebtList)
    {
        debtList.clear();
        debtList.addAll(newDebtList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView status;
        final TextView DescriptionTv;
        final TextView Amount;
        final TextView PaymentAmount;
        final TextView Deadline;
        final TextView UserNameTV;
        final TextView UserEmailTV;
        final Button addPaymentBt;

        ViewHolder(View view)
        {
            super(view);
            status = view.findViewById(R.id.status);
            DescriptionTv = view.findViewById(R.id.DescriptionTv);
            Amount = view.findViewById(R.id.Amount);
            PaymentAmount = view.findViewById(R.id.PaymentAmount);
            Deadline = view.findViewById(R.id.Deadline);
            UserNameTV = view.findViewById(R.id.UserNameTV);
            UserEmailTV = view.findViewById(R.id.UserEmailTV);
            addPaymentBt = view.findViewById(R.id.addPaymentBt);
        }
    }

    public interface OnDebtClickListener
    {
        void onDebtClick(Debt debt, int position);
    }

    public interface OnDebtLongClickListener
    {
        void onDebtLongClick(Debt debt, int position);
    }
}
