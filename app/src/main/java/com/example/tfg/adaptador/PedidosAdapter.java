package com.example.tfg.adaptador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Pedido;

import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.PedidosViewHolder> {

    private final Context context;
    private List<Pedido> pedidos;
    private final ConexionFirebase conexion = new ConexionFirebase();

    public PedidosAdapter(Context context, List<Pedido> pedidos) {
        this.context = context;
        this.pedidos = pedidos;
    }

    public void setDatos(List<Pedido> pedidos){this.pedidos = pedidos;}
    public List<Pedido> getDatos(){return pedidos;}

    public void borrarItem(int posicion){
        pedidos.remove(posicion);
        notifyItemRemoved(posicion);
    }

    @NonNull
    @Override
    public PedidosAdapter.PedidosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pedido_view, parent, false);
        return new PedidosViewHolder(view, this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull PedidosAdapter.PedidosViewHolder holder, int position) {

        Pedido pedido = pedidos.get(position);

        conexion.imagenPedido(context,holder.imagen, pedido.getPrenda());
        holder.nombre.setText(pedido.getPrenda());
        holder.cantidad.setText(String.format("%d", pedido.getCantidad()));
        holder.precio.setText(String.format("%.2f€", pedido.getPrecioUnidad()*pedido.getCantidad()));
        if (pedido.getTalla() != null){
            holder.talla.setText(String.format("Talla: %s", pedido.getTalla().toUpperCase()));
        } else{
            holder.talla.setVisibility(View.INVISIBLE);
        }
        holder.mas.setOnClickListener(view -> {
            if (Integer.parseInt(holder.cantidad.getText().toString()) == 9){
                Toast.makeText(view.getContext(), "No puedes añadir más de 9 unidades por prenda.", Toast.LENGTH_SHORT).show();
            }else {
                int cantidadNueva = (Integer.parseInt(holder.cantidad.getText().toString()) + 1);
                holder.cantidad.setText(cantidadNueva + "");
                pedido.setCantidad(Long.parseLong(holder.cantidad.getText().toString()));
                holder.precio.setText(String.format("%.2f€",pedido.getPrecioUnidad()*pedido.getCantidad()));
            }
        });

        holder.menos.setOnClickListener(view -> {
            if (Integer.parseInt(holder.cantidad.getText().toString()) == 1){
                Toast.makeText(view.getContext(), "Si quiere eliminar este producto de su compra pulse el icono de la papelera.", Toast.LENGTH_SHORT).show();
            }else {
                int cantidadNueva = (Integer.parseInt(holder.cantidad.getText().toString()) - 1);
                holder.cantidad.setText(cantidadNueva + "");
                pedido.setCantidad(Long.parseLong(holder.cantidad.getText().toString()));
                holder.precio.setText(String.format("%.2f€",pedido.getPrecioUnidad()*pedido.getCantidad()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class PedidosViewHolder extends RecyclerView.ViewHolder{

        ImageView imagen;
        TextView cantidad, nombre, precio, talla;
        Button mas, menos, trash;
        LinearLayout linear, linearCantidad, linearDetalles, linearMasMenos;
        ConexionFirebase conexion = new ConexionFirebase();
        PedidosAdapter adapter;


        public PedidosViewHolder(@NonNull View itemView, PedidosAdapter adapter1) {
            super(itemView);
            adapter = adapter1;
            imagen = itemView.findViewById(R.id.imagenPrendaPedido);
            cantidad = itemView.findViewById(R.id.cantidadPrendaPedido);
            nombre = itemView.findViewById(R.id.nombrePrendaPedido);
            precio = itemView.findViewById(R.id.precioPrendaPedido);
            talla = itemView.findViewById(R.id.tallaPrendaPedido);
            mas = itemView.findViewById(R.id.buttonMasPrendaPedido);
            menos = itemView.findViewById(R.id.buttonMenosprendaPedido);
            trash = itemView.findViewById(R.id.buttonBasuraPrendaPedido);
            linear = itemView.findViewById(R.id.linearPedido);
            linearCantidad = itemView.findViewById(R.id.linearCantidad);
            linearDetalles = itemView.findViewById(R.id.linearDetalles);
            linearMasMenos = itemView.findViewById(R.id.linearMasMenos);

            boolean modoOscuro = adapter1.context.getSharedPreferences("Ajustes", adapter1.context.MODE_PRIVATE).getBoolean("modoOscuro", false);
            if (modoOscuro){
                linear.setBackgroundColor(Color.BLACK);
                linearCantidad.setBackgroundColor(Color.BLACK);
                linearDetalles.setBackgroundColor(Color.BLACK);
                linearMasMenos.setBackgroundColor(Color.BLACK);
                nombre.setTextColor(Color.WHITE);
                talla.setTextColor(Color.WHITE);
                precio.setTextColor(Color.WHITE);
                cantidad.setTextColor(Color.WHITE);
                mas.setBackgroundResource(R.drawable.mas_night);
                menos.setBackgroundResource(R.drawable.menos_night);
            }else{
                linear.setBackgroundColor(Color.WHITE);
                linearCantidad.setBackgroundColor(Color.WHITE);
                linearDetalles.setBackgroundColor(Color.WHITE);
                linearMasMenos.setBackgroundColor(Color.WHITE);
                nombre.setTextColor(Color.BLACK);
                talla.setTextColor(Color.BLACK);
                precio.setTextColor(Color.BLACK);
                cantidad.setTextColor(Color.BLACK);
                mas.setBackgroundResource(R.drawable.mas);
                menos.setBackgroundResource(R.drawable.menos);
            }

            trash.setOnClickListener(view -> {
                new AlertDialog.Builder(view.getContext())
                        .setPositiveButton("Confirmar", (dialogInterface, i) -> {
                            conexion.borrarPedido(view.getContext(), adapter.getDatos().get(getAdapterPosition()));
                            adapter.borrarItem(getAdapterPosition());
                            Toast.makeText(view.getContext(), "Prenda de ropa eleminada de la lista.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setTitle("¿Estás seguro?")
                        .setMessage("Si confirmas eliminarás esta prenda de tu pedido.")
                        .show();
            });

            mas.setOnClickListener(view -> {
                if (Integer.parseInt(cantidad.getText().toString()) == 9){
                    Toast.makeText(view.getContext(), "No puedes añadir más de 9 unidades por prenda.", Toast.LENGTH_SHORT).show();
                }else {
                    int cantidadNueva = (Integer.parseInt(cantidad.getText().toString()) + 1);
                    cantidad.setText(cantidadNueva + "");
                    //conexion.updatePedido(cantidadNueva);
                }
            });

            menos.setOnClickListener(view -> {
                if (Integer.parseInt(cantidad.getText().toString()) == 1){
                    Toast.makeText(view.getContext(), "Si quiere eliminar este producto de su compra pulse el icono de la papelera.", Toast.LENGTH_SHORT).show();
                }else {
                    int cantidadNueva = (Integer.parseInt(cantidad.getText().toString()) - 1);
                    cantidad.setText(cantidadNueva + "");
                    //conexion.updatePedido(cantidadNueva);
                }
            });
        }
    }
}