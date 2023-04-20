package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.example.tfg.adaptador.PedidosAdapter;
import com.example.tfg.entidad.Pedido;

import java.util.List;

public class PreCompra extends AppCompatActivity {

    private RecyclerView recycler;
    private Button confirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_compra);

        recycler = findViewById(R.id.recyclerPedidos);
        confirmar = findViewById(R.id.buttonConfirmarPedido);

        List<Pedido> pedidos = (List<Pedido>) getIntent().getSerializableExtra("pedidos");

        recycler.setLayoutManager(new LinearLayoutManager(this));
        PedidosAdapter adapter = new PedidosAdapter(this, pedidos);
        recycler.setAdapter(adapter);
    }
}