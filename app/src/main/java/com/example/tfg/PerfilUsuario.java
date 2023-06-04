package com.example.tfg;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PerfilUsuario extends AppCompatActivity {

    private Usuario usuario;
    private TextInputEditText nombre, apellido1, correo, apellido2, tlf, direccion, portal, piso, ciudad, provincia;
    private TextInputLayout nombre1, apellido11, correo1, apellido21, tlf1, direccion1, portal1, piso1, ciudad1, provincia1;
    private ConstraintLayout constraintLayout, constraintLayout2;
    private TextView rol, titulo;
    private ImageView imagen;
    private ImageButton edit;
    private ConexionFirebase conexion = new ConexionFirebase();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private ImageButton guardar;
    private Uri nuevaUri;
    private List<Partido> partidos = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        nuevaUri = null;

        nombre = findViewById(R.id.nombreMiUsuario);
        nombre1 = findViewById(R.id.textInputLayoutNombreMiUsuario);
        imagen = findViewById(R.id.imagenMiUsuario);
        edit = findViewById(R.id.buttonEditarImagenUser);
        rol = findViewById(R.id.rolMiUsuario);
        apellido1 = findViewById(R.id.apellido1MiUsuario);
        apellido11 = findViewById(R.id.textInputLayoutApellido1MiUsuario);
        apellido2 = findViewById(R.id.apellido2MiUsuario);
        apellido21 = findViewById(R.id.textInputLayoutApellido2MiUsuario);
        tlf = findViewById(R.id.telefonoMiUsuario);
        tlf1 = findViewById(R.id.textInputLayoutTelefonoMiUsuario);
        direccion = findViewById(R.id.direccionMiUsuario);
        direccion1 = findViewById(R.id.textInputLayoutDireccionMiUsuario);
        provincia = findViewById(R.id.provinciaMiUsuario);
        provincia1 = findViewById(R.id.textInputLayoutprovinciaMiUsuario);
        ciudad = findViewById(R.id.ciudadMiUsuario);
        ciudad1 = findViewById(R.id.textInputLayoutCiudadMiUsuario);
        piso = findViewById(R.id.pisoMiUsuario);
        piso1 = findViewById(R.id.textInputLayoutPisoMiUsuario);
        portal = findViewById(R.id.portalMiUsuario);
        portal1 = findViewById(R.id.textInputLayoutPortalMiUsuario);
        correo = findViewById(R.id.correoMiUsuario);
        correo1 = findViewById(R.id.textInputLayoutCorreoMiUsuario);
        guardar = findViewById(R.id.buttonGuardar);
        constraintLayout = findViewById(R.id.constrainLayoutPerfilUsuario);
        constraintLayout2 = findViewById(R.id.constrainLayoutPerfilUsuario2);
        titulo = findViewById(R.id.textViewTitulo);

        Intent intent = getIntent();
        usuario = (Usuario) intent.getSerializableExtra("usuario");
        partidos = (List<Partido>) intent.getSerializableExtra("partidos");
        pedidos = (List<Pedido>) intent.getSerializableExtra("prendas");
        nombre.setText(usuario.getNombre());
        apellido1.setText(usuario.getApellido1());
        correo.setText(usuario.getCorreo());
        rol.setText(usuario.getRol());
        if (usuario.getImagen() != null && !usuario.getImagen().isEmpty()){conexion.cargarImagen(this, imagen, null, usuario.getImagen());}
        if (usuario.getDireccion() != null && !usuario.getDireccion().isEmpty()) {ponerDireccion(usuario.getDireccion());}
        if (usuario.getApellido2() != null && !usuario.getApellido2().isEmpty()) {apellido2.setText(usuario.getApellido2());}
        if (usuario.getTlf() != null && !usuario.getTlf().isEmpty()) {tlf.setText(usuario.getTlf());}

        guardar.setOnClickListener(v -> {
            if(comprobarNombreyApellidos() & tlfCorrecto() & comprobarDireccion()) {
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
                    .setTitle("Elige una opción:")
                    .setItems(opciones, (dialog, which) -> {
                        if (which == 0){
                            tomarFoto.launch(null);
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

        comprobarModo();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void comprobarModo() {
        boolean modoOscuro = getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                .getBoolean("modoOscuro", false);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (modoOscuro) {
            window.setStatusBarColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            constraintLayout2.setBackgroundColor(Color.BLACK);
            titulo.setTextColor(Color.WHITE);
            edit.setImageDrawable(getDrawable(R.drawable.lapiz_night));
            edit.setBackgroundColor(Color.BLACK);
            guardar.setBackgroundColor(Color.BLACK);
            cambiarInputLayout(nombre, nombre1);
            cambiarInputLayout(apellido1, apellido11);
            cambiarInputLayout(apellido2, apellido21);
            cambiarInputLayout(tlf, tlf1);
            cambiarInputLayout(direccion, direccion1);
            cambiarInputLayout(ciudad, ciudad1);
            cambiarInputLayout(provincia, provincia1);
            cambiarInputLayout(piso, piso1);
            piso1.setHelperTextColor(ColorStateList.valueOf(Color.WHITE));
            cambiarInputLayout(portal, portal1);
            correo1.setBoxBackgroundColor(Color.rgb(100, 100, 100));
            correo.setTextColor(Color.BLACK);
            correo1.setBoxStrokeColor(Color.WHITE);
            correo1.setDefaultHintTextColor(ColorStateList.valueOf(Color.WHITE));
        }else {
            window.setStatusBarColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
            constraintLayout2.setBackgroundColor(Color.WHITE);
            titulo.setTextColor(getColor(R.color.azul_oscuro));
            edit.setImageDrawable(getDrawable(R.drawable.lapiz));
        }
    }
    private void cambiarInputLayout(TextInputEditText inputEditText, TextInputLayout textInputLayout){
        textInputLayout.setBoxBackgroundColor(Color.rgb(26, 26, 26));
        inputEditText.setTextColor(Color.WHITE);
        textInputLayout.setBoxStrokeColor(Color.WHITE);
        textInputLayout.setDefaultHintTextColor(ColorStateList.valueOf(Color.WHITE));
    }

    private ActivityResultLauncher<Void> tomarFoto = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(), result -> {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imagen.setImageBitmap(result);
                result.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), result, correo.toString(), null);
                nuevaUri = Uri.parse(path);
                guardar.setVisibility(View.VISIBLE);
            }
    );

    private ActivityResultLauncher<Intent> elegirDeGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri imagenUri = result.getData().getData();
                    imagen.setImageURI(imagenUri);
                    nuevaUri = imagenUri;
                    guardar.setVisibility(View.VISIBLE);
                }
            }
    );

    private void subirImagen(Uri imagenUri){
        String[] partes = conexion.obtenerUser().getEmail().split("@");
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
        if (nuevaUri != null){
            subirImagen(nuevaUri);
            usuario.setImagen("gs://balonmano-f213a.appspot.com/perfilUsuario/" + conexion.obtenerUser().getEmail().split("@")[0] + ".jpg");
        }
        if (Objects.requireNonNull(tlf.getText()).toString().isEmpty()){
            usuario.setTlf("");
        }else{
            usuario.setTlf(tlf.getText().toString());
        }
        if (!Objects.requireNonNull(direccion.getText()).toString().isEmpty()){
            usuario.setDireccion(obtenerDireccion());
        }else{
            usuario.setDireccion("");
        }
        conexion.modificarUser(usuario, this);
        intentMainActivity();
    }

    private void intentMainActivity() {
        Intent intent = new Intent(PerfilUsuario.this, MainActivity.class);
        intent.putExtra("lista", (Serializable) partidos);
        intent.putExtra("ropa", (Serializable) pedidos);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (guardar.getVisibility() == View.VISIBLE) {
            new AlertDialog.Builder(PerfilUsuario.this)
                    .setPositiveButton("Confirmar", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setNegativeButton("Salir", (dialogInterface, i) -> finish())
                    .setTitle("Confirmar cambios")
                    .setMessage("Se han detectado cambios en su usuario. ¿Desea continuar?")
                    .show();
        }else{
            finish();
        }
    }
}