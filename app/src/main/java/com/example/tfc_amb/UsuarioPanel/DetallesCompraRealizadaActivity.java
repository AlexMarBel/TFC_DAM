package com.example.tfc_amb.UsuarioPanel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfc_amb.Modelos.ProductoCarrito;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Recyclers.ProductoCompradoAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductosCompraRealizadaActivity extends AppCompatActivity {

    private AppCompatButton btnVolver;
    private TextView textViewCantidadSubtotal, textViewCantidadIVA, textViewCantidadTotal, textViewGastosEnvio, textViewFecha;
    private String fecha, precio, gastosEnvio;
    private double iva = 0.04, precioCarrito;
    private RecyclerView recyclerView;
    private List<ProductoCarrito> listaProductoCarrito;
    private ProductoCompradoAdapter adaptadorProductosComprados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productos_compra_realizada);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVolver = findViewById(R.id.buttonVolver);
        textViewCantidadIVA = findViewById(R.id.textViewIVACantidad);
        textViewCantidadSubtotal = findViewById(R.id.textViewSubtotalCantidad);
        textViewCantidadTotal = findViewById(R.id.textViewTotalCantidad);
        textViewGastosEnvio = findViewById(R.id.textViewCompraGastosEnvioCantidad);
        textViewFecha = findViewById(R.id.textViewCompraFecha);


        recyclerView = findViewById(R.id.recyclerViewProductos);
        recyclerView.setHasFixedSize(true);

        listaProductoCarrito = new ArrayList<ProductoCarrito>();
        Intent intent = getIntent();
        listaProductoCarrito = (List<ProductoCarrito>) intent.getSerializableExtra("productos");
        fecha = intent.getStringExtra("fecha");
        gastosEnvio = intent.getStringExtra("gastosEnvio");

        Double doubleGastoEnvio = Double.parseDouble(gastosEnvio);

        String numeroConPunto = new String();
        Double precioTotalDouble = 0.0;

        //Para evitar problemas con el punto y la coma en valores numericos debido al idioma
        try{
            numeroConPunto = intent.getStringExtra("precio");
            precioTotalDouble = Double.parseDouble(numeroConPunto);
        }catch (NumberFormatException e){
            Log.d("Error parseo" , "Error parseo Double precio compra " + e);
        }

        Double totalSinEnvio = precioTotalDouble-doubleGastoEnvio;
        Double ivaDouble = totalSinEnvio*iva;
        Double subtotalDouble = totalSinEnvio - ivaDouble;



        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String ivaString = decimalFormat.format(ivaDouble);
        String subtotalString = decimalFormat.format(subtotalDouble);
        String precioTotalDoubleString = decimalFormat.format(precioTotalDouble);

        String mostrarFecha = getString(R.string.compra_realizada_fecha, fecha);


        textViewFecha.setText(mostrarFecha);
        textViewGastosEnvio.setText(gastosEnvio);
        textViewCantidadTotal.setText(precioTotalDoubleString);
        textViewCantidadIVA.setText(ivaString);
        textViewCantidadSubtotal.setText(subtotalString);

        adaptadorProductosComprados = new ProductoCompradoAdapter(listaProductoCarrito, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adaptadorProductosComprados);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }
}