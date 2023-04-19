package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private Button login;
    private EditText correo, passwd;
    private TextView nueva;
    FirebaseAuth user = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.loginButton);
        correo = findViewById(R.id.correoUser);
        passwd = findViewById(R.id.contrasenaUser);
        nueva = findViewById(R.id.nuevaCuenta);

        login.setOnClickListener(view -> {
            if (correoValido(correo.getText().toString()) && psswrdValida(passwd.getText().toString())) {
                user.signInWithEmailAndPassword(correo.getText().toString(), passwd.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "El usuario o la contraseña con erroneos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        nueva.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegistrarUsuario.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean correoValido(String correo){

        if (correo != null && !correo.equals("")){
            for (int i = 0; i <= correo.length(); i++){
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
            for (int i = 0; i <= psswrd.length(); i++){
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