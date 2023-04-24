package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.detalles.DetallesUsuario;
import com.google.android.gms.tasks.Task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrarUsuario extends AppCompatActivity {

    private Button registro, ver;

    private EditText correo, psswrd;
    private TextView iniciarSesion;
    private boolean isPsswrdVisible = false;
    private final ConexionFirebase conexion = new ConexionFirebase();
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
                Task<Boolean> taskCorreo = conexion.correoNoRegistrado(correo.getText().toString());
                taskCorreo.addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        boolean valido = task.getResult();
                        if (valido){
                            Intent intent = new Intent(getApplicationContext(), DetallesUsuario.class);
                            intent.putExtra("correo", correo.getText().toString());
                            intent.putExtra("psswrd", psswrd.getText().toString());
                            startActivity(intent);
                            finish();
                        }else {
                            correo.setError("Este correo ya está en uso.");
                        }
                    }else{
                        Toast.makeText(this, "Error validando el correo.", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
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
        });
    }

    private boolean correoValido(String correo){

        if (correo != null && !correo.equals("")){
            if (correo.contains(" ")) {
                this.correo.setError("El correo no puede contener espacios.");
                return false;
            }else{
                String formatoCorreo = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; //Expresión regular para validar que un correo está bien formado.
                Pattern pattern = Pattern.compile(formatoCorreo);
                Matcher matcher = pattern.matcher(correo);
                if (matcher.matches()){
                    return true;
                }else{
                    this.correo.setError("El correo está mal escrito.");
                    return false;
                }
            }
        }else {
            this.correo.setError("El correo no puede estar vacío.");
            return false;
        }
    }

    private boolean psswrdValida(String psswrd){
        if (psswrd != null && !psswrd.equals("")){
            if (psswrd.contains(" ")) {
                this.psswrd.setError("La contraseña no puede contener espacios.");
                return false;
            }else if (psswrd.length() < 6){
                this.psswrd.setError("La contraseña debe contener mínimo 6 caracteres.");
                return false;
            }
            return true;
        }else{
            this.psswrd.setError("La contraseña no puede estar vacía.");
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (!correo.getText().toString().isEmpty() || !psswrd.getText().toString().isEmpty()) {
            new AlertDialog.Builder(RegistrarUsuario.this)
                    .setPositiveButton("Confirmar", (dialogInterface, i) -> finish())
                    .setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setTitle("¿Seguro que quieres hacerlo?")
                    .setMessage("Si sales perderas los datos introducidos.")
                    .show();
        }else{
            finish();
        }
    }
}