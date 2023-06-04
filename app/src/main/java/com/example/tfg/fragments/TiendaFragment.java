package com.example.tfg.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.detalles.DetallesPrenda;
import com.example.tfg.PreCompra;
import com.example.tfg.R;
import com.example.tfg.adaptador.RecyclerItemClickListener;
import com.example.tfg.adaptador.TiendaAdapter;
import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Prenda;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TiendaFragment extends Fragment {

    private RecyclerView recyclerView;
    private TiendaAdapter adapter;
    private ImageButton compra;
    private List<Prenda> prendas = new ArrayList<>();
    private FirebaseUser user;
    private final ConexionFirebase conexion = new ConexionFirebase();
    private ConstraintLayout constraintLayout;

    public TiendaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey("ropa")){
            prendas = (List<Prenda>) args.getSerializable("ropa");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tienda, container, false);
        user  = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = root.findViewById(R.id.recyclerTienda);
        compra = root.findViewById(R.id.botonCompra);
        constraintLayout = root.findViewById(R.id.constrainTienda);

        recyclerView.setLayoutManager(new GridLayoutManager(root.getContext(), 2));
        adapter = new TiendaAdapter(getContext(), prendas);
        recyclerView.setAdapter(adapter);
        if (user == null){
            compra.setVisibility(View.INVISIBLE);
        }
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int posicion) {
                if (user != null) {
                    Intent intent = new Intent(v.getContext(), DetallesPrenda.class);
                    intent.putExtra("prenda", adapter.getDatos().get(posicion));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }else{
                    Toast.makeText(getContext(), "Debes estar registrado para realizar una compra.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onLongItemClick(View v, int posicion) {
                Toast.makeText(getContext(), "No puedes hacer nada.", Toast.LENGTH_SHORT).show();
            }
        }));

        compra.setOnClickListener(view -> {
            Task<List<Pedido>> pedidos = conexion.obtenerPedidos(user.getEmail());
            pedidos.addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()){
                    List<Pedido> prendas1 = task1.getResult();
                    if (prendas1.isEmpty()){
                        Toast.makeText(getContext(), "No hay ninguna prenda de ropa.", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(getContext(), PreCompra.class);
                        intent.putExtra("pedido", (Serializable) prendas1);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }else{
                    Toast.makeText(getContext(), "Error obteniendo los pedidos", Toast.LENGTH_SHORT).show();
                }
            });
        });

        boolean modoOscuro = getActivity().getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                .getBoolean("modoOscuro", false);

        if (modoOscuro) {
            constraintLayout.setBackgroundColor(Color.BLACK);
            compra.setBackgroundColor(Color.BLACK);
        } else {
            constraintLayout.setBackgroundColor(Color.WHITE);
            compra.setBackgroundColor(Color.WHITE);
        }

        return root;
    }
}