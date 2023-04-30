package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

public class PerfilUsuario extends AppCompatActivity {

    private TextInputEditText nombre, apellido1, apellido2, tlf, direccion;
    private TextView rol;
    private ImageView imagen;
    private ConexionFirebase conexion = new ConexionFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        nombre = findViewById(R.id.nombreMiUsuario);
        imagen = findViewById(R.id.imagenMiUsuario);
        rol = findViewById(R.id.rolMiUsuario);
        apellido1 = findViewById(R.id.apellido1MiUsuario);
        apellido2 = findViewById(R.id.apellido2MiUsuario);
        tlf = findViewById(R.id.telefonoMiUsuario);
        direccion = findViewById(R.id.direccionMiUsuario);

        Intent intent = getIntent();
        Usuario usuario = (Usuario) intent.getSerializableExtra("usuario");
        nombre.setText(usuario.getNombre());
        apellido1.setText(usuario.getApellido1());
        rol.setText(usuario.getRol());
        conexion.cargarImagen(this, imagen, null, usuario.getImagen());
        if (usuario.getApellido2() != null){apellido2.setText(usuario.getApellido2());}
        else if (usuario.getDireccion() != null){direccion.setText(usuario.getDireccion());}
        else if (usuario.getTlf() != null) { tlf.setText(usuario.getTlf());}
    }
}