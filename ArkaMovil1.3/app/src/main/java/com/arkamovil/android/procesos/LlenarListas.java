package com.arkamovil.android.procesos;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import com.arkamovil.android.servicios_web.WS_Funcionario;

import java.util.ArrayList;
import java.util.List;

public class LlenarListas extends Fragment{

    public ArrayAdapter<String> llenarSpinner(Activity act,Spinner spin) {
        List<String> toSpin = new ArrayList<String>();
        toSpin.add("Seleccione una opción");
        toSpin.add("sede 45");
        toSpin.add("Sede Vivero");
        toSpin.add("Sede Macarena");
        toSpin.add("Sede Tecnologica");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,toSpin);
        spin.setAdapter(adapter);
        return  adapter;
    }

    public ArrayAdapter<String> llenarSpinnerEstado1(Activity act,Spinner spin) {
        List<String> toSpin = new ArrayList<String>();
        toSpin.add("--Seleccione una opción--");
        toSpin.add("Existente y Activo");
        toSpin.add("Sobrante");
        toSpin.add("Faltante por Hurto");
        toSpin.add("Faltante Dependencia");
        toSpin.add("Baja");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,toSpin);
        spin.setAdapter(adapter);
        return  adapter;
    }

    public ArrayAdapter<String> llenarSpinnerEstado2(Activity act,Spinner spin) {
        List<String> toSpin = new ArrayList<String>();
        toSpin.add("--Seleccione una opción--");
        toSpin.add("Existente y Activo");
        toSpin.add("Sobrante");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item,toSpin);
        spin.setAdapter(adapter);
        return  adapter;
    }

    public ArrayAdapter<String> llenarAutoComplete(Activity act,AutoCompleteTextView aut) {
        List<String> toSpin = new ArrayList<String>();

        toSpin.add("sede 40");
        toSpin.add("Sede Vivero");
        toSpin.add("Sede Macarena");
        toSpin.add("Sede Tecnologica");
        toSpin.add("Sede 2");
        toSpin.add("Sede 3");
        toSpin.add("Sede 4");
        toSpin.add("Sede 5");
        toSpin.add("Sede 6");
        toSpin.add("Sede 7");
        toSpin.add("Sede 8");
        toSpin.add("Sede 9");
        toSpin.add("Sede 10");
        toSpin.add("Sede 11");
        toSpin.add("Sede 12");
        toSpin.add("Sede 13");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_dropdown_item_1line);
        aut.setAdapter(adapter);
        return  adapter;
    }

    public ArrayAdapter<String> llenarMultiAutocomplate(Activity act,MultiAutoCompleteTextView aut) {
        List<String> toSpin = new ArrayList<String>();
        toSpin.add("sede 45");
        toSpin.add("Sede Vivero");
        toSpin.add("Sede Macarena");
        toSpin.add("Sede Tecnologica");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_dropdown_item_1line,toSpin);
        aut.setAdapter(adapter);
        aut.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        return  adapter;
    }
}
