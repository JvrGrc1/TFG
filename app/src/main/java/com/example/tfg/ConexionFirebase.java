package com.example.tfg;

import androidx.annotation.NonNull;

import com.example.tfg.entidad.Partido;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ConexionFirebase {

    private List<String> divisiones = new ArrayList<>();
    private List<Partido> partidos = new ArrayList<>();

    public ConexionFirebase() {
        divisiones.add("1NacionalMasc");
        divisiones.add("DHPF");
        divisiones.add("2NacionalMasc");
        divisiones.add("1NacionalFem");
    }
    public List<Partido> obtenerPartidos(){
        List<Partido> partidos = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("temporadas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    List<DocumentSnapshot> documents = document.getDocuments();
                    List<Task<QuerySnapshot>> divisionTasks = new ArrayList<>();
                    for (DocumentSnapshot ds : documents) {
                        for (String division : divisiones) {
                            divisionTasks.add(ds.getReference().collection(division).get());
                        }
                    }
                    Tasks.whenAllComplete(divisionTasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Task<?>>> task) {
                            for (Task<QuerySnapshot> divisionTask : divisionTasks) {
                                if (divisionTask.isSuccessful()) {
                                    QuerySnapshot divisionDocument = divisionTask.getResult();
                                    List<DocumentSnapshot> documents = divisionDocument.getDocuments();
                                    for (DocumentSnapshot ds : documents) {
                                            Partido p = new Partido((String) ds.get("division"), (String) ds.get("local"), (String) ds.get("visitante"), (Long) ds.get("golesLocal"), (Long) ds.get("golesVisitante"), (String) ds.get("fecha"), (String) ds.get("pabellón"), (String) ds.get("hora"), (Long) ds.get("jornada"));
                                        partidos.add(p);
                                    }
                                }
                            }
                            latch.countDown();
                        }
                    });
                }
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return partidos;
    }

}
