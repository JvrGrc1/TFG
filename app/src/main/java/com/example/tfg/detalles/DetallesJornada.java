package com.example.tfg.detalles;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.adaptador.PartidosAdapter;
import com.example.tfg.adaptador.RecyclerItemClickListener;
import com.example.tfg.entidad.Partido;

import java.util.List;

public class DetallesJornada extends AppCompatActivity {

    private RecyclerView recycler;
    private List<Partido> partidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_jornada);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        recycler = findViewById(R.id.recyclerDetallesJornada);

        Intent intent = getIntent();
        partidos = (List<Partido>)intent.getSerializableExtra("partidos");

        View layout = findViewById(R.id.constrainDetallesJornadas);
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.escala);
        layout.startAnimation(animacion);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        PartidosAdapter adapter = new PartidosAdapter(this, partidos);
        recycler.setAdapter(adapter);

        recycler.addOnItemTouchListener(new RecyclerItemClickListener(this, recycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int posicion) {
                Intent intent = new Intent(v.getContext(), DetallesPartido.class);
                intent.putExtra("partido", adapter.getDatos().get(posicion));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View v, int posicion) {}
        }));
    }
}