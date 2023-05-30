package com.example.tfg.detalles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tfg.MainActivity;
import com.example.tfg.R;
import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetallesUsuario extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButton, admin, entrenador, jugador;
    private TextInputEditText nombre, apellido1, apellido2, tlf, codigo;
    private TextInputLayout nombre2, apellido12, apellido22, tlf2, codigo2;
    private ConstraintLayout constraintLayout, constrainScroll;
    private Button continuar;
    private TextView titulo, rol;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth user = FirebaseAuth.getInstance();

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_usuario);

        radioGroup = findViewById(R.id.radioGroup);
        codigo = findViewById(R.id.codigoDetallesUsuario);
        codigo2 = findViewById(R.id.textInputLayoutCodigoDetallesUsuario);
        continuar = findViewById(R.id.continuarDetallesUsuario);
        nombre = findViewById(R.id.nombreDetallesUsuario);
        nombre2 = findViewById(R.id.textInputLayoutNombreDetallesUsuario);
        apellido1 = findViewById(R.id.apellido1DetallesUsuario);
        apellido12 = findViewById(R.id.textInputLayoutApellido1DetallesUsuario);
        apellido2 = findViewById(R.id.apellido2DetallesUsuario);
        apellido22 = findViewById(R.id.textInputLayoutApellido2DetallesUsuario);
        tlf = findViewById(R.id.telefonoDetallesUsuario);
        tlf2 = findViewById(R.id.textInputLayoutTelefonoDetallesUsuario);
        constraintLayout = findViewById(R.id.constrainDetallesUsuario);
        constrainScroll = findViewById(R.id.constrainScrollDetallesUsuario);
        admin = findViewById(R.id.adminOption);
        entrenador = findViewById(R.id.entrenadorOption);
        jugador = findViewById(R.id.jugadorOption);
        titulo = findViewById(R.id.tituloDetallesUsuario);
        rol = findViewById(R.id.rolDetallesUsuario);


        String correo = getIntent().getStringExtra("correo");
        String psswrd = getIntent().getStringExtra("psswrd");

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            radioButton = radioGroup.findViewById(checkedId);
            switch (radioButton.getId()){
                case R.id.adminOption:
                    codigo2.setVisibility(View.VISIBLE);
                    codigo.setText("");
                    codigo2.setHint("Administrador");
                    break;
                case R.id.entrenadorOption:
                    codigo2.setVisibility(View.VISIBLE);
                    codigo.setText("");
                    codigo2.setHint("Entrenador");
                    break;
                case R.id.jugadorOption:
                    codigo2.setVisibility(View.INVISIBLE);
                    break;
                default:
                    Toast.makeText(DetallesUsuario.this, radioButton.getId() + "", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        continuar.setOnClickListener(view -> {
            if (correcto(nombre.getText().toString(), nombre) && correcto(apellido1.getText().toString(), apellido1) && comprobarRadioButton()){
                user.createUserWithEmailAndPassword(correo, psswrd).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                        String elegido = radioButton.getText().toString();
                        agregarUser(correo, nombre.getText().toString(), apellido1.getText().toString(), elegido);
                    } else {
                        Toast.makeText(DetallesUsuario.this, "El usuario o la contraseña con erroneos", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        boolean modoOscuro = getSharedPreferences("Ajustes", this.MODE_PRIVATE).getBoolean("modoOscuro", false);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (modoOscuro) {
            window.setStatusBarColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            constrainScroll.setBackgroundColor(Color.BLACK);
            radioGroup.setBackgroundColor(Color.BLACK);
            admin.setTextColor(Color.WHITE);
            entrenador.setTextColor(Color.WHITE);
            jugador.setTextColor(Color.WHITE);
            titulo.setTextColor(Color.WHITE);
            rol.setTextColor(Color.WHITE);
            cambiarTextInputLayout(nombre2, Color.WHITE, R.color.gris_oscurito);
            cambiarTextInputLayout(apellido12, Color.WHITE, R.color.gris_oscurito);
            cambiarTextInputLayout(apellido22, Color.WHITE, R.color.gris_oscurito);
            cambiarTextInputLayout(tlf2, Color.WHITE, R.color.gris_oscurito);
            cambiarTextInputLayout(codigo2, Color.WHITE, R.color.gris_oscurito);
        }else{
            window.setStatusBarColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
            constrainScroll.setBackgroundColor(Color.WHITE);
            radioGroup.setBackgroundColor(Color.WHITE);
            admin.setTextColor(Color.BLACK);
            entrenador.setTextColor(Color.BLACK);
            jugador.setTextColor(Color.BLACK);
            titulo.setTextColor(Color.BLACK);
            rol.setTextColor(Color.BLACK);
        }
    }

    private void cambiarTextInputLayout(TextInputLayout textInputLayout, int color, int colorFondo){
        textInputLayout.setBoxStrokeColor(color);
        textInputLayout.setBackgroundColor(getColor(colorFondo));
        textInputLayout.setHintTextColor(ColorStateList.valueOf(color));
        textInputLayout.setHelperTextColor(ColorStateList.valueOf(color));
    }

    private boolean correcto(String string, EditText text){
        if (!text.equals(apellido2) && string.isEmpty()){
            text.setError("Este campo no puede estar vacío.");
            return false;
        } else if(string.startsWith(" ") || string.endsWith(" ")){
            text.setError("No puede empezar ni termniar no espacios.");
            return false;
        }else if(!string.matches("[a-zA-Z\\s'-]+") || string.matches(".*[-']{2,}.*")){
            text.setError("El nombre solo puede contener: letras, espacios, guiones y apóstrofes.");
            return false;
        }else{
            return true;
        }
    }

    private boolean comprobarRadioButton(){
        if (radioGroup.getCheckedRadioButtonId() == -1){
            return false;
        } else if (radioGroup.getCheckedRadioButtonId() != R.id.jugadorOption && codigo.getText().toString().isEmpty()) {
            codigo.setError("El campo no puede estar vacío.");
            return false;
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.adminOption && !codigo.getText().toString().equals("admin123")) {
                codigo.setError("El codigo es incorrecto.");
                return false;
        }else{
            return true;
        }
    }

    private boolean tlfCorrecto(String tlf){
        if (tlf.isEmpty()){
            return false;
        }else if (tlf.contains(" ")){
            Toast.makeText(this, "No puede contener espacios", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!tlf.matches("^[6|7|8|9][0-9]{8}$")){
            Toast.makeText(this, "El telefono debe tener 9 digitos y empezar por: 6,7,8 o 9.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
        return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void agregarUser(String correo, String nombre, String apellido1, String rol){
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("correo", correo);
        usuario.put("nombre", nombre);
        usuario.put("apellido1", apellido1);
        usuario.put("imagen", "gs://balonmano-f213a.appspot.com/perfilUsuario/default.png");
        usuario.put("rol", rol);
        usuario.put("apellido2", "");
        usuario.put("tlf", "");
        usuario.put("direccion", "");
        if (!apellido2.getText().toString().isEmpty() && correcto(apellido2.getText().toString(), apellido2)){
            usuario.replace("apellido2", apellido2.getText().toString());
        }else if (tlfCorrecto(tlf.getText().toString())){
            usuario.replace("tlf", tlf.getText().toString());
        }
        db.collection("usuarios").add(usuario).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Usuario agregado correctamente.", Toast.LENGTH_SHORT).show();
            intentMainActivity();
        }).addOnFailureListener(e ->Toast.makeText(this, "Error al agregar el usuario a la base de datos.", Toast.LENGTH_SHORT).show());
    }

    private void intentMainActivity() {
        ConexionFirebase conexion = new ConexionFirebase();
        Task<List<Partido>> task = conexion.obtenerPartidos();
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                List<Partido> partidos = task1.getResult();
                Intent intent = new Intent(DetallesUsuario.this, MainActivity.class);
                intent.putExtra("lista", (Serializable) partidos);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            } else {
                Toast.makeText(DetallesUsuario.this, "Error obteniendo partidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {   //Si confirma la salida le devuelve a la MainActivity y si no se queda en DetallesUuario
        new AlertDialog.Builder(DetallesUsuario.this)
                .setPositiveButton("Confirmar", (dialogInterface, i) -> intentMainActivity())
                .setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss())
                .setTitle("¿Estás seguro?")
                .setMessage("Si confirmas volveras a la pantalla principal y no se creará tu usuario.")
                .show();
    }
}