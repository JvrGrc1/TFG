package com.example.tfg.adaptador;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Prenda;

import java.util.List;

public class TiendaAdapter extends RecyclerView.Adapter<TiendaAdapter.TiendaViewHolder> {

    private final Context contexto;
    private List<Prenda> prendas;
    private final ConexionFirebase conexion = new ConexionFirebase();

    public TiendaAdapter(Context contexto, List<Prenda> prendasList) {
        this.contexto = contexto;
        this.prendas = prendasList;
    }

    public void setDatos(List<Prenda> datos){this.prendas = datos;}
    public List<Prenda> getDatos(){return prendas;}

    @NonNull
    @Override
    public TiendaAdapter.TiendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contexto).inflate(R.layout.tienda_view, parent, false);
        return new TiendaViewHolder(view, contexto);
    }

    @Override
    public void onBindViewHolder(@NonNull TiendaAdapter.TiendaViewHolder holder, int position) {
        Prenda prenda = prendas.get(position);

        holder.nombre.setText(prenda.getNombre());
        holder.precio.setText(String.format("%.2f€", prenda.getPrecio()));
        conexion.cargarImagen(contexto,holder.imagen, null, prenda.getImagen().get(0));

    }

    @Override
    public int getItemCount() {
        return prendas.size();
    }

    public static class TiendaViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, precio;
        ImageView imagen;
        ConstraintLayout constraintLayout;

        public TiendaViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombrePrenda);
            precio = itemView.findViewById(R.id.precioPrenda);
            imagen = itemView.findViewById(R.id.imagenPrenda);
            constraintLayout = itemView.findViewById(R.id.constrainTiendaView);

            boolean modoOscuro = context.getSharedPreferences("Ajustes", Context.MODE_PRIVATE)
                    .getBoolean("modoOscuro", false);
            if (modoOscuro) {
                constraintLayout.setBackgroundColor(Color.rgb(24,23, 28));
                nombre.setTextColor(Color.WHITE);
                precio.setTextColor(Color.WHITE);
            } else {
                constraintLayout.setBackgroundColor(Color.WHITE);
                nombre.setTextColor(Color.BLACK);
                precio.setTextColor(Color.BLACK);
            }
        }
    }
}
