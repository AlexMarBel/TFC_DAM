package com.example.tfc_amb.AdminPanel;

import android.content.DialogInterface;
import android.os.Bundle;
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

public class EditarProductoActivity extends AppCompatActivity {
    private AppCompatButton btnModificar, btnEliminar, btnVolver;
    private EditText editTextIdentificador, editTextNombre, editTextURL, editTextPrecio, editTextCantidad, editTextCantidadVendida ;
    private Spinner spinnerCategorias, spinnerProductos;
    private ArrayAdapter<String> adaptadorSpinnerCategorias, adaptadorSpinnerProductos;
    private List<String> titulosCategorias, titulosProductos;
    private ArrayList<Categorias> listaCategorias;
    private ArrayList<Producto> listaProductos;
    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;
    private String categoriaID, categoriaTitulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVolver = findViewById(R.id.buttonVolver);
        btnModificar = findViewById(R.id.buttonCrearEditarProducto);
        btnEliminar = findViewById(R.id.buttonEliminarModificarProducto);
        spinnerCategorias = findViewById(R.id.spinnerCategoria);
        spinnerProductos = findViewById(R.id.spinnerProducto);
        editTextIdentificador = findViewById(R.id.editTextEmail);
        editTextNombre = findViewById(R.id.editTextTitulo);
        editTextURL = findViewById(R.id.editTextUrlFoto);
        editTextPrecio = findViewById(R.id.editTextPrecio);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        editTextCantidadVendida = findViewById(R.id.editTextCantidadVendida);
        editTextIdentificador.setEnabled(false);


        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();

        listaProductos = new ArrayList<Producto>();
        listaCategorias = new ArrayList<Categorias>();
        titulosCategorias = new ArrayList<>();
        titulosProductos = new ArrayList<>();

        adaptadorSpinnerCategorias = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, titulosCategorias);
        adaptadorSpinnerCategorias.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adaptadorSpinnerCategorias);

        adaptadorSpinnerProductos = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, titulosProductos);
        adaptadorSpinnerProductos.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerProductos.setAdapter(adaptadorSpinnerProductos);

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

                adaptadorSpinnerCategorias.notifyDataSetChanged();
            }
        });


        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoriaSeleccionada = titulosCategorias.get(position).toLowerCase();

                listaProductos.clear();
                titulosProductos.clear();

                editTextIdentificador.setText("");
                editTextNombre.setText("");
                editTextURL.setText("");
                editTextCantidad.setText("");
                editTextPrecio.setText("");
                editTextCantidadVendida.setText("");


                for (Categorias categoria : listaCategorias){
                    if(categoriaSeleccionada.matches(categoria.getTitulo())) {
                        categoriaID = String.valueOf(categoria.getId());
                        categoriaTitulo = categoria.getTitulo();

                        conexionDB.obtenerProductosPorCategoria(categoriaTitulo, new ConexionDB.OnObtenerProductosDeCategoriaListener() {
                            @Override
                            public void OnObtenerProductosDeCategoria(List<Producto> listaProductosDB) {
                                listaProductos.clear();
                                listaProductos.addAll(listaProductosDB);

                                for (Producto producto : listaProductos){
                                    String tituloFormateado = StringUtils.capitalize(producto.getTitulo());
                                    titulosProductos.add(tituloFormateado);
                                }

                                adaptadorSpinnerProductos.notifyDataSetChanged();


                                //Para que no aparezcan los datos en blanco tras la seleccion de una categoria forzamos
                                //que se muestren los datos del primer producto de esa categoria
                                if (!listaProductos.isEmpty()) {
                                    Producto primerProducto = listaProductos.get(0);
                                    editTextIdentificador.setText(String.valueOf(primerProducto.getId()));
                                    editTextNombre.setText(StringUtils.capitalize(primerProducto.getTitulo()));
                                    editTextURL.setText(primerProducto.getUrlFoto());
                                    editTextCantidad.setText(String.valueOf(primerProducto.getCantidad()));
                                    editTextPrecio.setText(String.valueOf(primerProducto.getPrecio()));
                                    editTextCantidadVendida.setText(String.valueOf(primerProducto.getCantidadVendida()));
                                }
                            }
                        });
                    }
                }

                //Forzamos que muestre los datos del primer producto, ya que si no no funciona correctamente
                //la seleccion del primer elemento de la lista
                /**
                if (!listaProductos.isEmpty()) {
                    Producto primerProducto = listaProductos.get(0);
                    editTextIdentificador.setText(String.valueOf(primerProducto.getId()));
                    editTextNombre.setText(primerProducto.getTitulo());
                    editTextURL.setText(primerProducto.getUrlFoto());
                    editTextCantidad.setText(String.valueOf(primerProducto.getCantidad()));
                    editTextPrecio.setText(String.valueOf(primerProducto.getPrecio()));
                    editTextCantidadVendida.setText(String.valueOf(primerProducto.getCantidadVendida()));
                }
                 */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                editTextIdentificador.setText("");
                editTextNombre.setText("");
                editTextURL.setText("");
                editTextCantidad.setText("");
                editTextPrecio.setText("");
                editTextCantidadVendida.setText("");

            }
        });

        spinnerProductos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String productoSeleccionado = titulosProductos.get(position);

                for (Producto producto : listaProductos) {
                    if (productoSeleccionado.matches(producto.getTitulo())) {
                        editTextIdentificador.setText(String.valueOf(producto.getId()));
                        editTextNombre.setText(StringUtils.capitalize(producto.getTitulo()));
                        editTextURL.setText(producto.getUrlFoto());
                        editTextCantidad.setText(String.valueOf(producto.getCantidad()));
                        editTextPrecio.setText(String.valueOf(producto.getPrecio()));
                        editTextCantidadVendida.setText(String.valueOf(producto.getCantidadVendida()));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editTextIdentificador.setText("");
                editTextNombre.setText("");
                editTextURL.setText("");
                editTextCantidad.setText("");
                editTextPrecio.setText("");
                editTextCantidadVendida.setText("");
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoBorrar(editTextIdentificador.getText().toString());
            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idString = editTextIdentificador.getText().toString();
                String titulo = editTextNombre.getText().toString().toLowerCase();
                String url = editTextURL.getText().toString();
                String precioString = editTextPrecio.getText().toString();
                String cantidadString = editTextCantidad.getText().toString();
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
                    conexionDB.actualizarProducto(producto);
                }else{
                    Toast.makeText(EditarProductoActivity.this, getString(R.string.error_datos_crear_categoria), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dialogoBorrar(String productoID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarProductoActivity.this);
        builder.setTitle(getString(R.string.confirmar_eliminar_producto));
        builder.setMessage(R.string.pregunta_eliminar_producto);
        builder.setPositiveButton(R.string.boton_si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, EditarProductoActivity.this);
                conexionDB.eliminarProductoPorId(productoID);
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