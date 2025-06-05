package com.example.tfc_amb.RegistroLogin;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc_amb.R;
import com.example.tfc_amb.Tienda.SplashActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class MainActivity extends AppCompatActivity {

    private EditText email,contrasena;
    private TextView textRegistrate, textRecuperaContrasena;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintlayoutMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnLogin = findViewById(R.id.buttonInicioSesion);
        textRegistrate = findViewById(R.id.textRegistrate);
        textRecuperaContrasena = findViewById(R.id.textRecuperarContrasena);

        email = findViewById(R.id.editTextEmail);
        contrasena = findViewById(R.id.editTextContrasena);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuarioEmail = email.getText().toString();
                String usuarioContrasena = contrasena.getText().toString();

                if (usuarioEmail.isEmpty()){
                    Toast.makeText(MainActivity.this, getString(R.string.error_email_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usuarioContrasena.isEmpty()){
                    Toast.makeText(MainActivity.this, getString(R.string.error_contrasena_vacio), Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(usuarioEmail, usuarioContrasena)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_iniciar_sesion), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        textRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        textRecuperaContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

     @Override
     public void onStart() {
         super.onStart();
         FirebaseUser currentUser = mAuth.getCurrentUser();
         if (currentUser != null && usuarioConectado()) {
             Intent intent = new Intent(MainActivity.this, SplashActivity.class);
             startActivity(intent);
         }
     }

     private boolean usuarioConectado(){
         ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
         if (connectivityManager != null){
             NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
             NetworkCapabilities networkCapabilities= connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
             if(networkCapabilities == null) {
                 Toast.makeText(MainActivity.this, getString(R.string.error_sin_conexion), Toast.LENGTH_SHORT).show();
                 return false;
             }
             if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                 return true;
             }
             if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                 return true;
             }
             return networkInfo != null && networkInfo.isConnected();
         }else{
             Toast.makeText(MainActivity.this, getString(R.string.error_sin_conexion), Toast.LENGTH_SHORT).show();
             return false;
         }
     }
}