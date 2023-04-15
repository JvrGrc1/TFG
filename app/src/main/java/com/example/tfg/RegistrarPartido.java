package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegistrarPartido extends AppCompatActivity {

    Spinner anios, division, jornada;
    TextView fecha, hora, local, visitante, gL, gV, pabellon, id;
    FloatingActionButton agregar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_partido);

        anios = findViewById(R.id.spinnerAnio);
        division = findViewById(R.id.spinnerDivision);
        jornada = findViewById(R.id.spinnerJornada);
        local = findViewById(R.id.localRegistro);
        visitante = findViewById(R.id.visitanteRegistro);
        agregar = findViewById(R.id.buttonAgregar);
        rellenarAnios();
        rellenarDivisiones();
        rellenarJornadas();

        jornada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarDatos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void rellenarAnios() {
        db.collection("temporadas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> listaAnios = new ArrayList<>();
                listaAnios.add("");
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        listaAnios.add(document.getId());
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "El año " + anios.getSelectedItem().toString() + " no existe.", Toast.LENGTH_SHORT).show();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listaAnios);
                anios.setAdapter(adapter);
                division.setEnabled(false);
                jornada.setEnabled(false);
            }
        });
    }
    private void rellenarDivisiones() {
        anios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!anios.getSelectedItem().equals("")) {
                    List<String> listaEquipos = new ArrayList<>();
                    listaEquipos.add("");
                    listaEquipos.add("1NacionalMasc");
                    listaEquipos.add("DHPF");
                    listaEquipos.add("1NacionalFem");
                    listaEquipos.add("2NacionalMasc");
                    division.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listaEquipos));
                    division.setEnabled(true);
                } else {
                    division.setSelection(0);
                    jornada.setSelection(0);
                    division.setEnabled(false);
                    jornada.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void rellenarJornadas() {
        division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!division.getSelectedItem().equals("") & !division.getSelectedItem().equals("TODOS")){
                    db.collection("temporadas").document(anios.getSelectedItem().toString()).collection(division.getSelectedItem().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<Integer> listaJornadas = new ArrayList<>();
                            if (task.isSuccessful()){
                                Integer numDocs = 1;
                                for (QueryDocumentSnapshot document : task.getResult()){
                                    listaJornadas.add(numDocs++);
                                }
                                jornada.setAdapter(new ArrayAdapter<Integer>(getApplicationContext(), android.R.layout.simple_spinner_item, listaJornadas));
                                jornada.setEnabled(true);
                            }else{
                                Toast.makeText(getApplicationContext(), "El año " + anios.getSelectedItem().toString() + " no existe.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    jornada.setSelection(0);
                    jornada.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void rellenarCampos(){
        jornada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showProgressDialog();
                db.collection("temporadas").document(anios.getSelectedItem().toString()).collection(division.getSelectedItem().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("jornada").equals(jornada.getSelectedItem().toString())) {
                                    System.out.println(document);
                                    local.setText(document.get("local").toString());
                                    visitante.setText(document.get("visitante").toString());
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "El año " + anios.getSelectedItem().toString() + " no existe.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void showProgressDialog() {
        progressDialog = new AlertDialog.Builder(this)
                .setMessage("Cargando datos...")
                .setCancelable(false)
                .create();
        progressDialog.show();
    }

    public void actualizarDatos() {
        db.collection("temporadas").document(anios.getSelectedItem().toString()).collection(division.getSelectedItem().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (Integer.parseInt(document.get("jornada").toString()) == (Integer.parseInt(jornada.getSelectedItem().toString()))){
                            System.out.println(document);
                            local.setText(document.get("local").toString());
                            visitante.setText(document.get("visitante").toString());
                        }
                    }
                } else {
                    Toast.makeText(RegistrarPartido.this, "Error al conseguir los documentos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
