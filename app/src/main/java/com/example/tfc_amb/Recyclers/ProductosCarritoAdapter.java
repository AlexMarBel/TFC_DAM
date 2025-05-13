package com.example.tfc_amb.Recyclers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfc_amb.Modelos.ProductoCarrito;
import com.example.tfc_amb.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.List;

public class ProductosCarritoAdapter extends RecyclerView.Adapter<ProductosCarritoAdapter.ViewHolder> {
    private List<ProductoCarrito> listaProductoCarrito;
    private Context context;
    private FirebaseFirestore db;
    private String userID;

    public ProductosCarritoAdapter(List<ProductoCarrito> listaProductoCarrito, Context context) {
        this.listaProductoCarrito = listaProductoCarrito;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductosCarritoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_mi_carrito,parent, false);
        return new ProductosCarritoAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosCarritoAdapter.ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ProductoCarrito productoCarrito = listaProductoCarrito.get(position);

        int cantidadComprado = productoCarrito.getCantidadComprada();
        double precioProducto = productoCarrito.getPrecio();
        double precioFinal = cantidadComprado*precioProducto;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String precioFormateado = decimalFormat.format(precioFinal);

        String mostrarPrecio = context.getString(R.string.precio_producto_carrito, precioFormateado);
        holder.textViewPrecio.setText(mostrarPrecio);

        holder.textViewTitulo.setText(StringUtils.capitalize(productoCarrito.getTitulo()));
        holder.textViewCantidad.setText(String.valueOf(productoCarrito.getCantidadComprada()));
        int idProducto = productoCarrito.getId();


        holder.btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidadInt = Integer.parseInt(holder.textViewCantidad.getText().toString());
                if(cantidadInt != 0){
                    cantidadInt--;
                    String nuevaCantidad = String.valueOf(cantidadInt);
                    holder.textViewCantidad.setText(nuevaCantidad);

                    //Actualizamos la cantidadVendida en la base de datos con cada click
                    actualizarCantidadVendida(idProducto, cantidadInt);
                }
            }
        });

        holder.btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidadInt = Integer.parseInt(holder.textViewCantidad.getText().toString());
                if(cantidadInt < 10){
                    cantidadInt++;
                    String nuevaCantidad = String.valueOf(cantidadInt);
                    holder.textViewCantidad.setText(nuevaCantidad);

                    //Actualizamos la cantidadVendida en la base de datos con cada click
                    actualizarCantidadVendida(idProducto, cantidadInt);
                }
            }
        });

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                db.collection("carrito").document(userID)
                        .collection("productos")
                        .document(String.valueOf(idProducto))
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("ProductosCarritoAdapter", "Producto eliminado del carrito correctamente");
                                notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("ProductosCarritoAdapter", "Error al eliminar producto del carrito");
                            }
                        })
                ;

            }
        });
    }

    private void actualizarCantidadVendida(int idProducto, int cantidad) {
        db.collection("carrito").document(userID)
                .collection("productos")
                .document(String.valueOf(idProducto))
                .update("cantidadComprada", cantidad)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("ProductosCarritoAdapter", "Cantidad actualizada correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ProductosCarritoAdapter", "Error al actualizar cantidad");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listaProductoCarrito.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewCantidad, textViewTitulo, textViewPrecio;
        private AppCompatButton btnEliminar, btnMas, btnMenos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCantidad = itemView.findViewById(R.id.textViewCarritoCantidadProducto);
            textViewTitulo = itemView.findViewById(R.id.textViewCarritoTitulo);
            textViewPrecio = itemView.findViewById(R.id.textViewProductoPrecioKg);
            btnEliminar = itemView.findViewById(R.id.buttonEliminar);
            btnMas = itemView.findViewById(R.id.buttonMas);
            btnMenos = itemView.findViewById(R.id.buttonMenos);
        }
    }
}
