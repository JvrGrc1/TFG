package com.example.tfg.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.adaptador.ClasificacionAdapter;
import com.example.tfg.adaptador.GoleadoresAdapter;
import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Clasificacion;
import com.example.tfg.entidad.Jugador;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Temporada;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class EstadisticasFragment extends Fragment {
    private ConexionFirebase conexionFirebase = new ConexionFirebase();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Partido> partidos = new ArrayList<>();
    private List<Jugador> jugadores = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private ScrollView scrollView;
    private TextView clasificacion, goleadores, posicion, equipo, temp, vde, puntos, nombre, temp2, goles, disparos;
    private Spinner spinnerTemp, spinnerGol;
    private RecyclerView recyclerView, recyclerGoleadores;

    public EstadisticasFragment() {/* Required empty public constructor */}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        constraintLayout = root.findViewById(R.id.constrainStats);
        clasificacion = root.findViewById(R.id.textViewClasificacion);
        goleadores = root.findViewById(R.id.maximosGoleadores);
        spinnerTemp = root.findViewById(R.id.spinnerStatsEquipos);
        spinnerGol = root.findViewById(R.id.spinnerGoleadores);
        recyclerView = root.findViewById(R.id.recyclerClasificacionEquipos);
        scrollView = root.findViewById(R.id.scrollStats);
        recyclerGoleadores = root.findViewById(R.id.recyclerGoleadores);
        posicion = root.findViewById(R.id.textPosicion);
        equipo = root.findViewById(R.id.textEquipo);
        temp = root.findViewById(R.id.textTemporada);
        vde = root.findViewById(R.id.textVDE);
        puntos = root.findViewById(R.id.textPuntos);
        nombre = root.findViewById(R.id.textNombreJugador);
        temp2 =  root.findViewById(R.id.textTempJugador);
        goles = root.findViewById(R.id.textGolesJugador);
        disparos = root.findViewById(R.id.textDisparosJugador);

        rellenarSpinner();
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerGoleadores.setLayoutManager(new LinearLayoutManager(root.getContext()));

        Bundle args = getArguments();
        if (args != null && args.containsKey("lista")){
            partidos = (List<Partido>) args.getSerializable("lista");
            jugadores = (List<Jugador>) args.getSerializable("jugadores");
        }

        comprobarModo();

        spinnerTemp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinnerTemp.getAdapter().getItem(position).toString().equals("Todas las temporadas")) {
                    Calendar cal = Calendar.getInstance();
                    String[] partesTemp = spinnerTemp.getAdapter().getItem(position).toString().split("-");      //Separa temporada en dos: 21-22 --> 21, 22
                    List<Partido> pElegidos = new ArrayList<>();
                    for (Partido p : partidos) {
                        if (p.getGolesVisitante() != 0 && p.getGolesLocal() != 0) {
                            String[] partesFecha = p.getFecha().toString().split(" ");
                            String anio = partesFecha[5].charAt(2) + "" + partesFecha[5].charAt(3);     //Obtiene del año del partido los ultimos valores: 2022 --> 22
                            cal.setTime(p.getFecha());
                            if (anio.equals(partesTemp[0]) && cal.get(Calendar.MONTH) + 1 > 8 || anio.equals(partesTemp[1]) && cal.get(Calendar.MONTH) + 1 < 8) {
                                pElegidos.add(p);
                            }
                        }
                    }
                    getClasificacion(pElegidos);
                    if (pElegidos.size() > 10){
                        clasificacion.setText("TOP 10 Clasificación de equipos");
                    }else{
                        clasificacion.setText("Clasificación de equipos");
                    }
                }else{
                    getClasificacion(partidos);
                    if (partidos.size() > 10){
                        clasificacion.setText("TOP 10 Clasificación de equipos");
                    }else{
                        clasificacion.setText("Clasificación de equipos");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerGol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Jugador> total = calibrar(jugadores);
                if (!spinnerGol.getAdapter().getItem(position).toString().equals("Todas las temporadas")) {
                    String temp = spinnerGol.getAdapter().getItem(position).toString();
                    List<Jugador> jElegidos = new ArrayList<>();
                    for (Jugador j : total) {
                        for (Temporada temporada : j.getTemporadas()){
                            if (temporada.getAnio().length() > 5){                      //Si es > de 5 chars recorto --> Ej: 21-22 (5 chars), 21-22-1 (7 chars)
                                temporada.setAnio(temporada.getAnio().substring(0, 5)); //Obtengo los primeros 5 chars de la cadena --> De 21-22-1 paso a 21-22
                            }
                            if (temporada.getAnio().equals(temp)){
                                jElegidos.add(j);
                            }
                        }
                    }
                    GoleadoresAdapter adapter = new GoleadoresAdapter(getContext(), jElegidos);
                    recyclerGoleadores.setAdapter(adapter);
                    if (jElegidos.size() > 10){
                        goleadores.setText("TOP 10 máximos goleadores");
                    }else{
                        goleadores.setText("Máximos goleadores");
                    }
                }else{
                    GoleadoresAdapter adapter = new GoleadoresAdapter(getContext(), total);
                    recyclerGoleadores.setAdapter(adapter);
                    if (total.size() > 10){
                        goleadores.setText("TOP 10 máximos goleadores");
                    }else{
                        goleadores.setText("Máximos goleadores");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return root;
    }

    private void comprobarModo() {
        boolean modoOscuro = requireActivity().getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                .getBoolean("modoOscuro", false);

        if (modoOscuro) {
            constraintLayout.setBackgroundColor(Color.BLACK);
            scrollView.setBackgroundColor(Color.BLACK);
            clasificacion.setTextColor(Color.WHITE);
            goleadores.setTextColor(Color.WHITE);
            posicion.setTextColor(Color.WHITE);
            equipo.setTextColor(Color.WHITE);
            temp.setTextColor(Color.WHITE);
            vde.setTextColor(Color.WHITE);
            puntos.setTextColor(Color.WHITE);
            nombre.setTextColor(Color.WHITE);
            temp2.setTextColor(Color.WHITE);
            goles.setTextColor(Color.WHITE);
            disparos.setTextColor(Color.WHITE);
        } else {
            constraintLayout.setBackgroundColor(Color.WHITE);
            scrollView.setBackgroundColor(Color.WHITE);
            clasificacion.setTextColor(getResources().getColor(R.color.azul_oscuro));
            goleadores.setTextColor(getResources().getColor(R.color.azul_oscuro));
            posicion.setTextColor(Color.BLACK);
            equipo.setTextColor(Color.BLACK);
            temp.setTextColor(Color.BLACK);
            vde.setTextColor(Color.BLACK);
            puntos.setTextColor(Color.BLACK);
            nombre.setTextColor(Color.BLACK);
            temp2.setTextColor(Color.BLACK);
            goles.setTextColor(Color.BLACK);
            disparos.setTextColor(Color.BLACK);
        }
    }

    private void rellenarSpinner(){
        db.collection("temporadas").get().addOnCompleteListener(task -> {
            List<String> lista = new ArrayList<>();
            lista.add("Todas las temporadas");
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    lista.add(document.getId());
                }
            }else{
                Toast.makeText(getContext(), "Error al encontrar temporadas", Toast.LENGTH_SHORT).show();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
            spinnerTemp.setAdapter(adapter);
            spinnerGol.setAdapter(adapter);
            spinnerTemp.setSelection(0);
            spinnerGol.setSelection(0);
        });
    }

    private void getClasificacion(List<Partido> partidos){
        Task<List<String>> task = getTemporadas();
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()){
                List<Clasificacion> clasificacions = new ArrayList<>();
                for (String temporada : task1.getResult()) {
                    Calendar cal = Calendar.getInstance();
                    String[]partesTemp = temporada.split("-");      //Separa temporada en dos: 21-22 --> 21, 22
                    for (Partido p : partidos){
                        if (p.getGolesVisitante() != 0 && p.getGolesLocal() != 0) {
                            String[] partesFecha = p.getFecha().toString().split(" ");
                            String anio = partesFecha[5].charAt(2) + "" + partesFecha[5].charAt(3);     //Obtiene del año del partido los ultimos valores: 2022 --> 22
                            cal.setTime(p.getFecha());
                            if (anio.equals(partesTemp[0]) && cal.get(Calendar.MONTH) + 1 > 8 || anio.equals(partesTemp[1]) && cal.get(Calendar.MONTH) + 1 < 8) {
                                //Comprueba si el partido se jugó despues de agosto del < de temporada o antes de agosto del > de temporada
                                if (!comprobarSiEsta(p, temporada, clasificacions)) {                   //Si no está creamos un objeto Clasificacion y añadimos resultados
                                    Clasificacion quali = new Clasificacion(p.getDivision(), temporada);
                                    if (p.getPabellon().equals("Manuel Cadenas") && p.getGolesLocal() > p.getGolesVisitante() ||
                                            !p.getPabellon().equals("Manuel Cadenas") && p.getGolesVisitante() > p.getGolesLocal()){
                                        quali.setVictoria(1);
                                    }else if (Objects.equals(p.getGolesLocal(), p.getGolesVisitante())){
                                        quali.setEmpate(1);
                                    } else{
                                        quali.setDerrota(1);
                                    }
                                    clasificacions.add(quali);
                                }else{                                                                  //Si existe uno igual, lo buscamos y actualizamos resultados
                                    for (Clasificacion c : clasificacions){
                                        if (c.getNombre().equals(p.getDivision()) && c.getTemporada().equals(temporada)){
                                            if (p.getPabellon().equals("Manuel Cadenas") && p.getGolesLocal() > p.getGolesVisitante() ||
                                                    !p.getPabellon().equals("Manuel Cadenas") && p.getGolesVisitante() > p.getGolesLocal()){
                                                c.setVictoria((c.getVictoria() + 1));
                                            }else if (Objects.equals(p.getGolesLocal(), p.getGolesVisitante())){
                                                c.setEmpate((c.getEmpate() + 1));
                                            } else{
                                                c.setDerrota((c.getDerrota() + 1));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ClasificacionAdapter adapter = new ClasificacionAdapter(getContext(), clasificacions);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private boolean comprobarSiEsta(Partido partido,String temporada, List<Clasificacion> clasificacion){
        if (clasificacion.isEmpty()){
            return false;
        }else{
            for (Clasificacion c : clasificacion){
                if (c.getNombre().equals(partido.getDivision()) && c.getTemporada().equals(temporada)){
                    return true;
                }
            }
            return false;
        }
    }

    private List<Jugador> calibrar(List<Jugador> jugadores){
        List<Jugador> js = new ArrayList<>();
        for (Jugador j : jugadores){
            if (j.getTemporadas().size() > 1){
                for (Temporada t : j.getTemporadas()){
                    List<Temporada> t1 = new ArrayList<>();
                    t1.add(t);
                    Jugador j1 = new Jugador(j.getNombre(), j.getApellido1(), j.getApellido2(), j.getManoDominante(), t1);
                    js.add(j1);
                }
            }else{
                js.add(j);
            }
        }
        return  js;
    }

    private Task<List<String>> getTemporadas(){
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        Task<List<String>> task = conexionFirebase.getTemporadas();
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()){
                List<String> temp = task1.getResult();
                taskCompletionSource.setResult(temp);
            }else{
                taskCompletionSource.setException(taskCompletionSource.getTask().getException());
            }
        });
        return taskCompletionSource.getTask();
    }
}