package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrarPartido extends AppCompatActivity {

    private Spinner anios, division, jornada;
    private EditText fecha, hora, local, visitante, gL, gV, pabellon;
    private FloatingActionButton agregar, editar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ConexionFirebase conexion = new ConexionFirebase();
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_partido);

        anios = findViewById(R.id.spinnerAnio);
        division = findViewById(R.id.spinnerDivision);
        jornada = findViewById(R.id.spinnerJornada);
        local = findViewById(R.id.localRegistro);
        visitante = findViewById(R.id.visitanteRegistro);
        gL = findViewById(R.id.golesLocalRegistro);
        gV = findViewById(R.id.golesVisitanteRegistro);
        fecha = findViewById(R.id.fechaRegistrar);
        hora = findViewById(R.id.horaRegistrar);
        pabellon = findViewById(R.id.pabellonRegistrar);
        agregar = findViewById(R.id.buttonAgregar);
        editar = findViewById(R.id.buttonEditar);

        if (auth.getCurrentUser() != null){
            isAdmin(auth.getCurrentUser());
        }
        local.setEnabled(false);
        visitante.setEnabled(false);
        hora.setEnabled(false);
        pabellon.setEnabled(false);
        fecha.setEnabled(false);

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
                /*Si no ha seleccionado nada no se realiza ninguna acción*/
            }
        });

        agregar.setOnClickListener(v -> {
            if (jornada.getSelectedItem() == null || anios.getSelectedItem().equals("")){
                Toast.makeText(RegistrarPartido.this, "No has seleccionado jornada", Toast.LENGTH_SHORT).show();
            }else{
                if (todoRelleno()){
                    DocumentReference docRef = db.collection("temporadas").document(anios.getSelectedItem().toString()).collection(division.getSelectedItem().toString()).document(id);

                    // Crear un mapa con los campos que se van a actualizar y sus valores
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("division", getDivision());
                    datos.put("fecha", getFecha());
                    datos.put("golesLocal", Integer.parseInt(gL.getText().toString()));
                    datos.put("golesVisitante", Integer.parseInt(gV.getText().toString()));
                    datos.put("hora", getHora());
                    datos.put("jornada", Integer.parseInt(jornada.getSelectedItem().toString()));
                    datos.put("local", local.getText().toString());
                    datos.put("pabellón", pabellon.getText().toString());
                    datos.put("visitante", visitante.getText().toString());

                    // Actualizar los campos del documento en Firebase Firestore
                    docRef.update(datos).addOnSuccessListener(aVoid -> {
                        Task<List<Partido>> task = conexion.obtenerPartidos();
                        task.addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(RegistrarPartido.this, "Cambios realizados correctamente", Toast.LENGTH_SHORT).show();
                                List<Partido> partidos = task1.getResult();
                                Intent intent = new Intent(RegistrarPartido.this, MainActivity.class);
                                intent.putExtra("lista", (Serializable) partidos);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            } else {
                                Toast.makeText(RegistrarPartido.this, "Error obteniendo partidos", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).addOnFailureListener(e -> Toast.makeText(RegistrarPartido.this, "Error al agregar el partido", Toast.LENGTH_SHORT).show());
                }
            }
        });

        editar.setOnClickListener(v -> {
            if (local.isEnabled()) {
                cambiarEnabled(false);
                Toast.makeText(RegistrarPartido.this, "Ya no puedes editar los campos", Toast.LENGTH_SHORT).show();
            }else{
                cambiarEnabled(true);
                Toast.makeText(RegistrarPartido.this, "Puedes editar los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rellenarAnios() {
        db.collection("temporadas").get().addOnCompleteListener(task -> {
            List<String> listaAnios = new ArrayList<>();
            listaAnios.add("");
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    listaAnios.add(document.getId());
                }
            }else{
                Toast.makeText(getApplicationContext(), "El año " + anios.getSelectedItem().toString() + " no existe.", Toast.LENGTH_SHORT).show();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, listaAnios);
            anios.setAdapter(adapter);
            division.setEnabled(false);
            jornada.setEnabled(false);
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
                    listaEquipos.add("1TerritorialMasc");
                    //List<String> equipos = conexion.equiposFromTemporada(anios.getSelectedItem().toString(), getApplicationContext());
                    //division.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, equipos));
                    division.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, listaEquipos));
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
                if (!division.getSelectedItem().equals("") && !division.getSelectedItem().equals("TODOS")){
                    db.collection("temporadas").document(anios.getSelectedItem().toString()).collection(division.getSelectedItem().toString()).get().addOnCompleteListener(task -> {
                        List<Integer> listaJornadas = new ArrayList<>();
                        if (task.isSuccessful()){
                            int numDocs = 1;
                            for (QueryDocumentSnapshot document : task.getResult()){
                                listaJornadas.add(numDocs++);
                            }
                            /*for (int i = 1; i <= task.getResult().size(); i++){
                                listaJornadas.add(i);
                            }*/
                            jornada.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, listaJornadas));
                            jornada.setEnabled(true);
                        }else{
                            Toast.makeText(getApplicationContext(), "El año " + anios.getSelectedItem().toString() + " no existe.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    jornada.setSelection(0);
                    jornada.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void actualizarDatos() {
        db.collection("temporadas").document(anios.getSelectedItem().toString()).collection(division.getSelectedItem().toString()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (Integer.parseInt(document.get("jornada").toString()) == (Integer.parseInt(jornada.getSelectedItem().toString()))){
                        id = document.getId();
                        local.setText(document.get("local").toString());
                        visitante.setText(document.get("visitante").toString());
                        gL.setText(document.get("golesLocal").toString());
                        gV.setText(document.get("golesVisitante").toString());
                        fecha.setText(document.get("fecha").toString());
                        hora.setText(document.get("hora").toString());
                        pabellon.setText(document.get("pabellón").toString());
                    }
                }
            } else {
                Toast.makeText(RegistrarPartido.this, "Error al conseguir los documentos.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void isAdmin(FirebaseUser currentUser){
        db.collection("usuarios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DocumentSnapshot document: task.getResult()){
                    if (document.get("correo").equals(currentUser.getEmail())){
                        if (document.get("rol").equals("Administrador")){
                            editar.setVisibility(View.VISIBLE);
                        }else{
                            editar.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }else{
                Toast.makeText(RegistrarPartido.this, "Error con isAdmin().", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean todoRelleno(){
        return !local.getText().toString().isEmpty() && !visitante.getText().toString().isEmpty() && !gV.getText().toString().isEmpty() && !gL.getText().toString().isEmpty() && !hora.getText().toString().isEmpty() && !fecha.getText().toString().isEmpty() && !pabellon.getText().toString().isEmpty() && getFecha() != null && getFecha() != null;
    }
    private String getDivision(){
        String divison = division.getSelectedItem().toString();
        switch (divison){
            case "1NacionalMasc":
                return "1NM";
            case "DHPF":
                return "DHPF";
            case "1NacionalFem":
                return "1NF";
            case "2NacionalMasc":
                return "2NM";
            case "1TerritorialMasc":
                return "1TM";
        }
        return null;
    }
    private String getHora(){
        String patron = "^(0?[1-9]|1\\d|2[0-4]):([0-5]?\\d)$";
        if (hora.getText().toString().matches(patron)){
            return hora.getText().toString();
        }else{
            Toast.makeText(this, "La hora está mal escrita", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    private String getFecha(){
        String patron = "\\d{2}-\\d{2}-(2022|2023)";
        if (fecha.getText().toString().matches(patron)){
            return fecha.getText().toString();
        }else{
            Toast.makeText(this, "La fecha está mal escrita", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void cambiarEnabled(boolean booleano){
        if (booleano){
            local.setEnabled(true);
            local.setBackgroundResource(R.drawable.fondo_local);
            visitante.setEnabled(true);
            visitante.setBackgroundResource(R.drawable.fondo_visitante);
            hora.setEnabled(true);
            hora.setBackgroundResource(R.drawable.fondo_spinner);
            pabellon.setEnabled(true);
            pabellon.setBackgroundResource(R.drawable.fondo_spinner);
            fecha.setEnabled(true);
            fecha.setBackgroundResource(R.drawable.fondo_spinner);
        }else{
            local.setEnabled(false);
            local.setBackgroundResource(R.drawable.fondo_local_disenabled);
            visitante.setEnabled(false);
            visitante.setBackgroundResource(R.drawable.fondo_visitante_disenabled);
            hora.setEnabled(false);
            hora.setBackgroundResource(R.drawable.fondo_spinner_disenabled);
            pabellon.setEnabled(false);
            pabellon.setBackgroundResource(R.drawable.fondo_spinner_disenabled);
            fecha.setEnabled(false);
            fecha.setBackgroundResource(R.drawable.fondo_spinner_disenabled);
        }

    }
}
