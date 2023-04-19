package com.example.tfg.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.DetallesJornada;
import com.example.tfg.DetallesPrenda;
import com.example.tfg.R;
import com.example.tfg.adaptador.RecyclerItemClickListener;
import com.example.tfg.adaptador.TiendaAdapter;
import com.example.tfg.entidad.Prenda;

import java.io.Serializable;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TiendaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TiendaFragment extends Fragment {

    private RecyclerView recyclerView;
    private TiendaAdapter adapter;
    private Button compra;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public TiendaFragment() {
        // Required empty public constructor
    }
    public static TiendaFragment newInstance(String param1, String param2) {
        TiendaFragment fragment = new TiendaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tienda, container, false);

        recyclerView = root.findViewById(R.id.recyclerTienda);
        compra = root.findViewById(R.id.botonCompra);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        Bundle args = getArguments();
        if (args != null && args.containsKey("lista")){
            List<Prenda> prendas = (List<Prenda>) args.getSerializable("ropa");
            adapter = new TiendaAdapter(getContext(), prendas);
            recyclerView.setAdapter(adapter);
        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int posicion) {
                Intent intent = new Intent(v.getContext(), DetallesPrenda.class);
                intent.putExtra("prenda", (Serializable) adapter.getDatos().get(posicion));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onLongItemClick(View v, int posicion) {
                Toast.makeText(getContext(), "No puedes hacer nada.", Toast.LENGTH_SHORT).show();
            }
        }));

        return root;
    }
}