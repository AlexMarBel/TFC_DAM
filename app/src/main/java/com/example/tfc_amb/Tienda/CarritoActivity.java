package com.example.tfc_amb.Tienda;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.ProductoCarrito;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Recyclers.ProductosCarritoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarritoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ProductoCarrito> listaProductoCarrito;
    private ProductosCarritoAdapter adaptadorProductosCarrito;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID;
    private AppCompatButton btnComprar, btnBorrar;
    private TextView textViewCantidadSubtotal, textViewCantidadIVA, textViewCantidadEnvio, textViewCantidadTotal;
    private double ofertaEnvio = 30, iva = 0.04, precioCarrito, gastosEnvio;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnComprar = findViewById(R.id.buttonRealizarCompra);
        btnBorrar = findViewById(R.id.buttonCarritoBorrar);
        textViewCantidadEnvio = findViewById(R.id.textViewGastosEnvioCantidad);
        textViewCantidadIVA = findViewById(R.id.textViewIVACantidad);
        textViewCantidadSubtotal = findViewById(R.id.textViewSubtotalCantidad);
        textViewCantidadTotal = findViewById(R.id.textViewTotalCantidad);

        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();

        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, CarritoActivity.this);
        listaProductoCarrito = new ArrayList<ProductoCarrito>();

        conexionDB.cargarCarrito(new ConexionDB.OnCarritoCargadoListener() {
            @Override
            public void onCarritoCargado(List<ProductoCarrito> listaProductoCarritoDB) {
                listaProductoCarrito.clear();
                listaProductoCarrito.addAll(listaProductoCarritoDB);

                precioCarrito = calcularPrecioCarrito();
                adaptadorProductosCarrito.notifyDataSetChanged();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewCarrito);
        recyclerView.setHasFixedSize(true);

        adaptadorProductosCarrito = new ProductosCarritoAdapter(listaProductoCarrito, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adaptadorProductosCarrito);


        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listaProductoCarrito.isEmpty()) {
                    Toast.makeText(CarritoActivity.this, getText(R.string.sin_productos), Toast.LENGTH_SHORT).show();
                    return;
                }


                //Forzamos que los numeros se guarden con punto para marcar los decimales, homogeneizando los datos
                //que se guardan en la base de datos y evitando problemas futuros de formato con el punto y la coma.
                DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
                decimalFormat.applyPattern("#.##");
                String total = decimalFormat.format(calcularPrecioCarrito());

                Intent intent = new Intent(CarritoActivity.this, ConfirmarDatosActivity.class);
                intent.putExtra("total", total);
                intent.putExtra("gastoEnvio", String.valueOf(gastosEnvio));
                Log.d("Precio carrito1", "total: "+total+", gastoEnvio: "+gastosEnvio);
                startActivity(intent);
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexionDB.borrarCarrito();
            }
        });
    }


    private double calcularPrecioCarrito() {
        double total = 0;
        double subtotal = 0;
        double cantidadIVA = 0;


        //Obtenemos el precio total del carrito iterando sobre los productos
        for (ProductoCarrito productoCarrito : listaProductoCarrito){
            int cantidadComprada = productoCarrito.getCantidadComprada();
            double precioProducto = productoCarrito.getPrecio();
            total += cantidadComprada * precioProducto;
        }

        //A partir del precio total calculamos el resto de cantidades a mostrar
        cantidadIVA = total * iva;
        subtotal = total - cantidadIVA;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String subtotalFormateado = decimalFormat.format(subtotal);
        String ivaFormateado = decimalFormat.format(cantidadIVA);

        String mostrarSubtotal = getString(R.string.cantidad_subtotal, String.valueOf(subtotalFormateado));
        textViewCantidadSubtotal.setText(mostrarSubtotal);

        String mostrarIVA = getString(R.string.cantidad_iva, String.valueOf(ivaFormateado));
        textViewCantidadIVA.setText(mostrarIVA);

        if(total > ofertaEnvio){
            gastosEnvio = 0;
            String mostrarGastosEnvio = getString(R.string.cantidad_gastos_envio, String.valueOf(gastosEnvio));
            textViewCantidadEnvio.setText(mostrarGastosEnvio);
        } else {
            gastosEnvio = 3;
            String mostrarGastosEnvio = getString(R.string.cantidad_gastos_envio, String.valueOf(gastosEnvio));
            textViewCantidadEnvio.setText(mostrarGastosEnvio);
            total = total + gastosEnvio;
        }

        String totalFormateado = decimalFormat.format(total);
        String mostrarPrecioTotal = getString(R.string.cantidad_total, totalFormateado);
        textViewCantidadTotal.setText(mostrarPrecioTotal);
        return total;
    }

}