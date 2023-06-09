package com.example.tfg.detalles;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.tfg.R;
import com.example.tfg.adaptador.TemporadaAdapter;
import com.example.tfg.entidad.Jugador;
import com.example.tfg.entidad.Temporada;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class DetallesJugador extends AppCompatActivity {

    private Jugador jugador;
    private ConstraintLayout constraintLayout;
    private ScrollView scrollView;
    private TextView nombre, apellidos, mano, manoText, equipo, equipoText, temporadas, titulo;
    private RecyclerView reycler;
    private TemporadaAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_jugador);

        Intent intent = getIntent();
        jugador = (Jugador) intent.getSerializableExtra("jugador");

        titulo = findViewById(R.id.textViewDetallesJugador);
        nombre = findViewById(R.id.textViewNombreDetalles);
        apellidos = findViewById(R.id.textViewApellidosDetalles);
        mano = findViewById(R.id.textViewManoDetalles);
        manoText = findViewById(R.id.textManoDetalles);
        equipo = findViewById(R.id.textViewEquipoDetalles);
        equipoText = findViewById(R.id.textEquipoDetalles);
        temporadas = findViewById(R.id.textViewTemporadaDetalles);
        reycler = findViewById(R.id.temporadasDetalles);
        constraintLayout = findViewById(R.id.constrainDetalles);
        scrollView = findViewById(R.id.scrollDetalles);

        nombre.setText(jugador.getNombre());
        if (jugador.getApellido2().isEmpty()){
            apellidos.setText(jugador.getApellido1());
        }else{
            apellidos.setText(String.format("%s %s", jugador.getApellido1(), jugador.getApellido2()));
        }
        manoText.setText(jugador.getManoDominante());
        equipoText.setText(comprobarAnio());

        reycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TemporadaAdapter(this, jugador.getTemporadas());
        reycler.setAdapter(adapter);

        if (jugador.getTemporadas().size() > 1){
            temporadas.setText("Temporadas");
        }else{
            temporadas.setText("Temporada");
        }

        comprobarModo();
    }

    @SuppressLint("ResourceAsColor")
    private void comprobarModo(){
        boolean modoOscuro = getSharedPreferences("Ajustes", Context.MODE_PRIVATE).getBoolean("modoOscuro", false);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (modoOscuro){
            window.setStatusBarColor(Color.BLACK);
            scrollView.setBackgroundColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            reycler.setBackgroundColor(Color.BLACK);
            titulo.setTextColor(Color.WHITE);
            nombre.setTextColor(Color.WHITE);
            apellidos.setTextColor(Color.WHITE);
            mano.setTextColor(Color.WHITE);
            manoText.setTextColor(Color.WHITE);
            equipo.setTextColor(Color.WHITE);
            equipoText.setTextColor(Color.WHITE);
            temporadas.setTextColor(Color.WHITE);
        }else{
            window.setStatusBarColor(Color.WHITE);
            scrollView.setBackgroundColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
            reycler.setBackgroundColor(Color.WHITE);
            titulo.setTextColor(getResources().getColor(R.color.azul_oscuro));
            nombre.setTextColor(Color.BLACK);
            apellidos.setTextColor(Color.BLACK);
            mano.setTextColor(Color.BLACK);
            manoText.setTextColor(Color.BLACK);
            equipo.setTextColor(Color.BLACK);
            equipoText.setTextColor(Color.BLACK);
            temporadas.setTextColor(getResources().getColor(R.color.azul_oscuro));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String comprobarAnio(){
        for (Temporada t : jugador.getTemporadas()){
            LocalDate now = LocalDate.now();
            String[] partes = now.toString().split("-");
            String anio = partes[0].substring(2,4);
            String[] temp = t.getAnio().split("-");
            if (anio.equals(temp[0]) || anio.equals(temp[1])){
                return t.getEquipo();
            }
        }
        return "Ninguno";
    }
}