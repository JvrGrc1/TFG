package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Usuario;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PerfilUsuario extends AppCompatActivity {

    private Usuario usuario;
    private TextInputEditText nombre, apellido1, apellido2, correo, tlf, direccion, portal, piso, ciudad, provincia;
    private TextView rol;
    private ImageView imagen;
    private ConexionFirebase conexion = new ConexionFirebase();
    private ImageButton guardar;

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
        provincia = findViewById(R.id.provinciaMiUsuario);
        ciudad = findViewById(R.id.ciudadMiUsuario);
        piso = findViewById(R.id.pisoMiUsuario);
        portal = findViewById(R.id.portalMiUsuario);
        correo = findViewById(R.id.correoMiUsuario);
        guardar = findViewById(R.id.buttonGuardar);

        Intent intent = getIntent();
        usuario = (Usuario) intent.getSerializableExtra("usuario");
        nombre.setText(usuario.getNombre());
        apellido1.setText(usuario.getApellido1());
        correo.setText(usuario.getCorreo());
        rol.setText(usuario.getRol());
        conexion.cargarImagen(this, imagen, null, usuario.getImagen());
        if (usuario.getDireccion() != null && !usuario.getDireccion().isEmpty()) {ponerDireccion(usuario.getDireccion());}
        if (usuario.getApellido2() != null && !usuario.getApellido2().isEmpty()) {apellido2.setText(usuario.getApellido2());}
        if (usuario.getTlf() != null && !usuario.getTlf().isEmpty()) {tlf.setText(usuario.getTlf());}

        guardar.setOnClickListener(v -> {
            if(comprobarNombre() & comprobarApellidos() & tlfCorrecto() & comprobarDireccion() & correoCorrecto()){
                Toast.makeText(this, "Todo bien", Toast.LENGTH_SHORT).show();
            }
        });

        textChangedListener();

    }

    private boolean comprobarNombre() {
        String nombre1 = nombre.getText().toString();
        if (nombre1.isEmpty()){
            nombre.setError("No puede estar vacío.");
            return false;
        } else if (nombre1.contains(" ")){
            nombre.setError("No puede contener espacios.");
            return false;
        }else{
            return true;
        }
    }
    private boolean comprobarApellidos(){
        String apellido11 = apellido1.getText().toString();
        String apellido21 = apellido2.getText().toString();
        if (apellido11.isEmpty()){
            apellido1.setError("No puede estar vacío.");
            return false;
        } else if (apellido11.contains(" ")){
            apellido1.setError("No puede contener espacios.");
            return false;
        } else if (apellido21.contains(" ")) {
            apellido2.setError("No puede contener espacios.");
            return false;
        }else {
            return true;
        }
    }
    private boolean comprobarDireccion(){
        String direccion1 = direccion.getText().toString();
        String piso1 = piso.getText().toString();
        String ciudad1 = ciudad.getText().toString();
        String provincia1 = provincia.getText().toString();
        String portal1 = portal.getText().toString();

        if (direccion1.isEmpty() & piso1.isEmpty() & portal1.isEmpty() & ciudad1.isEmpty() & provincia1.isEmpty()){
            return true;
        }else {
            if (direccion1.startsWith(" ") || direccion1.endsWith(" ")) {
                direccion.setError("No puede comenzar ni acabar con espacios.");
            } else if (direccion1.contains("^[\\d]*")) {
                direccion.setError("No puede contener números.");
            }
            if (piso1.matches("^\\d+º[a-z]$")){
                piso.setText(piso1.toUpperCase());
            }else if (!piso1.matches("^\\d+º[A-Z]$")) {
                piso.setError("El valor introducido no es válido.");
            }
            if (!portal1.matches("^[0-9]*$")){
                portal.setError("Este campo solo puede contener números");
            }
            if (ciudad1.contains("[^\\d]*")) {
                ciudad.setError("No puede contener números.");
            }
            if (provincia1.contains("[^\\d]*")){
                provincia.setError("No puede contener números.");
            }
            return false;
        }
    }

    private boolean tlfCorrecto(){    //Comprueba que no contenga espacios, que empieze por 6,7,8 o 9 y tenga 9 dígitos.
        String tlf1 = tlf.getText().toString();
        if ( tlf1.isEmpty() || !tlf1.contains(" ") && tlf1.matches("^[6|7|8|9][0-9]{8}$")){
            return true;
        }else{
            tlf.setError("El valor introducido no es válido.");
            return false;
        }
    }

    private boolean correoCorrecto(){
        String email = correo.getText().toString();
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private void ponerDireccion(String direccion1){
        String[] partes = direccion1.split(",");
        direccion.setText(partes[0].trim());
        portal.setText(partes[1].trim());
        piso.setText(partes[2].trim());
        provincia.setText(partes[3].trim());
        ciudad.setText(partes[4].trim());
    }

    private String obtenerDireccion(){
        return String.format("%s,%s,%s,%s,%s", direccion.getText().toString(), portal.getText().toString(), piso.getText().toString(), ciudad.getText().toString(), provincia.getText().toString());
    }

    public void addTextChangedListener(TextInputEditText editText, String usuario) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentValue = s.toString();

                if (!currentValue.equals(usuario)) {
                    guardar.setVisibility(View.VISIBLE);
                } else {
                    guardar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void textChangedListener(){
        addTextChangedListener(nombre, usuario.getNombre());
        addTextChangedListener(apellido1, usuario.getApellido1());
        addTextChangedListener(apellido2, usuario.getApellido2());
        addTextChangedListener(tlf, usuario.getTlf());
        addTextChangedListener(correo, usuario.getCorreo());
        String[] partes = usuario.getDireccion().split(",");
        if (usuario.getDireccion() != null && !usuario.getDireccion().isEmpty()) {
            addTextChangedListener(direccion, partes[0].trim());
            addTextChangedListener(portal, partes[1].trim());
            addTextChangedListener(piso, partes[2].trim());
            addTextChangedListener(ciudad, String.format("%s,%s", partes[3].trim(), partes[4].trim()));
        }else{
            addTextChangedListener(direccion, "");
            addTextChangedListener(portal, "");
            addTextChangedListener(piso, "");
            addTextChangedListener(ciudad, "");
        }
    }
}