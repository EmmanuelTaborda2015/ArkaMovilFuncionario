package com.arkamovil.android.borrar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.arkamovil.android.R;

public class ConsultaRapida extends Fragment {

    private static View rootView;

    private AutoCompleteTextView doc_funcionario;

    private Button consultar;

    private ImageView bajar;
    private ImageView subir;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fm_consulta_cedula, container, false);


        doc_funcionario = (AutoCompleteTextView) rootView.findViewById(R.id.documento_c7);
        consultar = (Button) rootView.findViewById(R.id.consultar_c7);
        bajar = (ImageView) rootView.findViewById(R.id.bajar_c7);
        subir = (ImageView) rootView.findViewById(R.id.subir_c7);

        bajar.setVisibility(View.INVISIBLE);
        subir.setVisibility(View.INVISIBLE);

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultar.setEnabled(false);
                if (!"".equals(String.valueOf(doc_funcionario.getText()))) {

                    limpiarTabla();

                    WS_Elemento_funcionario elem = new WS_Elemento_funcionario();
                    elem.startWebAccess(rootView, getActivity(), String.valueOf(doc_funcionario.getText()), 4);

                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                } else {
                    Toast.makeText(getActivity(), "Por favor ingrese el documento del funcionario a consultar", Toast.LENGTH_LONG).show();
                    consultar.setEnabled(true);
                }
            }
        });
        //boton para bajar los elementos
        bajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TablaInventarioCedula baj = new TablaInventarioCedula();
                baj.bajar(rootView, getActivity());
            }
        });
        //llamar metodo en clase CrearTablas para ir hacia los primeros registros mostrados en la tabla
        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TablaInventarioCedula sub = new TablaInventarioCedula();
                sub.subir(rootView, getActivity());
            }
        });
        return rootView;
    }

    public void limpiarTabla() {

        TablaInventarioCedula borrar = new TablaInventarioCedula();
        borrar.borrarTabla(rootView, getActivity());
        bajar.setVisibility(View.INVISIBLE);
        subir.setVisibility(View.INVISIBLE);
    }

}