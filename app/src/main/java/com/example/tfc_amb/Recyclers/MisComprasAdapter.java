package com.example.tfc_amb.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfc_amb.Modelos.CompraRealizada;
import com.example.tfc_amb.UsuarioPanel.DetallesCompraRealizadaActivity;
import com.example.tfc_amb.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MisComprasAdapter extends RecyclerView.Adapter<MisComprasAdapter.ViewHolder> {
    private List<CompraRealizada> listaCompras;
    private Context context;
    private Double precioTotalDouble;

    public MisComprasAdapter() {
    }

    public MisComprasAdapter(List<CompraRealizada> listaCompras, Context context) {
        this.listaCompras = listaCompras;
        this.context = context;
    }

    @NonNull
    @Override
    public MisComprasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_mis_compras,parent, false);
        return new MisComprasAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MisComprasAdapter.ViewHolder holder, int position) {
        CompraRealizada compraRealizada = listaCompras.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String fecha = String.valueOf(sdf.format(compraRealizada.getFechaCompra()));

        holder.textViewFecha.setText(fecha);

        /**
        String numeroConPunto = new String();

        //Para evitar problemas con el punto y la coma en valores numericos debido al idioma
        try{
            numeroConPunto = compraRealizada.getPrecio().replace(",", ".");
            precioTotalDouble = Double.parseDouble(numeroConPunto);
        }catch (NumberFormatException e){
            Log.d("Error parseo" , "Error parseo Double precio compra " + e);
        }
        */

        Double precioTotal = compraRealizada.getPrecio();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String precioTotalStringConPunto = decimalFormat.format(precioTotal);
        String numeroConComa = precioTotalStringConPunto.replace(".", ",");

        String mostrarPrecio = context.getString(R.string.precio_producto_carrito, numeroConComa);

        holder.textViewPrecio.setText(mostrarPrecio);

        holder.btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetallesCompraRealizadaActivity.class);
                intent.putExtra("fecha", fecha);
                intent.putExtra("precio", compraRealizada.getPrecio());
                intent.putExtra("productos", (Serializable) compraRealizada.getProductos());
                intent.putExtra("gastosEnvio", compraRealizada.getGastoEnvio());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCompras.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewFecha, textViewPrecio;
        private AppCompatButton btnVer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPrecio = itemView.findViewById(R.id.textViewCompraPrecio);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            btnVer = itemView.findViewById(R.id.buttonVer);
        }
    }
}
