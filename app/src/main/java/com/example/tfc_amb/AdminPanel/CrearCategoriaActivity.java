package com.example.tfc_amb.AdminPanel;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.Categorias;
import com.example.tfc_amb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CrearCategoriaActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;

    private AppCompatButton btnCrear, btnVolver;
    private EditText editTextIdentificador, editTextNombre, editTextURL;
    private ArrayList<Categorias> listaCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_categoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCrear = findViewById(R.id.buttonCrearEditarProducto);
        btnVolver = findViewById(R.id.buttonEliminarModificarProducto);
        editTextIdentificador = findViewById(R.id.editTextEmail);
        editTextNombre = findViewById(R.id.editTextTitulo);
        editTextURL = findViewById(R.id.editTextUrlFoto);


        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();


        listaCategorias = new ArrayList<Categorias>();
        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, CrearCategoriaActivity.this);
        conexionDB.obtenerCategorias(new ConexionDB.OnCategoriasObtenidasListener() {
            @Override
            public void onCategoriasObtenidas(List<Categorias> listaCategoriasDB) {
                listaCategorias.clear();
                listaCategorias.addAll(listaCategoriasDB);
            }
        });


        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean existe = false;
                String titulo = editTextNombre.getText().toString();
                String idString = editTextIdentificador.getText().toString();
                String urlFoto = editTextURL.getText().toString();

                Categorias categoria = new Categorias();
                if (!titulo.isEmpty() || !idString.isEmpty() || !urlFoto.isEmpty()){
                    categoria.setId(Integer.parseInt(idString));
                    categoria.setTitulo(titulo.toLowerCase());
                    categoria.setUrlFoto(urlFoto);

                } else {
                    Toast.makeText(CrearCategoriaActivity.this, getString(R.string.error_datos_crear_categoria), Toast.LENGTH_SHORT).show();
                }

                if(categoria != null) {
                    for (Categorias categoriasLista : listaCategorias) {
                        if (categoriasLista.getId() == categoria.getId()) {
                            Toast.makeText(CrearCategoriaActivity.this, getString(R.string.id_existe, categoriasLista.getTitulo()), Toast.LENGTH_SHORT).show();
                            existe = true;
                        }
                    }

                    if(!existe){
                        conexionDB.crearCategoria(categoria);
                        editTextIdentificador.setText("");
                        editTextNombre.setText("");
                        editTextURL.setText("");
                    }
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
}