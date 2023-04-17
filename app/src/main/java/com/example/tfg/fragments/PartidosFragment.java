package com.example.tfg.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.DetallesJornada;
import com.example.tfg.R;
import com.example.tfg.adaptador.JornadasAdapter;
import com.example.tfg.adaptador.RecyclerItemClickListener;
import com.example.tfg.entidad.Partido;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PartidosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartidosFragment extends Fragment {

    private ImageButton buscar;
    private RecyclerView recycler;
    private JornadasAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner temporadas, equipos, jornadas;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PartidosFragment() {
        // Required empty public constructor
    }

    public static PartidosFragment newInstance(String param1, String param2) {
        PartidosFragment fragment = new PartidosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_partidos, container, false);

        buscar = root.findViewById(R.id.botonBuscar);
        jornadas = root.findViewById(R.id.spinnerJornadaPartidos);
        temporadas = root.findViewById(R.id.spinnerTemporada);
        equipos = root.findViewById(R.id.spinnerDivisionPartidos);

        recycler = root.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle args = getArguments();
        if (args != null && args.containsKey("lista")){
            List<Partido> jugadores = (List<Partido>) args.getSerializable("lista");
            adapter = new JornadasAdapter(getContext(), jugadores);
            recycler.setAdapter(adapter);
        }

        recycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int posicion) {
                Intent intent = new Intent(v.getContext(), DetallesJornada.class);
                intent.putExtra("partidos", (Serializable) adapter.getDatos().get(posicion).getPartidos());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onLongItemClick(View v, int posicion) {
                Toast.makeText(getContext(), "Adios", Toast.LENGTH_SHORT).show();
            }
        }));

        buscar.setOnClickListener(v -> {
            if (temporadas.getVisibility() == View.VISIBLE) {
                buscar.setImageResource(R.drawable.menu_icon);
                temporadas.setVisibility(View.INVISIBLE);
                jornadas.setVisibility(View.INVISIBLE);
                equipos.setVisibility(View.INVISIBLE);
            }else{
                buscar.setImageResource(R.drawable.buscar);
                temporadas.setVisibility(View.VISIBLE);
                jornadas.setVisibility(View.VISIBLE);
                equipos.setVisibility(View.VISIBLE);
            }
        });

        List<String> listaEquipos = new ArrayList<>();
        listaEquipos.add("");
        listaEquipos.add("TODOS");
        listaEquipos.add("1NM");
        listaEquipos.add("DHPF");
        listaEquipos.add("1NF");
        listaEquipos.add("2NM");
        listaEquipos.add("1TM");

        rellenarTemporadas();
        rellenarEquipos(listaEquipos);
        rellenarJornadas();

        return root;
    }

    private void rellenarJornadas() {
        equipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!equipos.getSelectedItem().equals("") && !equipos.getSelectedItem().equals("TODOS")){
                    db.collection("temporadas").document(temporadas.getSelectedItem().toString()).collection(getEquipos()).get().addOnCompleteListener(task -> {
                        List<Integer> listaJornadas = new ArrayList<>();
                        if (task.isSuccessful()){
                            int numDocs = 1;
                            for (QueryDocumentSnapshot document : task.getResult()){
                                listaJornadas.add(numDocs++);
                            }
                            jornadas.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaJornadas));
                            jornadas.setEnabled(true);
                        }else{
                            Toast.makeText(getContext(), "El año " + temporadas.getSelectedItem().toString() + " no existe.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    jornadas.setSelection(0);
                    jornadas.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getEquipos() {
        String equipo = equipos.getSelectedItem().toString();
        switch (equipo){
            case "1NM":
                return "1NacionalMasc";
            case "DHPF":
                return "DHPF";
            case "1NF":
                return "1NacionalFem";
            case "2NM":
                return "2NacionalMasc";
            case "1TM":
                return "1TerritorialMasc";
        }
        return null;
    }

    private void rellenarTemporadas() {
        db.collection("temporadas").get().addOnCompleteListener(task -> {
            List<String> listaAnios = new ArrayList<>();
            listaAnios.add("");
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    listaAnios.add(document.getId());
                }
            }else{
                Toast.makeText(getContext(), "El año " + temporadas.getSelectedItem().toString() + " no existe.", Toast.LENGTH_SHORT).show();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaAnios);
            temporadas.setAdapter(adapter);
            equipos.setEnabled(false);
            jornadas.setEnabled(false);
        });
    }

    private void rellenarEquipos(List<String> listaEquipos) {
        temporadas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!temporadas.getSelectedItem().equals("")){
                    equipos.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaEquipos));
                    equipos.setEnabled(true);
                }else {
                    equipos.setSelection(0);
                    jornadas.setSelection(0);
                    equipos.setEnabled(false);
                    jornadas.setEnabled(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}