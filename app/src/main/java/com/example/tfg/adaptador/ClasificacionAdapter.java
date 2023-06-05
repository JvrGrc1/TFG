package com.example.tfg.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.entidad.Partido;

import java.util.List;

public class ClasificacionAdapter  extends RecyclerView.Adapter<ClasificacionAdapter.ClasificacionViewHolder>  {
    private final Context contexto;
    private List<Partido> partidos;

    public ClasificacionAdapter (Context context, List<Partido> partidosList){
        this.contexto = context;
        this.partidos = partidosList;
    }

    public void setDatos(List<Partido> listaDatos) {
        this.partidos = listaDatos;
    }
    public List<Partido> getDatos() {return partidos;}

    @NonNull
    @Override
    public ClasificacionAdapter.ClasificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.clasificacion_view, parent, false);
        return new ClasificacionViewHolder(v, contexto);
    }

    @Override
    public void onBindViewHolder(@NonNull ClasificacionAdapter.ClasificacionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {return partidos.size();}

    public static class ClasificacionViewHolder extends RecyclerView.ViewHolder{

        public ClasificacionViewHolder(@NonNull View itemView, Context contexto) {
            super(itemView);
        }
    }
}
