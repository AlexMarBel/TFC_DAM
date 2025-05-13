package com.example.tfc_amb.UsuarioPanel;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.CompraRealizada;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Recyclers.MisComprasAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MisComprasActivity extends AppCompatActivity {
    private AppCompatButton btnVolver;
    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerViewCompras;
    private List<CompraRealizada> compraRealizadas;
    private MisComprasAdapter adaptadorComprasRealizadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_compras);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();

        btnVolver = findViewById(R.id.buttonVolver);

        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, MisComprasActivity.this);

        compraRealizadas = new ArrayList<CompraRealizada>();

        recyclerViewCompras = findViewById(R.id.recyclerViewCompras);
        recyclerViewCompras.setHasFixedSize(true);
        recyclerViewCompras.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adaptadorComprasRealizadas = new MisComprasAdapter(compraRealizadas, this);
        recyclerViewCompras.setAdapter(adaptadorComprasRealizadas);
        recyclerViewCompras.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        conexionDB.cargarComprasRealizadas(new ConexionDB.OnComprasRealizadasCargadasListener() {
            @Override
            public void OnComprasRealizadasCargadas(List<CompraRealizada> listaComprasRealizadas) {
                compraRealizadas.clear();
                compraRealizadas.addAll(listaComprasRealizadas);
                adaptadorComprasRealizadas.notifyDataSetChanged();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }
}