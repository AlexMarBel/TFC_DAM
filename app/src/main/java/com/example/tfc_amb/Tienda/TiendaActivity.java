package com.example.tfc_amb.Tienda;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.Categorias;
import com.example.tfc_amb.Modelos.Direccion;
import com.example.tfc_amb.Modelos.Usuario;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Recyclers.CategoriasAdapter;
import com.example.tfc_amb.Modelos.Producto;
import com.example.tfc_amb.Recyclers.ProductosVendidosAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TiendaActivity extends AppCompatActivity {

    private ArrayList<Categorias> listaCategorias;
    private ArrayList<Producto> listaProductos, listaProductosOrdenada;

    private CategoriasAdapter adaptadorCategorias;
    private ProductosVendidosAdapter adaptadorProductosVendidos;

    private Button btnCategorias, btnProductos;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageSlider imageSlider;

    private RecyclerView recyclerViewCategorias, recyclerViewProductos;
    private TextView verCategorias, verProductos, textViewtenemosProductos;
    private AutoCompleteTextView buscadorProductos;
    private String tenemosProductos, buscaProducto, userID;
    private ArrayList<String> nombreProductos;
    private ArrayAdapter<String> adaptadorBuscador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();


        //FirestoreUploader uploader = new FirestoreUploader();
        //uploader.subirCategorias();

        //Direccion direccion = new Direccion("Marzo", "000", "3", "A", "Albacete", "02002");
        //Usuario usuario = new Usuario(userID, "Alejandro", "alexbq92@gmail.com", "Martin Belmonte", "662102718", false);
        //actualizarComprasConDireccionYUsuario(direccion, usuario);


        btnCategorias = findViewById(R.id.buttonCategorias);
        btnProductos = findViewById(R.id.buttonProductos);
        verCategorias = findViewById(R.id.textViewVerTodasCategorias);
        verProductos = findViewById(R.id.textViewVerTodosProductos);
        imageSlider = findViewById(R.id.imageSlider);
        recyclerViewCategorias = findViewById(R.id.recyclerViewCategoria);

        textViewtenemosProductos = findViewById(R.id.textViewNumCategorias);
        buscadorProductos = findViewById(R.id.editTextBuscar);

        recyclerViewCategorias.setHasFixedSize(true);
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //Configuramos el buscador
        nombreProductos = new ArrayList<>();
        adaptadorBuscador = new ArrayAdapter<>(TiendaActivity.this, android.R.layout.simple_dropdown_item_1line, nombreProductos);
        buscadorProductos.setAdapter(adaptadorBuscador);

        buscadorProductos.setThreshold(1);

        listaCategorias = new ArrayList<Categorias>();
        adaptadorCategorias = new CategoriasAdapter(listaCategorias, this);
        recyclerViewCategorias.setAdapter(adaptadorCategorias);


        recyclerViewProductos = findViewById(R.id.recyclerViewMasVendidos);
        recyclerViewProductos.setHasFixedSize(true);
        //Configuramos este segundo reyclerview para que muestre los elementos en dos columnas verticales.
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 2 columnas
        recyclerViewProductos.setLayoutManager(layoutManager);

        listaProductosOrdenada = new ArrayList<Producto>();
        listaProductos = new ArrayList<Producto>();
        adaptadorProductosVendidos = new ProductosVendidosAdapter(listaProductosOrdenada, this);
        recyclerViewProductos.setAdapter(adaptadorProductosVendidos);


        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, TiendaActivity.this);
        conexionDB.obtenerCategorias(new ConexionDB.OnCategoriasObtenidasListener() {
            @Override
            public void onCategoriasObtenidas(List<Categorias> listaCategoriasDB) {
                listaCategorias.clear();
                listaCategorias.addAll(listaCategoriasDB);

                adaptadorCategorias.notifyDataSetChanged();
            }
        });

        conexionDB.cargarProductos(new ConexionDB.OnCargarProductosListener() {
            @Override
            public void OnCargarProductos(List<Producto> listaProductosDB) {
                listaProductos.clear();
                listaProductos.addAll(listaProductosDB);

                //Actualizamos los textos que tienen en cuenta el numero de productos
                tenemosProductos = getString(R.string.tenemos_producto, String.valueOf(listaProductos.size()));
                textViewtenemosProductos.setText(tenemosProductos);

                buscaProducto = getString(R.string.buscar_producto, String.valueOf(listaProductos.size()));
                buscadorProductos.setHint(buscaProducto);

                nombreProductos.clear();
                for(Producto producto : listaProductos){
                    nombreProductos.add(producto.getTitulo());
                }

                adaptadorBuscador = new ArrayAdapter<>(TiendaActivity.this, android.R.layout.simple_dropdown_item_1line, nombreProductos);
                buscadorProductos.setAdapter(adaptadorBuscador);

                //Ordenamos los productos de la lista por el valor de cantidad mas vendida.
                Collections.sort(listaProductos, new Comparator<Producto>() {
                    @Override
                    public int compare(Producto producto1, Producto producto2) {
                        return Integer.compare(producto2.getCantidadVendida(), producto1.getCantidadVendida());
                    }
                });

                //Creamos la lista con los primeros 10 productos tras ordenarlos, de esta forma obtenemos los
                //productos mas vendidos.
                listaProductosOrdenada.clear();
                listaProductosOrdenada.addAll(listaProductos.subList(0, Math.min(10, listaProductos.size())));
                adaptadorProductosVendidos.notifyDataSetChanged();
            }
        });



        cargarImagenes();
        buscadorProductos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> listaFiltrada = new ArrayList<>();

                for(String nombreProducto : nombreProductos){
                    if(nombreProducto.toLowerCase().contains(s.toString().toLowerCase())){
                        listaFiltrada.add(nombreProducto);
                    }
                }

                adaptadorBuscador = new ArrayAdapter<>(TiendaActivity.this, android.R.layout.simple_dropdown_item_1line, listaFiltrada);
                buscadorProductos.setAdapter(adaptadorBuscador);
                adaptadorBuscador.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buscadorProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stringSeleccionado = String.valueOf(parent.getItemAtPosition(position));

                for (Producto producto : listaProductos){
                    if(producto.getTitulo().matches(stringSeleccionado)){
                        Intent intent = new Intent(TiendaActivity.this, DetallesProductoActivity.class);
                        intent.putExtra("id", producto.getId());
                        intent.putExtra("precio", producto.getPrecio());
                        intent.putExtra("urlFoto", producto.getUrlFoto());
                        intent.putExtra("titulo", producto.getTitulo());
                        intent.putExtra("cantidad", producto.getCantidad());
                        intent.putExtra("cantidadVendida", producto.getCantidadVendida());
                        startActivity(intent);
                    }
                }
            }
        });

        verCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TiendaActivity.this, CategoriasActivity.class);
                startActivity(intent);
            }
        });

        verProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TiendaActivity.this, ProductosActivity.class);
                startActivity(intent);
            }
        });

        btnCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TiendaActivity.this, CategoriasActivity.class);
                startActivity(intent);
            }
        });

        btnProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TiendaActivity.this, ProductosActivity.class);
                startActivity(intent);
            }
        });
    }


    //Implementacion del scroll lateral importado
    //https://www.youtube.com/watch?v=fiSWKebAZg8&t=138s
    private void cargarImagenes() {
        ArrayList<SlideModel> imagenesSlider = new ArrayList<>();

        imagenesSlider.add(new SlideModel(R.drawable.producto_tenemos, ScaleTypes.FIT));
        imagenesSlider.add(new SlideModel(R.drawable.mejores_precios, ScaleTypes.FIT));
        imagenesSlider.add(new SlideModel(R.drawable.envio_domicilio, ScaleTypes.FIT));
        imagenesSlider.add(new SlideModel(R.drawable.productos_frescos, ScaleTypes.FIT));

        imageSlider.setImageList(imagenesSlider, ScaleTypes.FIT);
    }

    public void actualizarComprasConDireccionYUsuario(Direccion nuevaDireccion, Usuario nuevoUsuario) {
        db.collection("compraRealizada").document(userID)
                .collection("compras")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot compraDoc : queryDocumentSnapshots) {
                            // Actualizamos los campos de dirección y usuario
                            compraDoc.getReference().update(
                                    "direccion", nuevaDireccion,
                                    "user", nuevoUsuario
                            ).addOnSuccessListener(unused -> {
                                Log.d("Firestore", "Compra actualizada correctamente: " + compraDoc.getId());
                            }).addOnFailureListener(e -> {
                                Log.e("Firestore", "Error al actualizar compra: " + compraDoc.getId(), e);
                            });
                        }
                    } else {
                        Log.d("Firestore", "No se encontraron compras para actualizar.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener compras para actualizar", e);
                });
    }
}