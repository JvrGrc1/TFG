package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ConexionFirebase conexion = new ConexionFirebase();
        Task<List<Partido>> partidos = conexion.obtenerPartidos();
        partidos.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Partido> partidos1 = task.getResult();
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.putExtra("lista", (Serializable) partidos1);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            } else {
                Toast.makeText(SplashScreen.this, "Error obteniendo partidos", Toast.LENGTH_SHORT).show();
            }
        });

    }
}