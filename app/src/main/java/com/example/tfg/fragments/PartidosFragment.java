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

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.detalles.DetallesJornada;
import com.example.tfg.R;
import com.example.tfg.adaptador.JornadasAdapter;
import com.example.tfg.adaptador.RecyclerItemClickListener;
import com.example.tfg.entidad.Partido;
import com.google.android.gms.tasks.Task;
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
    private ConexionFirebase conexion = new ConexionFirebase();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext()));

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
        rellenarEquipos();
        rellenarJornadas();

        jornadas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //actualizarDatos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                /*Si no se ha seleccionado nada se muestra toda la temporada*/
            }
        });

        return root;
    }

    private void actualizarDatos() {
        List<Partido> partidos = new ArrayList<>();
        if (!jornadas.getSelectedItem().toString().equals("TODAS")) {
            db.collection("temporadas").document(temporadas.getSelectedItem().toString()).collection(equipos.getSelectedItem().toString()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (Integer.parseInt(document.get("jornada").toString()) == (Integer.parseInt(jornadas.getSelectedItem().toString()))) {
                            Partido partido = new Partido(document.getString("division"), document.getString("local"), document.getString("visitante"), document.getLong("golesLocal"), document.getLong("golesVisitante"), document.getString("fecha"), document.getString("pabellon"), document.getString("hora"), document.getLong("jornada"));
                            partidos.add(partido);
                            adapter = new JornadasAdapter(getContext(), partidos);
                            recycler.setAdapter(adapter);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Error al conseguir los documentos.", Toast.LENGTH_SHORT).show();
                }
            });
        }else if (jornadas.getSelectedItem().toString().equals("TODAS")){
            db.collection("temporadas").document(temporadas.getSelectedItem().toString()).collection(equipos.getSelectedItem().toString()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Partido partido = new Partido(document.getString("division"), document.getString("local"), document.getString("visitante"), document.getLong("golesLocal"), document.getLong("golesVisitante"), document.getString("fecha"), document.getString("pabellon"), document.getString("hora"), document.getLong("jornada"));
                        partidos.add(partido);
                        adapter = new JornadasAdapter(getContext(), partidos);
                        recycler.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al conseguir los documentos.", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (jornadas.getSelectedItem().toString().equals("")) {
            //No se que hacer en este caso.
        }
    }

    private void rellenarJornadas() {
        equipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!equipos.getSelectedItem().equals("") && !equipos.getSelectedItem().equals("TODOS")){
                    db.collection("temporadas").document(temporadas.getSelectedItem().toString()).collection(getEquipos(null)).get().addOnCompleteListener(task -> {
                        List<String> listaJornadas = new ArrayList<>();
                        listaJornadas.add("TODAS");
                        if (task.isSuccessful()){
                            int numDocs = 1;
                            for (QueryDocumentSnapshot document : task.getResult()){
                                listaJornadas.add(numDocs++ + "");
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private String getEquipos(String equipo) {
        if (equipo == null) {
            equipo = equipos.getSelectedItem().toString();
        }
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
            case "1NacionalMasc":
                return "1NM";
            case "1NacionalFem":
                return "1NF";
            case "2NacionalMasc":
                return "2NM";
            case "1TerritorialMasc":
                return "1TM";
        }
        return null;
    }

    private void rellenarTemporadas() {
        db.collection("temporadas").get().addOnCompleteListener(task -> {
            List<String> lista = new ArrayList<>();
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    lista.add(document.getId());
                }
            }else{
                Toast.makeText(getContext(), "Error al encontrar temporadas", Toast.LENGTH_SHORT).show();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
            temporadas.setAdapter(adapter);
            temporadas.setSelection(lista.size() - 1);
            equipos.setEnabled(false);
            jornadas.setEnabled(false);
        });
    }

    private void rellenarEquipos() {
        temporadas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!temporadas.getSelectedItem().equals("")){
                    Task<List<String>> listEquiposConexion = conexion.equiposFromTemporada(temporadas.getSelectedItem().toString());
                    listEquiposConexion.addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            List<String> equipos1 = task1.getResult();
                            for (String equipo : equipos1){
                                equipos1.set(equipos1.indexOf(equipo), getEquipos(equipo));
                            }
                            equipos1.add(0,"TODOS");
                            equipos.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, equipos1));
                            equipos.setEnabled(true);
                        }else{
                            Toast.makeText(getContext(), "Error obteniendo los pedidos", Toast.LENGTH_SHORT).show();
                        }
                    });
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