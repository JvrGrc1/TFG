package com.example.tfg.conexion;


import com.example.tfg.entidad.Partido;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConexionFirebase {

    private final List<String> divisiones = new ArrayList<>();

    public ConexionFirebase() {
        divisiones.add("1NacionalMasc");
        divisiones.add("DHPF");
        divisiones.add("2NacionalMasc");
        divisiones.add("1NacionalFem");
        divisiones.add("1TerritorialMasc");
    }
    public Task<List<Partido>> obtenerPartidos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TaskCompletionSource<List<Partido>> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("temporadas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot document = task.getResult();
                List<DocumentSnapshot> documents = document.getDocuments();
                List<Task<QuerySnapshot>> divisionTasks = new ArrayList<>();
                for (DocumentSnapshot ds : documents) {
                    for (String division : divisiones) {
                        divisionTasks.add(ds.getReference().collection(division).get());
                    }
                }
                Tasks.whenAllComplete(divisionTasks).addOnCompleteListener(task2 -> {
                    List<Partido> partidos = new ArrayList<>();
                    for (Task<QuerySnapshot> divisionTask : divisionTasks) {
                        if (divisionTask.isSuccessful()) {
                            QuerySnapshot divisionDocument = divisionTask.getResult();
                            List<DocumentSnapshot> documents1 = divisionDocument.getDocuments();
                            for (DocumentSnapshot ds : documents1) {
                                Partido p = new Partido((String) ds.get("division"), (String) ds.get("local"), (String) ds.get("visitante"), (Long) ds.get("golesLocal"), (Long) ds.get("golesVisitante"), (String) ds.get("fecha"), (String) ds.get("pabellón"), (String) ds.get("hora"), (Long) ds.get("jornada"));
                                partidos.add(p);
                            }
                        }
                    }
                    taskCompletionSource.setResult(partidos);
                });
            } else {
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });

        return taskCompletionSource.getTask();
    }
}
