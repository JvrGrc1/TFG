package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.tfg.adaptador.PedidosAdapter;
import com.example.tfg.entidad.Pedido;

import java.io.Serializable;
import java.util.List;

public class PreCompra extends AppCompatActivity {

    private RecyclerView recycler;
    private Button confirmar;
    private ConstraintLayout constraintLayout;
    private TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_compra);

        recycler = findViewById(R.id.recyclerPedidos);
        confirmar = findViewById(R.id.buttonConfirmarPedido);
        constraintLayout = findViewById(R.id.constrainPreCompra);
        titulo = findViewById(R.id.textViewTituloPreCompra);

        List<Pedido> pedidos = (List<Pedido>) getIntent().getSerializableExtra("pedido");

        recycler.setLayoutManager(new LinearLayoutManager(this));
        PedidosAdapter adapter = new PedidosAdapter(this, pedidos);
        recycler.setAdapter(adapter);

        boolean modoOscuro = getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                .getBoolean("modoOscuro", false);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (modoOscuro){
            window.setStatusBarColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            titulo.setTextColor(Color.WHITE);
        }else{
            window.setStatusBarColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
            titulo.setTextColor(Color.BLACK);
        }

        confirmar.setOnClickListener(v -> {
            Intent intent = new Intent(this, MetodoDePago.class);
            intent.putExtra("pedido", (Serializable) pedidos);
            startActivity(intent);
        });
    }
}