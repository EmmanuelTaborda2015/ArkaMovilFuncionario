package com.arkamovil.android.borrar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.support.v4.app.Fragment;

import com.arkamovil.android.R;
import com.arkamovil.android.servicios_web.WS_Placa;

public class LeerPlaca extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fm_leer_placa, container, false);

        Button scanear = (Button) rootView.findViewById(R.id.escanear_c8);
        scanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.arkamovil.android.SCAN");
                startActivityForResult(intent, 0);
            }
        });

        return rootView;
    }

    //En esta función se llama a la libreria encargada de leer y obtener la información de los códigos de barras despues de que se ha generado el intent.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == getActivity().RESULT_OK) {
            String contenido = intent.getStringExtra("SCAN_RESULT");
            String formato = intent.getStringExtra("SCAN_RESULT_FORMAT");

            AutoCompleteTextView placa = (AutoCompleteTextView) rootView.findViewById(R.id.placa_c8);
            AutoCompleteTextView formatoplaca = (AutoCompleteTextView) rootView.findViewById(R.id.formato_placa_c8);

            placa.setText("Placa: " + contenido);
            formatoplaca.setText("Formato: " + formato);

        } else if (resultCode == getActivity().RESULT_CANCELED) {
            // Si se cancelo la captura.
        }

    }
}
