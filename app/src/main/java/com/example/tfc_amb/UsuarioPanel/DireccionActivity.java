package com.example.tfc_amb.UsuarioPanel;

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
import com.example.tfc_amb.Modelos.Direccion;
import com.example.tfc_amb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DireccionActivity extends AppCompatActivity {

    private AppCompatButton btnGuardar, btnVolver;
    private EditText editTextCalle, editTextPortal, editTextPiso, editTextPuerta, editTextCiudad, editTextCP;
    private String calle, portal, piso, puerta, ciudad, codigoPostal, userID;
    private Direccion direccionUsuario;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_direccion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextCalle = findViewById(R.id.textViewCalleValor);
        editTextPortal = findViewById(R.id.textViewPortalValor);
        editTextPiso = findViewById(R.id.textViewPisoValor);
        editTextPuerta = findViewById(R.id.textViewPuertaValor);
        editTextCiudad = findViewById(R.id.textViewCiudadValor);
        editTextCP = findViewById(R.id.textViewCodigoPostalValor);

        btnGuardar = findViewById(R.id.buttonDireccionGuardar);
        btnVolver = findViewById(R.id.buttonDireccionVolver);

        FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        ConexionDB conexionDB = new ConexionDB(userID, db, mAuth, this);
        conexionDB.cargarDireccion(new ConexionDB.OnDireccionCargadaListener() {
            @Override
            public void onDireccionCargada(Direccion direccion) {
                editTextCalle.setText(direccion.getCalle());
                editTextPortal.setText(direccion.getPortal());
                editTextPiso.setText(direccion.getPiso());
                editTextPuerta.setText(direccion.getPuerta());
                editTextCiudad.setText(direccion.getCiudad());
                editTextCP.setText(direccion.getCodigoPostal());
            }
        });

        //cargarDireccion();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calle = editTextCalle.getText().toString();
                portal = editTextPortal.getText().toString();
                piso = editTextPiso.getText().toString();
                puerta = editTextPuerta.getText().toString();
                ciudad = editTextCiudad.getText().toString();
                codigoPostal = editTextCP.getText().toString();

                if(calle.isEmpty()){
                    Toast.makeText(DireccionActivity.this, getString(R.string.error_calle_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(portal.isEmpty()){
                    Toast.makeText(DireccionActivity.this, getString(R.string.error_portal_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(piso.isEmpty()){
                    Toast.makeText(DireccionActivity.this, getString(R.string.error_piso_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(puerta.isEmpty()){
                    Toast.makeText(DireccionActivity.this, getString(R.string.error_puerta_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(ciudad.isEmpty()){
                    Toast.makeText(DireccionActivity.this, getString(R.string.error_ciudad_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(codigoPostal.isEmpty()){
                    Toast.makeText(DireccionActivity.this, getString(R.string.error_codigo_postal_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                Direccion direccionUsuario = new Direccion(calle, portal, piso, puerta, ciudad, codigoPostal);
                conexionDB.guardarDireccion(direccionUsuario);
                Toast.makeText(DireccionActivity.this, getText(R.string.direccion_registrada), Toast.LENGTH_SHORT).show();
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