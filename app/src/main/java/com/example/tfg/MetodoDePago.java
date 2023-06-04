package com.example.tfg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

public class MetodoDePago extends AppCompatActivity {

    private ImageButton volver;
    private TextView nombreTextView, total, tarjeta, dir, titulo, pago, aniadir;
    private RadioButton direccionRadio;
    private RadioGroup grupo;
    private Usuario usuario;
    private Button continuar, cancelar, pagar;
    private TextInputEditText direccion, portal, piso, ciudad, provincia;
    private EditText numTarjeta;
    private TextInputLayout layDir, layPor, layPiso, layCiu, layPro, numero;
    private ConexionFirebase conexion = new ConexionFirebase();
    private LinearLayout nuevaDireccion, crearDireccion;
    private ConstraintLayout constraintLayout, constrainMetodoPago, constrainPago;
    private CardView cardView;
    private Spinner spinner;
    private float totalCompra = 0;
    private boolean modoOscuro;
    private Window window;
    private String[] categorias = {"Elige tu tarjeta", "American Express", "Discover", "MasterCard", "VISA"};

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_de_pago);
        volver = findViewById(R.id.buttonVolver);
        titulo = findViewById(R.id.textViewTituloPago);
        constrainMetodoPago = findViewById(R.id.constrainMetodoPago);
        nombreTextView = findViewById(R.id.nombreApellidosPago);
        direccionRadio = findViewById(R.id.direccionPagoButton);
        nuevaDireccion = findViewById(R.id.layoutNuevaDireccion);
        crearDireccion = findViewById(R.id.crearDireccionPago);
        cardView = findViewById(R.id.cardView);
        constraintLayout = findViewById(R.id.constrainCard);
        constrainPago = findViewById(R.id.constrainPago);
        grupo = findViewById(R.id.radioGroupPago);
        total = findViewById(R.id.textViewTotal);
        pago = findViewById(R.id.totalPago);
        tarjeta = findViewById(R.id.textViewTarjeta);
        dir = findViewById(R.id.textViewDireccionPago);
        direccion = findViewById(R.id.direccionPago);
        portal = findViewById(R.id.portalPago);
        piso = findViewById(R.id.pisoPago);
        ciudad = findViewById(R.id.ciudadPago);
        provincia = findViewById(R.id.provinciaPago);
        layCiu = findViewById(R.id.textInputLayoutCiudadPago);
        layDir = findViewById(R.id.textInputLayoutDireccionPago);
        layPiso = findViewById(R.id.textInputLayoutPisoPago);
        layPor = findViewById(R.id.textInputLayoutPortalPago);
        layPro = findViewById(R.id.textInputLayoutProvinciaPago);
        continuar = findViewById(R.id.buttonContinuarPago);
        cancelar = findViewById(R.id.buttonCancelarPago);
        pagar = findViewById(R.id.buttonPago);
        aniadir = findViewById(R.id.textViewAniadirDireccion);
        spinner = findViewById(R.id.spinner);
        numero = findViewById(R.id.textInputLayoutNumeroTarjeta);
        numTarjeta = findViewById(R.id.numeroTarjeta);

        spinner.setAdapter(new ArrayAdapter<>(MetodoDePago.this, android.R.layout.simple_spinner_dropdown_item, categorias));

        modoOscuro = getSharedPreferences("Ajustes", Context.MODE_PRIVATE).getBoolean("modoOscuro", false);
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);getSharedPreferences("Ajustes", Context.MODE_PRIVATE);

        comprobarModo(modoOscuro);

        Intent intent = getIntent();
        List<Pedido> pedidos = (List<Pedido>) intent.getSerializableExtra("pedido");
        Task<Usuario> task = conexion.datosUsuario(conexion.obtenerUser().getEmail());
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()){
                usuario = task1.getResult();
                datosUsuario();
            }
        });
        volver.setOnClickListener(v -> finish());
        ponerPrecio(pedidos);

        nuevaDireccion.setOnClickListener(v -> {
            crearDireccion.setVisibility(View.VISIBLE);
            nuevaDireccion.setVisibility(View.GONE);
        });

        cancelar.setOnClickListener(v -> {
            nuevaDireccion.setVisibility(View.VISIBLE);
            crearDireccion.setVisibility(View.GONE);
        });

        continuar.setOnClickListener(v -> {
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItem().equals("Elige tu tarjeta")) {
                    numTarjeta.setEnabled(false);
                    numTarjeta.setText("");
                    numero.setBoxBackgroundColor(getColor(R.color.gris_oscurito));
                } else if (spinner.getSelectedItem().equals("American Express")) {
                    numTarjeta.setEnabled(true);
                    numTarjeta.setText("");
                    numero.setBoxBackgroundColor(Color.WHITE);
                }else{
                    numTarjeta.setEnabled(true);
                    numTarjeta.setText("");
                    numero.setBoxBackgroundColor(Color.WHITE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void ponerPrecio(List<Pedido> pedidos) {
        for (Pedido p : pedidos){
            totalCompra += p.getCantidad() * p.getPrecioUnidad();
        }
        pago.setText(String.format("%s€", totalCompra));
    }


    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    private void comprobarModo(boolean modoOscuro){
        if (modoOscuro){
            window.setStatusBarColor(Color.BLACK);
            constrainMetodoPago.setBackgroundColor(Color.BLACK);
            volver.setBackgroundColor(Color.BLACK);
            volver.setImageDrawable(getDrawable(R.drawable.flecha_atras_night));
            nombreTextView.setTextColor(Color.WHITE);
            direccionRadio.setTextColor(Color.WHITE);
            direccionRadio.setButtonDrawable(R.drawable.radio_night);
            titulo.setTextColor(Color.WHITE);
            dir.setTextColor(Color.WHITE);
            tarjeta.setTextColor(Color.WHITE);
            total.setTextColor(Color.WHITE);
            pago.setTextColor(Color.WHITE);
            aniadir.setTextColor(Color.rgb(192, 192, 221));
            cambiarInputLayout(direccion, layDir);
            cambiarInputLayout(ciudad, layCiu);
            cambiarInputLayout(piso, layPiso);
            layPiso.setHelperTextColor(ColorStateList.valueOf(Color.WHITE));
            cambiarInputLayout(portal, layPor);
            cambiarInputLayout(provincia, layPro);
        }
    }

    private void cambiarInputLayout(TextInputEditText inputEditText, TextInputLayout textInputLayout){
        textInputLayout.setBoxBackgroundColor(Color.rgb(26, 26, 26));
        inputEditText.setTextColor(Color.WHITE);
        textInputLayout.setBoxStrokeColor(Color.WHITE);
        textInputLayout.setDefaultHintTextColor(ColorStateList.valueOf(Color.WHITE));
    }

    private void datosUsuario() {
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
    }

    private boolean comprobarDireccion(){
        String direccion1 = Objects.requireNonNull(direccion.getText()).toString().trim();
        String piso1 = Objects.requireNonNull(piso.getText()).toString().trim();
        String ciudad1 = Objects.requireNonNull(ciudad.getText()).toString().trim();
        String provincia1 = Objects.requireNonNull(provincia.getText()).toString().trim();
        String portal1 = Objects.requireNonNull(portal.getText()).toString().trim();

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
        return String.format("%s,%s,%s,%s,%s",
                Objects.requireNonNull(direccion.getText()), Objects.requireNonNull(portal.getText()),
                Objects.requireNonNull(piso.getText()), Objects.requireNonNull(provincia.getText()),
                Objects.requireNonNull(ciudad.getText()));
    }
}