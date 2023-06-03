package com.example.tfg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ajustes extends AppCompatActivity {

    private TextView borrar, cambiar;
    private ConexionFirebase conexion = new ConexionFirebase();
    private List<Partido> j = new ArrayList<>();
    private List<Pedido> prendas = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private TextView titulo, oscuro;
    private Switch modo;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        borrar = findViewById(R.id.borrarCuenta);
        cambiar = findViewById(R.id.cambiarCuenta);
        constraintLayout = findViewById(R.id.constrainAjustes);
        modo = findViewById(R.id.switchModo);
        titulo = findViewById(R.id.textViewTituloAjustes);
        oscuro = findViewById(R.id.oscuro);

        Intent intent = getIntent();
        j = (List<Partido>) intent.getSerializableExtra("lista");
        prendas = (List<Pedido>) intent.getSerializableExtra("ropa");

        if (conexion.obtenerUser() == null) {
            borrar.setVisibility(View.INVISIBLE);
            cambiar.setVisibility(View.INVISIBLE);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("Ajustes", Context.MODE_PRIVATE);
        boolean modoOscuro = sharedPreferences.getBoolean("modoOscuro", false);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        modo.setChecked(modoOscuro);
        if (!modoOscuro){
            window.setStatusBarColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
            modo.setTextColor(Color.BLACK);
            titulo.setTextColor(getColor(R.color.azul_oscuro));
            oscuro.setTextColor(Color.BLACK);
        }else{
            window.setStatusBarColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            modo.setTextColor(Color.WHITE);
            titulo.setTextColor(Color.WHITE);
            oscuro.setTextColor(Color.WHITE);
        }
        modo.setOnClickListener(v -> {
            if (sharedPreferences.getBoolean("modoOscuro", false)){
                window.setStatusBarColor(Color.WHITE);
                constraintLayout.setBackgroundColor(Color.WHITE);
                modo.setTextColor(Color.BLACK);
                oscuro.setTextColor(Color.BLACK);
                titulo.setTextColor(getColor(R.color.azul_oscuro));
                sharedPreferences.edit().putBoolean("modoOscuro", !sharedPreferences.getBoolean("modoOscuro", false)).apply();
            }else{
                window.setStatusBarColor(Color.BLACK);
                constraintLayout.setBackgroundColor(Color.BLACK);
                modo.setTextColor(Color.WHITE);
                oscuro.setTextColor(Color.WHITE);
                titulo.setTextColor(Color.WHITE);
                sharedPreferences.edit().putBoolean("modoOscuro", !sharedPreferences.getBoolean("modoOscuro", false)).apply();
            }
        });

        borrar.setOnClickListener(v -> {
            Task<Boolean> borrado = conexion.borrarCuenta(conexion.obtenerUser().getEmail(), Ajustes.this);
            borrado.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent1 = new Intent(this, MainActivity.class);
                    intent1.putExtra("lista", (Serializable) j);
                    intent1.putExtra("ropa", (Serializable) prendas);
                    startActivity(intent1);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finishAffinity();
                    Toast.makeText(this, "Cuenta borrada", Toast.LENGTH_SHORT).show();
                }
            });
        });

        cambiar.setOnClickListener(v -> {
            conexion.signOut();
            Intent intentLogin = new Intent(this, Login.class);
            intentLogin.putExtra("lista", (Serializable) j);
            intentLogin.putExtra("ropa", (Serializable) prendas);
            startActivity(intentLogin);
            finishAffinity();
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("lista", (Serializable) j);
        intent.putExtra("ropa", (Serializable) prendas);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finishAffinity();
    }
}