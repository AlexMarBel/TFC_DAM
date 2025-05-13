package com.example.tfc_amb.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfc_amb.Tienda.DetallesProductoActivity;
import com.example.tfc_amb.Modelos.Producto;
import com.example.tfc_amb.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {
    private List<Producto> listProductos;
    private Context context;
    private FirebaseFirestore db;
    private String userID;

    public FavoritosAdapter(List<Producto> listProductos, Context context) {
        this.listProductos = listProductos;
        this.context = context;
    }

    @NonNull
    @Override
    public FavoritosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_favoritos,parent, false);
        return new FavoritosAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritosAdapter.ViewHolder holder, int position) {

        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Producto producto = listProductos.get(position);

        String tituloFormateado = StringUtils.capitalize(producto.getTitulo());
        holder.titulo.setText(tituloFormateado);

        String url = producto.getUrlFoto();

        //Mostramos la imagen utilizando la libreria glide para facilitar el manejo de imagenes.
        if(url != null && !url.isEmpty()){
            Glide.with(context)
                    .load(url)
                    .into(holder.imagen);
        }

        holder.btnFavorito.setChecked(true);
        holder.btnFavorito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int idProducto = producto.getId();

                String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

                if (userId == null) {
                    Toast.makeText(context, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isChecked) {
                    listProductos.remove(position);
                    notifyItemRemoved(position);


                    // Si se desmarca, se elimina de la lista
                    db.collection("usuarios").document(userID)
                            .collection("favoritos")
                            .document(String.valueOf(idProducto))
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("FavoritosActivity", "Producto desmarcado como favorito correctamente");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("FavoritosActivity", "Error al desmarcar producto como favorito");
                                }
                            });
                }
            }
        });

        holder.titulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetallesProductoActivity.class);
                intent.putExtra("id", producto.getId());
                intent.putExtra("precio", producto.getPrecio());
                intent.putExtra("urlFoto", producto.getUrlFoto());
                intent.putExtra("titulo", producto.getTitulo());
                intent.putExtra("cantidad", producto.getCantidad());
                intent.putExtra("cantidadVendida", producto.getCantidadVendida());
                intent.putExtra("categoria", producto.getCategoria());
                context.startActivity(intent);
            }
        });

        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetallesProductoActivity.class);
                intent.putExtra("id", producto.getId());
                intent.putExtra("precio", producto.getPrecio());
                intent.putExtra("urlFoto", producto.getUrlFoto());
                intent.putExtra("titulo", producto.getTitulo());
                intent.putExtra("cantidad", producto.getCantidad());
                intent.putExtra("cantidadVendida", producto.getCantidadVendida());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProductos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imagen;
        TextView titulo;
        ToggleButton btnFavorito;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imageViewItemFavorito);
            titulo = itemView.findViewById(R.id.textViewItemFavorito);
            btnFavorito = itemView.findViewById(R.id.toggleButtonItemFavorito);
        }
    }
}
