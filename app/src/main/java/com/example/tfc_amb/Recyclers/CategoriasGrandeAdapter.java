package com.example.tfc_amb.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfc_amb.Tienda.DetallesCategoriaActivity;
import com.example.tfc_amb.Modelos.Categorias;
import com.example.tfc_amb.R;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CategoriasGrandeAdapter extends RecyclerView.Adapter<CategoriasGrandeAdapter.ViewHolder> {

    List<Categorias> listaCategorias;
    Context context;

    public CategoriasGrandeAdapter(List<Categorias> listaCategorias, Context context) {
        this.listaCategorias = listaCategorias;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoriasGrandeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_categorias_grande,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriasGrandeAdapter.ViewHolder holder, int position) {
        Categorias categoria = listaCategorias.get(position);

        //Utilizamos la libreria stringutils para facilitar el formateo del string y poner en mayuscula la primera letra.
        String tituloFormateado = StringUtils.capitalize(categoria.getTitulo());
        holder.titulo.setText(tituloFormateado);

        if(categoria.getUrlFoto() != null && !categoria.getUrlFoto().isEmpty()){
            //Mostramos la imagen utilizando la libreria glide para facilitar el manejo de imagenes.
            Glide.with(context)
                    .load(categoria.getUrlFoto())
                    .into(holder.imagen);
        }else{
            Glide.with(context)
                    .load(R.drawable.baseline_no_photography_24)
                    .into(holder.imagen);
        }

        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetallesCategoriaActivity.class);
                intent.putExtra("id", categoria.getId());
                intent.putExtra("titulo", categoria.getTitulo());
                intent.putExtra("urlFoto", categoria.getUrlFoto());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView titulo;
        ImageView imagen;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textViewItemTitulo);
            imagen = itemView.findViewById(R.id.imageViewItemImagen);
        }
    }
}