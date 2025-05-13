package com.example.tfc_amb.AdminPanel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc_amb.R;

public class AdminActivity extends AppCompatActivity {

    private AppCompatButton btnCrearCategoria, btnCrearProducto, btnEditarCategoria,
            btnEditarProducto, btnAdministrarCompras, btnVolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCrearCategoria = findViewById(R.id.buttonCrearCategoria);
        btnCrearProducto = findViewById(R.id.buttonCrearProducto);
        btnEditarCategoria = findViewById(R.id.buttonModificarCategoria);
        btnEditarProducto = findViewById(R.id.buttonModificarProducto);
        btnAdministrarCompras = findViewById(R.id.buttonAdministrarCompras);
        btnVolver = findViewById(R.id.buttonVolver);


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btnCrearCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, CrearCategoriaActivity.class);
                startActivity(intent);
            }
        });

        btnEditarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, EditarCategoriaActivity.class);
                startActivity(intent);
            }
        });

        btnEditarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, EditarProductoActivity.class);
                startActivity(intent);
            }
        });

        btnCrearProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, CrearProductoActivity.class);
                startActivity(intent);
            }
        });

        btnAdministrarCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdministrarComprasActivity.class);
                startActivity(intent);
            }
        });
    }
}