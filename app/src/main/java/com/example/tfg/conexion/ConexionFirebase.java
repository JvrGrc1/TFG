package com.example.tfg.conexion;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Prenda;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConexionFirebase {

    private final List<String> divisiones = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public ConexionFirebase() {
        divisiones.add("1NacionalMasc");
        divisiones.add("DHPF");
        divisiones.add("2NacionalMasc");
        divisiones.add("1NacionalFem");
        divisiones.add("1TerritorialMasc");
    }
    public Task<List<Partido>> obtenerPartidos() {
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
                                Partido p = new Partido(ds.getString("division"), ds.getString("local"), ds.getString("visitante"), ds.getLong("golesLocal"), ds.getLong("golesVisitante"), ds.getString("fecha"), ds.getString("pabellón"), ds.getString("hora"), ds.getLong("jornada"));
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

    public Task<List<Prenda>> obtenerTienda(){
        TaskCompletionSource<List<Prenda>> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("prendas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot document  = task.getResult();
                List<DocumentSnapshot> documents = document.getDocuments();
                List<Prenda> prendas = new ArrayList<>();
                for (DocumentSnapshot snapsot : documents){
                    Prenda prenda = new Prenda(snapsot.getString("nombre"), snapsot.get("tallas", ArrayList.class), snapsot.getDouble("precio"), snapsot.getString("imagen"));
                    prendas.add(prenda);
                }
                taskCompletionSource.setResult(prendas);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    public Task<List<Pedido>> obtenerPedidos(String usuario){
        TaskCompletionSource<List<Pedido>> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("pedidos").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot document = task.getResult();
                List<DocumentSnapshot> documents = document.getDocuments();
                List<Pedido> pedidos = new ArrayList<>();
                for (DocumentSnapshot ds : documents){
                    if (ds.getString("comprador") == usuario && Boolean.TRUE.equals(ds.getBoolean("pagado"))){
                        Pedido pedido = new Pedido(ds.getString("prenda"), ds.getString("talla"), ds.getLong("cantidad"), ds.getLong("precioUnidad"));
                        pedidos.add(pedido);
                    }
                }
                taskCompletionSource.setResult(pedidos);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    public void subirPedido(Context contexto, Map<String, Object> pedido) {
        db.collection("pedidos").add(pedido).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(contexto, "Se ha añadido el pedido correctamente.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(contexto, "No se ha podido añadir el pedido. Intentelo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void imagenPrenda(Context contexto, ImageView holder, String url){
        StorageReference gsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 5 * 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,200,200,true);
            holder.setImageBitmap(bitmap1);
        }).addOnFailureListener(exception -> Toast.makeText(contexto, "Error al descargar la imagen de la prenda", Toast.LENGTH_SHORT).show());
    }

    public void imagenPedido(Context contexto, ImageView holder, String nombrePrenda){
        String url = String.valueOf(buscarIamgenPorPrenda(contexto, nombrePrenda));
        StorageReference gsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 5 * 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            holder.setImageBitmap(bitmap1);
        }).addOnFailureListener(exception -> Toast.makeText(contexto, "Error al descargar la imagen de la prenda", Toast.LENGTH_SHORT).show());
    }

    private Task<String> buscarIamgenPorPrenda(Context contexto, String nombrePrenda) {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("prendas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot snapsot = task.getResult();
                List<DocumentSnapshot> documents = snapsot.getDocuments();
                String url = null;
                for (DocumentSnapshot ds : documents){
                    if (ds.getString("nombre").matches(nombrePrenda)){
                        url = ds.getString("imagen");
                    }
                }
                taskCompletionSource.setResult(url);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    public void borrarPedido(Context contexto, String nombreprenda){
        db.collection("pedidos").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot snapsot = task.getResult();
                List<DocumentSnapshot> documents = snapsot.getDocuments();
                for (DocumentSnapshot ds : documents){
                    if (ds.getString("nombre").matches(nombreprenda) && ds.getString("comprador").matches(user.getEmail())){
                        db.collection("pedidos").document(ds.getId()).delete().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                Toast.makeText(contexto, "Borrado con éxito.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(contexto, "Error al borrar el pedido", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }else{
                Toast.makeText(contexto, "Error al encontrar el pedido.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
