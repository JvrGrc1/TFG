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
import com.example.tfg.entidad.Jugador;
import com.example.tfg.entidad.Temporada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GoleadoresAdapter extends RecyclerView.Adapter<GoleadoresAdapter.GoleadoresViewHolder>  {

    private final Context contexto;
    private List<Jugador> jugadores;

    public GoleadoresAdapter(Context contexto, List<Jugador> jugadores) {
        this.contexto = contexto;
        this.jugadores = jugadores;
        Collections.sort(this.jugadores, new GoleadoresComparator());
        if (jugadores.size() > 20){
            this.jugadores = jugadores.subList(0,10);
        }
    }

    public void setDatos(List<Jugador> listaDatos) {
        this.jugadores = listaDatos;
    }
    public List<Jugador> getDatos() {return jugadores;}

    @NonNull
    @Override
    public GoleadoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.goleadores_view, parent, false);
        return new GoleadoresViewHolder(v, contexto);
    }

    @Override
    public void onBindViewHolder(@NonNull GoleadoresViewHolder holder, int position) {
        Jugador jugador = jugadores.get(position);

        holder.nombre.setText(jugador.getNombre());
        holder.temp.setText(jugador.getTemporadas().get(0).getAnio());
        holder.goles.setText("" + jugador.getTemporadas().get(0).getGoles());
        holder.disparos.setText(""+ jugador.getTemporadas().get(0).getDisparos());

    }

    @Override
    public int getItemCount() {return jugadores.size();}

    public static class GoleadoresViewHolder extends RecyclerView.ViewHolder{

        TextView nombre, goles, disparos, temp;

        public GoleadoresViewHolder(@NonNull View itemView, Context contexto) {
            super(itemView);

            nombre = itemView.findViewById(R.id.textViewNombre);
            goles = itemView.findViewById(R.id.textViewGoles);
            disparos = itemView.findViewById(R.id.textViewDisparos);
            temp = itemView.findViewById(R.id.textViewTemporadaGol);

            boolean modoOscuro = contexto.getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                    .getBoolean("modoOscuro", false);
            if (modoOscuro) {
                nombre.setTextColor(Color.WHITE);
                goles.setTextColor(Color.WHITE);
                disparos.setTextColor(Color.WHITE);
                temp.setTextColor(Color.WHITE);
            } else {
                nombre.setTextColor(Color.BLACK);
                goles.setTextColor(Color.BLACK);
                disparos.setTextColor(Color.BLACK);
                temp.setTextColor(Color.BLACK);
            }
        }
    }
    static class GoleadoresComparator implements Comparator<Jugador> {
        @Override
            public int compare(Jugador p1, Jugador p2) {
            // Ordenar por victorias descendiente
            int resultado = Long.compare(p2.getTemporadas().get(0).getGoles(), p1.getTemporadas().get(0).getGoles());
            if (resultado != 0) {
                return resultado;
            }
            // Ordenar por empates descendiente
            return Long.compare(p1.getTemporadas().get(0).getDisparos(), p2.getTemporadas().get(0).getDisparos());
        }
    }
}
