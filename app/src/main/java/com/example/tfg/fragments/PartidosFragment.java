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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.Calendar;
import java.util.List;
public class PartidosFragment extends Fragment {

    private ImageButton buscar;
    private RecyclerView recycler;
    private JornadasAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner temporadas, equipos, jornadas;
    private final ConexionFirebase conexion = new ConexionFirebase();
    private List<Partido> jugadores = new ArrayList<>();

    private SwipeRefreshLayout refresh;

    public PartidosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_partidos, container, false);

        buscar = root.findViewById(R.id.botonBuscar);
        jornadas = root.findViewById(R.id.spinnerJornadaPartidos);
        temporadas = root.findViewById(R.id.spinnerTemporada);
        equipos = root.findViewById(R.id.spinnerDivisionPartidos);
        refresh = root.findViewById(R.id.refreshLayout);

        rellenarTemporadas();
        rellenarEquipos();
        rellenarJornadas();

        recycler = root.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext()));

        Bundle args = getArguments();
        if (args != null && args.containsKey("lista")){
            jugadores = ((List<Partido>) args.getSerializable("lista"));
            List<Partido> lista = listaSegunTemporada("22-23", jugadores);
            adapter = new JornadasAdapter(getContext(), lista);
            recycler.setAdapter(adapter);
        }

        recycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int posicion) {
                Intent intent = new Intent(v.getContext(), DetallesJornada.class);
                intent.putExtra("partidos", (Serializable) adapter.getDatos().get(posicion).getPartidos());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onLongItemClick(View v, int posicion) {}
        }));

        buscar.setOnClickListener(v -> {
            if (temporadas.getVisibility() == View.VISIBLE) {
                adapter = new JornadasAdapter(getContext(), listaSegunSpinner(jugadores));
                recycler.setAdapter(adapter);
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

        refresh.setOnRefreshListener(() -> {
            Task<List<Partido>> partidos = conexion.obtenerPartidos();
            partidos.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    jugadores = task.getResult();
                    adapter = new JornadasAdapter(getContext(), listaSegunSpinner(jugadores));
                    recycler.setAdapter(adapter);
                    refresh.setRefreshing(false);
                }else {
                    Toast.makeText(getContext(), "Error al recargar partidos.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return root;
    }

    private List<Partido> listaSegunSpinner(List<Partido> completa){
        List<Partido> listaFinal;
        String temporada = temporadas.getSelectedItem().toString();
        if (temporada.equals("TODAS")){
            listaFinal = completa;
        }else {
            String equipo = equipos.getSelectedItem().toString();
            if (equipo.equals("TODOS")) {
                listaFinal = listaSegunTemporada(temporada, completa);
            } else {
                String jornada = jornadas.getSelectedItem().toString();
                listaFinal = listaSegunEquipo(listaSegunTemporada(temporada, completa), equipo, jornada);
            }
        }
        return listaFinal;
    }

    private List<Partido> listaSegunTemporada(String temporada, List<Partido> completa){
        List<Partido> lista = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        String[]partesTemp = temporada.split("-");
        for (Partido p : completa){
            String[] partesFecha = p.getFecha().toString().split(" ");
            String anio = partesFecha[5].charAt(2) + "" + partesFecha[5].charAt(3);
            cal.setTime(p.getFecha());
            if (anio.equals(partesTemp[0]) && cal.get(Calendar.MONTH) + 1 > 8 || anio.equals(partesTemp[1]) && cal.get(Calendar.MONTH) + 1 < 8){
                lista.add(p);
            }
        }
        return lista;
    }

    private List<Partido> listaSegunEquipo(List<Partido> temporada, String equipo, String jornada){
        List<Partido> lista = new ArrayList<>();
        if (!jornada.equals("TODAS")) {
            Long jorn = Long.parseLong(jornada);
            for (Partido partido : temporada) {
                if (partido.getJornada().equals(jorn) && partido.getDivision().equals(equipo)) {
                    lista.add(partido);
                }
            }
        }else{
            for (Partido p : temporada){
                if (p.getDivision().equals(equipo)){
                    lista.add(p);
                }
            }
        }
        return lista;
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
            lista.add("TODAS");
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    lista.add(document.getId());
                }
            }else{
                Toast.makeText(getContext(), "Error al encontrar temporadas", Toast.LENGTH_SHORT).show();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
            temporadas.setAdapter(adapter);
            temporadas.setSelection(adapter.getCount() - 1);
            equipos.setEnabled(false);
            jornadas.setEnabled(false);
        });
    }

    private void rellenarEquipos() {
        temporadas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!temporadas.getSelectedItem().equals("") && !temporadas.getSelectedItem().equals("TODAS")){
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