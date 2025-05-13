package com.example.tfc_amb.AdminPanel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc_amb.ConexionDB;
import com.example.tfc_amb.Modelos.Categorias;
import com.example.tfc_amb.Modelos.Producto;
import com.example.tfc_amb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CrearProductoActivity extends AppCompatActivity {
    private AppCompatButton btnGuardar, btnVolver;
    private EditText editTextIdentificador, editTextNombre, editTextURL, editTextPrecio, editTextCantidad, editTextCantidadVendida ;
    private Spinner spinnerCategorias, spinnerProductos;
    private ArrayAdapter<String> adaptadorSpinnerCategorias, adaptadorSpinnerProductos;
    private List<String> titulosCategorias, titulosProductos;
    private ArrayList<Categorias> listaCategorias;
    private ArrayList<Producto> listaProductos;
    private FirebaseFirestore db;
    private String userID, categoriaTitulo;
    private FirebaseAuth mAuth;
    private String categoriaID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVolver = findViewById(R.id.buttonVolver);
        btnGuardar = findViewById(R.id.buttonCrearEditarProducto);
        spinnerCategorias = findViewById(R.id.spinnerCategoria);
        editTextIdentificador = findViewById(R.id.editTextEmail);
        editTextNombre = findViewById(R.id.editTextTitulo);
        editTextURL = findViewById(R.id.editTextUrlFoto);
        editTextPrecio = findViewById(R.id.editTextPrecio);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        editTextCantidadVendida = findViewById(R.id.editTextCantidadVendida);


        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();

        titulosCategorias = new ArrayList<>();
        listaCategorias = new ArrayList<Categorias>();
        adaptadorSpinnerCategorias = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, titulosCategorias);
        adaptadorSpinnerCategorias.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adaptadorSpinnerCategorias);

        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, this);
        conexionDB.obtenerCategorias(new ConexionDB.OnCategoriasObtenidasListener() {
            @Override
            public void onCategoriasObtenidas(List<Categorias> listaCategoriasDB) {
                titulosCategorias.clear();
                listaCategorias.clear();

                listaCategorias.addAll(listaCategoriasDB);

                for(Categorias categoria : listaCategoriasDB){
                    titulosCategorias.add(categoria.getTitulo());
                }

                adaptadorSpinnerCategorias.notifyDataSetChanged();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idString = editTextIdentificador.getText().toString();
                String titulo = editTextNombre.getText().toString();
                String url = editTextURL.getText().toString();
                String cantidadString = editTextCantidad.getText().toString();
                String precioString = editTextPrecio.getText().toString();
                String cantidadVendidaString = editTextCantidadVendida.getText().toString();

                if(!idString.isEmpty() && !titulo.isEmpty() && !url.isEmpty() && !precioString.isEmpty()
                        && !cantidadString.isEmpty() && !cantidadVendidaString.isEmpty()) {

                    //Antes de convertir el precio a double indicamos que si se ha introducido con "," en
                    //lugar de "." se reemplace, para que no de error al hacer la conversion.
                    precioString = precioString.replace(",", ".");
                    double precio = Double.parseDouble(precioString);
                    int cantidad = Integer.parseInt(cantidadString);
                    int cantidadVendida = Integer.parseInt(cantidadVendidaString);
                    int id = Integer.parseInt(idString);

                    Producto producto = new Producto(id, cantidad, cantidadVendida, titulo, url, categoriaTitulo, precio);

                    conexionDB.crearProducto(producto);

                    editTextIdentificador.setText("");
                    editTextNombre.setText("");
                    editTextURL.setText("");
                    editTextCantidad.setText("");
                    editTextPrecio.setText("");
                    editTextCantidadVendida.setText("");
                }else{
                    Toast.makeText(CrearProductoActivity.this, getString(R.string.error_datos_crear_categoria), Toast.LENGTH_SHORT).show();
                }

            }
        });

        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoriaSeleccionada = titulosCategorias.get(position);

                for (Categorias categoria : listaCategorias){
                    if(categoriaSeleccionada.matches(categoria.getTitulo())){
                        categoriaID = String.valueOf(categoria.getId());
                        categoriaTitulo = categoria.getTitulo();

                    }
                }
                Log.d("String", "Categoria seleccionada"+categoriaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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