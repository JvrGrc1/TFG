package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrarUsuario extends AppCompatActivity {

    private Button registro, ver;

    private EditText correo, psswrd;
    private TextView iniciarSesion;
    private final FirebaseAuth user = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isPsswrdVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        registro = findViewById(R.id.registroButton);
        correo = findViewById(R.id.correoUserRegistro);
        psswrd = findViewById(R.id.psswrdRegistroUser);
        ver = findViewById(R.id.buttonVer);
        iniciarSesion = findViewById(R.id.iniciarSesion);

        registro.setOnClickListener(view -> {
            if (correoValido(correo.getText().toString()) && psswrdValida(psswrd.getText().toString())) {
                user.createUserWithEmailAndPassword(correo.getText().toString(), psswrd.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        agregarUser();
                    } else {
                        Toast.makeText(RegistrarUsuario.this, "El usuario o la contraseña con erroneos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ver.setOnClickListener(v -> {
            if (isPsswrdVisible) {
                psswrd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ver.setBackgroundResource(R.drawable.ojo_abierto);
                isPsswrdVisible = false;
            }else {
                psswrd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ver.setBackgroundResource(R.drawable.ojo_cerrado);
                isPsswrdVisible = true;
            }
        });
        iniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });
    }

    private boolean correoValido(String correo){

        if (correo != null && !correo.equals("")){
            for (int i = 0; i < correo.length(); i++){
                if (correo.charAt(i) == ' ') {
                    this.correo.setError("El correo no puede contener espacios.");
                    return false;
                }
            }
            String formatoCorreo = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; //Expresión regular para validar que un correo está bien formado.
            Pattern pattern = Pattern.compile(formatoCorreo);
            Matcher matcher = pattern.matcher(correo);
            if (matcher.matches()){
                return true;
            }else{
                this.correo.setError("El correo está mal escrito.");
                return false;
            }
        }else {
            this.correo.setError("El correo no puede estar vacío.");
            return false;
        }
    }

    private boolean psswrdValida(String psswrd){
        if (psswrd != null && !psswrd.equals("")){
            for (int i = 0; i < psswrd.length(); i++){
                if (psswrd.charAt(i) == ' ') {
                    this.psswrd.setError("La contraseña no puede contener espacios.");
                    return false;
                }
            }
            if (psswrd.length() < 6){
                this.psswrd.setError("La contraseña debe contener mínimo 6 caracteres.");
                return false;
            }
            return true;
        }else{
            this.psswrd.setError("La contraseña no puede estar vacía.");
            return false;
        }
    }

    private void agregarUser(){
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("correo", correo.getText().toString());
        db.collection("usuarios").add(usuario).addOnSuccessListener(documentReference -> {
            Intent intent = new Intent(getApplicationContext(), DetallesUsuario.class);
            intent.putExtra("correo", correo.getText().toString());
            startActivity(intent);
            finish();
        }).addOnFailureListener(e ->{
            Toast.makeText(this, "Error al agregar el usuario a la base de datos.", Toast.LENGTH_SHORT).show();
        });

    }
}