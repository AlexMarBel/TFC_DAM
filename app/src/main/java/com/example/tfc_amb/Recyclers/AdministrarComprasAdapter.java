package com.example.tfc_amb.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfc_amb.Modelos.CompraRealizada;
import com.example.tfc_amb.AdminPanel.ProductosCompraRealizadaAdminActivity;
import com.example.tfc_amb.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdministrarComprasAdapter extends RecyclerView.Adapter<AdministrarComprasAdapter.ViewHolder>{

    private List<CompraRealizada> listaCompras;
    private Context context;
    private Double precioTotalDouble;

    public AdministrarComprasAdapter() {
    }

    public AdministrarComprasAdapter(List<CompraRealizada> listaCompras, Context context) {
        this.listaCompras = listaCompras;
        this.context = context;
    }

    @NonNull
    @Override
    public AdministrarComprasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_administrar_compras,parent, false);
        return new AdministrarComprasAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministrarComprasAdapter.ViewHolder holder, int position) {
        CompraRealizada compraRealizada = listaCompras.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String fecha = String.valueOf(sdf.format(compraRealizada.getFechaCompra()));

        holder.textViewFecha.setText(fecha);

        Double precioTotal = compraRealizada.getPrecio();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String precioTotalStringConPunto = decimalFormat.format(precioTotal);
        String precioTotalStringConPuntoConComa = precioTotalStringConPunto.replace(".", ",");

        String mostrarPrecio = context.getString(R.string.precio_producto_carrito, precioTotalStringConPuntoConComa);
        holder.textViewPrecio.setText(mostrarPrecio);

        holder.checkBoxProcesado.setChecked(compraRealizada.isCompraProcesada());

        holder.btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProductosCompraRealizadaAdminActivity.class);

                intent.putExtra("fecha", fecha);
                intent.putExtra("fechaCompra", compraRealizada.getFechaCompra().getTime());
                intent.putExtra("precio", compraRealizada.getPrecio());
                intent.putExtra("productos", (Serializable) compraRealizada.getProductos());
                intent.putExtra("gastosEnvio", compraRealizada.getGastoEnvio());
                intent.putExtra("compraProcesado", compraRealizada.isCompraProcesada());
                intent.putExtra("direccion", compraRealizada.getDireccion());
                intent.putExtra("user", compraRealizada.getUser());
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
        private CheckBox checkBoxProcesado;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPrecio = itemView.findViewById(R.id.textViewCompraPrecio);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            btnVer = itemView.findViewById(R.id.buttonVer);
            checkBoxProcesado = itemView.findViewById(R.id.checkBoxProcesado);
        }
    }
}
