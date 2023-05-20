package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

public class MetodoDePago extends AppCompatActivity {

    private ImageButton volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_de_pago);
        volver = findViewById(R.id.buttonVolver);

        volver.setOnClickListener(v -> finish());
    }
}