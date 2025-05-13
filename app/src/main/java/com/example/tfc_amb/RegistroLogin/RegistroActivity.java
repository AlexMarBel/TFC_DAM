package com.example.tfc_amb.RegistroLogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Patterns;

import com.example.tfc_amb.Modelos.Usuario;
import com.example.tfc_amb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;

public class RegistroActivity extends AppCompatActivity {

    private EditText nombre, apellidos, email, contrasena, confirmarContrasena;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.butonMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        nombre = findViewById(R.id.editTextNombre);
        apellidos = findViewById(R.id.editTextApellidos);
        email = findViewById(R.id.editTextEmail);
        contrasena = findViewById(R.id.editTextContrasena);
        confirmarContrasena = findViewById(R.id.editTextContrasena2);

        Button btnRegistro = findViewById(R.id.buttonRecuperar);
        TextView textInicioSesion = findViewById(R.id.textIniciaSesion);

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usuarioNombre = nombre.getText().toString();
                String usuarioApellidos = apellidos.getText().toString();
                String usuarioEmail = email.getText().toString();
                String usuarioContrasena = contrasena.getText().toString();
                String usuarioConfirmarContrasena = confirmarContrasena.getText().toString();

                if(!usuarioContrasena.equals(usuarioConfirmarContrasena)){
                    Toast.makeText(RegistroActivity.this, getString(R.string.error_contrasena_no_iguales), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usuarioNombre.isEmpty()){
                    Toast.makeText(RegistroActivity.this, getString(R.string.error_nombre_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usuarioNombre.length() < 6 || usuarioNombre.length() > 20) {
                    Toast.makeText(RegistroActivity.this, getString(R.string.error_nombre_composicion), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usuarioApellidos.isEmpty()){
                    Toast.makeText(RegistroActivity.this, getString(R.string.error_apellidos_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usuarioApellidos.length() < 6 || usuarioApellidos.length() > 30) {
                    Toast.makeText(RegistroActivity.this, getString(R.string.error_apellidos_composicion), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usuarioEmail.isEmpty()){
                    Toast.makeText(RegistroActivity.this, getString(R.string.error_email_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(usuarioEmail).matches()){
                    Toast.makeText(RegistroActivity.this, getString(R.string.error_email_composicion), Toast.LENGTH_SHORT).show();
                    return;
                }


                if (usuarioContrasena.isEmpty()){
                    Toast.makeText(RegistroActivity.this, getString(R.string.error_contrasena_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validarContrasena(usuarioContrasena)) {
                    Toast.makeText(RegistroActivity.this, getString(R.string.error_contrasena_composicion), Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("usuario creado", "usuaario creado");
                //Creamos la cuenta en firebase
                mAuth.createUserWithEmailAndPassword(usuarioEmail, usuarioContrasena)
                        .addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //Obtenemos el id de firestore para este usuario
                                    String userId = mAuth.getCurrentUser().getUid();

                                    //Guardamos los nombres y apellidos del usuario creado
                                    String usuarioEmailDB = mAuth.getCurrentUser().getEmail();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    // Creamos un objeto con los datos del usuario
                                    Usuario usuario =  new Usuario();
                                    usuario.setId(userId);
                                    usuario.setNombre(usuarioNombre);
                                    usuario.setApellidos(usuarioApellidos);
                                    usuario.setEmail(usuarioEmail);
                                    usuario.setAdmin(false);


                                    // Guardamos en Firestore en la tabla usuarios
                                    db.collection("usuarios").document(userId)
                                            .set(usuario)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(RegistroActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                                Log.d("Firestore", "usuario creado");
                                                Toast.makeText(RegistroActivity.this, getString(R.string.usuario_registrado), Toast.LENGTH_SHORT).show();

                                                //Cerramos la sesion del usuario para que tenga que autenticarse por lo menos la primera vez
                                                mAuth.signOut();
                                                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(RegistroActivity.this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                                                Log.e("Firestore", "Error al guardar datos del usuario: " + e.getMessage(), e);
                                            });

                                }else{
                                    // Capturamos el error que indica si ya hay un email en uso
                                    if (task.getException() != null && task.getException().getMessage().contains("email address is already in use")) {
                                        Toast.makeText(RegistroActivity.this, getString(R.string.error_email_en_uso), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegistroActivity.this, getString(R.string.error_registro), Toast.LENGTH_SHORT).show();
                                        Log.e("Firestore", "Error al guardar datos del usuario2)");
                                    }
                                }
                            }
                        });
            }
        });

        textInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //Metodo para validar una contraseña que contenga un numero, un caracter especial y entre 6 y 20 caracteres.

    private boolean validarContrasena(String contrasena) {
        // (?=.*[0-9]) Al menos un numero
        // (?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]) Al menos un caracter especial de esta lista
        // .{6,20} -> Entre 6 y 20 caracteres
        String patron = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,20}$";

        return contrasena.matches(patron);
    }
}