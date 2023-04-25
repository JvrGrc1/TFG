package com.example.tfg.conexion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.collection.LruCache;

import com.bumptech.glide.Glide;
import com.example.tfg.R;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Prenda;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConexionFirebase {

    private final List<String> divisiones = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();
    private static final int CACHE_SIZE = 100 * 1024 * 1024;

    public ConexionFirebase() {
        divisiones.add("DHPF");
        divisiones.add("1NacionalMasc");
        divisiones.add("1NacionalFem");
        divisiones.add("2NacionalMasc");
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

    /*public Task<List<Partido>> obtenerPartidos2() {
        TaskCompletionSource<List<Partido>> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("temporadas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot document = task.getResult();
                List<DocumentSnapshot> documents = document.getDocuments();
                List<Task<QuerySnapshot>> divisionTasks = new ArrayList<>();
                int d = 1;
                for (DocumentSnapshot ds : documents) {
                    divisionTasks.add(ds.getReference().collection(ds.getString("division" + d)).get());
                    d++;
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
    }*/

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

    public void imagenPrenda(Context contexto, ImageView holder, String url){
        Bitmap img = buscarImagenEnCache(url);
        if (img == null) {
            StorageReference gsReference = storage.getReferenceFromUrl(url);
            final long ONE_MEGABYTE = 5 * 1024 * 1024;
            gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                holder.setImageBitmap(bitmap1);
                guardarImagenEnCache(contexto,url, bitmap);
            }).addOnFailureListener(exception -> Toast.makeText(contexto, "Error al descargar la imagen de la prenda", Toast.LENGTH_SHORT).show());
        }else {
            holder.setImageBitmap(img);
        }
    }

    private void guardarImagenEnCache(Context context, String url, Bitmap bitmap) {
        LruCache<String, Bitmap> cache = new LruCache<>(CACHE_SIZE);
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(context.getCacheDir(), url));

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            cache.put(url, bitmap);

        } catch (IOException e) {}
    }


    private Bitmap buscarImagenEnCache(String url) {
        LruCache<String, Bitmap> cache = new LruCache<>(CACHE_SIZE);
        Bitmap cachedImage = cache.get(url);
        return cachedImage;
    }

    public void imagenPedido(Context contexto, ImageView holder, String nombrePrenda){
        Task<String> taskUrl = buscarImagenPorPrenda(contexto, nombrePrenda);
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

    private Task<String> buscarImagenPorPrenda(Context contexto, String nombrePrenda) {
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

    public List<String> equiposFromTemporada(String temporada, Context context) {
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
        List<String> equipos1 = new ArrayList<>();
        taskCompletionSource.getTask().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()){
                List<String> equipos = task1.getResult();
                if (equipos.isEmpty()){
                    equipos1.add("");
                }else {
                    equipos1.add("");
                    equipos1.addAll(equipos);
                }
            }else{
                Toast.makeText(context, "Error obteniendo los pedidos", Toast.LENGTH_SHORT).show();
            }
        });
        return equipos1;
    }

    public void conseguirImagen(String url, ImageView imagenUser, ImageView imagenBoton, Context context){
        StorageReference gsReference;
        if (url != null){
            gsReference = storage.getReferenceFromUrl(url);
        }else{
            String foto = randomFoto();
            gsReference = storage.getReferenceFromUrl("gs://balonmano-f213a.appspot.com/imagenes-default/" + foto);
        }
        final long MEGABYTES =  5 * 1024 * 1024;                               //Sirve para establecer un limite de tamaño a la imagen y si se pasa no se descargara completamente.
        gsReference.getBytes(MEGABYTES).addOnSuccessListener(bytes -> {
            //Glide.with(context).load(url).centerCrop().placeholder(R.drawable.carrito_compra).into(imagenUser);
            //Glide.with(context).load(url).centerCrop().placeholder(R.drawable.carrito_compra).into(imagenBoton);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);              //Decodifica los bytes de la img descargada en un Bitmap
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,200,200,true);    //Redimensionar el Bitmap anteriro a una imagen 200*200
            imagenUser.setImageBitmap(bitmap1);
            imagenBoton.setImageBitmap(bitmap1);
        }).addOnFailureListener(exception -> Toast.makeText(context, "Error al descargar la imagen", Toast.LENGTH_SHORT).show());

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
}
