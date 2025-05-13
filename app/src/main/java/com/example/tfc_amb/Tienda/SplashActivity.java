package com.example.tfc_amb.Tienda;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc_amb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    public static int tiempoCarga = 3000;
    TextView textViewBienvenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewBienvenido = findViewById(R.id.textViewBienvenida);

        //Obtenemos el email con el que se ha iniciado sesion
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String usuarioID = mAuth.getCurrentUser().getUid();

        //Obtenemos de la base de datos el nombre asociado al email
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(usuarioID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreUsuario = documentSnapshot.getString("nombre");
                        String textBienvenido = getString(R.string.bienvenido, nombreUsuario);
                        textViewBienvenido.setText(textBienvenido);
                    } else {
                        Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
                );


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, TiendaActivity.class);
                startActivity(intent);
            }
        }, tiempoCarga);
    }
}