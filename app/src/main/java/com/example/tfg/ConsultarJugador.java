package com.example.tfg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.tfg.adaptador.JugadorListAdapter;
import com.example.tfg.adaptador.RecyclerItemClickListener;
import com.example.tfg.detalles.DetallesJugador;
import com.example.tfg.detalles.DetallesPrenda;
import com.example.tfg.entidad.Jugador;
import com.example.tfg.viewmodel.JugadorViewModel;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class ConsultarJugador extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private List<Jugador> jugadores = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private RecyclerView recyclerJugadores;
    private JugadorViewModel jugadorViewModel;
    SearchView buscador;
    String string;
    private TextView nada, titulo;
    private MutableLiveData<List<Jugador>> liveData = new MutableLiveData<>();
    private boolean busqueda = false;
    private final JugadorListAdapter adapter = new JugadorListAdapter(new JugadorListAdapter.JugadorDiff());

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_jugador);

        Intent intent = getIntent();
        jugadores = (List<Jugador>) intent.getSerializableExtra("jugadores");
        liveData.setValue(jugadores);
        constraintLayout = findViewById(R.id.constrainBusqueda);
        recyclerJugadores = findViewById(R.id.recyclerJugadores);
        buscador = (SearchView) findViewById(R.id.buscar);
        nada = findViewById(R.id.textViewNada);
        titulo = findViewById(R.id.textViewConsultarJugador);

        comprobarModo();

        buscador.setOnQueryTextListener(this);
        buscador.setQueryHint("Buscar por nombre");

        recyclerJugadores.setLayoutManager(new LinearLayoutManager(this));
        recyclerJugadores.setAdapter(adapter);

        jugadorViewModel = new ViewModelProvider(this).get(JugadorViewModel.class);
        jugadorViewModel.setTodosLosJugadores(liveData);
        jugadorViewModel.getTodosLosJugadores().observe(this, jugadors -> adapter.submitList(jugadors));
        recyclerJugadores.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerJugadores, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int posicion) {
                if (busqueda) {
                    Intent intent = new Intent(v.getContext(), DetallesJugador.class);
                    intent.putExtra("jugador", liveData.getValue().get(posicion));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(v.getContext(), DetallesJugador.class);
                    intent.putExtra("jugador", jugadores.get(posicion));
                    startActivity(intent);
                }
            }

            @Override
            public void onLongItemClick(View v, int posicion) {}
        }));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {return false;}

    @Override
    public boolean onQueryTextChange(String newText) {
        MutableLiveData<List<Jugador>> nuevo = new MutableLiveData<>();
        nuevo.setValue(jugadorViewModel.devuelveBusqueda(newText));
        this.string = newText;
        this.liveData = nuevo;
        if (liveData.getValue().isEmpty()){
            nada.setVisibility(View.VISIBLE);
        }else{
            nada.setVisibility(View.GONE);
        }
        liveData.observe(this, adapter::submitList);    //adapter::submitList es lo mismo que: jugadors -> adapter.submitList(jugadors) (linea 75)
        busqueda = true;
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void comprobarModo() {
        boolean modoOscuro = getSharedPreferences("Ajustes", Context.MODE_PRIVATE).getBoolean("modoOscuro", false);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (modoOscuro){
            window.setStatusBarColor(Color.BLACK);
            recyclerJugadores.setBackgroundColor(Color.BLACK);
            constraintLayout.setBackgroundColor(Color.BLACK);
            titulo.setTextColor(Color.WHITE);
            nada.setTextColor(Color.WHITE);
        }else{
            window.setStatusBarColor(Color.WHITE);
            nada.setTextColor(Color.BLACK);
            titulo.setTextColor(getColor(R.color.azul_oscuro));
            recyclerJugadores.setBackgroundColor(Color.WHITE);
            constraintLayout.setBackgroundColor(Color.WHITE);
        }
    }
}