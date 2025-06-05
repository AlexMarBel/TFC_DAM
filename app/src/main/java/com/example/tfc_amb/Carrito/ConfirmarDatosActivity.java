package com.example.tfc_amb.Carrito;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.Direccion;
import com.example.tfc_amb.Modelos.Producto;
import com.example.tfc_amb.Modelos.Usuario;
import com.example.tfc_amb.Modelos.ProductoCarrito;
import com.example.tfc_amb.R;
import com.example.tfc_amb.RegistroLogin.MainActivity;
import com.example.tfc_amb.Tienda.TiendaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ConfirmarDatosActivity extends AppCompatActivity {

    private AppCompatButton btnGuardar, btnVolver;
    private EditText editTextCalle, editTextPortal, editTextPiso, editTextPuerta, editTextCiudad, editTextCP, editTextTelefono;
    private String calle, portal, piso, puerta, ciudad, codigoPostal, userID, telefono;
    private TextView textViewTotal;
    private Direccion direccionUsuario;
    private List<ProductoCarrito> listaCarrito;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Usuario usuarioCargado;
    private boolean realizarCompra;
    private int consultasRealizadas, consultasRealizadas2, consultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirmar_datos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextCalle = findViewById(R.id.textViewCalleValor);
        editTextPortal = findViewById(R.id.textViewPortalValor);
        editTextPiso = findViewById(R.id.textViewPisoValor);
        editTextPuerta = findViewById(R.id.textViewPuertaValor);
        editTextCiudad = findViewById(R.id.textViewCiudadValor);
        editTextCP = findViewById(R.id.textViewCodigoPostalValor);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        textViewTotal = findViewById(R.id.textViewConfirmarTotalCantidad);

        btnGuardar = findViewById(R.id.buttonDireccionGuardar);
        btnVolver = findViewById(R.id.buttonDireccionVolver);

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();


        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, this);
        conexionDB.cargarDireccion(new ConexionDB.OnDireccionCargadaListener() {
            @Override
            public void onDireccionCargada(Direccion direccion) {
                editTextCalle.setText(direccion.getCalle());
                editTextPortal.setText(direccion.getPortal());
                editTextPiso.setText(direccion.getPiso());
                editTextPuerta.setText(direccion.getPuerta());
                editTextCiudad.setText(direccion.getCiudad());
                editTextCP.setText(direccion.getCodigoPostal());
            }
        });

        conexionDB.cargarUsuario(new ConexionDB.OnUsuarioCargadoListener() {
            @Override
            public void onUsuarioCargado(Usuario usuario) {
                editTextTelefono.setText(usuario.getTelefono());
                usuarioCargado = usuario;
            }
        });


        listaCarrito = new ArrayList<>();
        conexionDB.cargarCarrito(new ConexionDB.OnCarritoCargadoListener() {
            @Override
            public void onCarritoCargado(List<ProductoCarrito> listaProductoCarrito) {
                listaCarrito.addAll(listaProductoCarrito);
            }
        });

        Intent intent = getIntent();
        double total = intent.getDoubleExtra("total", -1);
        double gastoEnvio = intent.getDoubleExtra("gastoEnvio", -1);

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String totalFormateado = decimalFormat.format(total);
        String mostrarTotal = getString(R.string.cantidad_total, totalFormateado);
        textViewTotal.setText(mostrarTotal);

        //Log.d("Precio carrito1", "Confirmar datos: total: "+total+", gastoEnvio: "+gastoEnvio);


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calle = editTextCalle.getText().toString();
                portal = editTextPortal.getText().toString();
                piso = editTextPiso.getText().toString();
                puerta = editTextPuerta.getText().toString();
                ciudad = editTextCiudad.getText().toString();
                codigoPostal = editTextCP.getText().toString();
                telefono = editTextTelefono.getText().toString();

                if(calle.isEmpty()){
                    Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_calle_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(portal.isEmpty()){
                    Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_portal_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(piso.isEmpty()){
                    Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_piso_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(puerta.isEmpty()){
                    Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_puerta_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(ciudad.isEmpty()){
                    Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_ciudad_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(codigoPostal.isEmpty()){
                    Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_codigo_postal_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(telefono.isEmpty()){
                    Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_telefono_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!usuarioConectado()){
                    return;
                }

                //Guardamos los datos nuevos
                direccionUsuario = new Direccion(calle, portal, piso, puerta, ciudad, codigoPostal);
                conexionDB.guardarDireccion(direccionUsuario);

                usuarioCargado.setTelefono(telefono);
                conexionDB.guardarUsuario(usuarioCargado);

                realizarCompra = true;
                consultas = listaCarrito.size();
                consultasRealizadas = 0;

                for(ProductoCarrito p : listaCarrito){
                    int productoID = p.getId();

                    //Verificamos que quede cantidad suficiente de producto
                    conexionDB.obtenerProductoPorId(productoID, new ConexionDB.OnObtenerProductoPorIdListener() {
                        @Override
                        public void OnObtenerProductoPorId(Producto productoDB) {
                            if (p.getCantidadComprada() > productoDB.getCantidad()){
                                realizarCompra = false;
                                if(productoDB.getCantidad()> 1) {
                                    //"No hay suficiente cantidad de " + p.getTitulo() + ". Solo quedan " + productoDB.getCantidad()
                                    Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_cantidad_insuficiente, p.getTitulo(), productoDB.getCantidad()), Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_producto_no_disponible, p.getTitulo()) , Toast.LENGTH_SHORT).show();
                                    //"Lo sentimos, no queda: "+ productoDB.getTitulo()
                                }
                                consultasRealizadas++;
                            }else{
                                consultasRealizadas++;
                            }

                            //Si quedan suficientes productos realizamos la compra. Primero se actualizaran los datos de
                            //producto restante y despues se realiza la compra borrandose el carrito
                            //Como las consultas a la base de datos son asincronas pongo contadores para esperar
                            //a que se hagan todas las consultas antes de que el codigo continue.

                            if(consultasRealizadas == consultas && realizarCompra) {

                                consultasRealizadas2 = 0;

                                for (ProductoCarrito p : listaCarrito) {
                                    conexionDB.actualizarCantidadProducto(p.getId(), p.getCantidadComprada(), new ConexionDB.OnActualizarCantidadProductoListener() {
                                        @Override
                                        public void OnActualizarCantidadProducto() {
                                            consultasRealizadas2++;

                                            if(consultasRealizadas2 == consultas){

                                                conexionDB.realizarCompra(listaCarrito, total, gastoEnvio, direccionUsuario, usuarioCargado, new ConexionDB.OnCompraRealizadaListener() {
                                                    @Override
                                                    public void onCompraRealizada() {
                                                        conexionDB.borrarCarrito();
                                                        Toast.makeText(ConfirmarDatosActivity.this, "Compra realizada", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(ConfirmarDatosActivity.this, TiendaActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private boolean usuarioConectado(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            NetworkCapabilities networkCapabilities= connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if(networkCapabilities == null) {
                Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_sin_conexion), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true;
            }
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true;
            }
            return networkInfo != null && networkInfo.isConnected();
        }else{
            Toast.makeText(ConfirmarDatosActivity.this, getString(R.string.error_sin_conexion), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}