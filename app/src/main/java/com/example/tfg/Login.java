package com.example.tfg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    private Button login;
    private TextInputEditText correo, psswrd;
    private TextInputLayout psswrd1, correo1;
    private TextView nueva, inicioSesion;
    private ConstraintLayout constraintLayout;
    private ImageView logo;
    private List<Partido> j = new ArrayList<>();
    private List<Pedido> prendas = new ArrayList<>();
    private final ConexionFirebase conexion = new ConexionFirebase();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        j = (List<Partido>) intent.getSerializableExtra("lista");
        prendas = (List<Pedido>) intent.getSerializableExtra("ropa");

        login = findViewById(R.id.loginButton);
        correo = findViewById(R.id.correoUser);
        psswrd = findViewById(R.id.contrasenaUser);
        nueva = findViewById(R.id.nuevaCuenta);
        constraintLayout = findViewById(R.id.constrainLogin);
        inicioSesion = findViewById(R.id.textViewInicioSesion);
        logo = findViewById(R.id.imageViewInicioSesionLogo);
        psswrd1 = findViewById(R.id.contrasenaUser1);
        correo1 = findViewById(R.id.correoUser1);

        login.setOnClickListener(view -> {
            if (correoValido(correo.getText().toString()) && psswrdValida(psswrd.getText().toString())) {
                conexion.signIn(correo.getText().toString(), psswrd.getText().toString(), this);
            }
        });

        nueva.setOnClickListener(view -> {
            Intent intent1 = new Intent(Login.this, RegistrarUsuario.class);
            intent1.putExtra("lista", (Serializable) j);
            intent1.putExtra("ropa", (Serializable) prendas);
            startActivity(intent1);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finishAffinity();
        });

        SharedPreferences sp = getSharedPreferences("Ajustes", this.MODE_PRIVATE);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        boolean modoOscuro = sp.getBoolean("modoOscuro", false);
        if (modoOscuro){
            window.setStatusBarColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            nueva.setTextColor(Color.WHITE);
            inicioSesion.setTextColor(Color.WHITE);
            logo.setImageDrawable(getDrawable(R.drawable.logo_night));
            correo1.setBoxStrokeColor(Color.WHITE);
            correo1.setHintTextColor(ColorStateList.valueOf(Color.WHITE));
            psswrd1.setBoxStrokeColor(Color.WHITE);
            psswrd1.setHintTextColor(ColorStateList.valueOf(Color.WHITE));
        }else{
            window.setStatusBarColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
            nueva.setTextColor(Color.BLACK);
            inicioSesion.setTextColor(getColor(R.color.azul_oscuro));
            logo.setImageDrawable(getDrawable(R.drawable.logo));
        }
    }

    public void iniciarMainActivity(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("lista", (Serializable) j);
            intent.putExtra("ropa", (Serializable) prendas);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finishAffinity();
        } else {
            Toast.makeText(Login.this, "El usuario o la contraseña con erroneos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean correoValido(String correo){
        if (correo != null && !correo.equals("")){
            for (int i = 0; i < correo.length(); i++){
                if (correo.charAt(i) == ' ') {
                    Toast.makeText(this, "El correo no puede contener espacios", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }else {
            Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean psswrdValida(String psswrd){
        if (psswrd != null && !psswrd.equals("")){
            for (int i = 0; i < psswrd.length(); i++){
                if (psswrd.charAt(i) == ' ') {
                    Toast.makeText(this, "La contraseña no puede contener espacios", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }else{
            Toast.makeText(this, "La contraseña no puede estar vacía.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}