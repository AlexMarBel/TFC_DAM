package com.example.tfc_amb.AdminPanel;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EditarCategoriaActivity extends AppCompatActivity {

    private AppCompatButton btnModificar, btnEliminar, btnVolver;
    private EditText editTextIdentificador, editTextNombre, editTextURL;
    private Spinner spinnerCategorias;
    private ArrayAdapter<String> adaptadorSpinner;
    private List<String> titulosCategorias;
    private ArrayList<Categorias> listaCategorias;
    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_categoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVolver = findViewById(R.id.buttonVolver);
        btnModificar = findViewById(R.id.buttonCrearEditarProducto);
        btnEliminar = findViewById(R.id.buttonEliminarModificarProducto);
        spinnerCategorias = findViewById(R.id.spinner);
        editTextIdentificador = findViewById(R.id.editTextEmail);
        editTextNombre = findViewById(R.id.editTextTitulo);
        editTextURL = findViewById(R.id.editTextUrlFoto);
        editTextIdentificador.setEnabled(false);


        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();

        listaCategorias = new ArrayList<Categorias>();
        titulosCategorias = new ArrayList<>();
        adaptadorSpinner = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, titulosCategorias);
        adaptadorSpinner.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adaptadorSpinner);

        //android.R.layout.simple_spinner_item
        //androidx.appcompat.R.layout.support_simple_spinner_dropdown_item

        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, this);
        conexionDB.obtenerCategorias(new ConexionDB.OnCategoriasObtenidasListener() {
            @Override
            public void onCategoriasObtenidas(List<Categorias> listaCategoriasDB) {
                titulosCategorias.clear();
                listaCategorias.clear();

                listaCategorias.addAll(listaCategoriasDB);

                for(Categorias categoria : listaCategoriasDB){
                    String tituloFormateado = StringUtils.capitalize(categoria.getTitulo());
                    titulosCategorias.add(tituloFormateado);
                }

                adaptadorSpinner.notifyDataSetChanged();
            }
        });



        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoriaSeleccionada = titulosCategorias.get(position).toLowerCase();

                for (Categorias categoria : listaCategorias){
                    if(categoriaSeleccionada.matches(categoria.getTitulo())){
                        editTextIdentificador.setText(String.valueOf(categoria.getId()));
                        editTextNombre.setText(categoria.getTitulo());
                        editTextURL.setText(categoria.getUrlFoto());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idString = editTextIdentificador.getText().toString();
                String titulo = editTextNombre.getText().toString();
                String url = editTextURL.getText().toString();

                if(!idString.isEmpty() || !url.isEmpty()){
                    conexionDB.actualizarCategoria(idString, titulo, url);
                    Toast.makeText(EditarCategoriaActivity.this, getString(R.string.categoria_actualizada), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idString = editTextIdentificador.getText().toString();
                String titulo = editTextNombre.getText().toString().toLowerCase();
                if(!idString.isEmpty()){
                    dialogoBorrar(idString, titulo);
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

    private void dialogoBorrar(String id, String titulo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarCategoriaActivity.this);
        builder.setTitle(getString(R.string.confirmar_eliminar_categoria));
        builder.setMessage(R.string.pregunta_eliminar_categoria);
        builder.setPositiveButton(R.string.boton_si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, EditarCategoriaActivity.this);
                conexionDB.eliminarCategoria(id, titulo);

                Toast.makeText(EditarCategoriaActivity.this, getString(R.string.categoria_eliminada), Toast.LENGTH_SHORT).show();
                editTextIdentificador.setText("");
                editTextNombre.setText("");
                editTextURL.setText("");

            }
        });
        builder.setNegativeButton(R.string.boton_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}