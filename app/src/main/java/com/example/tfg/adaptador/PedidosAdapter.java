package com.example.tfg.adaptador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.DetallesUsuario;
import com.example.tfg.R;
import com.example.tfg.conexion.ConexionFirebase;
import com.example.tfg.entidad.Pedido;

import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.PedidosViewHolder> {

    private final Context context;
    private List<Pedido> pedidos;
    private ConexionFirebase conexion = new ConexionFirebase();

    public PedidosAdapter(Context context, List<Pedido> pedidos) {
        this.context = context;
        this.pedidos = pedidos;
    }

    public void setDatos(List<Pedido> pedidos){this.pedidos = pedidos;}
    public List<Pedido> getDatos(){return pedidos;}

    @NonNull
    @Override
    public PedidosAdapter.PedidosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pedido_view, parent, false);
        return new PedidosViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull PedidosAdapter.PedidosViewHolder holder, int position) {

        Pedido pedido = pedidos.get(position);

        conexion.imagenPedido(context,holder.imagen, pedido.getPrenda());
        holder.nombre.setText(pedido.getPrenda());
        holder.cantidad.setText(String.format("%d", pedido.getCantidad()));
        holder.precio.setText(String.format("%f", pedido.getPrecioUnidad()*pedido.getCantidad()));

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class PedidosViewHolder extends RecyclerView.ViewHolder{

        ImageView imagen;
        TextView cantidad, nombre, precio;
        Button mas, menos, trash;
        ConexionFirebase conexion = new ConexionFirebase();


        public PedidosViewHolder(@NonNull View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.imagenPrendaPedido);
            cantidad = itemView.findViewById(R.id.cantidadPrendaPedido);
            nombre = itemView.findViewById(R.id.nombrePrendaPedido);
            precio = itemView.findViewById(R.id.precioPrendaPedido);
            mas = itemView.findViewById(R.id.buttonMasPrendaPedido);
            menos = itemView.findViewById(R.id.buttonMenosprendaPedido);
            trash = itemView.findViewById(R.id.buttonBasuraPrendaPedido);

            trash.setOnClickListener(view -> {
                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setPositiveButton("Confirmar", (dialogInterface, i) -> conexion.borrarPedido(view.getContext(), nombre.getText().toString()))
                        .setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setTitle("¿Estás seguro?")
                        .setMessage("Si confirmas eliminarás esta prenda de tu pedido.")
                        .create();
                dialog.create();
            });

            mas.setOnClickListener(view -> {
                if (Integer.parseInt(cantidad.getText().toString()) == 9){
                    Toast.makeText(view.getContext(), "No puedes añadir más de 9 unidades por prenda.", Toast.LENGTH_SHORT).show();
                }else{
                    int cantidadNueva = (Integer.parseInt(cantidad.getText().toString()) + 1);
                    cantidad.setText(String.format("%d", cantidadNueva));
                }
            });

        }
    }
}
