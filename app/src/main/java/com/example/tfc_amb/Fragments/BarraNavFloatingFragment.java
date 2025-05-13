package com.example.tfc_amb.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfc_amb.Tienda.CarritoActivity;
import com.example.tfc_amb.Tienda.FavoritoActivity;
import com.example.tfc_amb.RegistroLogin.MainActivity;
import com.example.tfc_amb.R;
import com.example.tfc_amb.Tienda.TiendaActivity;
import com.example.tfc_amb.UsuarioPanel.UsuarioActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarraNavFloatingFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarraNavFloatingFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;

    public BarraNavFloatingFragment (){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barra_nav_floating, container, false);
        bottomNavigationView = view.findViewById(R.id.bottom_navigationView);
        floatingActionButton = view.findViewById(R.id.floatingButton);

        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.btn_home){
                    Intent intent = new Intent (getActivity(), TiendaActivity.class);
                    startActivity(intent);
                    return true;
                }
                if(item.getItemId() == R.id.btn_cuenta){
                    Intent intent = new Intent (getActivity(), UsuarioActivity.class);
                    startActivity(intent);
                    return true;
                }
                /**
                if(item.getItemId() == R.id.bottom_carrito){
                    Intent intent = new Intent (getActivity(), CarritoActivity.class);
                    startActivity(intent);
                    return true;
                }
                 */
                if(item.getItemId() == R.id.btn_favoritos){
                    Intent intent = new Intent (getActivity(), FavoritoActivity.class);
                    startActivity(intent);
                    return true;
                }
                if(item.getItemId() == R.id.btn_salir){
                    dialogoCerrarSesion();
                    return true;
                }
                return false;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getActivity(), CarritoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    //Ventana de confirmacion para cerrar sesion
    private void dialogoCerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.cerrar_sesion));
        builder.setMessage(R.string.pregunta_salir);
        builder.setPositiveButton(R.string.boton_si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
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