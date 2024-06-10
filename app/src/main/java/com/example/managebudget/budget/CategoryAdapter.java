package com.example.managebudget.budget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managebudget.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>
{
    private final LayoutInflater inflater;
    private final List<Category> categories;
    private OnCategoryClickListener onCategoryClickListener;
    private OnCategoryLongClickListener onCategoryLongClickListener;

    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener onCategoryClickListener, OnCategoryLongClickListener onCategoryLongClickListener)
    {
        this.categories = categories;
        this.inflater = LayoutInflater.from(context);
        this.onCategoryClickListener = onCategoryClickListener;
        this.onCategoryLongClickListener = onCategoryLongClickListener;
    }


    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryNameTextView.setText(category.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClickListener.onCategoryClick(category, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onCategoryLongClickListener.onCategoryLongClick(category, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void UpdateAdapter(List<Category> newCategories)
    {
        categories.clear();
        categories.addAll(newCategories);

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView categoryNameTextView;
        ViewHolder(View view)
        {
            super(view);
            categoryNameTextView = view.findViewById(R.id.categoryNameTextView);
        }
    }

    interface OnCategoryClickListener
    {
        void onCategoryClick(Category category, int position);
    }

    interface OnCategoryLongClickListener
    {
        void onCategoryLongClick(Category category, int position);
    }

}


