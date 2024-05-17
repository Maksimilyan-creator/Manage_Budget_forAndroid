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

public class UsersAdapter extends ArrayAdapter<Users> {

    public UsersAdapter(Context context, ArrayList<Users> users)
    {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Users user = getItem(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.person_item_rv, parent, false);
        }

        ImageView profileImage_iv = convertView.findViewById(R.id.profile_iv);
        TextView username_tv = convertView.findViewById(R.id.username_tv);
        TextView userEmail_tv = convertView.findViewById(R.id.userEmail_tv);

        username_tv.setText(user.getUsername());
        userEmail_tv.setText(user.getUserEmail());

        if (!user.getProfileImage().isEmpty())
        {
            Glide.with(getContext()).load(user.getProfileImage()).into(profileImage_iv);
        }

        return convertView;
    }
@Override
    public long getItemId(int position)
    {
        return position;
    }

}
