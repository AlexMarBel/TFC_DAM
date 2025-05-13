package com.example.tfc_amb.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tfc_amb.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BotonAtrasFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class BotonAtrasFragment extends Fragment {

    private ImageView btnAtras;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boton_atras, container, false);

        btnAtras = view.findViewById(R.id.imageViewFlecha);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });
        return view;
    }
}