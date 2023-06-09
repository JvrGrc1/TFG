package com.example.tfg.conexion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.tfg.Login;
import com.example.tfg.MainActivity;
import com.example.tfg.PerfilUsuario;
import com.example.tfg.R;
import com.example.tfg.entidad.Jugador;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Prenda;
import com.example.tfg.entidad.Temporada;
import com.example.tfg.entidad.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ConexionFirebase {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();
    final long ONE_MEGABYTE = 100 * 1024 * 1024;


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
                    Prenda prenda = new Prenda(snapsot.getString("nombre"), (List<String>) snapsot.get("tallas"), snapsot.getDouble("precio"), (List<String>) snapsot.get("imagen"));
                    prendas.add(prenda);
                }
                taskCompletionSource.setResult(prendas);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }
    public Task<List<Jugador>> obtenerJugadores(){
        TaskCompletionSource<List<Jugador>> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("jugadores").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot document  = task.getResult();
                List<DocumentSnapshot> documents = document.getDocuments();
                List<Jugador> jugadores = new ArrayList<>();
                for (DocumentSnapshot snapsot : documents){
                    Jugador jugador = new Jugador(snapsot.getString("nombre"), snapsot.getString("apellido1"), snapsot.getString("apellido2"), snapsot.getString("manoDominante"), listaTemporadas(snapsot.get("temporada")));
                    jugadores.add(jugador);
                }
                taskCompletionSource.setResult(jugadores);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }
    private List<Temporada> listaTemporadas(Object mapa){
        Map<String, Object> map = (Map<String, Object>) mapa;
        List<Temporada> temporadas = new ArrayList<>();
        if(map.values() != null) {
            for (Object object : map.values()) {
                Map<String, Object> mapaFinal = (Map<String, Object>) object;
                String anio = key(map, object);
                if (anio != null) {
                    Temporada temporada = new Temporada(anio, (Long) mapaFinal.get("2minutos"), (Long) mapaFinal.get("amarillas"), (Long) mapaFinal.get("rojas"), (Long) mapaFinal.get("paradas"), (Long) mapaFinal.get("disparos"), (String) mapaFinal.get("posicion"), (Long) mapaFinal.get("dorsal"), (Long) mapaFinal.get("goles"), (String) mapaFinal.get("equipo"));
                    temporadas.add(temporada);
                }
            }
        }
        return temporadas;
    }
    private String key(Map<String, Object> map, Object object) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue().equals(object)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public Task<List<Pedido>> obtenerPedidos(String usuario){
        TaskCompletionSource<List<Pedido>> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("pedidos").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot document = task.getResult();
                List<DocumentSnapshot> documents = document.getDocuments();
                List<Pedido> pedidos = new ArrayList<>();
                for (DocumentSnapshot ds : documents){
                    if (Objects.equals(ds.getString("comprador"), usuario) && Objects.equals(ds.getBoolean("pagado"), Boolean.FALSE)){
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
    public Task<Usuario> datosUsuario(String email){
        TaskCompletionSource<Usuario> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("usuarios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot document = task.getResult();
                List<DocumentSnapshot> documents = document.getDocuments();
                Usuario user = new Usuario();
                for (DocumentSnapshot ds : documents){
                    if (Objects.equals(ds.getString("correo"), email)){
                        user.setNombre(ds.getString("nombre"));
                        user.setCorreo(ds.getString("correo"));
                        user.setApellido1(ds.getString("apellido1"));
                        user.setApellido2(ds.getString("apellido2"));
                        user.setImagen(ds.getString("imagen"));
                        user.setDireccion(ds.getString("direccion"));
                        user.setRol(ds.getString("rol"));
                        user.setTlf(ds.getString("tlf"));
                    }
                }
                taskCompletionSource.setResult(user);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }
    public FirebaseUser obtenerUser(){
        return auth.getCurrentUser();
    }
    public void subirPedido(Context contexto, Map<String, Object> pedido) {
        Task<List<Pedido>> pedidos = obtenerPedidos(user.getEmail());
        pedidos.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<Pedido> pedidos1 = task.getResult();
                if (pedidos1.isEmpty()){
                    db.collection("pedidos").add(pedido).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            Toast.makeText(contexto, "Se ha añadido el pedido correctamente.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(contexto, "No se ha podido añadir el pedido. Intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    boolean repetido = false;
                    long cantidad = 0;
                    for (Pedido p : pedidos1){
                        if (!p.isPagado() && p.getPrenda().equals(pedido.get("prenda")) && comprobarTallas(p.getTalla(), (String) pedido.get("talla"))){
                            repetido = true;
                            cantidad = p.getCantidad();
                            borrarPedido(contexto, p);
                        }
                    }
                    if (repetido){
                        cantidad = cantidad + (long )pedido.get("cantidad");
                        pedido.put("cantidad", cantidad);
                    }
                    db.collection("pedidos").add(pedido).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            Toast.makeText(contexto, "Se ha añadido el pedido correctamente.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(contexto, "No se ha podido añadir el pedido. Intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }else{
                Toast.makeText(contexto, "Error al obtener los pedidos del usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean comprobarTallas(String p, String pedido){
        if (p == null && pedido == null){return true;}
        else if (p == null && pedido != null){return false;}
        else if (p != null && pedido == null){return false;}
        else if (p.equals(pedido)){return true;}
        else {return false;}
    }
    public void cargarImagen(Context contexto, ImageView holder, ImageView img, String url){
        StorageReference gsReference;
        if (url == null || url.isEmpty()){
            gsReference = storage.getReferenceFromUrl("gs://balonmano-f213a.appspot.com/imagenes-default/" + randomFoto());
        }else{
            gsReference = storage.getReferenceFromUrl(url);
        }
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.setImageBitmap(bitmap);
            if (img != null){img.setImageBitmap(bitmap);}
        }).addOnFailureListener(exception -> Toast.makeText(contexto, "Error al descargar la imagen de la prenda", Toast.LENGTH_SHORT).show());
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
                        List<String> lista = (List<String>) ds.get("imagen");
                        url = lista.get(0);
                    }
                }
                taskCompletionSource.setResult(url);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }
    public void borrarPedido(Context contexto, Pedido prenda){
        db.collection("pedidos").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot snapsot = task.getResult();
                List<DocumentSnapshot> documents = snapsot.getDocuments();
                for (DocumentSnapshot ds : documents){
                    if (ds.getString("comprador").equals(obtenerUser().getEmail()) && ds.getBoolean("pagado").equals(false)) {
                        Pedido pedido = new Pedido(ds.getString("prenda"), ds.getString("talla"), ds.getLong("cantidad"), ds.getLong("precioUnidad"));

                        if (pedido.getPrenda().equals(prenda.getPrenda()) && pedido.getCantidad().equals(prenda.getCantidad()) && comprobarTallas(pedido.getTalla(), prenda.getTalla()) && (pedido.getPrecioUnidad() == prenda.getPrecioUnidad())) {

                            db.collection("pedidos").document(ds.getId()).delete().addOnCompleteListener(task1 -> {
                                if (!task1.isSuccessful()) {
                                    Toast.makeText(contexto, "Error al borrar el pedido", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
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
    public DocumentReference getDocumento(String anios, String division, String id){
        return db.collection("temporadas").document(anios).collection(division).document(id);
    }
    public void signIn(String correo, String psswrd, Login login){
        auth.signInWithEmailAndPassword(correo, psswrd).addOnCompleteListener(task -> {
            if (!auth.getCurrentUser().isEmailVerified()) {
                sendVerfificacion(correo);
                signOut();
                AlertDialog.Builder builder = new AlertDialog.Builder(login);

                LayoutInflater inflater = LayoutInflater.from(login);
                View dialogView = inflater.inflate(R.layout.dialog_sino, null);
                builder.setView(dialogView);

                TextView titulo = dialogView.findViewById(R.id.textViewTitulo);
                TextView msg = dialogView.findViewById(R.id.textViewMsg);
                Button continuar = dialogView.findViewById(R.id.buttonSi);
                Button cancelar = dialogView.findViewById(R.id.buttonNo);

                AlertDialog dialog = builder.create();
                dialog.show();

                titulo.setText("Verifica tu correo");
                msg.setText("Se ha enviado un correo de verificación al email");
                continuar.setText("Verificado");
                cancelar.setText("Salir");

                continuar.setOnClickListener(v1 -> {
                    auth.signInWithEmailAndPassword(correo, psswrd).addOnCompleteListener(task1 -> {
                        if (!auth.getCurrentUser().isEmailVerified()) {
                            sendVerfificacion(correo);
                            signOut();
                            Toast.makeText(login, "No has verificado el correo", Toast.LENGTH_SHORT).show();
                        }else{
                            login.iniciarMainActivity(task1);
                        }
                    });
                });
                cancelar.setOnClickListener(v1 -> {
                    login.iniciarMainActivity(task);
                });
            }else {
                login.iniciarMainActivity(task);
            }
        });
    }
    public void signOut(){auth.signOut();}
    public Task<Boolean> borrarCuenta(String correo, Context context){
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                db.collection("usuarios")
                        .whereEqualTo("correo", correo)
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task1.getResult()) {
                                    db.collection("usuarios").document(document.getId()).delete()
                                            .addOnSuccessListener(aVoid -> {
                                                taskCompletionSource.setResult(true);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(context, "Error al eliminar el documento", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                taskCompletionSource.setResult(false);
                                Toast.makeText(context, "Error al borrar documentos", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else{
                taskCompletionSource.setResult(false);
                Toast.makeText(context, "Error al borrar la cuenta", Toast.LENGTH_SHORT).show();
            }
        });
        return taskCompletionSource.getTask();
    }
    public void rellenarSpinnerTemporadas(Context context, Spinner temporadas, Spinner division, Spinner jornada){
        db.collection("temporadas").get().addOnCompleteListener(task -> {
            List<String> listaAnios = new ArrayList<>();
            listaAnios.add("");
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    listaAnios.add(document.getId());
                }
            }else{
                Toast.makeText(context, "El año " + temporadas.getSelectedItem().toString() + " no existe.", Toast.LENGTH_SHORT).show();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listaAnios);
            temporadas.setAdapter(adapter);
            division.setEnabled(false);
            jornada.setEnabled(false);
        });
    }
    public void rellenarJornadas(Context context, Spinner temporadas, Spinner division, Spinner jornada){
        db.collection("temporadas").document(temporadas.getSelectedItem().toString()).collection(division.getSelectedItem().toString()).get().addOnCompleteListener(task -> {
            List<Integer> listaJornadas = new ArrayList<>();
            if (task.isSuccessful()){
                int numDocs = 1;
                for (QueryDocumentSnapshot document : task.getResult()){
                    listaJornadas.add(numDocs++);
                }
                jornada.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listaJornadas));
                jornada.setEnabled(true);
            }else{
                Toast.makeText(context, "El año " + temporadas.getSelectedItem().toString() + " no existe.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public Task<Map<String, Object>> actualizarDatos(Spinner temporadas, Spinner division, Spinner jornada){
        TaskCompletionSource<Map<String, Object>> taskCompletionSource = new TaskCompletionSource<>();
        db.collection("temporadas").document(temporadas.getSelectedItem().toString()).collection(division.getSelectedItem().toString()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> mapa = new HashMap<>();
                    if (Integer.parseInt(document.get("jornada").toString()) == (Integer.parseInt(jornada.getSelectedItem().toString()))) {
                        mapa.put("id",document.getId());
                        mapa.put("local", document.get("local").toString());
                        mapa.put("visitante", document.get("visitante").toString());
                        mapa.put("golesLocal", document.get("golesLocal").toString());
                        mapa.put("golesVisitante", document.get("golesVisitante").toString());
                        mapa.put("fecha", document.get("fecha").toString());
                        mapa.put("hora", document.get("hora").toString());
                        mapa.put("pabellon", document.get("pabellón").toString());
                        taskCompletionSource.setResult(mapa);
                    }
                }
            } else {
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }
    public void modificarUser(Usuario usuario, PerfilUsuario perfilUsuario) {
        String correo = obtenerUser().getEmail();
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", usuario.getNombre());
        user.put("apellido1", usuario.getApellido1());
        user.put("apellido2", usuario.getApellido2());
        user.put("tlf", usuario.getTlf());
        user.put("correo", usuario.getCorreo());
        user.put("imagen", usuario.getImagen());
        user.put("rol", usuario.getRol());
        user.put("direccion", usuario.getDireccion());
        db.collection("usuarios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot documents : task.getResult()){
                    if (documents.getString("correo").equals(correo)){
                        db.collection("usuarios").document(documents.getId()).update(user).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                Toast.makeText(perfilUsuario,"Modificado correctamente", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(perfilUsuario,"Algo ha ido mal.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
            }
        });
    }
    public void updatePedido(Map pedido, Context context){
        db.collection("pedidos").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot documents : task.getResult()) {
                    if (documents.getString("comprador").equals(pedido.get("comprador")) && documents.getBoolean("pagado").equals(false) && documents.getString("prenda").equals(pedido.get("prenda")) && documents.getString("talla").equals(pedido.get("talla"))){
                        db.collection("pedidos").document(documents.getId()).update(pedido).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(context, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Algo ha ido mal.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
            }else{
                Toast.makeText(context, "Task mala", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sendVerfificacion(String correo){auth.getCurrentUser().sendEmailVerification();}
    public Task<List<String>> getTemporadas(){
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();

        db.collection("temporadas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<String> lista = new ArrayList<>();
                QuerySnapshot snapshots = task.getResult();
                for (DocumentSnapshot ds : snapshots){
                    lista.add(ds.getId());
                }
                taskCompletionSource.setResult(lista);
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }
    public Task<Boolean> cambiarCorreo(String correo){
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        String cNow = user.getEmail();
        user.updateEmail(correo).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                db.collection("usuarios").get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        QuerySnapshot snapshots = task1.getResult();
                        for (DocumentSnapshot ds : snapshots){
                            if (ds.getString("correo").equals(cNow)){
                                Map<String, Object> user1 = new HashMap<>();
                                user1.put("nombre", ds.getString("nombre"));
                                user1.put("apellido1", ds.getString("apellido1"));
                                user1.put("apellido2", ds.getString("apellido2"));
                                user1.put("tlf", ds.getString("tlf"));
                                user1.put("correo", correo);
                                user1.put("imagen", ds.getString("imagen"));
                                user1.put("rol", ds.getString("rol"));
                                user1.put("direccion", ds.getString("direccion"));
                                db.collection("usuarios").document(ds.getId()).update(user1).addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()){
                                        taskCompletionSource.setResult(true);
                                    }else{
                                        taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
                                    }
                                });
                                break;
                            }
                        }
                    }else{
                        taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
                    }
                });
            }else{
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }
    public Task<Boolean> cambiarPsswrd(String psswrd){
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        user.updatePassword(psswrd).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                taskCompletionSource.setResult(true);
            }else {
                taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
            }
        });
        return taskCompletionSource.getTask();
    }
}
