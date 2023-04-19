package com.example.tfg.adaptador;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.MainActivity;
import com.example.tfg.R;
import com.example.tfg.entidad.Prenda;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class TiendaAdapter extends RecyclerView.Adapter<TiendaAdapter.TiendaViewHolder> {

    private final Context contexto;
    private List<Prenda> prendas;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public TiendaAdapter(Context contexto, List<Prenda> prendas) {
        this.contexto = contexto;
        this.prendas = prendas;
    }

    public void setDatos(List<Prenda> datos){this.prendas = datos;}
    public List<Prenda> getDatos(){return prendas;}

    @NonNull
    @Override
    public TiendaAdapter.TiendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contexto).inflate(R.layout.tienda_view, parent, false);
        return new TiendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TiendaAdapter.TiendaViewHolder holder, int position) {
        Prenda prenda = prendas.get(position);

        holder.nombre.setText(prenda.getNombre());
        holder.precio.setText(String.format("%f€", prenda.getPrecio()));
        imagen(holder, prenda);

    }

    private void imagen(@NonNull TiendaViewHolder holder, Prenda prenda) {
        StorageReference gsReference = storage.getReferenceFromUrl(prenda.getImagen());
        final long ONE_MEGABYTE = 5 * 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,200,200,true);
            holder.imagen.setImageBitmap(bitmap1);
        }).addOnFailureListener(exception -> Toast.makeText(contexto, "Error al descargar la imagen de perfil", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return prendas.size();
    }

    public static class TiendaViewHolder extends RecyclerView.ViewHolder {
        private TextView nombre, precio;
        private ImageView imagen;

        public TiendaViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombrePrenda);
            precio = itemView.findViewById(R.id.precioPrenda);
            imagen = itemView.findViewById(R.id.imagenPrenda);
        }
    }
}
