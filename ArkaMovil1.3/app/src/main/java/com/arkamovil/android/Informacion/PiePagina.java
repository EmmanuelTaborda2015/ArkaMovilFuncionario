package com.arkamovil.android.Informacion;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arkamovil.android.R;


public class PiePagina extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fm_piepagina, container, false);

        TextView fecha = (TextView)rootView.findViewById(R.id.fecha_pie);

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        fecha.setText(today.year + "/" + (today.month+1) + "/" + today.monthDay);

        return rootView;
    }

}