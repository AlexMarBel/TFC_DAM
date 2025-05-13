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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.Categorias;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Recyclers.CategoriasGrandeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoriasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategorias;
    private ArrayList<Categorias> listCategorias;
    private CategoriasGrandeAdapter adaptadorCategorias;
    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;
    private String tenemosCategorias, buscaCategorias;
    private TextView textViewtenemosCategorias;
    private EditText buscadorCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();

        textViewtenemosCategorias = findViewById(R.id.textViewNumCategorias);
        buscadorCategorias = findViewById(R.id.editTextBuscar);

        recyclerViewCategorias = findViewById(R.id.recyclerViewCategorias);
        recyclerViewCategorias.setHasFixedSize(true);
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        listCategorias = new ArrayList<Categorias>();
        adaptadorCategorias = new CategoriasGrandeAdapter(listCategorias, this);
        recyclerViewCategorias.setAdapter(adaptadorCategorias);

        //Configuramos este segundo reyclerview para que muestre los elementos en dos columnas verticales.
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 2 columnas
        recyclerViewCategorias.setLayoutManager(layoutManager);


        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, CategoriasActivity.this);
        conexionDB.obtenerCategorias(new ConexionDB.OnCategoriasObtenidasListener() {
            @Override
            public void onCategoriasObtenidas(List<Categorias> listaCategorias) {
                listCategorias.clear();
                listCategorias.addAll(listaCategorias);
                adaptadorCategorias.notifyDataSetChanged();


                tenemosCategorias = getString(R.string.tenemos_categorias, String.valueOf(listCategorias.size()));
                textViewtenemosCategorias.setText(tenemosCategorias);

                buscaCategorias = getString(R.string.buscar_categoria, String.valueOf(listCategorias.size()));
                buscadorCategorias.setHint(buscaCategorias);
            }
        });

        buscadorCategorias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Categorias> listaFiltrada = new ArrayList<>();

                for(Categorias categoria : listCategorias){
                    if(categoria.getTitulo().toLowerCase().contains(s.toString().toLowerCase())){
                        listaFiltrada.add(categoria);
                    }
                }

                if(!listaFiltrada.isEmpty()){
                    adaptadorCategorias = new CategoriasGrandeAdapter(listaFiltrada, CategoriasActivity.this);
                    recyclerViewCategorias.setAdapter(adaptadorCategorias);
                    adaptadorCategorias.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

}