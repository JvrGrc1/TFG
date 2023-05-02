package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Prenda;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.List;

public class Login extends AppCompatActivity {

    private Button login;
    private TextInputEditText correo, psswrd;
    private TextView nueva;
    private final ConexionFirebase conexion = new ConexionFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.loginButton);
        correo = findViewById(R.id.correoUser);
        psswrd = findViewById(R.id.contrasenaUser);
        nueva = findViewById(R.id.nuevaCuenta);

        login.setOnClickListener(view -> {
            if (correoValido(correo.getText().toString()) && psswrdValida(psswrd.getText().toString())) {
                conexion.signIn(correo.getText().toString(), psswrd.getText().toString(), this);
            }
        });

        nueva.setOnClickListener(view -> finish());
    }

    public void iniciarMainActivity(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            Task<List<Partido>> partidos = conexion.obtenerPartidos();
            partidos.addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    List<Partido> partidos1 = task1.getResult();
                    Task<List<Prenda>> prendas = conexion.obtenerTienda();
                    prendas.addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()){
                            List<Prenda> prendas1 = task2.getResult();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("lista", (Serializable) partidos1);
                            intent.putExtra("ropa", (Serializable) prendas1);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finishAffinity();
                        }else{
                            Toast.makeText(Login.this, "Error obteniendo las prendas", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Error obteniendo partidos", Toast.LENGTH_SHORT).show();
                }
            });
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