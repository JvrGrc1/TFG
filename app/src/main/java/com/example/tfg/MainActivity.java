package com.example.tfg;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.databinding.ActivityMainBinding;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Prenda;
import com.example.tfg.fragments.EstadisticasFragment;
import com.example.tfg.fragments.PartidosFragment;
import com.example.tfg.fragments.TiendaFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNav;
    private NavigationView lateral;
    private ImageView abrir;
    private List<Partido> j = new ArrayList<>();
    private List<Prenda> prendas = new ArrayList<>();
    private final ConexionFirebase conexion = new ConexionFirebase();
    private DrawerLayout drawerLayout;
    private MenuItem cerrar, registroUser, usuario, registroPartido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (!intent.hasExtra("lista")) {
            iniciarPartidos();
        } else {
            j = (List<Partido>) intent.getSerializableExtra("lista");
            prendas = (List<Prenda>) intent.getSerializableExtra("ropa");
            iniciarPrendas();
            iniciarPartidos();
        }

        bottomNav = findViewById(R.id.bottomNavigationView);
        lateral = findViewById(R.id.lateral);
        abrir = findViewById(R.id.botonAbrir);
        drawerLayout = findViewById(R.id.drawer);
        cerrar = lateral.getMenu().findItem(R.id.cerrar);
        registroUser = lateral.getMenu().findItem(R.id.registrarse);
        usuario = lateral.getMenu().findItem(R.id.user);
        registroPartido = lateral.getMenu().findItem(R.id.registrarPartido);

        comprobarUser();

        //Selecciona partidos como el fragment principal
        bottomNav.setSelectedItemId(R.id.partidos);
        bottomNav.setSelected(true);

        //Establece el fragment Partidos para que sea el primero en aparecer

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(
                ObjectAnimator.ofFloat(bottomNav, "scaleY", 1f, 0.90f, 1f),
                ObjectAnimator.ofFloat(bottomNav, "scaleX", 1f, 0.99f, 1f)
        );

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() != bottomNav.getSelectedItemId()){
                resetIcons();
                switch (item.getItemId()) {
                    case R.id.partidos:
                        animSet.start();
                        item.setIcon(R.drawable.partidos);
                        Bundle b = new Bundle();
                        if (!j.isEmpty()) {b.putSerializable("lista", (Serializable) j);}
                        Fragment partidos = new PartidosFragment();
                        partidos.setArguments(b);
                        cambiarFragment(partidos);
                        break;
                    case R.id.tienda:
                        animSet.start();
                        item.setIcon(R.drawable.tienda);
                        Bundle bt = new Bundle();
                        if (!j.isEmpty()) {bt.putSerializable("ropa", (Serializable) prendas);}
                        Fragment tienda = new TiendaFragment();
                        tienda.setArguments(bt);
                        cambiarFragment(tienda);
                        break;
                    case R.id.estadisticas:
                        animSet.start();
                        item.setIcon(R.drawable.estadisticas);
                        cambiarFragment(new EstadisticasFragment());
                        break;
                }
            }
            return true;
        });

        lateral.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.registrarPartido:
                    String user = conexion.obtenerUser();
                    if (user != null){
                    Intent intentRegistrar = new Intent(this, RegistrarPartido.class);
                    startActivity(intentRegistrar);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    }else{
                        Toast.makeText(this, "Solo los entrenadores y administradores pueden usar esta función", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.ajustes:
                    Intent intentAjustes = new Intent(this, Ajustes.class);
                    startActivity(intentAjustes);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.user:
                    Intent intentPerfilUsuario = new Intent(this, PerfilUsuario.class);
                    startActivity(intentPerfilUsuario);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.registrarse:
                    Intent intentSignIn = new Intent(this, RegistrarUsuario.class);
                    startActivity(intentSignIn);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.cerrar:
                    conexion.signOut();
                    intentMainActivity();
                case R.id.consultaEquipo:
                    break;
            }
            return false;
        });

        abrir.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    private void comprobarUser() {
        View header = lateral.getHeaderView(0);
        TextView nombreUsuario = header.findViewById(R.id.nombreUsuario);
        TextView posicion = header.findViewById(R.id.posicionUsuario);
        ImageView imagen = header.findViewById(R.id.imagenUsuario);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            nombreUsuario.setText("Sin registrar");
            posicion.setVisibility(View.INVISIBLE);
            cerrar.setVisible(false);
            registroUser.setVisible(true);
            usuario.setVisible(false);
            registroPartido.setVisible(false);
            //imagenRandom(imagen);
            conexion.cargarImagen(MainActivity.this, imagen, abrir, null);
        }else{
            comprobarExiste(user.getEmail(), nombreUsuario, posicion, imagen);
        }
    }

    private void imagenRandom(ImageView imagen) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String foto = randomFoto();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://balonmano-f213a.appspot.com/imagenes-default/" + foto);
        final long MEGABYTES =  5 * 1024 * 1024;    //Sirve para establecer un limite de tamaño a la imagen y si se pasa no se descargara completamente.
        gsReference.getBytes(MEGABYTES).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);              //Decodifica los bytes de la img descargada en un Bitmap
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,200,200,true);    //Redimensionar el Bitmap anteriro a una imagen 200*200
            imagen.setImageBitmap(bitmap1);
            abrir.setImageBitmap(bitmap1);
        }).addOnFailureListener(exception -> Toast.makeText(MainActivity.this, "Error al descargar la imagen", Toast.LENGTH_SHORT).show());

    }
    private void imagenPerfil(ImageView imagen, String url){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 5 * 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,200,200,true);
            imagen.setImageBitmap(bitmap1);
            abrir.setImageBitmap(bitmap1);
        }).addOnFailureListener(exception -> Toast.makeText(MainActivity.this, "Error al descargar la imagen de perfil", Toast.LENGTH_SHORT).show());
    }

    private String randomFoto(){
        int numero = (int) (Math.random() * 6) + 1;
        switch (numero) {
            case 1:
                return "corriendo.png";
            case 2:
                return "culturismo.png";
            case 3:
                return "futbol-americano.png";
            case 4:
                return "futbol.png";
            case 5:
                return "surf.png";
            case 6:
                return "voleibol.png";
        }
        return "corriendo.png";
    }

    @SuppressLint("RestrictedApi")
    private void comprobarExiste(String email, TextView nombre, TextView posicion, ImageView imagen) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapsot = task.getResult();
                for (DocumentSnapshot ds : snapsot) {
                    if (Objects.equals(ds.getString("correo"), email)) {
                        nombre.setText(String.format("%s %s", ds.getString("nombre"), ds.getString("apellido1")));
                        posicion.setVisibility(View.VISIBLE);
                        posicion.setText(ds.getString("rol"));
                        if (ds.getString("imagen") != null) {
                            conexion.cargarImagen(MainActivity.this, imagen, abrir, ds.getString("imagen"));
                        }
                        if (ds.getString("rol").equals("Jugador")){
                            registroPartido.setVisible(false);
                        }else{
                            registroPartido.setVisible(true);
                        }
                    }
                }
                registroUser.setVisible(false);
                cerrar.setVisible(true);
                usuario.setVisible(true);
            }
        });
    }

    private void iniciarPartidos() {
        Bundle bundle = new Bundle();
        if ( j != null && !j.isEmpty()) {
            bundle.putSerializable("lista", (Serializable) j);
        }
        Fragment partidos = new PartidosFragment();
        partidos.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout, partidos);
        transaction.commit();
    }

    private void iniciarPrendas() {
        Bundle bundle = new Bundle();
        if (prendas != null && !prendas.isEmpty()){
            bundle.putSerializable("ropa", (Serializable) prendas);
        }
        Fragment tienda = new TiendaFragment();
        tienda.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout, tienda);
        transaction.commit();
    }

    private void resetIcons() {
        Menu menu = binding.bottomNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++){
            menu.getItem(i).setIcon(null);
        }
    }

    private void cambiarFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void intentMainActivity(){
        Task<List<Partido>> partidos = conexion.obtenerPartidos();
        partidos.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                List<Partido> partidos1 = task1.getResult();
                Task<List<Prenda>> prendas = conexion.obtenerTienda();
                prendas.addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()){
                        List<Prenda> prendas1 = task2.getResult();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("lista", (Serializable) partidos1);
                        intent.putExtra("ropa", (Serializable) prendas1);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finishAffinity();
                    }else{
                        Toast.makeText(this, "Error obteniendo las prendas", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Error obteniendo partidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}