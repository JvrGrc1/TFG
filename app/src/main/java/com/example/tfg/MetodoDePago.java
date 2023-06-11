package com.example.tfg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetodoDePago extends AppCompatActivity {
    private TextView nombreTextView, total, tarjeta, dir, titulo, pago, aniadir;
    private RadioButton direccionRadio;
    private RadioGroup grupo;
    private Usuario usuario;
    private Button continuar, cancelar, pagar;
    private TextInputEditText direccion, portal, piso, ciudad, provincia, numTarjeta, titular, cvv, fecha;
    private TextInputLayout layDir, layPor, layPiso, layCiu, layPro, numero, layTitular, layCvv, layFecha;
    private ConexionFirebase conexion = new ConexionFirebase();
    private LinearLayout nuevaDireccion, crearDireccion;
    private ConstraintLayout constrainMetodoPago;
    private Spinner spinner;
    private float totalCompra = 0;
    private boolean modoOscuro;
    private Window window;
    private String[] categorias = {"Elige tu tarjeta", "American Express", "Discover", "MasterCard", "VISA"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_de_pago);
        titulo = findViewById(R.id.textViewTituloPago);
        constrainMetodoPago = findViewById(R.id.constrainMetodoPago);
        nombreTextView = findViewById(R.id.nombreApellidosPago);
        direccionRadio = findViewById(R.id.direccionPagoButton);
        nuevaDireccion = findViewById(R.id.layoutNuevaDireccion);
        crearDireccion = findViewById(R.id.crearDireccionPago);
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
        titular = findViewById(R.id.titularTarjeta);
        layTitular = findViewById(R.id.textInputLayoutTitularTarjeta);
        cvv = findViewById(R.id.cvvTarjeta);
        layCvv = findViewById(R.id.textInputLayoutCvvTarjeta);
        layFecha = findViewById(R.id.textInputLayoutFechaTarjeta);
        fecha = findViewById(R.id.fechaTarjeta);

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
                if (modoOscuro){radioButton.setTextColor(Color.WHITE);radioButton.setButtonDrawable(R.drawable.radio_night);}
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
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItem().equals("Elige tu tarjeta")) {
                    activar(false, numTarjeta, numero);
                    activar(false, titular, layTitular);
                    activar(false, cvv, layCvv);
                    activar(false, fecha, layFecha);
                } else if (spinner.getSelectedItem().equals("American Express")) {
                    InputFilter[] filter = new InputFilter[1];              //Filtro para poner max lenght = 15
                    filter[0] = new InputFilter.LengthFilter(15);
                    numTarjeta.setFilters(filter);
                    activar(true, numTarjeta, numero);
                    activar(true, titular, layTitular);
                    activar(true, cvv, layCvv);
                    activar(true, fecha, layFecha);
                }else{
                    InputFilter[] filter = new InputFilter[1];              //Filtro para poner max lenght = 16
                    filter[0] = new InputFilter.LengthFilter(16);
                    numTarjeta.setFilters(filter);activar(true, numTarjeta, numero);
                    activar(true, titular, layTitular);
                    activar(true, cvv, layCvv);
                    activar(true, fecha, layFecha);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        pagar.setOnClickListener(v -> {
            if (grupo.getCheckedRadioButtonId() == -1){
                Toast.makeText(this, "Elige una direccion", Toast.LENGTH_SHORT).show();
            }else if (comprobarValores()){
                comprobarTarjeta(pedidos);
            }
        });
    }

    private void comprobarTarjeta(List<Pedido> pedidos){
        String numeroTarjeta = Objects.requireNonNull(numTarjeta.getText()).toString();
        int primerNumero = Integer.parseInt(String.valueOf(numeroTarjeta.charAt(0)));
        switch (spinner.getSelectedItem().toString()){
            case "American Express":
                if (primerNumero == 3 && validacionLuhn(numeroTarjeta)){
                    pagarPedido(pedidos);
                }else{
                    Toast.makeText(this, "Número de tarjeta erróneo", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Discover":
                if (primerNumero == 6 && validacionLuhn(numeroTarjeta)){
                    pagarPedido(pedidos);
                }else{
                    Toast.makeText(this, "Número de tarjeta erróneo", Toast.LENGTH_SHORT).show();
                }
                break;
            case "MasterCard":
                if (primerNumero == 5 && validacionLuhn(numeroTarjeta)){
                    pagarPedido(pedidos);
                }else{
                    Toast.makeText(this, "Número de tarjeta erróneo", Toast.LENGTH_SHORT).show();
                }
                break;
            case "VISA":
                if (primerNumero == 4 && validacionLuhn(numeroTarjeta)){
                    pagarPedido(pedidos);
                }else{
                    Toast.makeText(this, "Número de tarjeta erróneo", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "Elige una tarjeta", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void pagarPedido(List<Pedido> pedidos){
        for (Pedido pedido : pedidos) {
            Map<String, Object> map = new HashMap<>();
            map.put("comprador", conexion.obtenerUser().getEmail());
            map.put("prenda", pedido.getPrenda());
            map.put("talla", pedido.getTalla());
            map.put("cantidad", pedido.getCantidad());
            map.put("precioUnidad", pedido.getPrecioUnidad());
            map.put("pagado", true);
            conexion.updatePedido(map, this);
        }
        finish();
    }

    private boolean validacionLuhn(String numeroTarjeta){
        int suma = 0;
        boolean alternar = false;
        for (int i = numeroTarjeta.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(numeroTarjeta.charAt(i));

            if (alternar) {
                digit *= 2;
                if (digit > 9) {
                    digit = digit % 10 + 1;
                }
            }

            suma += digit;
            alternar = !alternar;
        }

        return suma % 10 == 0;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void activar(boolean enabled, TextInputEditText editText, TextInputLayout textInputLayout){
        if (enabled){
            editText.setEnabled(true);
            editText.setText("");
            textInputLayout.setBoxBackgroundColor(Color.WHITE);
            textInputLayout.setErrorEnabled(false);
        }else{
            editText.setEnabled(false);
            editText.setText("");
            textInputLayout.setBoxBackgroundColor(getColor(R.color.gris_oscurito));
            textInputLayout.setErrorEnabled(false);
        }
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
            Toast.makeText(this, "Los campos están vacios", Toast.LENGTH_SHORT).show();
                return false;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean comprobarValores(){
        boolean correcto = true;
        if (Objects.requireNonNull(numTarjeta.getText()).toString().equals("")){
            numero.setError("Campo obligatorio");
            correcto = false;
        } else{
            if (spinner.getSelectedItem().equals("American Express")){
                if (numTarjeta.getText().length() < 15) {
                    numero.setError("Número demasiado corto");
                    correcto = false;
                }else{
                    numero.setErrorEnabled(false);
                }
            }else if (!spinner.getSelectedItem().equals("American Express")){
                if (numTarjeta.getText().length() < 16) {
                    numero.setError("Número demasiado corto");
                    correcto = false;
                }else{
                    numero.setErrorEnabled(false);
                }
            }
        }
        if (Objects.requireNonNull(titular.getText()).toString().equals("")){
            layTitular.setError("Campo obligatorio");
            correcto = false;
        }else if (titular.getText().toString().startsWith(" ") || titular.getText().toString().endsWith(" ")){
            layTitular.setError("Empieza o acaba con espacios.");
            correcto = false;
        } else if (contieneNumeros(titular.getText().toString())){
            layTitular.setError("No puede contener números");
            correcto = false;
        }else{
            layTitular.setErrorEnabled(false);
        }
        if (cvv.getText().toString().length() < 3){
            layCvv.setError("3 0 4 dígitos");
            correcto = false;
        }else{
            layCvv.setErrorEnabled(false);
        }
        if (fecha.getText().toString().length() < 5){
            layFecha.setError("Campo incorrecto");
            correcto = false;
        }else if (!fechaCorrecta(fecha.getText().toString())){
            layFecha.setError("Fecha no válida");
            correcto = false;
        }else{
            layFecha.setErrorEnabled(false);
        }
        return correcto;
    }

    private boolean contieneNumeros(String titular){
        Pattern pattern = Pattern.compile(".*\\d.*");
        Matcher matcher = pattern.matcher(titular);
        return matcher.matches();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean fechaCorrecta(String fecha){
        String[] partes;
        if (fecha.contains("/")) {
            partes = fecha.split("/");
        }else if (fecha.contains("-")) {
            partes = fecha.split("-");
        }else {
            return false;
        }
        int mes = Integer.parseInt(partes[0].trim());
        int anio = Integer.parseInt(partes[1].trim());
        LocalDate now = LocalDate.now();
        if (mes < 1 || mes > 12) {
            return false;
        } else if (anio < (now.getYear() - 2000)) {
            return false;
        } else if (anio == (now.getYear() - 2000) && mes < now.getMonthValue()) {
            return false;
        } else {
            return true;
        }
    }
}