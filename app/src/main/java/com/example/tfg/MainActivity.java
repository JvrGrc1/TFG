package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.databinding.ActivityMainBinding;
import com.example.tfg.entidad.Partido;
import com.example.tfg.fragments.EstadisticasFragment;
import com.example.tfg.fragments.PartidosFragment;
import com.example.tfg.fragments.TiendaFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenuItemView;
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

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    BottomNavigationView bottomNav;
    NavigationView lateral;
    ImageView logo, abrir;
    List<Partido> j = new ArrayList<>();

    FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (!intent.hasExtra("lista")) {
            iniciar();
        } else {
            j = (List<Partido>) intent.getSerializableExtra("lista");
            iniciar();
        }

        logo = findViewById(R.id.logo);
        bottomNav = findViewById(R.id.bottomNavigationView);
        lateral = findViewById(R.id.lateral);
        abrir = findViewById(R.id.botonAbrir);

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
                        cambiarFragment(new TiendaFragment());
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
                    Intent intentRegistrar = new Intent(this, RegistrarPartido.class);
                    startActivity(intentRegistrar);
                    break;
                case R.id.ajustes:
                    if (auth.getCurrentUser() == null) {
                        auth.signInWithEmailAndPassword("zurdocbl@gmail.com", "gt02102002");
                        finish();
                    }
                    break;
                case R.id.ajustesUser:
                    if (auth.getCurrentUser() != null) {
                        auth.signOut();
                        finish();
                    }
                    break;
                case R.id.registrarse:
                    Intent intentSignIn = new Intent(this, RegistrarUsuario.class);
                    startActivity(intentSignIn);
                    break;
            }
            return false;
        });

        abrir.setOnClickListener(v -> {
            DrawerLayout drawerLayout = findViewById(R.id.drawer);
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
            imagenRandom(imagen);
            NavigationMenuItemView registrar = lateral.findViewById(R.id.registrarse);
            if (registrar != null){registrar.setVisibility(View.VISIBLE);}
            NavigationMenuItemView cerrar = lateral.findViewById(R.id.cerrarSesion);
            if (cerrar != null){cerrar.setVisibility(View.INVISIBLE);}
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

    private void comprobarExiste(String email, TextView nombre,TextView posicion, ImageView imagen) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot snapsot = task.getResult();
                for (DocumentSnapshot ds : snapsot){
                    if (ds.get("correo").equals(email)){
                        nombre.setText(ds.get("nombre") + " " + ds.get("apellido1"));
                        posicion.setVisibility(View.VISIBLE);
                        posicion.setText(ds.get("rol").toString());
                        if (ds.get("imagen").toString() != null) {
                            imagenPerfil(imagen, ds.get("imagen").toString());
                        }
                    }
                }
                NavigationMenuItemView registrar = lateral.findViewById(R.id.registrarse);
                if (registrar != null){registrar.setVisibility(View.INVISIBLE);}
                NavigationMenuItemView cerrar = lateral.findViewById(R.id.cerrarSesion);
                if (cerrar != null){cerrar.setVisibility(View.VISIBLE);}
            }
        });
    }

    private void iniciar() {
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

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}