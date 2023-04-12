package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.databinding.ActivityMainBinding;
import com.example.tfg.entidad.Jugador;
import com.example.tfg.entidad.Partido;
import com.example.tfg.fragments.EstadisticasFragment;
import com.example.tfg.fragments.PartidosFragment;
import com.example.tfg.fragments.TiendaFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    BottomNavigationView bottomNav;
    NavigationView lateral;
    ImageView logo;
    List<Partido> j = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        j = (List<Partido>) intent.getSerializableExtra("lista");

        logo = findViewById(R.id.logo);
        bottomNav = findViewById(R.id.bottomNavigationView);
        lateral = findViewById(R.id.lateral);

        comprobarUser();

        //Selecciona partidos como el fragment principal
        bottomNav.setSelectedItemId(R.id.partidos);
        bottomNav.setSelected(true);

        //Establece el fragment Partidos para que sea el primero en aparecer
        iniciar();

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
                    /*ConexionFirebase conexion = new ConexionFirebase();
                    j = conexion.obtenerPartidos();*/
                case R.id.ajustes:
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword("zurdocbl@gmail.com", "gt02102002");

            }
            return false;
        });
    }

    private void comprobarUser() {
        View header = lateral.getHeaderView(0);
        TextView nombreUsuario = header.findViewById(R.id.nombreUsuario);
        TextView posicion = header.findViewById(R.id.posicionUsuario);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            nombreUsuario.setText("Sin registrar");
            posicion.setVisibility(View.INVISIBLE);
        }else{
            comprobarExiste(user.getEmail(), nombreUsuario, posicion);
        }
    }

    private boolean comprobarExiste(String email, TextView nombre,TextView posicion) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot snapsot = task.getResult();
                    for (DocumentSnapshot ds : snapsot){
                        if (ds.get("correo").equals(email)){
                            nombre.setText(ds.get("nombre") + " " + ds.get("apellido1"));
                            posicion.setVisibility(View.VISIBLE);
                            posicion.setText(ds.get("rol").toString());
                        }
                    }
                }
            }
        });
        return false;
    }

    private void iniciar() {
        Bundle bundle = new Bundle();
        if (!j.isEmpty()) {
            bundle.putSerializable("lista", (Serializable) j);
        }
        Fragment partidos = new PartidosFragment();
        partidos.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout,partidos);
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
}