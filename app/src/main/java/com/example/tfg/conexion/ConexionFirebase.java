package com.example.tfg.conexion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tfg.PreCompra;
import com.example.tfg.R;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Prenda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConexionFirebase {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();
    private static final int CACHE_SIZE = 100 * 1024 * 1024;

    public Task<List<Partido>> obtenerPartidos() {
        TaskCompletionSource<List<Partido>> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("temporadas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot document = task.getResult();
                List<DocumentSnapshot> documents = document.getDocuments();
                List<Task<QuerySnapshot>> divisionTasks = new ArrayList<>();
                for (DocumentSnapshot ds : documents) {
                    for (int d = 1; d <=4; d++) {
                        divisionTasks.add(ds.getReference().collection(ds.getString("division" + d)).get());
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
                    Prenda prenda = new Prenda(snapsot.getString("nombre"), (List<String>) snapsot.get("tallas"), snapsot.getDouble("precio"), snapsot.getString("imagen"));
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
                    if (ds.getString("comprador").equals(usuario) && ds.getBoolean("pagado").equals(Boolean.FALSE)){
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

    public void cargarImagen(Context contexto, ImageView holder, ImageView img, String url){
        StorageReference gsReference;
        if (url == null){
            gsReference = storage.getReferenceFromUrl("gs://balonmano-f213a.appspot.com/imagenes-default/" + randomFoto());
        }else{
            gsReference = storage.getReferenceFromUrl(url);
        }
        final long ONE_MEGABYTE = 5 * 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
            holder.setImageBitmap(bitmap1);
            if (img != null){img.setImageBitmap(bitmap1);}
        }).addOnFailureListener(exception -> Toast.makeText(contexto, "Error al descargar la imagen de la prenda", Toast.LENGTH_SHORT).show());
    }

    public void imagenPedido(Context contexto, ImageView holder, String nombrePrenda){
        Task<String> taskUrl = buscarImagenPorPrenda(nombrePrenda);
        taskUrl.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()){
                String url = task1.getResult();
                if (url.isEmpty()){
                    Toast.makeText(contexto, "No hay ninguna imagen de la prenda.", Toast.LENGTH_SHORT).show();
                }else {
                    StorageReference gsReference = storage.getReferenceFromUrl(url);
                    final long ONE_MEGABYTE = 5 * 1024 * 1024;
                    gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                        holder.setImageBitmap(bitmap1);
                    }).addOnFailureListener(exception -> Toast.makeText(contexto, "Error al descargar la imagen de la prenda", Toast.LENGTH_SHORT).show());
                }
            }else{
                Toast.makeText(contexto, "Error obteniendo la imagen de la prenda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Task<String> buscarImagenPorPrenda(String nombrePrenda) {
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

    public void borrarPedido(Context contexto, Pedido nombreprenda){
        db.collection("pedidos").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot snapsot = task.getResult();
                List<DocumentSnapshot> documents = snapsot.getDocuments();
                for (DocumentSnapshot ds : documents){
                    Pedido pedido = new Pedido(ds.getString("prenda"), ds.getString("talla"), ds.getLong("cantidad"), ds.getLong("precioUnidad"));
                    if (pedido.equals(nombreprenda)){
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

    public Task<Boolean> correoNoRegistrado(String correo) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        auth.fetchSignInMethodsForEmail(correo).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                SignInMethodQueryResult result = task.getResult();
                if (result.getSignInMethods().size() == 0){
                    taskCompletionSource.setResult(true);
                }else{
                    taskCompletionSource.setResult(false);
                }
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }

    public Task<List<String>> equiposFromTemporada(String temporada) {
        TaskCompletionSource<List<String>> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("temporadas").document(temporada).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                List<String> lista = new ArrayList<>();
                lista.add(ds.getString("division1"));
                lista.add(ds.getString("division2"));
                lista.add(ds.getString("division3"));
                lista.add(ds.getString("division4"));
                taskCompletionSource.setResult(lista);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }
    private String randomFoto(){
        int numero = (int) (Math.random() * 6) + 1;
        switch (numero) {
            case 1:
                return "corriendo.png";
            case 2:
                return "culturismo.png";
            case 3:
                return "futbol-americano.png";
            case 4:
                return "futbol.png";
            case 5:
                return "surf.png";
            case 6:
                return "voleibol.png";
        }
        return "corriendo.png";
    }

    public void comprobarAdmin(FloatingActionButton floating, Context contexto){
        if (user != null) {
            db.collection("usuarios").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.get("correo").equals(user.getEmail())) {
                            if (document.get("rol").equals("Administrador")) {
                                floating.setVisibility(View.VISIBLE);
                            } else {
                                floating.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                } else {
                    Toast.makeText(contexto, "Error con isAdmin().", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public DocumentReference getDocumento(String anios, String division, String id){
        return db.collection("temporadas").document(anios).collection(division).document(id);
    }
}
