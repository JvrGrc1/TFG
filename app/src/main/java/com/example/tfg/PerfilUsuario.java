package com.example.tfg;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PerfilUsuario extends AppCompatActivity {

    private Usuario usuario;
    private TextInputEditText nombre, apellido1, apellido2, correo, tlf, direccion, portal, piso, ciudad, provincia;
    private TextView rol;
    private ImageView imagen;
    private ImageButton edit;
    private ConexionFirebase conexion = new ConexionFirebase();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private ImageButton guardar;
    private Uri cam_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        nombre = findViewById(R.id.nombreMiUsuario);
        imagen = findViewById(R.id.imagenMiUsuario);
        edit = findViewById(R.id.buttonEditarImagenUser);
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
        if (usuario.getImagen() != null && !usuario.getImagen().isEmpty()){conexion.cargarImagen(this, imagen, null, usuario.getImagen());}
        if (usuario.getDireccion() != null && !usuario.getDireccion().isEmpty()) {ponerDireccion(usuario.getDireccion());}
        if (usuario.getApellido2() != null && !usuario.getApellido2().isEmpty()) {apellido2.setText(usuario.getApellido2());}
        if (usuario.getTlf() != null && !usuario.getTlf().isEmpty()) {tlf.setText(usuario.getTlf());}

        guardar.setOnClickListener(v -> {
            if(comprobarNombreyApellidos() & tlfCorrecto() & comprobarDireccion() & correoCorrecto()) {
                new AlertDialog.Builder(PerfilUsuario.this)
                        .setPositiveButton("Confirmar", (dialogInterface, i) -> modificarUser())
                        .setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setTitle("¿Estás seguro?")
                        .setMessage("Si confirmas modificarás los datos de tu usuario.")
                        .show();
            }
        });

        edit.setOnClickListener(v -> {
            String[] opciones = {"Tomar foto", "Elegir de la galeria"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Title")
                    .setItems(opciones, (dialog, which) -> {
                        if (which == 0){
                            //Intent para abrir la camara.
                        }else if (which == 1){
                            Intent elegirImagen = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            elegirDeGaleria.launch(elegirImagen);
                        }else{
                            Toast.makeText(getApplicationContext(), "¿Como has pulsado el " + which + "?. No existe esa opción...", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        textChangedListener();
    }



    private ActivityResultLauncher<Intent> elegirDeGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri imagenUri = result.getData().getData();
                    subirImagen(imagenUri);
                }
            }
    );

    private void subirImagen(Uri imagenUri){
        String[] partes = conexion.obtenerUser().split("@");
        StorageReference ref = storageRef.child("perfilUsuario/" + partes[0] + ".jpg");
        UploadTask uploadTask = ref.putFile(imagenUri);
        uploadTask.continueWithTask(contTask -> {
            if (!contTask.isSuccessful()) {
                Toast.makeText(PerfilUsuario.this, "Error al subir la imagen.", Toast.LENGTH_SHORT).show();
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(completeTask -> {
            if (completeTask.isSuccessful()) {
                Uri downloadUri = completeTask.getResult();
                String urlFoto = downloadUri.toString();
                conexion.cargarImagen(PerfilUsuario.this, imagen, null, urlFoto);
                usuario.setImagen(urlFoto);
            } else {
                Toast.makeText(PerfilUsuario.this, "Error al poner la imagen.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean comprobarNombreyApellidos(){
        String nombre1 = nombre.getText().toString();
        String apellido11 = apellido1.getText().toString();
        String apellido21 = apellido2.getText().toString();
        if (!apellido11.isEmpty() & !nombre1.isEmpty()){
            return true;
        }else{
            if (nombre1.contains(" ")) {
                nombre.setError("No puede contener espacios.");
            } else if (!nombre1.matches("^[^0-9]*$")) {
                nombre.setError("No puede contener números.");
            }
            if (apellido11.contains(" ")){
                apellido1.setError("No puede contener espacios.");
            }else if (!apellido11.matches("^[^0-9]*$")){
                apellido1.setError("No puede contener números.");
            }
            if (apellido21.contains(" ")){
                apellido2.setError("No puede contener espacios.");
            }else if (!apellido21.matches("^[^0-9]*$")){
                apellido2.setError("No puede contener números.");
            }
            return false;
        }
    }
    private boolean comprobarDireccion(){
        String direccion1 = direccion.getText().toString().trim();
        String piso1 = piso.getText().toString().trim();
        String ciudad1 = ciudad.getText().toString().trim();
        String provincia1 = provincia.getText().toString().trim();
        String portal1 = portal.getText().toString().trim();

        if (direccion1.isEmpty() || piso1.isEmpty() || portal1.isEmpty() || ciudad1.isEmpty() || provincia1.isEmpty()){
            if (direccion1.isEmpty() && piso1.isEmpty() && portal1.isEmpty() && ciudad1.isEmpty() && provincia1.isEmpty()) {
                return true;
            }else{
                Toast.makeText(this, "Dirección, Piso, Portal, Ciudad y Provincia tienen que estar: rellenos o vacíos.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            int t = 0;
            if (direccion1.startsWith(" ") || direccion1.endsWith(" ")) {
                direccion.setError("No puede comenzar ni acabar con espacios.");
                t++;
            } else if (!direccion1.matches("^[^0-9]*$")) {
                direccion.setError("No puede contener números.");
                t++;
            }
            if (piso1.matches("^[0-9]+°[a-z]$")){
                piso.setText(piso1.toUpperCase());
            }else if (!piso1.matches("^[0-9]+°[A-Z]$")){
                piso.setError("Formato incorrecto.");
                t++;
            }
            if (ciudad1.startsWith(" ") || ciudad1.endsWith(" ")) {
                ciudad.setError("No puede comenzar ni acabar con espacios.");
                t++;
            } else if (!ciudad1.matches("^[^0-9]*$")){
                ciudad.setError("No puede contener números.");
                t++;
            }
            if (provincia1.startsWith(" ") || provincia1.endsWith(" ")) {
                provincia.setError("No puede comenzar ni acabar con espacios.");
                t++;
            } else if (!provincia1.matches("^[^0-9]*$")){
                provincia.setError("No puede contener números.");
                t++;
            }
            return t == 0;
        }
    }

    private boolean tlfCorrecto(){    //Comprueba que no contenga espacios, que empieze por 6,7,8 o 9 y tenga 9 dígitos.
        String tlf1 = tlf.getText().toString();
        if (tlf1.isEmpty() || tlf1.matches("^[6|7|8|9][0-9]{8}$")){
            return true;
        }else {
            tlf.setError("El valor introducido no es válido.");
            return false;
        }
    }

    private boolean correoCorrecto(){
        String email = correo.getText().toString();
        if (email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            return true;
        }else {
            correo.setError("Mal formao.");
            return false;
        }
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
        return String.format("%s,%s,%s,%s,%s", direccion.getText().toString(), portal.getText().toString(), piso.getText().toString(), provincia.getText().toString(), ciudad.getText().toString());
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
            addTextChangedListener(provincia, partes[3].trim());
            addTextChangedListener(ciudad, partes[4].trim());
        }else{
            addTextChangedListener(direccion, "");
            addTextChangedListener(portal, "");
            addTextChangedListener(piso, "");
            addTextChangedListener(ciudad, "");
            addTextChangedListener(provincia, "");
        }
    }

    private void modificarUser(){
        usuario.setNombre(nombre.getText().toString());
        usuario.setApellido1(apellido1.getText().toString());
        if (apellido2.getText().toString().isEmpty()){
            usuario.setApellido2("");
        }else{
            usuario.setApellido2(apellido2.getText().toString());
        }
        usuario.setCorreo(correo.getText().toString());
        if (tlf.getText().toString().isEmpty()){
            usuario.setTlf("");
        }else{
            usuario.setTlf(tlf.getText().toString());
        }
        if (!direccion.getText().toString().isEmpty()){
            usuario.setDireccion(obtenerDireccion());
        }else{
            usuario.setDireccion("");
        }
        conexion.modificarUser(usuario, this);
        finish();
    }
}