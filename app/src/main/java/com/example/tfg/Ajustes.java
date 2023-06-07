package com.example.tfg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class Ajustes extends AppCompatActivity {
    private ConexionFirebase conexion = new ConexionFirebase();
    private List<Partido> j = new ArrayList<>();
    private List<Pedido> prendas = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private TextView titulo, oscuro, correo, psswrd, historia, redes, from, ajustes, tema, borrar, cambiar;;
    private LinearLayout user, line, nuevoEmail;
    private EditText email;
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
        redes = findViewById(R.id.redes);
        jvr = findViewById(R.id.jvrAjustes);
        from = findViewById(R.id.fromAjustes);
        ajustes = findViewById(R.id.textViewAjustes);
        user = findViewById(R.id.linearUser);
        tema = findViewById(R.id.textViewTema);
        line = findViewById(R.id.line1);
        nuevoEmail = findViewById(R.id.nuevoEmail);
        email = findViewById(R.id.correoNuevo);

        sharedPreferences = getSharedPreferences("Ajustes", Context.MODE_PRIVATE);
        window = getWindow();

        Intent intent = getIntent();
        j = (List<Partido>) intent.getSerializableExtra("lista");
        prendas = (List<Pedido>) intent.getSerializableExtra("ropa");

        if (conexion.obtenerUser() == null) {
            line.setVisibility(View.GONE);
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
            jvr.setImageDrawable(getDrawable(R.drawable.jvr_night));
            from.setTextColor(Color.WHITE);
        }
    }

    private void cambiarCorreo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_correo, null);
        builder.setView(dialogView);

        TextInputEditText correoChange = dialogView.findViewById(R.id.correoChange);
        TextInputLayout textInputLayoutCorreoChange = dialogView.findViewById(R.id.textInputLayoutCorreoChange);
        Button contiunar = dialogView.findViewById(R.id.buttonContinuar);
        Button cancelar = dialogView.findViewById(R.id.buttonCancelar);

        correoChange.setEnabled(true);
        AlertDialog dialog = builder.create();
        dialog.show();

        contiunar.setOnClickListener(v -> {
            if (correoValido(correoChange.getText().toString(), textInputLayoutCorreoChange)){
                Task<Boolean> cambiado = conexion.cambiarCorreo(correoChange.getText().toString());
                cambiado.addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Correo modificado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else {
                        Toast.makeText(this, "Error al modificar el correo", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        cancelar.setOnClickListener(v -> dialog.dismiss());
    }
    private boolean correoValido(String correo, TextInputLayout textInputLayout){

        if (correo != null && !correo.equals("")){
            if (correo.contains(" ")) {
                textInputLayout.setError("El correo no puede contener espacios.");
                return false;
            }else{
                String formatoCorreo = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; //Expresión regular para validar que un correo está bien formado.
                Pattern pattern = Pattern.compile(formatoCorreo);
                Matcher matcher = pattern.matcher(correo);
                if (matcher.matches()){
                    textInputLayout.setErrorEnabled(false);
                    return true;
                }else{
                    textInputLayout.setError("El correo está mal escrito.");
                    return false;
                }
            }
        }else {
            textInputLayout.setError("El correo no puede estar vacío.");
            return false;
        }
    }

    private void cambiarPsswrd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_psswrd, null);
        builder.setView(dialogView);

        TextInputEditText psswrdChange = dialogView.findViewById(R.id.psswrdChange);
        TextInputLayout textInputLayoutCorreoChange = dialogView.findViewById(R.id.textInputLayoutPsswrdChange);
        Button contiunar = dialogView.findViewById(R.id.buttonContinuar);
        Button cancelar = dialogView.findViewById(R.id.buttonCancelar);

        psswrdChange.setEnabled(true);
        AlertDialog dialog = builder.create();
        dialog.show();

        contiunar.setOnClickListener(v -> {
            if (psswrdValida(psswrdChange.getText().toString(), textInputLayoutCorreoChange)){
                Task<Boolean> cambiado = conexion.cambiarPsswrd(psswrdChange.getText().toString());
                cambiado.addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Contraseña modificada", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else {
                        Toast.makeText(this, "Error al modificar la contraseña", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        cancelar.setOnClickListener(v -> dialog.dismiss());
    }

    private boolean psswrdValida(String psswrd, TextInputLayout textInputLayout){
        if (psswrd != null && !psswrd.equals("")){
            if (psswrd.contains(" ")) {
                textInputLayout.setError("La contraseña no puede contener espacios.");
                return false;
            }else if (psswrd.length() < 6){
                textInputLayout.setError("La contraseña debe contener mínimo 6 caracteres.");
                return false;
            }
            textInputLayout.setErrorEnabled(false);
            return true;
        }else{
            textInputLayout.setError("La contraseña no puede estar vacía.");
            return false;
        }
    }

    private void verHistoria(){
        Intent intent = new Intent(this, HistoriaClub.class);
        startActivity(intent);
    }

    private void verRedes(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_redes, null);
        builder.setView(dialogView);

        Button ig = dialogView.findViewById(R.id.buttonIg);
        Button yt = dialogView.findViewById(R.id.buttonYT);
        Button twitter = dialogView.findViewById(R.id.buttonTwitter);

        String paqueteIg = "com.instagram.android";
        String paqueteYt = "com.twitter.android";
        String paqueteTwitter = "com.youtube.android";

        AlertDialog dialog = builder.create();
        dialog.show();


        ig.setOnClickListener(v -> {
            if (appInstalada(paqueteIg)) {// Abrir la aplicación de Instagram
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("instagram://user?username=bmleganes"));
                intent.setPackage(paqueteIg);
                startActivity(intent);
            } else {// Abrir el perfil de Instagram en el navegador web
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.instagram.com/bmleganes/"));
                startActivity(intent);
            }
        });
        yt.setOnClickListener(v -> {
            if (appInstalada(paqueteYt)) {// Abrir la aplicación de Instagram
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/user/balonmanoleganes"));
                intent.setPackage(paqueteIg);
                startActivity(intent);
            } else {// Abrir el perfil de Instagram en el navegador web
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/user/balonmanoleganes"));
                startActivity(intent);
            }
        });
        twitter.setOnClickListener(v -> {
            if (appInstalada(paqueteTwitter)) {
                // Abrir la aplicación de Twitter
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("twitter://user?screen_name=cbleganes"));
                intent.setPackage(paqueteTwitter);
                startActivity(intent);
            } else {
                // Abrir el perfil de Twitter en el navegador web
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://twitter.com/cbleganes"));
                startActivity(intent);
            }
        });
    }

    private boolean appInstalada(String app){
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(app, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void cambiarCuenta() {
        conexion.signOut();
        Intent intentLogin = new Intent(this, Login.class);
        intentLogin.putExtra("lista", (Serializable) j);
        intentLogin.putExtra("ropa", (Serializable) prendas);
        startActivity(intentLogin);
        finishAffinity();
    }

    @SuppressLint("ResourceAsColor")
    private void borrarCuenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_sino, null);
        builder.setView(dialogView);

        TextView titulo = dialogView.findViewById(R.id.textViewTitulo);
        TextView msg = dialogView.findViewById(R.id.textViewMsg);
        Button continuar = dialogView.findViewById(R.id.buttonSi);
        Button cancelar = dialogView.findViewById(R.id.buttonNo);

        titulo.setText("¿Estas seguro?");
        msg.setVisibility(View.GONE);

        AlertDialog dialog = builder.create();
        dialog.show();
        continuar.setOnClickListener(v -> {
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
        cancelar.setOnClickListener(v -> dialog.dismiss());
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