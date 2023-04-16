package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrarPartido extends AppCompatActivity {

    Spinner anios, division, jornada;
    EditText fecha, hora, local, visitante, gL, gV, pabellon;
    FloatingActionButton agregar, editar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    String id;

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
        }else{
            local.setFocusable(false);
            visitante.setFocusable(false);
            fecha.setFocusable(false);
            hora.setFocusable(false);
            pabellon.setFocusable(false);
        }

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

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jornada.getSelectedItem() == null){
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
                        datos.put("jornada", jornada.getSelectedItem().toString());
                        datos.put("local", local.getText().toString());
                        datos.put("pabellón", pabellon.getText().toString());
                        datos.put("visitante", visitante.getText().toString());

                        // Actualizar los campos del documento en Firebase Firestore
                        docRef.update(datos)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegistrarPartido.this, "Partido agregado correctamente.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegistrarPartido.this, "Error al agregar el partido", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, listaAnios);
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
                                jornada.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, listaJornadas));
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
    private void actualizarDatos() {
        db.collection("temporadas").document(anios.getSelectedItem().toString()).collection(division.getSelectedItem().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
            }
        });
    }
    private void isAdmin(FirebaseUser currentUser){
        db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot document: task.getResult()){
                        if (document.get("correo").equals(currentUser.getEmail())){
                            if (document.get("rol").equals("Administrador")){
                                editar.setVisibility(View.VISIBLE);
                                local.setFocusable(true);
                                visitante.setFocusable(true);
                                fecha.setFocusable(true);
                                hora.setFocusable(true);
                                pabellon.setFocusable(true);
                            }else{
                                local.setFocusable(false);
                                visitante.setFocusable(false);
                                fecha.setFocusable(false);
                                hora.setFocusable(false);
                                pabellon.setFocusable(false);
                            }
                        }
                    }
                }else{
                    Toast.makeText(RegistrarPartido.this, "Error con isAdmin().", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean todoRelleno(){
        return !local.getText().toString().isEmpty() && !visitante.getText().toString().isEmpty() && !gV.getText().toString().isEmpty() && !gL.getText().toString().isEmpty() && !hora.getText().toString().isEmpty() && !fecha.getText().toString().isEmpty() && !pabellon.getText().toString().isEmpty();
    }
    private String getDivision(){
        String divison = anios.getSelectedItem().toString();
        switch (divison){
            case "1NacionalMasc":
                return "1NM";
            case "DHPF":
                return "DHPF";
            case "1NacionalFem":
                return "1NF";
            case "2NacionalMasc":
                return "2NM";
        }
        return null;
    }
    private String getHora(){

        /*Puede estar interesante cambiar a un TimePicker
          quedaría más estético.*/
        return null;
    }
    private String getFecha(){

        //Puede estar interesante cambiar a un CalendarView
        /*
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String formattedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, month + 1, year);

            // Use formattedDate as needed
            }
         });*/
        return null;
    }

}
