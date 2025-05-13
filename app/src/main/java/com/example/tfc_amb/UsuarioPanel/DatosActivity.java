package com.example.tfc_amb.UsuarioPanel;

import android.os.Bundle;
import android.util.Patterns;
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
import com.example.tfc_amb.Modelos.Usuario;
import com.example.tfc_amb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatosActivity extends AppCompatActivity {
    private AppCompatButton btnVolver, btnGuardar;
    private EditText editTextNombre, editTextApellidos, editTextTelefono, editTextEmail;
    private String nombre, apellidos,telefono, email, userID;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellidos = findViewById(R.id.editTextApellidos);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        editTextEmail = findViewById(R.id.editTextEmail);

        btnGuardar = findViewById(R.id.buttonCrearEditarProducto);
        btnVolver = findViewById(R.id.buttonEliminarModificarProducto);

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, this);

        conexionDB.cargarUsuario(new ConexionDB.OnUsuarioCargadoListener() {
            @Override
            public void onUsuarioCargado(Usuario usuario) {
                editTextNombre.setText(usuario.getNombre());
                editTextApellidos.setText(usuario.getApellidos());
                editTextEmail.setText(usuario.getEmail());
                editTextTelefono.setText(usuario.getTelefono());
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = editTextNombre.getText().toString();
                apellidos = editTextApellidos.getText().toString();
                telefono = editTextTelefono.getText().toString();
                email = editTextEmail.getText().toString();


                if (nombre.isEmpty()){
                    Toast.makeText(DatosActivity.this, getString(R.string.error_nombre_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (nombre.length() < 6 || nombre.length() > 20) {
                    Toast.makeText(DatosActivity.this, getString(R.string.error_nombre_composicion), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (apellidos.isEmpty()){
                    Toast.makeText(DatosActivity.this, getString(R.string.error_apellidos_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (apellidos.length() < 6 || apellidos.length() > 30) {
                    Toast.makeText(DatosActivity.this, getString(R.string.error_apellidos_composicion), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty()){
                    Toast.makeText(DatosActivity.this, getString(R.string.error_email_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(DatosActivity.this, getString(R.string.error_email_composicion), Toast.LENGTH_SHORT).show();
                    return;
                }

                Usuario usuario = new Usuario();
                usuario.setNombre(nombre);
                usuario.setTelefono(telefono);
                usuario.setApellidos(apellidos);
                usuario.setId(userID);
                usuario.setEmail(email);
                conexionDB.guardarUsuario(usuario);

                Toast.makeText(DatosActivity.this, getText(R.string.datos_registrados), Toast.LENGTH_SHORT).show();
            }
        });
    }

}