package com.iamlaky.emergency119.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.ivIcon.setImageResource(category.getIconRes());

        // Select වුණාම border එකක් හෝ background එකක් මාරු කරන්න
        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFE5E9")); // ලා රතු පාට
            holder.cardView.setStrokeColor(Color.parseColor("#FF004D")); // තද රතු බෝඩරය
            holder.cardView.setStrokeWidth(4);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.cardView.setStrokeWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() { return categories.size(); }

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