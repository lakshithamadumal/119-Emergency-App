package com.iamlaky.emergency119.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iamlaky.emergency119.R;

import java.util.List;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ViewHolder> {
    private List<Uri> imageUris;

    public SelectedImageAdapter(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = imageUris.get(position);
        holder.imageView.setImageURI(uri);
        holder.btnRemove.setOnClickListener(v -> {
            imageUris.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() { return imageUris.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        com.google.android.material.button.MaterialButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSelectedImage);
            btnRemove = (com.google.android.material.button.MaterialButton) itemView.findViewById(R.id.btnRemoveImage);
        }
    }
}
