package com.example.tfg.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.entidad.Jornada;
import com.example.tfg.entidad.Partido;

import java.util.ArrayList;
import java.util.List;

public class JornadasAdapter extends RecyclerView.Adapter<JornadasAdapter.JornadaViewHolder> {

    private final Context contexto;
    private List<Jornada> jornada;

    public JornadasAdapter(Context context, List<Partido> partidos) {

        this.contexto = context;
        this.jornada = new ArrayList<>();

        //Quiero rellenar una lista de Jornadas pasandole una lista de Partidos.
        //Cada Jornada consta de: nº de jornada y lista de Partidos
        //Tengo que separar por jornadas los Partidos y con cada lista crear una Jornada que añado a la lista de Jornadas
        if (!partidos.isEmpty()) {
            setJornadas(partidos);
        }
    }

    private void setJornadas(List<Partido> partidos) {
        for(int x = 1; x < partidos.size(); x++){
            List<Partido> elegidos = new ArrayList<>();
            for (Partido p : partidos){
                if (p.getJornada() == x){
                    elegidos.add(p);
                }
            }
            if (!elegidos.isEmpty()) {
                Jornada j = new Jornada(x, elegidos);
                this.jornada.add(j);
            }
        }
    }

    public void setDatos(List<Jornada> listaDatos) {
        this.jornada = listaDatos;
    }
    public void setDatosPartidos(List<Partido> listaPartidos){setJornadas(listaPartidos);}
    public List<Jornada> getDatos() {
        return jornada;
    }

    @NonNull
    @Override
    public JornadasAdapter.JornadaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contexto).inflate(R.layout.jornadas_view, parent, false);
        return new JornadaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JornadasAdapter.JornadaViewHolder holder, int position) {

        Jornada jornada1 =jornada.get(position);

        holder.jornada.setText("Jornada " + jornada1.getJornada());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(contexto));
        PartidosAdapter adapter = new PartidosAdapter(contexto, jornada1.getPartidos());
        holder.recyclerView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return jornada.size();
    }

    public static class JornadaViewHolder extends RecyclerView.ViewHolder{

        TextView jornada;
        RecyclerView recyclerView;

        public JornadaViewHolder(@NonNull View itemView) {
            super(itemView);

            jornada = itemView.findViewById(R.id.jornada);
            recyclerView = itemView.findViewById(R.id.recyclerPartidos);

        }
    }
}
