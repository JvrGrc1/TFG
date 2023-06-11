package com.example.tfg.adaptador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.entidad.Temporada;

import java.util.List;

public class TemporadaAdapter extends RecyclerView.Adapter<TemporadaAdapter.TemporadaViewHolder> {

    private final Context contexto;
    private List<Temporada> temporadas;

    public TemporadaAdapter(Context contexto, List<Temporada> temporadas) {
        this.contexto = contexto;
        this.temporadas = temporadas;
    }

    public Temporada getJugador() {return (Temporada) temporadas;}

    public void setJugador(Temporada temporadas) {this.temporadas = (List<Temporada>) temporadas;}

    @NonNull
    @Override
    public TemporadaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contexto).inflate(R.layout.temporada_view, parent, false);
        return new TemporadaViewHolder(view, contexto);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull TemporadaViewHolder holder, int position) {
        Temporada temporada = temporadas.get(position);
        if (temporada.getAnio().length() > 5){
            holder.temp.setText(temporada.getAnio().substring(0,5));
        }else {
            holder.temp.setText(temporada.getAnio());
        }
        holder.equipoJugador.setText(temporada.getEquipo());
        holder.posicionJugador.setText(temporada.getPosicion());
        holder.golesJugador.setText(String.format("%d", temporada.getGoles()));
        holder.disparosJugador.setText(String.format("%d", temporada.getDisparos()));
        holder.paradasJugador.setText(String.format("%d", temporada.getParadas()));
        holder.amarillasJugador.setText(String.format("%d", temporada.getAmarillas()));
        holder.rojasJugador.setText(String.format("%d", temporada.getRojas()));
        holder.dosMinJugador.setText(String.format("%d", temporada.getDosMinutos()));

    }

    @Override
    public int getItemCount() {
        return temporadas.size();
    }

    public static class TemporadaViewHolder extends RecyclerView.ViewHolder {

        TextView temp, equipo, equipoJugador, posicion, posicionJugador,
                goles, golesJugador, disparos, disparosJugador, paradas, paradasJugador,
                amarillas, amarillasJugador, rojas, rojasJugador, dosMin, dosMinJugador;

        CardView cardView;
        ConstraintLayout constraintLayout;

        public TemporadaViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardTemp);
            constraintLayout = itemView.findViewById(R.id.constrianTemp);
            temp = itemView.findViewById(R.id.textViewTemp);
            equipo = itemView.findViewById(R.id.textViewEquipoTemp);
            equipoJugador = itemView.findViewById(R.id.textViewEquipoJugadorTemp);
            posicion = itemView.findViewById(R.id.textViewPosicionTemp);
            posicionJugador = itemView.findViewById(R.id.textViewPosicionJugadorTemp);
            goles = itemView.findViewById(R.id.textViewGolesTemp);
            golesJugador = itemView.findViewById(R.id.textViewGolesJugadorTemp);
            disparos = itemView.findViewById(R.id.textViewDisparosTemp);
            disparosJugador = itemView.findViewById(R.id.textViewDisparosJugadorTemp);
            paradas = itemView.findViewById(R.id.textViewParadasTemp);
            paradasJugador = itemView.findViewById(R.id.textViewParadasJugadorTemp);
            amarillas = itemView.findViewById(R.id.textViewAmarillasTemp);
            amarillasJugador = itemView.findViewById(R.id.textViewAmarillasJugadorTemp);
            rojas = itemView.findViewById(R.id.textViewRojasTemp);
            rojasJugador = itemView.findViewById(R.id.textViewRojasJugadorTemp);
            dosMin = itemView.findViewById(R.id.textViewDosMinTemp);
            dosMinJugador = itemView.findViewById(R.id.textViewDosMinJugadorTemp);

            comprobarModo(context);
        }

        private void comprobarModo(Context context) {
            boolean modoOscuro = context.getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                    .getBoolean("modoOscuro", false);
            if (modoOscuro) {
                cardView.setBackgroundColor(Color.rgb(26, 26, 26));
                constraintLayout.setBackgroundColor(Color.rgb(26, 26, 26));
                temp.setTextColor(Color.WHITE);
                equipo.setTextColor(Color.WHITE);
                equipoJugador.setTextColor(Color.WHITE);
                posicion.setTextColor(Color.WHITE);
                posicionJugador.setTextColor(Color.WHITE);
                goles.setTextColor(Color.WHITE);
                golesJugador.setTextColor(Color.WHITE);
                disparos.setTextColor(Color.WHITE);
                disparosJugador.setTextColor(Color.WHITE);
                paradas.setTextColor(Color.WHITE);
                paradasJugador.setTextColor(Color.WHITE);
                amarillas.setTextColor(Color.WHITE);
                amarillasJugador.setTextColor(Color.WHITE);
                rojas.setTextColor(Color.WHITE);
                rojasJugador.setTextColor(Color.WHITE);
                dosMin.setTextColor(Color.WHITE);
                dosMinJugador.setTextColor(Color.WHITE);
            } else {
                cardView.setBackgroundColor(Color.WHITE);
                constraintLayout.setBackgroundColor(Color.WHITE);
                temp.setTextColor(Color.BLACK);
                equipo.setTextColor(Color.BLACK);
                equipoJugador.setTextColor(Color.BLACK);
                posicion.setTextColor(Color.BLACK);
                posicionJugador.setTextColor(Color.BLACK);
                goles.setTextColor(Color.BLACK);
                golesJugador.setTextColor(Color.BLACK);
                disparos.setTextColor(Color.BLACK);
                disparosJugador.setTextColor(Color.BLACK);
                paradas.setTextColor(Color.BLACK);
                paradasJugador.setTextColor(Color.BLACK);
                amarillas.setTextColor(Color.BLACK);
                amarillasJugador.setTextColor(Color.BLACK);
                rojas.setTextColor(Color.BLACK);
                rojasJugador.setTextColor(Color.BLACK);
                dosMin.setTextColor(Color.BLACK);
                dosMinJugador.setTextColor(Color.BLACK);
            }
        }
    }
}
