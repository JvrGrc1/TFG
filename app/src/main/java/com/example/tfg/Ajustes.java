package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Ajustes extends AppCompatActivity {

    private AutoCompleteTextView temporadas, divisiones, jornadas;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ConexionFirebase conexion = new ConexionFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        temporadas = findViewById(R.id.autoCOmpleteTemporadas);
        divisiones = findViewById(R.id.autoCOmpleteDivisiones);
        jornadas = findViewById(R.id.autoCOmpleteJornadas);

        rellenarTemporadas();
        rellenarDivisiones();
        rellenarJornadas();
    }

    private void rellenarTemporadas(){
        db.collection("temporadas").get().addOnCompleteListener(task -> {
            List<String> lista = new ArrayList<>();
            lista.add("TODAS");
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    lista.add(document.getId());
                }
            }else{
                Toast.makeText(this, "Error al encontrar temporadas", Toast.LENGTH_SHORT).show();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lista);
            temporadas.setAdapter(adapter);
            temporadas.setSelection(0);
            divisiones.setEnabled(false);
            jornadas.setEnabled(false);
        });
    }

    private void rellenarDivisiones(){
        temporadas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!temporadas.getText().toString().equals("") && !temporadas.getText().toString().equals("TODAS")){
                    Task<List<String>> listEquiposConexion = conexion.equiposFromTemporada(temporadas.getText().toString());
                    listEquiposConexion.addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            List<String> equipos1 = task1.getResult();
                            for (String equipo : equipos1){
                                equipos1.set(equipos1.indexOf(equipo), getEquipos(equipo));
                            }
                            equipos1.add(0,"TODOS");
                            divisiones.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, equipos1));
                            divisiones.setEnabled(true);
                        }else{
                            Toast.makeText(view.getContext(), "Error obteniendo los pedidos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    divisiones.setSelection(0);
                    jornadas.setSelection(0);
                    divisiones.setEnabled(false);
                    jornadas.setEnabled(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private String getEquipos(String equipo) {
        if (equipo == null) {
            equipo = divisiones.getText().toString();
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

    private void rellenarJornadas() {
        divisiones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!divisiones.getText().equals("") && !divisiones.getText().equals("TODOS")){
                    db.collection("temporadas").document(temporadas.getText().toString()).collection(getEquipos(null)).get().addOnCompleteListener(task -> {
                        List<String> listaJornadas = new ArrayList<>();
                        listaJornadas.add("TODAS");
                        if (task.isSuccessful()){
                            int numDocs = 1;
                            for (QueryDocumentSnapshot document : task.getResult()){
                                listaJornadas.add(numDocs++ + "");
                            }
                            jornadas.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, listaJornadas));
                            jornadas.setEnabled(true);
                        }else{
                            Toast.makeText(view.getContext(), "El año " + temporadas.getText().toString() + " no existe.", Toast.LENGTH_SHORT).show();
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
}