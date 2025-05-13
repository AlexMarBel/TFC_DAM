package com.example.tfc_amb.Tienda;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.Categorias;
import com.example.tfc_amb.Modelos.Producto;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Recyclers.ProductosVendidosAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductosActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ArrayList<Categorias> listCategorias;
    private ArrayList<Producto> listaProductos;

    private ProductosVendidosAdapter adaptadorProductosVendidos;
    private RecyclerView recyclerViewProductos;

    private TextView textViewtenemosProductos;
    private EditText buscadorProductos;
    private String tenemosProductos, buscaProducto;
    private FirebaseAuth mAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();


        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        buscadorProductos = findViewById(R.id.editTextBuscar);
        textViewtenemosProductos = findViewById(R.id.textViewNumCategorias);
        listCategorias = new ArrayList<Categorias>();

        recyclerViewProductos = findViewById(R.id.recyclerViewProductos);
        recyclerViewProductos.setHasFixedSize(true);

        //Configuramos este segundo reyclerview para que muestre los elementos en dos columnas verticales.
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 2 columnas
        recyclerViewProductos.setLayoutManager(layoutManager);

        listaProductos = new ArrayList<Producto>();
        adaptadorProductosVendidos = new ProductosVendidosAdapter(listaProductos, this);
        recyclerViewProductos.setAdapter(adaptadorProductosVendidos);

        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, ProductosActivity.this);
        conexionDB.cargarProductos(new ConexionDB.OnCargarProductosListener() {
            @Override
            public void OnCargarProductos(List<Producto> listaProductosDB) {
                listaProductos.clear();
                listaProductos.addAll(listaProductosDB);

                adaptadorProductosVendidos.notifyDataSetChanged();

                tenemosProductos = getString(R.string.tenemos_producto, String.valueOf(listaProductos.size()));
                textViewtenemosProductos.setText(tenemosProductos);

                buscaProducto = getString(R.string.buscar_producto, String.valueOf(listaProductos.size()));
                buscadorProductos.setHint(buscaProducto);
            }
        });

        buscadorProductos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Producto> listaFiltrada = new ArrayList<>();

                for(Producto producto : listaProductos){
                    if(producto.getTitulo().toLowerCase().contains(s.toString().toLowerCase())){
                        listaFiltrada.add(producto);
                    }
                }

                //Si la lista filtrada no esta vacia la muestra en el recycleviewer
                if(!listaFiltrada.isEmpty()){
                    adaptadorProductosVendidos = new ProductosVendidosAdapter((ArrayList<Producto>) listaFiltrada, ProductosActivity.this);
                    recyclerViewProductos.setAdapter(adaptadorProductosVendidos);
                    adaptadorProductosVendidos.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}