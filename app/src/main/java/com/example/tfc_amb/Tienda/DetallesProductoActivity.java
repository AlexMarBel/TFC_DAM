package com.example.tfc_amb.Tienda;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.Producto;
import com.example.tfc_amb.Modelos.ProductoCarrito;
import com.example.tfc_amb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetallesProductoActivity extends AppCompatActivity {

private TextView precio, titulo, cantidadProducto;
private ImageView imagen;
private Button btnCarrito;
private ToggleButton botonFav;
private FirebaseFirestore db;
private FirebaseAuth mAuth;
private String userID;
private AppCompatButton btnMas, btnMenos;
private int idProducto;
private Producto producto;
private ProductoCarrito productoCarrito;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalles_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        //Abrimos sesion con la base de datos y obtenemos el id del usuario
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();
        producto = new Producto();
        productoCarrito = new ProductoCarrito();


        botonFav = findViewById(R.id.toggleButtonFav);
        imagen = findViewById(R.id.imageViewDetallesProducto);
        precio = findViewById(R.id.textViewDetallesProductoPrecio);
        titulo = findViewById(R.id.textViewDetallesProductoTitulo);
        btnCarrito = findViewById(R.id.buttonDetalleProductoCarrito);
        btnMas = findViewById(R.id.buttonMas);
        btnMenos = findViewById(R.id.buttonMenos);
        cantidadProducto = findViewById(R.id.textViewCantidadProducto);


        cantidadProducto.setText("0");


        //Obtenemos del intent los datos del producto
        Intent intent = getIntent();
        idProducto = intent.getIntExtra("id", -1);

        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, DetallesProductoActivity.this);

        conexionDB.obtenerProductoPorId(idProducto, new ConexionDB.OnObtenerProductoPorIdListener() {
            @Override
            public void OnObtenerProductoPorId(Producto productoDB) {
                Log.d("FirestoreDebug", "Producto obtenido: " + productoDB.toString());
                producto.setPrecio(productoDB.getPrecio());
                producto.setId(productoDB.getId());
                producto.setTitulo(productoDB.getTitulo());
                producto.setUrlFoto(productoDB.getUrlFoto());
                producto.setCategoria(productoDB.getCategoria());
                producto.setCantidad(productoDB.getCantidad());
                producto.setCantidadVendida(productoDB.getCantidadVendida());

                String precioFormateado = String.format("%.2f", producto.getPrecio());
                try {
                    producto.setPrecio(Double.parseDouble(precioFormateado));
                } catch (NumberFormatException e){
                    precioFormateado = precioFormateado.replace(",", ".");
                    producto.setPrecio(Double.parseDouble(precioFormateado));
                }


                if(producto.getUrlFoto() != null && !producto.getUrlFoto().isEmpty()) {
                    //Mostramos la imagen utilizando la libreria glide para facilitar el manejo de imagenes.
                    Glide.with(DetallesProductoActivity.this)
                            .load(producto.getUrlFoto())
                            .into(imagen);
                } else {
                    Glide.with(DetallesProductoActivity.this)
                            .load(R.drawable.baseline_no_photography_24)
                            .into(imagen);
                }

                titulo.setText(producto.getTitulo());
                precio.setText(getString(R.string.precio, precioFormateado));
            }
        });

        /*
        if(idProducto != -1){
            producto.setId(idProducto);
            producto.setTitulo(intent.getStringExtra("titulo"));
            producto.setUrlFoto(intent.getStringExtra("urlFoto"));
            producto.setCantidad(intent.getIntExtra("cantidad", -1));
            producto.setCantidadVendida(intent.getIntExtra("cantidadVendida", -1));

            String precioFormateado = String.format("%.2f", producto.getPrecio());
            try {
                producto.setPrecio(Double.parseDouble(precioFormateado));
            } catch (NumberFormatException e){
                precioFormateado = precioFormateado.replace(",", ".");
                producto.setPrecio(Double.parseDouble(precioFormateado));
            }


            if(producto.getUrlFoto() != null && !producto.getUrlFoto().isEmpty()) {
                //Mostramos la imagen utilizando la libreria glide para facilitar el manejo de imagenes.
                Glide.with(this)
                        .load(producto.getUrlFoto())
                        .into(imagen);
            } else {
                Glide.with(this)
                        .load(R.drawable.baseline_no_photography_24)
                        .into(imagen);
            }

            titulo.setText(producto.getTitulo());
            precio.setText(getString(R.string.precio, precioFormateado));
        /**
            //Formateamos el precio para que solo tenga dos decimales
            precio.setText(getString(R.string.precio, precioFormateado));
        } else {
            Log.d("Error productos", "Error al obtener el producto");
        }

         */


        //Verificamos si ya teniamos anteriormente el producto añadido en favoritos para asi iniciar el icono
        //corazon en la posicion correcta (activado o inactivado)
        conexionDB.verificarProductoFavorito(idProducto, new ConexionDB.OnVerificarProductoFavoritoListener() {
            @Override
            public void OnVerificarProductoFavorito(boolean esFavorito) {
                botonFav.setChecked(esFavorito);
            }
        });



        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidadInt = Integer.parseInt(cantidadProducto.getText().toString());
                if(cantidadInt != 0){
                    cantidadInt--;
                    String nuevaCantidad = String.valueOf(cantidadInt);
                    cantidadProducto.setText(nuevaCantidad);
                }
            }
        });

        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidadInt = Integer.parseInt(cantidadProducto.getText().toString());
                if(cantidadInt < 10){
                    cantidadInt++;
                    String nuevaCantidad = String.valueOf(cantidadInt);
                    cantidadProducto.setText(nuevaCantidad);
                }
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int comprarCantidadProducto = Integer.parseInt(cantidadProducto.getText().toString());

                if (comprarCantidadProducto <= 0) {
                    Toast.makeText(DetallesProductoActivity.this, "Selecciona al menos un producto", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(comprarCantidadProducto > 0){
                    productoCarrito.setTitulo(producto.getTitulo());
                    productoCarrito.setId(producto.getId());
                    productoCarrito.setPrecio(producto.getPrecio());
                    productoCarrito.setCantidadComprada(comprarCantidadProducto);

                    conexionDB.anadirACarrito(productoCarrito, new ConexionDB.OnProductoAnadidoACarritoListener() {
                        @Override
                        public void OnProductoAnadidoACarrito() {
                            Toast.makeText(DetallesProductoActivity.this, getText(R.string.anadido_al_carrito), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        botonFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int idProducto = intent.getIntExtra("id", -1);
                String tituloProducto = intent.getStringExtra("titulo");
                String urlProducto = intent.getStringExtra("urlFoto");
                String categoria = intent.getStringExtra("categoria");


                String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

                if (userId == null) {
                    Toast.makeText(DetallesProductoActivity.this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
                    return;
                }

                Producto productoFavorito = new Producto(idProducto, 0, 0, tituloProducto, urlProducto, categoria, 0.0);

                //Añadimos o eliminamos de la tabla favoritos en la base de datos

                if (isChecked) {
                    conexionDB.anadirFavoritos(productoFavorito, new ConexionDB.OnFavoritoAnadidoListener() {
                        @Override
                        public void OnFavoritoAnadido() {
                            Toast.makeText(DetallesProductoActivity.this, getString(R.string.anadido_favoritos), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    conexionDB.eliminarFavorito(productoFavorito, new ConexionDB.OnFavoritoEliminadoListener() {
                        @Override
                        public void OnFavoritoEliminado() {
                            Toast.makeText(DetallesProductoActivity.this, getString(R.string.eliminado_favoritos), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}