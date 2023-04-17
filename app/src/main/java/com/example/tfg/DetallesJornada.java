package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.tfg.adaptador.PartidosAdapter;
import com.example.tfg.entidad.Partido;

import java.util.List;

public class DetallesJornada extends AppCompatActivity {

    private TextView detalles;

    private RecyclerView recycler;
    private List<Partido> partidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_jornada);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        detalles = findViewById(R.id.detallesPartidos);
        recycler = findViewById(R.id.recyclerDetallesJornada);

        Intent intent = getIntent();
        partidos = (List<Partido>)intent.getSerializableExtra("partidos");

        View layout = findViewById(R.id.l);
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.escala);

        // Asignar la animación al layout
        layout.startAnimation(animacion);

        layout.setOnClickListener(v -> finish());
        detalles.setOnClickListener(v -> {/*Método vacío*/});

        recycler.setLayoutManager(new LinearLayoutManager(this));
        PartidosAdapter adapter = new PartidosAdapter(this, partidos);
        recycler.setAdapter(adapter);
    }
}