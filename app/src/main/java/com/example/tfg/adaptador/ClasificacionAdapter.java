package com.example.tfg.adaptador;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.entidad.Clasificacion;
import com.example.tfg.entidad.Partido;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClasificacionAdapter  extends RecyclerView.Adapter<ClasificacionAdapter.ClasificacionViewHolder>  {
    private final Context contexto;
    private List<Clasificacion> clasificacion;

    public ClasificacionAdapter (Context context, List<Clasificacion> clasificacionsList){
        this.contexto = context;
        this.clasificacion = clasificacionsList;
        Collections.sort(this.clasificacion, new ClasificacionComparator());
        if (clasificacionsList.size() > 10){
            this.clasificacion = clasificacionsList.subList(0,10);
        }
    }

    public void setDatos(List<Clasificacion> listaDatos) {
        this.clasificacion = listaDatos;
    }
    public List<Clasificacion> getDatos() {return clasificacion;}

    @NonNull
    @Override
    public ClasificacionAdapter.ClasificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.clasificacion_view, parent, false);
        return new ClasificacionViewHolder(v, contexto);
    }

    @Override
    public void onBindViewHolder(@NonNull ClasificacionAdapter.ClasificacionViewHolder holder, int position) {
        Clasificacion clasificacion1 = clasificacion.get(position);

        holder.posicion.setText("" + (position + 1));
        holder.temp.setText(clasificacion1.getTemporada());
        holder.puntos.setText("" + ((clasificacion1.getVictoria() * 2) + clasificacion1.getEmpate()));
        holder.vde.setText(String.format("%d/%d/%d", clasificacion1.getVictoria(), clasificacion1.getDerrota(), clasificacion1.getEmpate()));
        holder.equipo.setText(setNombre(clasificacion1.getNombre()));

    }
    private String setNombre(String nombre){
        switch (nombre){
            case "1NM":
                return "1NacionalMasc";
            case "DHPF":
                return "DHPF";
            case "1NF":
                return "1NacionalFem";
            case "2NM":
                return "2NacionalMasc";
            case "1TM":
                return "1TerritorialMasc";
            case "2TM":
                return "2TerritorialMasc";
        }
        return null;
    }

    @Override
    public int getItemCount() {return clasificacion.size();}

    public static class ClasificacionViewHolder extends RecyclerView.ViewHolder{

        TextView posicion, equipo, temp, vde, puntos;

        public ClasificacionViewHolder(@NonNull View itemView, Context contexto) {
            super(itemView);

            posicion = itemView.findViewById(R.id.textViewPosicion);
            equipo = itemView.findViewById(R.id.textViewEquipo);
            temp = itemView.findViewById(R.id.textViewTemporada);
            vde = itemView.findViewById(R.id.textViewVicEmpDer);
            puntos = itemView.findViewById(R.id.textViewPuntos);

            boolean modoOscuro = contexto.getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                    .getBoolean("modoOscuro", false);
            if (modoOscuro) {
                posicion.setTextColor(Color.WHITE);
                equipo.setTextColor(Color.WHITE);
                temp.setTextColor(Color.WHITE);
                vde.setTextColor(Color.WHITE);
                puntos.setTextColor(Color.WHITE);
            } else {
                posicion.setTextColor(Color.BLACK);
                equipo.setTextColor(Color.BLACK);
                temp.setTextColor(Color.BLACK);
                vde.setTextColor(Color.BLACK);
                puntos.setTextColor(Color.BLACK);
            }
        }
    }

    static class ClasificacionComparator implements Comparator<Clasificacion> {
        @Override
        public int compare(Clasificacion p1, Clasificacion p2) {
            // Ordenar por victorias descendiente
            int resultado = Long.compare(p2.getVictoria(), p1.getVictoria());
            if (resultado != 0) {
                return resultado;
            }
            // Ordenar por empates descendiente
            resultado = Long.compare(p2.getEmpate(), p1.getEmpate());
            if (resultado != 0) {
                return resultado;
            }
            // Ordenar por derrotas descendiente
            return Long.compare(p2.getDerrota(), p1.getDerrota());
        }
    }
}
