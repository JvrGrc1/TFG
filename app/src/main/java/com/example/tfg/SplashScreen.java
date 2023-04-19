package com.example.tfg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Prenda;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    private final ConexionFirebase conexion = new ConexionFirebase();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Task<List<Partido>> partidos = conexion.obtenerPartidos();
        partidos.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Partido> partidos1 = task.getResult();
                Task<List<Prenda>> prendas = conexion.obtenerTienda();
                prendas.addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        List<Prenda> prendas1 = task1.getResult();
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        intent.putExtra("lista", (Serializable) partidos1);
                        intent.putExtra("ropa", (Serializable) prendas1);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }else{
                        Toast.makeText(SplashScreen.this, "Error obteniendo las prendas", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(SplashScreen.this, "Error obteniendo partidos", Toast.LENGTH_SHORT).show();
            }
        });

    }
}