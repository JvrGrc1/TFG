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
import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Clasificacion;
import com.example.tfg.entidad.Jugador;
import com.example.tfg.entidad.Partido;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EstadisticasFragment extends Fragment {
    private ConexionFirebase conexionFirebase = new ConexionFirebase();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Partido> partidos = new ArrayList<>();
    private List<Jugador> jugadores = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private ScrollView scrollView;
    private TextView clasificacion, goleadores;
    private Spinner spinner;
    private RecyclerView recyclerView;

    public EstadisticasFragment() {/* Required empty public constructor */}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        constraintLayout = root.findViewById(R.id.constrainStats);
        clasificacion = root.findViewById(R.id.textViewClasificacion);
        goleadores = root.findViewById(R.id.maximosGoleadores);
        spinner = root.findViewById(R.id.spinnerStatsEquipos);
        recyclerView = root.findViewById(R.id.recyclerClasificacionEquipos);
        scrollView = root.findViewById(R.id.scrollStats);

        rellenarSpinner();
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        Bundle args = getArguments();
        if (args != null && args.containsKey("lista")){
            partidos = (List<Partido>) args.getSerializable("lista");
        }

        getClasificacion(partidos);

        Task<List<Jugador>> task = conexionFirebase.obtenerJugadores();
        task.addOnCompleteListener(command -> {
            if (command.isSuccessful()){
                jugadores = command.getResult();

            }
        });

        comprobarModo();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinner.getAdapter().getItem(position).toString().equals("Todas las temporadas")) {
                    Calendar cal = Calendar.getInstance();
                    String[] partesTemp = spinner.getAdapter().getItem(position).toString().split("-");      //Separa temporada en dos: 21-22 --> 21, 22
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
                }else{
                    getClasificacion(partidos);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
        } else {
            constraintLayout.setBackgroundColor(Color.WHITE);
            scrollView.setBackgroundColor(Color.WHITE);
            clasificacion.setTextColor(getResources().getColor(R.color.azul_oscuro));
            goleadores.setTextColor(getResources().getColor(R.color.azul_oscuro));
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
            spinner.setAdapter(adapter);
            spinner.setSelection(0);
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