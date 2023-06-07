package com.example.tfg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

public class HistoriaClub extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private ScrollView scrollView;
    private TextView titulo, historia, historia1, historia2, historia3;
    private Window window;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia_club);

        constraintLayout = findViewById(R.id.constrainHistoria);
        scrollView = findViewById(R.id.scrollHistoria);
        titulo = findViewById(R.id.textViewTituloHistoria);
        historia = findViewById(R.id.historia);
        historia1 = findViewById(R.id.historia1);
        historia2 = findViewById(R.id.historia2);
        historia3 = findViewById(R.id.historia3);
        window = getWindow();

        boolean modoOscuro = getSharedPreferences("Ajustes", Context.MODE_PRIVATE).getBoolean("modoOscuro", false);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (!modoOscuro){
            window.setStatusBarColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
            scrollView.setBackgroundColor(Color.WHITE);
            titulo.setTextColor(getColor(R.color.azul_oscuro));
            historia.setTextColor(Color.BLACK);
            historia1.setTextColor(Color.BLACK);
            historia2.setTextColor(Color.BLACK);
            historia3.setTextColor(Color.BLACK);
        }else{
            window.setStatusBarColor(Color.BLACK);
            scrollView.setBackgroundColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            titulo.setTextColor(Color.WHITE);
            historia.setTextColor(Color.WHITE);
            historia1.setTextColor(Color.WHITE);
            historia2.setTextColor(Color.WHITE);
            historia3.setTextColor(Color.WHITE);
        }
    }
}