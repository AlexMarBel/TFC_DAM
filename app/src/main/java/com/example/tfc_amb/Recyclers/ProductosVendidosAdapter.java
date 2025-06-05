package com.example.tfc_amb.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfc_amb.Tienda.DetallesProductoActivity;
import com.example.tfc_amb.Modelos.Producto;
import com.example.tfc_amb.R;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ProductosVendidosAdapter extends RecyclerView.Adapter<ProductosVendidosAdapter.ViewHolder>{

    List<Producto> listaProducto;
    Context context;

    public ProductosVendidosAdapter(List<Producto> listaProducto, Context context) {
        this.listaProducto = listaProducto;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductosVendidosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_producto,parent, false);

        return new ProductosVendidosAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosVendidosAdapter.ViewHolder holder, int position) {

        Producto producto = listaProducto.get(position);

        holder.titulo.setText(StringUtils.capitalize(producto.getTitulo()));

        if(producto.getUrlFoto() != null && !producto.getUrlFoto().isEmpty()){
            //Mostramos la imagen utilizando la libreria glide para facilitar el manejo de imagenes.
            Glide.with(context)
                    .load(producto.getUrlFoto())
                    .into(holder.imagen);
        }else{
            Glide.with(context)
                    .load(R.drawable.baseline_no_photography_24)
                    .into(holder.imagen);
        }


        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetallesProductoActivity.class);
                intent.putExtra("id", producto.getId());
                /**
                intent.putExtra("precio", producto.getPrecio());
                intent.putExtra("urlFoto", producto.getUrlFoto());
                intent.putExtra("titulo", producto.getTitulo());
                intent.putExtra("cantidad", producto.getCantidad());
                intent.putExtra("cantidadVendida", producto.getCantidadVendida());
                 */
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaProducto.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titulo;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textViewVendidoProductoTitulo);
            imagen = itemView.findViewById(R.id.imageViewVendidoProductoImagen);
        }
    }
}
