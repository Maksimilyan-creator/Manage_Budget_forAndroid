package com.example.managebudget.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managebudget.R;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder>
{
    private List<Users> users = new ArrayList<>();
    private OnUserClickListener onUserClickListener;

    public UsersAdapter(List<Users> users, OnUserClickListener onUserClickListener)
    {
        this.users = users;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item_rv, parent, false);

        return new UsersViewHolder(view, onUserClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position)
    {
        holder.username_tv.setText(users.get(position).getUsername());
        holder.userEmail_tv.setText(users.get(position).getUserEmail());

        if(!users.get(position).profileImage.isEmpty())
        {
            Glide.with(holder.itemView.getContext()).load(users.get(position).profileImage).into(holder.profile_iv);
        }

    }

    public void UpdateUsersAdapter(List<Users> newUsers)
    {
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface OnUserClickListener
    {
        void onUserClick(int position);
    }
}
