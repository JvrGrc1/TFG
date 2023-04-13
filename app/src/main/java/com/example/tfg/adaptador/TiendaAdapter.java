package com.example.tfg.adaptador;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TiendaAdapter extends RecyclerView.Adapter<TiendaAdapter.TiendaViewHolder> {
    @NonNull
    @Override
    public TiendaAdapter.TiendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TiendaAdapter.TiendaViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class TiendaViewHolder extends RecyclerView.ViewHolder {
        public TiendaViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
