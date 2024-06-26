package com.example.tfg.adaptador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.entidad.Partido;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PartidosAdapter extends RecyclerView.Adapter<PartidosAdapter.PartidosViewHolder>  {

    private final Context contexto;
    private List<Partido> partidos;

    public PartidosAdapter(Context context, List<Partido> partidosList){
        this.contexto = context;
        this.partidos = partidosList;
        ordenar(this.partidos);
    }

    private void ordenar(List<Partido> partidos) {
        Comparator<Partido> comparador = (partido1, partido2) -> {
            String division1 = partido1.getDivision();
            String division2 = partido2.getDivision();

            if (division1.equals("DHPF")) {
                return -1;
            } else if (division2.equals("DHPF")) {
                return 1;
            } else if (division1.equals("1NM")) {
                return -1;
            } else if (division2.equals("1NM")) {
                return 1;
            } else if (division1.equals("1NF")) {
                return -1;
            } else if (division2.equals("1NF")) {
                return 1;
            } else if (division1.equals("2NM")) {
                return -1;
            } else if (division2.equals("2NM")) {
                return 1;
            } else if (division1.equals("1TM")) {
                return -1;
            } else if (division2.equals("1TM")) {
                return 1;
            } else {
                return 0;
            }
        };
        Collections.sort(partidos, comparador);
    }

    public void setDatos(List<Partido> listaDatos) {
        this.partidos = listaDatos;
    }
    public List<Partido> getDatos() {return partidos;}

    @NonNull
    @Override
    public PartidosAdapter.PartidosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.partidos_view, parent, false);
        return new PartidosViewHolder(v, contexto);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidosAdapter.PartidosViewHolder holder, int position) {

        Partido partido = partidos.get(position);
        holder.division.setText(partido.getDivision());
        holder.local.setText(partido.getLocal());
        holder.visitante.setText(partido.getVisitante());
        setGoles(holder, partido);
        setCoronas(holder, partido);
    }

    private static void setCoronas(@NonNull PartidosViewHolder holder, Partido partido) {
        if (Integer.parseInt(partido.getGolesLocal().toString()) > Integer.parseInt(partido.getGolesVisitante().toString())){
            holder.coronaLocal.setVisibility(View.VISIBLE);
        } else if (Integer.parseInt(partido.getGolesLocal().toString()) < Integer.parseInt(partido.getGolesVisitante().toString())){
            holder.coronaVisitante.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("DefaultLocale")
    private static void setGoles(@NonNull PartidosViewHolder holder, Partido partido) {
        if (partido.getGolesVisitante() != 0 && partido.getGolesLocal() != 0) {
            holder.golesLocal.setText(String.format("%d", partido.getGolesLocal()));
            holder.golesVisitante.setText(String.format("%d", partido.getGolesVisitante()));
        }else{
            holder.golesLocal.setText("-");
            holder.golesVisitante.setText("-");
        }
    }

    @Override
    public int getItemCount() {return partidos.size();}

    public static class PartidosViewHolder extends RecyclerView.ViewHolder{

        TextView division, local, visitante, golesLocal,  golesVisitante, puntos;
        ImageView coronaLocal, coronaVisitante;

        public PartidosViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            division = itemView.findViewById(R.id.division);
            local = itemView.findViewById(R.id.local);
            visitante = itemView.findViewById(R.id.visitante);
            golesLocal = itemView.findViewById(R.id.golesLocal);
            golesVisitante = itemView.findViewById(R.id.golesVisitante);
            coronaLocal = itemView.findViewById(R.id.coronaLocal);
            coronaVisitante = itemView.findViewById(R.id.coronaVisitante);
            puntos = itemView.findViewById(R.id.puntos);

            comprobarModo(context);
        }

        private void comprobarModo(Context context) {
            boolean modoOscuro = context.getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                    .getBoolean("modoOscuro", false);
            if (modoOscuro) {
                division.setTextColor(Color.WHITE);
                local.setTextColor(Color.WHITE);
                visitante.setTextColor(Color.WHITE);
                golesLocal.setTextColor(Color.WHITE);
                golesVisitante.setTextColor(Color.WHITE);
                puntos.setTextColor(Color.WHITE);
            } else {
                division.setTextColor(Color.BLACK);
                local.setTextColor(Color.BLACK);
                visitante.setTextColor(Color.BLACK);
                golesLocal.setTextColor(Color.BLACK);
                golesVisitante.setTextColor(Color.BLACK);
                puntos.setTextColor(Color.BLACK);
            }
        }
    }
}
