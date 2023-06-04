package com.example.tfg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@SuppressLint("UseSwitchCompatOrMaterialCode")
public class Ajustes extends AppCompatActivity {

    private TextView borrar, cambiar;
    private ConexionFirebase conexion = new ConexionFirebase();
    private List<Partido> j = new ArrayList<>();
    private List<Pedido> prendas = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private TextView titulo, oscuro, correo, psswrd, historia, redes, patrocinadores, from, ajustes, tema;
    private LinearLayout user;
    private ImageView jvr;
    private View divider;
    private Switch modo;
    private SharedPreferences sharedPreferences;
    private Window window;
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
        correo = findViewById(R.id.correoAjustes);
        psswrd = findViewById(R.id.psswrdAjustes);
        divider = findViewById(R.id.divider6);
        historia = findViewById(R.id.historia);
        patrocinadores = findViewById(R.id.patrocinadores);
        redes = findViewById(R.id.redes);
        jvr = findViewById(R.id.jvrAjustes);
        from = findViewById(R.id.fromAjustes);
        ajustes = findViewById(R.id.textViewAjustes);
        user = findViewById(R.id.linearUser);
        tema = findViewById(R.id.textViewTema);


        sharedPreferences = getSharedPreferences("Ajustes", Context.MODE_PRIVATE);
        window = getWindow();

        Intent intent = getIntent();
        j = (List<Partido>) intent.getSerializableExtra("lista");
        prendas = (List<Pedido>) intent.getSerializableExtra("ropa");

        if (conexion.obtenerUser() == null) {
            borrar.setVisibility(View.GONE);
            cambiar.setVisibility(View.GONE);
            ajustes.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            //FALTA COLOCAR BIEN: TEMAS
        }

        comprobarModo();
        modo.setOnClickListener(v -> {
            cambiarModo();
            sharedPreferences.edit().putBoolean("modoOscuro", !sharedPreferences.getBoolean("modoOscuro", false)).apply();
        });

        correo.setOnClickListener(v -> cambiarCorreo());
        psswrd.setOnClickListener(v -> cambiarPsswrd());
        borrar.setOnClickListener(v -> borrarCuenta());
        cambiar.setOnClickListener(v -> cambiarCuenta());
        historia.setOnClickListener(v -> verHistoria());
        patrocinadores.setOnClickListener(v -> verPatrocinadores());
        redes.setOnClickListener(v -> verRedes());

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cambiarModo() {
        if (sharedPreferences.getBoolean("modoOscuro", false)){
            window.setStatusBarColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
            modo.setTextColor(Color.BLACK);
            oscuro.setTextColor(Color.BLACK);
            titulo.setTextColor(getColor(R.color.azul_oscuro));
            correo.setTextColor(Color.BLACK);
            psswrd.setTextColor(Color.BLACK);
            historia.setTextColor(Color.BLACK);
            redes.setTextColor(Color.BLACK);
            patrocinadores.setTextColor(Color.BLACK);
            jvr.setImageDrawable(getDrawable(R.drawable.jvr));
            from.setTextColor(getColor(R.color.azul_oscuro_56));
        }else{
            window.setStatusBarColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            modo.setTextColor(Color.WHITE);
            oscuro.setTextColor(Color.WHITE);
            titulo.setTextColor(Color.WHITE);
            correo.setTextColor(Color.WHITE);
            psswrd.setTextColor(Color.WHITE);
            historia.setTextColor(Color.WHITE);
            redes.setTextColor(Color.WHITE);
            patrocinadores.setTextColor(Color.WHITE);
            jvr.setImageDrawable(getDrawable(R.drawable.jvr_night));
            from.setTextColor(Color.WHITE);
        }
    }

    private void cambiarCorreo(){
        //TODO
    }

    private void cambiarPsswrd(){
        //TODO
    }

    private void verHistoria(){
        //TODO
    }

    private void verPatrocinadores(){
        //TODO
    }

    private void verRedes(){
        //TODO
    }

    private void cambiarCuenta() {
        conexion.signOut();
        Intent intentLogin = new Intent(this, Login.class);
        intentLogin.putExtra("lista", (Serializable) j);
        intentLogin.putExtra("ropa", (Serializable) prendas);
        startActivity(intentLogin);
        finishAffinity();
    }

    private void borrarCuenta() {
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
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void comprobarModo() {
        boolean modoOscuro = getSharedPreferences("Ajustes", Context.MODE_PRIVATE).getBoolean("modoOscuro", false);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        modo.setChecked(modoOscuro);
        if (!modoOscuro){
            window.setStatusBarColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
            modo.setTextColor(Color.BLACK);
            titulo.setTextColor(getColor(R.color.azul_oscuro));
            oscuro.setTextColor(Color.BLACK);
            correo.setTextColor(Color.BLACK);
            psswrd.setTextColor(Color.BLACK);
            historia.setTextColor(Color.BLACK);
            redes.setTextColor(Color.BLACK);
            patrocinadores.setTextColor(Color.BLACK);
            jvr.setImageDrawable(getDrawable(R.drawable.jvr));
            from.setTextColor(getColor(R.color.azul_oscuro_56));
        }else{
            window.setStatusBarColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            modo.setTextColor(Color.WHITE);
            titulo.setTextColor(Color.WHITE);
            oscuro.setTextColor(Color.WHITE);
            correo.setTextColor(Color.WHITE);
            psswrd.setTextColor(Color.WHITE);
            historia.setTextColor(Color.WHITE);
            redes.setTextColor(Color.WHITE);
            patrocinadores.setTextColor(Color.WHITE);
            jvr.setImageDrawable(getDrawable(R.drawable.jvr_night));
            from.setTextColor(Color.WHITE);
        }
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