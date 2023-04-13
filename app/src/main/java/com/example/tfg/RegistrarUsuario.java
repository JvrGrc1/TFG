package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrarUsuario extends AppCompatActivity {

    private Button registro;

    private EditText correo, psswrd;
    private FirebaseAuth user = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        registro = findViewById(R.id.registroButton);
        correo = findViewById(R.id.correoUserRegistro);
        psswrd = findViewById(R.id.psswrdRegistroUser);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (correoValido(correo.getText().toString()) && psswrdValida(psswrd.getText().toString())) {
                    user.signInWithEmailAndPassword(correo.getText().toString(), psswrd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), DetallesUsuario.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegistrarUsuario.this, "El usuario o la contraseña con erroneos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean correoValido(String correo){

        if (correo != null & !correo.equals("")){
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
        if (psswrd != null & !psswrd.equals("")){
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