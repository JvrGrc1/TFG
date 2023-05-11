package com.example.tfg.detalles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.R;
import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Pedido;
import com.example.tfg.entidad.Prenda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetallesPrenda extends AppCompatActivity {

    private ConstraintLayout constrain, constrain2;
    private TextView nombrePrenda, precioPrenda, tallas;
    private Button aniadir;
    private RadioGroup grupo;
    private RadioButton radioButton;
    private Spinner cantidad;
    private ImageView imagen;
    private final ConexionFirebase conexion = new ConexionFirebase();

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_prenda);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        constrain = findViewById(R.id.constrainDetallesJornadas);
        constrain2 = findViewById(R.id.constrain2);
        nombrePrenda = findViewById(R.id.nombreDetallesPrenda);
        precioPrenda = findViewById(R.id.precioDetallesPrenda);
        aniadir = findViewById(R.id.buttonAniadirPrenda);
        grupo = findViewById(R.id.radioGroupTallas);
        cantidad = findViewById(R.id.cantidadSpinner);
        imagen = findViewById(R.id.imagenDetallesPrenda);
        tallas = findViewById(R.id.textViewTalla);

        Prenda prenda = (Prenda) getIntent().getSerializableExtra("prenda");
        Pedido pedido = new Pedido();

        if (prenda.getTallas().isEmpty()){
            grupo.setVisibility(View.INVISIBLE);
            tallas.setVisibility(View.INVISIBLE);
            pedido.setTalla(null);
        }else{
            pedido.setTalla("-1");
        }

        pedido.setPrenda(prenda.getNombre());
        conexion.cargarImagen(this, imagen, null, prenda.getImagen());

        nombrePrenda.setText(prenda.getNombre());
        precioPrenda.setText(String.format("Desde: %.2f€", prenda.getPrecio()));
        pedido.setPrecioUnidad(prenda.getPrecio());

        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.escala);
        constrain.startAnimation(animacion);

        constrain.setOnClickListener(v -> finish());
        constrain2.setOnClickListener(v -> {});


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
                    pedido.setTalla("-1");
                    Toast.makeText(this, radioButton.getId() + "", Toast.LENGTH_SHORT).show();
            }
        });

        cantidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pedido.setCantidad(parent.getItemIdAtPosition(position + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aniadir.setOnClickListener(view -> {
            if (pedido.getTalla() == null || !pedido.getTalla().equals("-1")){
                Map<String, Object> map = new HashMap<>();
                map.put("comprador", conexion.obtenerUser());
                map.put("prenda", pedido.getPrenda());
                map.put("talla", pedido.getTalla());
                map.put("cantidad", pedido.getCantidad());
                map.put("precioUnidad", prenda.getPrecio());
                map.put("pagado", false);
                conexion.subirPedido(this, map);

            }else{
                Toast.makeText(this, "Debes elegir la talla antes de añadirlo al carrito.", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<Integer> lista = new ArrayList<>();
        for (int x = 1; x <= 9; x++){
            lista.add(x);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, lista);
        cantidad.setAdapter(adapter);
    }
}