package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class DetallesJornada extends AppCompatActivity {

    private TextView detalles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_jornada);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        detalles = findViewById(R.id.detallesPartidos);

        Intent intent = getIntent();

        detalles.setText(intent.getSerializableExtra("partidos").toString());

        View layout = findViewById(R.id.l);
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.escala);

        // Asignar la animación al layout
        layout.startAnimation(animacion);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        detalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}