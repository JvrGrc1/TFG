package com.example.tfg.fragments;

import android.content.Context;
import android.graphics.Color;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EstadisticasFragment extends Fragment {
    private ConexionFirebase conexionFirebase = new ConexionFirebase();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Partido> partidos = new ArrayList<>();
    private List<Jugador> jugadores = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private ScrollView scrollView;
    private TextView clasificacion;
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
        spinner = root.findViewById(R.id.spinnerStatsEquipos);
        recyclerView = root.findViewById(R.id.recyclerClasificacionEquipos);
        scrollView = root.findViewById(R.id.scrollStats);

        rellenarSpinner();
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        Bundle args = getArguments();
        if (args != null && args.containsKey("lista")){
            partidos = (List<Partido>) args.getSerializable("lista");
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItem().equals("Todas las temporadas")) {
                    List<Clasificacion> clasificacion = pasarLista(partidos, "todas");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ClasificacionAdapter adapter = new ClasificacionAdapter(getContext(), partidos);
        recyclerView.setAdapter(adapter);

        Task<List<Jugador>> task = conexionFirebase.obtenerJugadores();
        task.addOnCompleteListener(command -> {
            if (command.isSuccessful()){
                jugadores = command.getResult();
            }else{
                Toast.makeText(getContext(), "Error al cargar jugadores", Toast.LENGTH_SHORT).show();
            }
        });

        boolean modoOscuro = requireActivity().getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                .getBoolean("modoOscuro", false);

        if (modoOscuro) {
            constraintLayout.setBackgroundColor(Color.BLACK);
            scrollView.setBackgroundColor(Color.BLACK);
            clasificacion.setTextColor(Color.WHITE);
        } else {
            constraintLayout.setBackgroundColor(Color.WHITE);
            scrollView.setBackgroundColor(Color.WHITE);
            clasificacion.setTextColor(getResources().getColor(R.color.azul_oscuro));
        }

        return root;
    }

    private List<Clasificacion> pasarLista(List<Partido> partidos, String detalle){
        List<Clasificacion> retorno = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        switch (detalle){
            case "todas":
                int sp = spinner.getAdapter().getCount();       //Obtengo el numero de items del Spinner que corresponde con el numero de temporadas + 1
                for (int i = 1; i <= sp; i++){
                    temp.add((String) spinner.getAdapter().getItem(i));
                    Task<List<String>> equiposTask = conexionFirebase.obtenerEquipos((String) spinner.getAdapter().getItem(i));
                    int temporada = i;
                    equiposTask.addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            List<String> equipos = task.getResult();
                            List<Clasificacion> clasificacions = new ArrayList<>();
                            for (String equipo : equipos){
                                clasificacions = recogerDatos(equipo, (String) spinner.getAdapter().getItem(temporada));
                            }
                        }
                    });
                }
                break;
        }

        return retorno;
    }

    private List<Clasificacion> recogerDatos(String equipo, String temporada){
        List<Clasificacion> clasificacions = new ArrayList<>();
        Clasificacion quali = new Clasificacion(equipo, temporada);
        for (Partido partido : partidos){                               //Recorre todos los partidos
            if (partido.getDivision().equals(equipo) && fechaCorrecta(temporada, partido.getFecha())){              //Comprueba si es la division y temporada correcta
                if (partido.getGolesLocal() != 0 && partido.getGolesVisitante() != 0) {
                    if (partido.getLocal().contains("Leganés") && partido.getGolesLocal() > partido.getGolesVisitante()                         //Si Leganes es local y local gana
                            || partido.getVisitante().contains("Leganés") && partido.getGolesVisitante() > partido.getGolesVisitante()) {       //Si leganes es visitante y visitante gana
                        quali.setVictoria(quali.getVictoria() + 1);
                    }else if (partido.getGolesLocal() == partido.getGolesVisitante()){                                                          //Si local y visitante empatan
                        quali.setEmpate(quali.getEmpate() + 1);
                    }else{                                                                                                                      //[(leganes visitante,gana local) o (leganes local,gana visitante)]
                        quali.setDerrota(quali.getDerrota() + 1);
                    }
                    clasificacions.add(quali);
                }
            }
        }
        return clasificacions;
    }

    private boolean fechaCorrecta(String temporada, Date fecha){
        Calendar cal = Calendar.getInstance();
        String[]partesTemp = temporada.split("-");
        String[] partesFecha = fecha.toString().split(" ");
        String anio = partesFecha[5].charAt(2) + "" + partesFecha[5].charAt(3);
        cal.setTime(fecha);
        if (anio.equals(partesTemp[0]) && cal.get(Calendar.MONTH) + 1 > 8 || anio.equals(partesTemp[1]) && cal.get(Calendar.MONTH) + 1 < 8){
            return true;
        }
        return false;
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
}