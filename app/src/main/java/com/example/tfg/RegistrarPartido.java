package com.example.tfg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Partido;
import com.example.tfg.entidad.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrarPartido extends AppCompatActivity {

    private Spinner anios, division, jornada;
    private EditText fecha, hora, local, visitante, gL, gV, pabellon;
    private FloatingActionButton agregar, editar;
    private final ConexionFirebase conexion = new ConexionFirebase();
    private String id;
    private TextView titulo;
    private ConstraintLayout constraintLayout;
    private LinearLayout linearSpinners, linearEquipos, linearGoles, linearFecha;

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        titulo = findViewById(R.id.textViewTitulo);
        constraintLayout = findViewById(R.id.constrainLayoutRegistroPartidos);
        linearEquipos = findViewById(R.id.linearLayoutEquipos);
        linearSpinners = findViewById(R.id.linearLayoutSpinners);
        linearGoles = findViewById(R.id.linearLayoutGoles);
        linearFecha = findViewById(R.id.linearLayoutFecha);
        comprobarModo();

        Task<Usuario> user = conexion.datosUsuario(conexion.obtenerUser().getEmail());
        user.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Usuario usuario = task.getResult();
                if (usuario.getRol().equals("Administrador")){editar.setVisibility(View.VISIBLE);}
                else {editar.setVisibility(View.INVISIBLE);}
            }
        });
        local.setEnabled(false);
        visitante.setEnabled(false);
        hora.setEnabled(false);
        pabellon.setEnabled(false);
        fecha.setEnabled(false);

        conexion.rellenarSpinnerTemporadas(RegistrarPartido.this, anios, division, jornada);
        rellenarDivisiones();
        rellenarJornadas();
        jornada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarDatos2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        agregar.setOnClickListener(v -> {
            if (jornada.getSelectedItem() == null || anios.getSelectedItem().equals("") || division.getSelectedItem().equals("")){
                Toast.makeText(RegistrarPartido.this, "Error en los Spinners", Toast.LENGTH_SHORT).show();
            }else{
                if (todoRelleno()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    LayoutInflater inflater = LayoutInflater.from(this);
                    View dialogView = inflater.inflate(R.layout.dialog_sino, null);
                    builder.setView(dialogView);

                    TextView titulo = dialogView.findViewById(R.id.textViewTitulo);
                    TextView msg = dialogView.findViewById(R.id.textViewMsg);
                    Button continuar = dialogView.findViewById(R.id.buttonSi);
                    Button cancelar = dialogView.findViewById(R.id.buttonNo);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    titulo.setText("Confirmar cambios");
                    msg.setText("Has realizado cambios en el partido ¿Deseas continuar?");
                    continuar.setText("Confirmar");
                    cancelar.setText("Salir");

                    continuar.setOnClickListener(v1 -> {
                        DocumentReference docRef = conexion.getDocumento(anios.getSelectedItem().toString(), division.getSelectedItem().toString(), id);
                        // Crear un mapa con los campos que se van a actualizar y sus valores
                        Map<String, Object> partido = crearMapaPartido();
                        // Actualizar los campos del documento en Firebase Firestore
                        actualizarPartido(docRef, partido);
                    });
                    cancelar.setOnClickListener(v1 -> dialog.dismiss());
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

    private void actualizarPartido(DocumentReference docRef, Map<String, Object> partido) {
        docRef.update(partido).addOnSuccessListener(aVoid -> {
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

    @NonNull
    private Map<String, Object> crearMapaPartido() {
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
        return datos;
    }
    private void rellenarDivisiones() {
        anios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!anios.getSelectedItem().equals("")) {
                    Task<List<String>> equipos = conexion.equiposFromTemporada(anios.getSelectedItem().toString());
                    equipos.addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            List<String> equipos1 = task1.getResult();
                            equipos1.add(0, "");
                            division.setAdapter(new ArrayAdapter<>(RegistrarPartido.this, android.R.layout.simple_spinner_item, equipos1));
                            division.setEnabled(true);
                        }else{
                            Toast.makeText(RegistrarPartido.this, "Error obteniendo los pedidos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    division.setSelection(0);
                    jornada.setSelection(0);
                    division.setEnabled(false);
                    jornada.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void rellenarJornadas() {
        division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!division.getSelectedItem().equals("") && !division.getSelectedItem().equals("TODOS")){
                    conexion.rellenarJornadas(RegistrarPartido.this, anios, division, jornada);
                }else{
                    jornada.setSelection(0);
                    jornada.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void actualizarDatos2(){
        if (jornada.isEnabled()){
            Task<Map<String, Object>> mapa = conexion.actualizarDatos(anios, division, jornada);
            mapa.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Map<String, Object> datos = task.getResult();
                    id = (String) datos.get("id");
                    local.setText((String) datos.get("local"));
                    visitante.setText((String) datos.get("visitante"));
                    gL.setText((String) datos.get("golesLocal"));
                    gV.setText((String) datos.get("golesVisitante"));
                    fecha.setText((String) datos.get("fecha"));
                    hora.setText((String) datos.get("hora"));
                    pabellon.setText((String) datos.get("pabellon"));
                }else{
                    Toast.makeText(RegistrarPartido.this, "Error al actualizar datos.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private boolean todoRelleno(){
        if (!local.getText().toString().isEmpty() && !visitante.getText().toString().isEmpty() && !gV.getText().toString().isEmpty() && !gL.getText().toString().isEmpty() && !hora.getText().toString().isEmpty() && !fecha.getText().toString().isEmpty() && !pabellon.getText().toString().isEmpty() && getFecha() != null && getFecha() != null){
            if (getHora() != null && getDivision() != null && getFecha() != null){
                return true;
            }else{
                return false;
            }
        }else {
            return false;
        }
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
        String patron = "^([01]\\d|2[0-3]):[0-5]\\d$";
        if (hora.getText().toString().matches(patron)){
            return hora.getText().toString();
        }else{
            Toast.makeText(this, "La hora está mal escrita", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    private String getFecha(){
        String patron = "^(0[1-9]|1\\d|2[0-9]|3[01])-(0[1-9]|1[0-2])-20(20|21|22|23)$";
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
            pabellon.setEnabled(true);
            fecha.setEnabled(true);
        }else{
            local.setEnabled(false);
            visitante.setEnabled(false);
            hora.setEnabled(false);
            pabellon.setEnabled(false);
            fecha.setEnabled(false);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void comprobarModo() {
        boolean modoOscuro = getSharedPreferences("Ajustes", Context.MODE_PRIVATE).getBoolean("modoOscuro", false);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (modoOscuro){
            window.setStatusBarColor(Color.BLACK);
            titulo.setTextColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.BLACK);
            linearFecha.setBackgroundColor(Color.BLACK);
            linearGoles.setBackgroundColor(Color.BLACK);
            linearSpinners.setBackgroundColor(Color.BLACK);
            linearEquipos.setBackgroundColor(Color.BLACK);
        }else{
            window.setStatusBarColor(Color.WHITE);
            titulo.setTextColor(getColor(R.color.azul_oscuro));
            constraintLayout.setBackgroundColor(Color.WHITE);
            linearFecha.setBackgroundColor(Color.WHITE);
            linearGoles.setBackgroundColor(Color.WHITE);
            linearSpinners.setBackgroundColor(Color.WHITE);
            linearEquipos.setBackgroundColor(Color.WHITE);
        }
    }
}
