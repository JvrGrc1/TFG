package com.example.tfg.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.tfg.adaptador.JornadasAdapter;
import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Jugador;
import com.example.tfg.entidad.Partido;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
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

        ClasificacionAdapter adapter = new ClasificacionAdapter(getContext(), partidos);
        recyclerView.setAdapter(adapter);

        Task<List<Jugador>> task = conexionFirebase.obtenerJugadores();
        task.addOnCompleteListener(command -> {
            if (command.isSuccessful()){
            jugadores = command.getResult();

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