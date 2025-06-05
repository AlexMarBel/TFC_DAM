package com.example.tfc_amb.Recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfc_amb.Modelos.ProductoCarrito;
import com.example.tfc_amb.R;

import java.text.DecimalFormat;
import java.util.List;

public class ProductoCompradoAdapter extends RecyclerView.Adapter<ProductoCompradoAdapter.ViewHolder> {
    private List<ProductoCarrito> listaProductosCarrito;
    private Context context;

    public ProductoCompradoAdapter(List<ProductoCarrito> listaProductosCarrito, Context context) {
        this.listaProductosCarrito = listaProductosCarrito;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductoCompradoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_producto_comprado,parent, false);
        return new ProductoCompradoAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoCompradoAdapter.ViewHolder holder, int position) {
        ProductoCarrito productoCarrito = listaProductosCarrito.get(position);

        Double precioUnidad = productoCarrito.getPrecio();
        int kgComprados = productoCarrito.getCantidadComprada();
        Double precioTotal = precioUnidad*kgComprados;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String precioUnidadFormateado = decimalFormat.format(precioUnidad);
        String precioTotalFormateado = decimalFormat.format(precioTotal);

        String mostrarPrecioUnidad = context.getString(R.string.precio_producto_carrito_kg, precioUnidadFormateado.replace(".", ","));
        String mostrarPrecioTotal = context.getString(R.string.cantidad_total, precioTotalFormateado.replace(".", ","));

        String mostrarCantidadKg = "";
        if(kgComprados>1) {
            mostrarCantidadKg = context.getString(R.string.cantidad_producto_carrito_kgs, String.valueOf(kgComprados));
        }else{
            mostrarCantidadKg = context.getString(R.string.cantidad_producto_carrito_kg, String.valueOf(kgComprados));
        }

        holder.textViewTitulo.setText(productoCarrito.getTitulo());
        holder.textViewPrecioUnidad.setText(mostrarPrecioUnidad);
        holder.textViewProductoCantidad.setText(mostrarCantidadKg);
        holder.textViewPrecioTotal.setText(mostrarPrecioTotal);
    }

    @Override
    public int getItemCount() {
        return listaProductosCarrito.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewTitulo, textViewPrecioUnidad, textViewProductoCantidad, textViewPrecioTotal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitulo = itemView.findViewById(R.id.textViewProductoTitulo);
            textViewPrecioUnidad = itemView.findViewById(R.id.textViewProductoPrecioKg);
            textViewProductoCantidad = itemView.findViewById(R.id.textViewProductoCantidad);
            textViewPrecioTotal = itemView.findViewById(R.id.textViewProductoPrecioTotal);
        }
    }
}
