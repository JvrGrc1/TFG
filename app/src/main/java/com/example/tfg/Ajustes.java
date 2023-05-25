package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ajustes extends AppCompatActivity {

    private TextView borrar, cambiar;
    private ConexionFirebase conexion = new ConexionFirebase();
    private List<Partido> j = new ArrayList<>();
    private List<Pedido> prendas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        borrar = findViewById(R.id.borrarCuenta);
        cambiar = findViewById(R.id.cambiarCuenta);

        Intent intent = getIntent();
        j = (List<Partido>) intent.getSerializableExtra("lista");
        prendas = (List<Pedido>) intent.getSerializableExtra("ropa");

        borrar.setOnClickListener(v -> {
            Task<Boolean> borrado = conexion.borrarCuenta(conexion.obtenerUser(), Ajustes.this);
            borrado.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
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

        cambiar.setOnClickListener(v -> {
            conexion.signOut();
            Intent intentLogin = new Intent(this, Login.class);
            intentLogin.putExtra("lista", (Serializable) j);
            intentLogin.putExtra("ropa", (Serializable) prendas);
            startActivity(intentLogin);
            finishAffinity();
        });
    }
}