package com.example.tfg.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.entidad.Jugador;

public class JugadorListAdapter extends ListAdapter<Jugador, JugadorListAdapter.JugadorViewHolder> {


    public JugadorListAdapter(JugadorDiff jugadorDiff) {
        super(jugadorDiff);
    }

    @NonNull
    @Override
    public JugadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.jugador_vista_unidad, parent, false);
        return new JugadorViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull JugadorViewHolder holder, int position) {
        Jugador jugador =getItem(position);

        holder.nombre.setText(jugador.getNombre());
        holder.apellido.setText(jugador.getApellido1());
        if (!jugador.getApellido2().isEmpty() || jugador.getApellido2().equals("")){
            holder.apellido.setText(String.format("%s %s", jugador.getApellido1(), jugador.getApellido2()));
        }
    }

    public static class JugadorDiff extends DiffUtil.ItemCallback<Jugador>{

        @Override
        public boolean areItemsTheSame(@NonNull Jugador oldItem, @NonNull Jugador newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Jugador oldItem, @NonNull Jugador newItem) {
            return oldItem.toString().equals(newItem.toString());
        }
    }

    public static class JugadorViewHolder extends RecyclerView.ViewHolder{

        TextView nombre, apellido;

        public JugadorViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.textViewNombre);
            apellido = itemView.findViewById(R.id.textViewApellidos);
        }
    }
}
