package com.example.tfc_amb.AdminPanel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.Direccion;
import com.example.tfc_amb.Modelos.ProductoCarrito;
import com.example.tfc_amb.Modelos.Usuario;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Recyclers.ProductoCompradoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductosCompraRealizadaAdminActivity extends AppCompatActivity {

    private AppCompatButton btnVolver, btnGuardar;
    private TextView textViewCantidadSubtotal, textViewCantidadIVA, textViewCantidadTotal, textViewGastosEnvio, textViewFecha, textViewCalle, textViewPortal, textViewPiso, textViewPuerta, textViewCiudad, textViewCP, textViewNombre, textViewApellidos, textViewTelefono;
    private CheckBox checkBoxProcesado;
    private String fecha, precio, gastosEnvio;
    private double iva = 0.04, precioCarrito;
    private RecyclerView recyclerView;
    private List<ProductoCarrito> listaProductoCarrito;
    private ProductoCompradoAdapter adaptadorProductosComprados;
    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productos_compra_realizada_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVolver = findViewById(R.id.buttonVolver);
        btnGuardar = findViewById(R.id.buttonGuardar);
        textViewCantidadIVA = findViewById(R.id.textViewIVACantidad);
        textViewCantidadSubtotal = findViewById(R.id.textViewSubtotalCantidad);
        textViewCantidadTotal = findViewById(R.id.textViewTotalCantidad);
        textViewGastosEnvio = findViewById(R.id.textViewCompraGastosEnvioCantidad);
        textViewFecha = findViewById(R.id.textViewCompraFecha);
        textViewCiudad = findViewById(R.id.textViewCiudadValor);
        textViewCalle = findViewById(R.id.textViewCalleValor);
        textViewPortal = findViewById(R.id.textViewPortalValor);
        textViewPiso = findViewById(R.id.textViewPisoValor);
        textViewPuerta = findViewById(R.id.textViewPuertaValor);
        textViewCP = findViewById(R.id.textViewCodigoPostalValor);
        textViewNombre = findViewById(R.id.textViewNombreValor);
        textViewApellidos = findViewById(R.id.textViewApellidosValor);
        textViewTelefono = findViewById(R.id.textViewTelefonoValor);
        checkBoxProcesado = findViewById(R.id.checkBoxProcesado);

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();


        recyclerView = findViewById(R.id.recyclerViewProductos);
        recyclerView.setHasFixedSize(true);

        listaProductoCarrito = new ArrayList<ProductoCarrito>();
        Intent intent = getIntent();
        listaProductoCarrito = (List<ProductoCarrito>) intent.getSerializableExtra("productos");
        fecha = intent.getStringExtra("fecha");
        gastosEnvio = intent.getStringExtra("gastosEnvio");

        Direccion direccion = (Direccion) intent.getSerializableExtra("direccion");
        Usuario user = (Usuario) intent.getSerializableExtra("user");

        Double doubleGastoEnvio = Double.parseDouble(gastosEnvio);


        String numeroConPunto = new String();
        String numeroConComa = new String();
        Double precioTotalDouble = 0.0;

        //Para evitar problemas con el punto y la coma en valores numericos debido al idioma
        try{
            numeroConPunto = intent.getStringExtra("precio");
            precioTotalDouble = Double.parseDouble(numeroConPunto);
            Log.d("precio", String.valueOf(precioTotalDouble));
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
        textViewGastosEnvio.setText(getString(R.string.cantidad_gastos_envio, gastosEnvio));
        textViewCantidadTotal.setText(getString(R.string.cantidad_total, precioTotalDoubleString));
        textViewCantidadIVA.setText(getString(R.string.cantidad_iva, ivaString));
        textViewCantidadSubtotal.setText(getString(R.string.cantidad_subtotal, subtotalString));


        textViewCalle.setText(direccion.getCalle());
        textViewPortal.setText(direccion.getPortal());
        textViewPiso.setText(direccion.getPiso());
        textViewPuerta.setText(direccion.getPuerta());
        textViewCiudad.setText(direccion.getCiudad());
        textViewCP.setText(direccion.getCodigoPostal());
        textViewNombre.setText(user.getNombre());
        textViewApellidos.setText(user.getApellidos());
        textViewTelefono.setText(user.getTelefono());
        checkBoxProcesado.setChecked(intent.getBooleanExtra("compraProcesado", false));


        adaptadorProductosComprados = new ProductoCompradoAdapter(listaProductoCarrito, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adaptadorProductosComprados);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, ProductosCompraRealizadaAdminActivity.this);

                long millis = intent.getLongExtra("fechaCompra", -1);

                Date fechaCompra = new Date();

                if(millis != -1){
                    fechaCompra = new Date(millis);
                }

                conexionDB.actualizarCompraProcesada(user.getId(), fechaCompra, checkBoxProcesado.isChecked());

                Intent intent = new Intent(ProductosCompraRealizadaAdminActivity.this, AdministrarComprasActivity.class);
                startActivity(intent);
            }
        });
    }
}