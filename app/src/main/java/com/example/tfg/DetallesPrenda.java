package com.example.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Prenda;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class DetallesPrenda extends AppCompatActivity {

    private TextView nombrePrenda, precioPrenda;
    private Button aniadir;
    private RadioGroup grupo;
    private RadioButton radioButton;
    private Spinner cantidad;
    private ConexionFirebase conexion = new ConexionFirebase();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_prenda);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        nombrePrenda = findViewById(R.id.nombreDetallesPrenda);
        precioPrenda = findViewById(R.id.precioDetallesPrenda);
        aniadir = findViewById(R.id.buttonAniadirPrenda);
        grupo = findViewById(R.id.radioGroupTallas);
        cantidad = findViewById(R.id.cantidadSpinner);

        Prenda prenda = (Prenda) getIntent().getSerializableExtra("prenda");
        Pedido pedido = new Pedido();

        pedido.setPrenda(prenda.getNombre());

        precioPrenda.setText(String.format("Desde: %f€", prenda.getPrecio()));
        pedido.setPrecioUnidad(prenda.getPrecio());

        grupo.setOnCheckedChangeListener((group, id) -> {
            radioButton = grupo.findViewById(id);
            switch (radioButton.getId()){
                case R.id.buttonXL:
                    pedido.setTalla("XL");
                    break;
                case R.id.buttonL:
                    pedido.setTalla("L");
                    break;
                case R.id.buttonM:
                    pedido.setTalla("M");
                    break;
                case R.id.buttonS:
                    pedido.setTalla("S");
                    break;
                case R.id.buttonXS:
                    pedido.setTalla("XS");
                    break;
                default:
                    Toast.makeText(this, radioButton.getId() + "", Toast.LENGTH_SHORT).show();
            }
        });

        aniadir.setOnClickListener(view -> {
            if (grupo.getCheckedRadioButtonId() != -1){
                Map<String, Object> map = new HashMap<>();
                map.put("comprador", auth.getCurrentUser().getEmail());
                map.put("prenda", pedido.getPrenda());
                map.put("talla", pedido.getTalla());
                map.put("cantidad", pedido.getCantidad());
                map.put("precioUnidad", prenda.getPrecio());
                conexion.subirPedido(this, map);

            }else{
                Toast.makeText(this, "Debes elegir la talla antes de añadirlo al carrito.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}