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
import java.util.Date;
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
    private List<Partido> jugadores = new ArrayList<>();
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

        rellenarTemporadas();
        rellenarEquipos();
        rellenarJornadas();

        recycler = root.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext()));

        Bundle args = getArguments();
        if (args != null && args.containsKey("lista")){
            jugadores = listaSegunSpinner((List<Partido>) args.getSerializable("lista"));
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
                adapter = new JornadasAdapter(getContext(), listaSegunSpinner(jugadores));
                recycler.setAdapter(adapter);
                recycler.notifyAll();
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

        return root;
    }

    private List<Partido> listaSegunSpinner(List<Partido> completa){
        List<Partido> listaFinal = new ArrayList<>();
        String temp = temporadas.getSelectedItem().toString();
        String eq = null;
        String jorn = null;
        if (equipos.isEnabled()){
            eq = equipos.getSelectedItem().toString();
        } else if (jornadas.isEnabled()){
            jorn = equipos.getSelectedItem().toString();
        }

        for (Partido p : completa){
            if (compararRequisitos(p, temp, eq, jorn)){
                 listaFinal.add(p);
            }
        }

        return listaFinal;
    }

    private boolean compararRequisitos(Partido partido, String temporada, String equipo, String jornada){
        String[] partesTemporada = temporada.split(" ");
        String anio = String.valueOf(partido.getFecha().toString().charAt(2) + partido.getFecha().toString().charAt(3));
        if (anio.equals(partesTemporada[0]) || anio.equals(partesTemporada[1])){            //Comprobamos que el año del partido es el que queremos.
            if (equipo.equals("TODOS")){
                return true;                                                                //Si elige "TODOS" retornamos True para q la list se llene con todos los partidos de ese año.
            }else{                                                                          //Si no:
                if (equipo.equals(partido.getDivision())){                                  //Comprobamos que la division del partido es la que queremos.
                    if (jornada.equals("TODAS")) {
                        return true;                                   //Si elige "TODAS" retornamos True para que la list se llene con todas las jornadas disputadas por el equipo ese año.
                    }else{                                                                  //Si no:
                        return jornada.equals(String.valueOf(partido.getJornada()));        //Comprobamos que la jornada del partido es la que queremos  y retornamos true o False.
                    }
                }else{
                    return false;                                                           //Si la division no es la misma: False.
                }
            }
        }else {
            return false;                                                                   //Si el año no es el mismo: False.
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}