package com.arkamovil.android.herramientas;

import android.view.View;
import android.widget.AutoCompleteTextView;

// Clase que permite desplegar los items de un control Autocpmplete.
public class Despliegue {

    public Despliegue(final AutoCompleteTextView control){
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control.showDropDown();
            }
        });
    }
}
