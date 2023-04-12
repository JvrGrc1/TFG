package com.example.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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
    TextView fecha, hora, local, visitante, gL, gV, pabellon, id;
    Button agregar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_partido);

        anios = findViewById(R.id.spinnerAnio);
        division = findViewById(R.id.spinnerDivision);
        jornada = findViewById(R.id.spinnerJornada);

        rellenarAnios();

        anios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (anios.getSelectedItem().equals("")){
                    division.setEnabled(false);
                    jornada.setEnabled(false);
                }else{
                    rellenarDivisiones();
                    division.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                division.setEnabled(false);
                jornada.setEnabled(false);
            }
        });


    }

    private void rellenarAnios(){
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
                    Toast.makeText(RegistrarPartido.this, "El año " + anios.getSelectedItem().toString() + " no existe.", Toast.LENGTH_SHORT).show();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listaAnios);
                anios.setAdapter(adapter);
            }
        });
    }

    private void rellenarDivisiones(){
        db.collection("temporadas").document(anios.getSelectedItem().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    List<String> listaDivisiones = new ArrayList<>();
                    listaDivisiones.add("");
                }
            }
        });
    }
}
