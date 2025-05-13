package com.example.tfc_amb.Tienda;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Recyclers.FavoritosAdapter;
import com.example.tfc_amb.Modelos.Producto;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

public class FavoritoActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFavoritos;
    private FavoritosAdapter adaptadorFavoritos;
    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;
    private List<Producto> listaProductos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorito);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();

        recyclerViewFavoritos = findViewById(R.id.recyclerViewFavoritos);

        recyclerViewFavoritos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewFavoritos.setHasFixedSize(true);

        listaProductos = new ArrayList<Producto>();
        adaptadorFavoritos = new FavoritosAdapter(listaProductos, this);
        recyclerViewFavoritos.setAdapter(adaptadorFavoritos);


        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, FavoritoActivity.this);
        conexionDB.cargarFavoritos(userID, new ConexionDB.OnFavoritosCargadosListener() {
            @Override
            public void OnFavoritosCargados(List<Producto> listaProductosObtenidos) {
                listaProductos.clear();
                listaProductos.addAll(listaProductosObtenidos);
                adaptadorFavoritos.notifyDataSetChanged();
            }

            @Override
            public void onProductoEliminado(String idProducto) {
                eliminarProductoDeLista(idProducto);
                adaptadorFavoritos.notifyDataSetChanged();
            }
        });


    }

    private void eliminarProductoDeLista(String idProducto) {
        for (int i = 0; i < listaProductos.size(); i++) {
            if (listaProductos.get(i).getId() == Integer.parseInt(idProducto)) {
                listaProductos.remove(i);
                break;
            }
        }
    }

}