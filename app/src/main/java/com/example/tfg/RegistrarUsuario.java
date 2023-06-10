package com.example.tfg;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.detalles.DetallesUsuario;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrarUsuario extends AppCompatActivity {

    private Button registro;

    private EditText correo, psswrd;
    private TextView iniciarSesion, registroUsuario, ayuda;
    private ConstraintLayout constraintLayout;
    private TextInputLayout psswrd1, correo1;
    private ImageView logo;
    private List<Partido> j = new ArrayList<>();
    private List<Pedido> prendas = new ArrayList<>();
    private final ConexionFirebase conexion = new ConexionFirebase();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        Intent intentRecibido = getIntent();
        j = (List<Partido>) intentRecibido.getSerializableExtra("lista");
        prendas = (List<Pedido>) intentRecibido.getSerializableExtra("ropa");

        registro = findViewById(R.id.registroButton);
        correo = findViewById(R.id.correoUser);
        psswrd = findViewById(R.id.contrasenaUser);
        iniciarSesion = findViewById(R.id.iniciarSesion);
        constraintLayout = findViewById(R.id.constrainRegistrarUsuario);
        logo = findViewById(R.id.imageViewRegistroLogo);
        registroUsuario = findViewById(R.id.textViewRegistro);
        ayuda = findViewById(R.id.textView6);
        psswrd1 = findViewById(R.id.contrasenaUser1);
        correo1 = findViewById(R.id.correoUser1);

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
                            intent.putExtra("lista", (Serializable) j);
                            intent.putExtra("ropa", (Serializable) prendas);
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

        iniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            intent.putExtra("lista", (Serializable) j);
            intent.putExtra("ropa", (Serializable) prendas);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        boolean modoOscuro = getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                .getBoolean("modoOscuro", false);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (modoOscuro){
            window.setStatusBarColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            logo.setImageDrawable(getDrawable(R.drawable.logo_night));
            iniciarSesion.setTextColor(Color.WHITE);
            registroUsuario.setTextColor(Color.WHITE);
            ayuda.setTextColor(Color.WHITE);
            correo1.setBoxStrokeColor(Color.WHITE);
            correo1.setHintTextColor(ColorStateList.valueOf(Color.WHITE));
            psswrd1.setBoxStrokeColor(Color.WHITE);
            psswrd1.setHintTextColor(ColorStateList.valueOf(Color.WHITE));
        }else {
            window.setStatusBarColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
            logo.setImageDrawable(getDrawable(R.drawable.logo));
            iniciarSesion.setTextColor(Color.BLACK);
            registroUsuario.setTextColor(getColor(R.color.azul_oscuro));
            ayuda.setTextColor(Color.BLACK);
        }
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_sino, null);
            builder.setView(dialogView);

            TextView titulo = dialogView.findViewById(R.id.textViewTitulo);
            TextView msg = dialogView.findViewById(R.id.textViewMsg);
            Button continuar = dialogView.findViewById(R.id.buttonSi);
            Button cancelar = dialogView.findViewById(R.id.buttonNo);

            AlertDialog dialog = builder.create();
            dialog.show();

            titulo.setText("¿Seguro que quieres hacerlo?");
            msg.setText("Si sales perderas los datos introducidos.");
            cancelar.setText("Salir");

            cancelar.setOnClickListener(v -> finish());
            continuar.setOnClickListener(v -> dialog.dismiss());
        }else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("correo", correo.getText().toString());
            intent.putExtra("psswrd", psswrd.getText().toString());
            intent.putExtra("lista", (Serializable) j);
            intent.putExtra("ropa", (Serializable) prendas);
            startActivity(intent);
            finish();
        }
    }
}