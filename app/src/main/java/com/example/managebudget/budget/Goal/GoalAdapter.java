package com.example.managebudget.budget.Goal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managebudget.R;
import com.example.managebudget.budget.Debt.Debt;
import com.example.managebudget.budget.Debt.DebtAdapter;
import com.example.managebudget.budget.Payments.Payments;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder>
{
    private final LayoutInflater inflater;
    private final List<Goal> goalList;
    private final DatabaseReference usersRef;

    private OnGoalClickListener onGoalClickListener;
    private OnGoalLongClickListener onGoalLongClickListener;
    private OnButtonCliclListener onButtonCliclListener;

    public GoalAdapter (Context context, List<Goal> goalList, DatabaseReference usersRef, OnGoalClickListener onGoalClickListener, OnGoalLongClickListener onGoalLongClickListener, OnButtonCliclListener onButtonCliclListener)
    {
        this.inflater = LayoutInflater.from(context);
        this.goalList = goalList;
        this.usersRef = usersRef;
        this.onGoalClickListener = onGoalClickListener;
        this.onGoalLongClickListener = onGoalLongClickListener;
        this.onButtonCliclListener = onButtonCliclListener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.goal_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goal goal = goalList.get(position);
        double totalPayments = 0;

        if(goal.getPayments() != null)
        {
            for (Payments payments: goal.getPayments())
            {
                totalPayments += payments.getAmount();
            }

            if(totalPayments >= goal.getAmount())
            {
                holder.status.setText("Средства собраны");
            }
            else
            {
                holder.status.setText("Средства не собраны");
            }
        }
        else
        {
            holder.status.setText("Средства не собраны");
        }

        holder.DescriptionTv.setText(goal.getDescription());
        holder.Amount.setText(String.valueOf(goal.getAmount() + " ₽"));
        holder.PaymentAmount.setText(String.valueOf(totalPayments + " ₽"));
        holder.Deadline.setText(goal.getDeadline());

        usersRef.child(goal.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                onButtonCliclListener.onButtonClick(goal, position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoalClickListener.onGoalClick(goal, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onGoalLongClickListener.onGoalLongClick(goal, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    public void UpdateAdapter(List<Goal> newGoalList)
    {
        goalList.clear();
        goalList.addAll(newGoalList);
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

    public interface OnGoalClickListener
    {
        void onGoalClick(Goal goal, int position);
    }

    public interface OnGoalLongClickListener
    {
        void onGoalLongClick(Goal goal, int position);
    }

    public interface OnButtonCliclListener
    {
        void onButtonClick (Goal goal, int position);
    }
}
