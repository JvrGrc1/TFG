package com.example.tfg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Prenda;
import com.google.android.gms.tasks.Task;

import org.checkerframework.checker.units.qual.C;

import java.io.Serializable;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    private final ConexionFirebase conexion = new ConexionFirebase();
    private ConstraintLayout constraintLayout;
    private TextView from;
    private ImageView logo, jvr;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        constraintLayout = findViewById(R.id.constrainSplashScreen);
        from = findViewById(R.id.textViewFrom);
        logo = findViewById(R.id.imageViewLogo);
        jvr = findViewById(R.id.imageViewJvr);

        boolean isDarkModeEnabled = getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                .getBoolean("modoOscuro", false);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (isDarkModeEnabled) {
            window.setStatusBarColor(Color.BLACK);
            from.setTextColor(Color.WHITE);
            logo.setImageDrawable(getDrawable(R.drawable.logo_night));
            jvr.setImageDrawable(getDrawable(R.drawable.jvr_night));
            constraintLayout.setBackgroundColor(Color.BLACK);
        }else{
            window.setStatusBarColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
        }

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