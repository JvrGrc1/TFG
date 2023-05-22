package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MetodoDePago extends AppCompatActivity {

    private ImageButton volver;
    private TextView nombreTextView, total;
    private RadioButton direccionRadio;
    private RadioGroup grupo;
    private Usuario usuario;
    private Button contrinuar, cancelar, pagar;
    private TextInputEditText direccion, portal, piso, ciudad, provincia;
    private LinearLayout nuevaDireccion, crearDireccion;
    private float totalCompra = 0;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_de_pago);
        volver = findViewById(R.id.buttonVolver);
        nombreTextView = findViewById(R.id.nombreApellidosPago);
        direccionRadio = findViewById(R.id.direccionPagoButton);
        nuevaDireccion = findViewById(R.id.layoutNuevaDireccion);
        crearDireccion = findViewById(R.id.crearDireccionPago);
        grupo = findViewById(R.id.radioGroupPago);
        total = findViewById(R.id.totalPago);

        direccion = findViewById(R.id.direccionPago);
        portal = findViewById(R.id.portalPago);
        piso = findViewById(R.id.pisoPago);
        ciudad = findViewById(R.id.ciudadPago);
        provincia = findViewById(R.id.provinciaPago);

        contrinuar = findViewById(R.id.buttonContinuarPago);
        cancelar = findViewById(R.id.buttonCancelarPago);
        pagar = findViewById(R.id.buttonPago);

        Intent intent = getIntent();
        List<Pedido> pedidos = (List<Pedido>) intent.getSerializableExtra("pedido");
        usuario = (Usuario) intent.getSerializableExtra("user");
        volver.setOnClickListener(v -> finish());
        if (usuario.getApellido2().isEmpty()){
            nombreTextView.setText(String.format("%s %s", usuario.getNombre(), usuario.getApellido1()));
        }else{
            nombreTextView.setText(String.format("%s %s %s", usuario.getNombre(), usuario.getApellido1(), usuario.getApellido2()));
        }
        if (usuario.getDireccion().isEmpty()){
            direccionRadio.setVisibility(View.GONE);
            nuevaDireccion.setTranslationY(-10);
        }else{
            direccionRadio.setText(usuario.getDireccion());
        }
        for (Pedido p : pedidos){
            totalCompra += p.getCantidad() * p.getPrecioUnidad();
        }
        total.setText(totalCompra + "€");

        nuevaDireccion.setOnClickListener(v -> {
            crearDireccion.setVisibility(View.VISIBLE);
            nuevaDireccion.setVisibility(View.INVISIBLE);
        });

        cancelar.setOnClickListener(v -> {
            nuevaDireccion.setVisibility(View.VISIBLE);
            crearDireccion.setVisibility(View.GONE);
        });

        contrinuar.setOnClickListener(v -> {
            RadioButton radioButton = new RadioButton(this);
            if (comprobarDireccion()){
                radioButton.setText(obtenerDireccion());
                Typeface typeface = ResourcesCompat.getFont(this, R.font.industrial_light);
                radioButton.setTypeface(typeface);
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                grupo.addView(radioButton);
                crearDireccion.setVisibility(View.GONE);
            }
        });

        grupo.setOnCheckedChangeListener((group, checkedId) -> {
            if (grupo.getCheckedRadioButtonId() == -1){
                pagar.setVisibility(View.INVISIBLE);
            }else {
                pagar.setVisibility(View.VISIBLE);
            }
        });
    }
    private boolean comprobarDireccion(){
        String direccion1 = direccion.getText().toString().trim();
        String piso1 = piso.getText().toString().trim();
        String ciudad1 = ciudad.getText().toString().trim();
        String provincia1 = provincia.getText().toString().trim();
        String portal1 = portal.getText().toString().trim();

        if (direccion1.isEmpty() || piso1.isEmpty() || portal1.isEmpty() || ciudad1.isEmpty() || provincia1.isEmpty()){
            if (direccion1.isEmpty() && piso1.isEmpty() && portal1.isEmpty() && ciudad1.isEmpty() && provincia1.isEmpty()) {
                return true;
            }else{
                Toast.makeText(this, "Dirección, Piso, Portal, Ciudad y Provincia tienen que estar: rellenos o vacíos.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            int t = 0;
            if (direccion1.startsWith(" ") || direccion1.endsWith(" ")) {
                direccion.setError("No puede comenzar ni acabar con espacios.");
                t++;
            } else if (!direccion1.matches("^[^0-9]*$")) {
                direccion.setError("No puede contener números.");
                t++;
            }
            if (piso1.matches("^[0-9]+°[a-z]$")){
                piso.setText(piso1.toUpperCase());
            }else if (!piso1.matches("^[0-9]+°[A-Z]$")){
                piso.setError("Formato incorrecto.");
                t++;
            }
            if (ciudad1.startsWith(" ") || ciudad1.endsWith(" ")) {
                ciudad.setError("No puede comenzar ni acabar con espacios.");
                t++;
            } else if (!ciudad1.matches("^[^0-9]*$")){
                ciudad.setError("No puede contener números.");
                t++;
            }
            if (provincia1.startsWith(" ") || provincia1.endsWith(" ")) {
                provincia.setError("No puede comenzar ni acabar con espacios.");
                t++;
            } else if (!provincia1.matches("^[^0-9]*$")){
                provincia.setError("No puede contener números.");
                t++;
            }
            return t == 0;
        }
    }

    private String obtenerDireccion(){
        return String.format("%s,%s,%s,%s,%s", direccion.getText().toString(), portal.getText().toString(), piso.getText().toString(), provincia.getText().toString(), ciudad.getText().toString());
    }
}