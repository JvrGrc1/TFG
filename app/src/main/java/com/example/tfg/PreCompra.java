package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.tfg.adaptador.PedidosAdapter;
import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Usuario;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.List;

public class PreCompra extends AppCompatActivity {

    private RecyclerView recycler;
    private Button confirmar;
    private final ConexionFirebase conexion = new ConexionFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_compra);

        recycler = findViewById(R.id.recyclerPedidos);
        confirmar = findViewById(R.id.buttonConfirmarPedido);

        List<Pedido> pedidos = (List<Pedido>) getIntent().getSerializableExtra("pedido");

        recycler.setLayoutManager(new LinearLayoutManager(this));
        PedidosAdapter adapter = new PedidosAdapter(this, pedidos);
        recycler.setAdapter(adapter);

        confirmar.setOnClickListener(v -> {
            Task<Usuario> user = conexion.datosUsuario(conexion.obtenerUser());
            user.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Intent intent = new Intent(this, MetodoDePago.class);
                    intent.putExtra("pedido",(Serializable) pedidos);
                    intent.putExtra("user", task.getResult());
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Error al obtener el usuario.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}