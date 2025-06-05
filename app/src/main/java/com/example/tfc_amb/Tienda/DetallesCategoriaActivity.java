package com.example.tfc_amb.Tienda;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.Producto;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Recyclers.ProductosVendidosAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DetallesCategoriaActivity extends AppCompatActivity {
    private ArrayList<Producto> listaProductos;
    private RecyclerView recyclerViewProductos;
    private ProductosVendidosAdapter adaptadorProductos;
    private TextView textViewTitulo, textViewNumProductos;
    private String tituloCategoria, numProductos, urlFoto;
    private int idCategoria;
    private ImageView imagen;

    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_categoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();


        textViewNumProductos = findViewById(R.id.textViewDetallesCategoriaNumProductos);
        textViewTitulo = findViewById(R.id.textViewDetallesProductoTitulo);
        imagen = findViewById(R.id.imageViewDetallesCategoria);

        Intent intent = getIntent();
        idCategoria = intent.getIntExtra("id", -1);
        tituloCategoria = intent.getStringExtra("titulo");
        urlFoto = intent.getStringExtra("urlFoto");

        tituloCategoria = getString(R.string.titulo_categoria, tituloCategoria);
        textViewTitulo.setText(StringUtils.capitalize(tituloCategoria));

        if(urlFoto != null && !urlFoto.isEmpty()) {
            //Mostramos la imagen utilizando la libreria glide para facilitar el manejo de imagenes.
            Glide.with(this)
                    .load(urlFoto)
                    .into(imagen);
        } else {
            Glide.with(this)
                    .load(R.drawable.baseline_no_photography_24)
                    .into(imagen);
        }

        recyclerViewProductos = findViewById(R.id.recyclerViewDetallesCategoriaProductos);
        recyclerViewProductos.setHasFixedSize(true);

        //Configuramos este segundo reyclerview para que muestre los elementos en dos columnas verticales.
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 2 columnas
        recyclerViewProductos.setLayoutManager(layoutManager);

        listaProductos = new ArrayList<Producto>();
        adaptadorProductos = new ProductosVendidosAdapter(listaProductos, this);
        recyclerViewProductos.setAdapter(adaptadorProductos);


        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, DetallesCategoriaActivity.this);
        conexionDB.obtenerProductosPorCategoria(tituloCategoria, new ConexionDB.OnObtenerProductosDeCategoriaListener() {
            @Override
            public void OnObtenerProductosDeCategoria(List<Producto> listaProductosDB) {
                listaProductos.clear();
                listaProductos.addAll(listaProductosDB);

                numProductos = getString(R.string.num_productos_categoria, String.valueOf(listaProductos.size()));
                textViewNumProductos.setText(numProductos);
                adaptadorProductos.notifyDataSetChanged();
            }
        });
    }
}