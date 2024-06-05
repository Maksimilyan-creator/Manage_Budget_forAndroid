package com.example.managebudget.users;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managebudget.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener
{
    CircleImageView profile_iv;
    TextView username_tv;
    TextView userEmail_tv;
    UsersAdapter.OnUserClickListener onUserClickListener;

    public UsersViewHolder (@NonNull View itemView, UsersAdapter.OnUserClickListener onUserClickListener)
    {
        super(itemView);
        profile_iv = itemView.findViewById(R.id.profile_iv);
        username_tv = itemView.findViewById(R.id.username_tv);
        userEmail_tv = itemView.findViewById(R.id.userEmail_tv);

        this.onUserClickListener = onUserClickListener;
        itemView.setOnClickListener(this);

    }
    @Override
    public void onClick(View v)
    {
        onUserClickListener.onUserClick(getAdapterPosition());
    }
}
