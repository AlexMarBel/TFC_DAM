package com.example.tfc_amb.UsuarioPanel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc_amb.AdminPanel.AdminActivity;
import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.Usuario;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Tienda.TiendaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsuarioActivity extends AppCompatActivity {
    private Button btnSalir, btnDatos, btnDireccion, btnCompras, btnAdministrar;
    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;
    private TextView texViewNombre, textViewEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();
        
        texViewNombre = findViewById(R.id.textViewPerfilUsuarioNombre);
        textViewEmail = findViewById(R.id.textViewPerfilUsuarioEmail);
        btnSalir = findViewById(R.id.buttonEliminarCategoria);
        btnDatos = findViewById(R.id.buttonCrearCategoria);
        btnDireccion = findViewById(R.id.buttonCrearProducto);
        btnCompras = findViewById(R.id.buttonModificarCategoria);
        btnAdministrar = findViewById(R.id.buttonModificarProducto);

        btnAdministrar.setVisibility(View.INVISIBLE);



        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, this);
        conexionDB.cargarUsuario(new ConexionDB.OnUsuarioCargadoListener() {
            @Override
            public void onUsuarioCargado(Usuario usuario) {
                texViewNombre.setText(usuario.getNombre());
                textViewEmail.setText(usuario.getEmail());

                if(usuario.isAdmin()){
                    btnAdministrar.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsuarioActivity.this, TiendaActivity.class);
                startActivity(intent);
            }
        });

        btnDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsuarioActivity.this, DatosActivity.class);
                startActivity(intent);
            }
        });

        btnDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsuarioActivity.this, DireccionActivity.class);
                startActivity(intent);
            }
        });

        btnCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsuarioActivity.this, MisComprasActivity.class);
                startActivity(intent);
            }
        });

        btnAdministrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsuarioActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
    }
}