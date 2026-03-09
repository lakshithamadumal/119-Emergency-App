package com.iamlaky.emergency119.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private int selectedPosition = -1;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.tvName.setText(category.getName());

        Glide.with(holder.itemView.getContext())
                .load(category.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(holder.ivIcon);

        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFE5E9"));
            holder.cardView.setStrokeColor(Color.parseColor("#FF004D"));
            holder.cardView.setStrokeWidth(4);
            category.setSelected(true);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.cardView.setStrokeWidth(0);
            category.setSelected(false);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
        });
    }

    public Category getSelectedCategory() {
        if (selectedPosition != -1) {
            return categories.get(selectedPosition);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        com.google.android.material.card.MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            cardView = (com.google.android.material.card.MaterialCardView) itemView.findViewById(R.id.cardCategory);
        }
    }
}