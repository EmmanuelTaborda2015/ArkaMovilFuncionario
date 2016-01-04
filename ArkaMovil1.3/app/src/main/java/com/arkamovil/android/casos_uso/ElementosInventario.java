package com.arkamovil.android.casos_uso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.arkamovil.android.Informacion.Informacion_Elementos;
import com.arkamovil.android.Informacion.Observaciones;
import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.procesos.FinalizarSesion;
import com.arkamovil.android.servicios_web.WS_ElementoPlaca;
import com.arkamovil.android.servicios_web.WS_ElementosInventario;
import com.arkamovil.android.servicios_web.WS_Imagen;
import com.arkamovil.android.servicios_web.WS_Observaciones;
import com.arkamovil.android.servicios_web.WS_ValidarSesion;

import java.util.List;

public class ElementosInventario extends Fragment {

    private Thread thread;
    private Handler handler = new Handler();

    private ProgressDialog circuloProgreso;

    ImageView subir;
    ImageView bajar;

    private WS_ElementosInventario elementos;
    private WS_Observaciones observaciones;

    private int index_info = -1;
    private int index_obser = -1;

    private View rootView;
    private int offset = 0;
    private int limit = 0;
    private String func;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fm_inventario, container, false);

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

        func  = getArguments().getString("doc_fun");
        final String depe  = getArguments().getString("id_dep");

        circuloProgreso = ProgressDialog.show(getActivity(), "", "Espere por favor ...", true);

        thread = new Thread() {
            public void run() {

                Looper.prepare();

                String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                elementos = new WS_ElementosInventario();
                elementos.startWebAccess(func, depe, new Login().getUsuarioSesion(), id_dispositivo);

                handler.post(createUI);
            }
        };

        thread.start();

        bajar = (ImageView) rootView.findViewById(R.id.bajar_elementos_inventario);
        subir = (ImageView) rootView.findViewById(R.id.subir_elementos_inventario);

        bajar.setVisibility(View.GONE);
        subir.setVisibility(View.GONE);

        bajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bajar();
            }
        });

        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subir();
            }
        });

        return rootView;
    }

    final Runnable createUI = new Runnable() {

        public void run() {
            circuloProgreso.dismiss();
            crear(rootView, getActivity(), elementos.getId_elemento(), elementos.getPlaca(), elementos.getDescripcion(), elementos.getEstado());
        }
    };

    final Runnable Obser = new Runnable() {

        public void run() {
            circuloProgreso.dismiss();
            Observaciones dialog = new Observaciones(actividad, observaciones, elementos, func, index_obser);
            dialog.show();
        }
    };


    public void limpiarTabla() {
        borrarTabla();
    }


    //************DESDE AQUÍ SE CREAN LOS EVENTOS DE LA TABLA************//

    private double f1 = 0.20;
    private double f2 = 0.35;
    private double f3 = 0.14;
    private double f4 = 0.14;
    private double f5 = 0.14;

    private static TableLayout tabla;
    private static TableLayout cabecera;

    private static TableRow.LayoutParams layoutFila;
    private static TableRow.LayoutParams layoutPlaca;
    private static TableRow.LayoutParams layoutDescripcion;
    private static TableRow.LayoutParams layoutEstadoAprob;
    private static TableRow.LayoutParams layoutObservacion;
    private static TableRow.LayoutParams layoutDetalle;

    private Informacion_Elementos dialog;

    private static Activity actividad;
    private static View vista;

    private static final int factor = 10;


    private static List<String> id_elementos;
    private static List<String> placa;
    private static List<String> descripcion;
    private static List<String> estado;

    private static int inicio;

    private static int tamanoPantalla;


    private static int MAX_FILAS = 0;


    public void crear(View rootView, Activity actividad, List<String> id_elemento, List<String> placa, List<String> descripcion, List<String> estado) {

        this.actividad = actividad;
        this.vista = rootView;

        id_elementos = id_elemento;
        this.placa = placa;
        this.descripcion = descripcion;
        this.estado = estado;

        this.tamanoPantalla = rootView.getWidth();

        if (id_elemento.size() < this.factor) {
            this.MAX_FILAS = id_elementos.size();
        } else {
            this.MAX_FILAS = this.factor;
        }

        this.inicio = 0;

        cargarElementos();

        if (id_elementos.size() > 0) {
            agregarCabecera();
            agregarFilasTabla();
        } else {
            Toast.makeText(actividad, "Se ha presentado un error, por favor intente de nuevo.", Toast.LENGTH_LONG).show();
        }

        if(id_elemento.size() > factor){
            subir.setVisibility(View.VISIBLE);
            bajar.setVisibility(View.VISIBLE);
        }
    }

    public void agregarCabecera() {

        TableRow fila;
        TextView txtPlaca;
        TextView txtDescripcion;
        TextView txtEstadoAprob;
        TextView txtObservacion;
        TextView txtDetalle;

        fila = new TableRow(actividad);
        fila.setLayoutParams(layoutFila);

        txtPlaca = new TextView(actividad);
        txtDescripcion = new TextView(actividad);
        txtEstadoAprob = new TextView(actividad);
        txtObservacion = new TextView(actividad);
        txtDetalle = new TextView(actividad);

        txtPlaca.setText("Placa");
        txtPlaca.setGravity(Gravity.CENTER_HORIZONTAL);
        txtPlaca.setTextAppearance(actividad, R.style.etiqueta);
        txtPlaca.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtPlaca.setLayoutParams(layoutPlaca);

        txtDescripcion.setText("Descripcion");
        txtDescripcion.setGravity(Gravity.CENTER_HORIZONTAL);
        txtDescripcion.setTextAppearance(actividad, R.style.etiqueta);
        txtDescripcion.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtDescripcion.setLayoutParams(layoutDescripcion);

        txtEstadoAprob.setText("Estado");
        txtEstadoAprob.setGravity(Gravity.CENTER_HORIZONTAL);
        txtEstadoAprob.setTextAppearance(actividad, R.style.etiqueta);
        txtEstadoAprob.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtEstadoAprob.setLayoutParams(layoutEstadoAprob);

        txtObservacion.setText("Info.");
        txtObservacion.setGravity(Gravity.CENTER_HORIZONTAL);
        txtObservacion.setTextAppearance(actividad, R.style.etiqueta);
        txtObservacion.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtObservacion.setLayoutParams(layoutObservacion);

        txtDetalle.setText("Obser.");
        txtDetalle.setGravity(Gravity.CENTER_HORIZONTAL);
        txtDetalle.setTextAppearance(actividad, R.style.etiqueta);
        txtDetalle.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtDetalle.setLayoutParams(layoutDetalle);

        fila.addView(txtPlaca);
        fila.addView(txtDescripcion);
        fila.addView(txtEstadoAprob);
        fila.addView(txtObservacion);
        fila.addView(txtDetalle);

        cabecera.addView(fila);
    }

    public void agregarFilasTabla() {

        TableRow fila;
        TextView txtPlaca;
        TextView txtDescripcion;
        CheckBox txtEstadoAprob;
        ImageView txtObservacion;
        ImageView txtDetalle;

        for (int i = 0; i < MAX_FILAS; i++) {
            fila = new TableRow(actividad);
            fila.setLayoutParams(layoutFila);

            txtPlaca = new TextView(actividad);
            txtDescripcion = new TextView(actividad);
            txtEstadoAprob = new CheckBox(actividad);
            txtObservacion = new ImageView(actividad);
            txtDetalle = new ImageView(actividad);

            txtPlaca.setText(placa.get(inicio + i));
            txtPlaca.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtPlaca.setTextAppearance(actividad, R.style.etiqueta);
            txtPlaca.setBackgroundResource(R.drawable.tabla_celda);
            txtPlaca.setLayoutParams(layoutPlaca);

            txtDescripcion.setText(descripcion.get(inicio + i));
            txtDescripcion.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtDescripcion.setTextAppearance(actividad, R.style.etiqueta);
            txtDescripcion.setBackgroundResource(R.drawable.tabla_celda);
            txtDescripcion.setLayoutParams(layoutDescripcion);

            txtEstadoAprob.setId(this.inicio + i);
            txtEstadoAprob.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtEstadoAprob.setTextAppearance(actividad, R.style.etiqueta);
            txtEstadoAprob.setBackgroundResource(R.drawable.tabla_celda);
            if("t".equals(estado.get(this.inicio + i))){
                txtEstadoAprob.setChecked(true);

            }
            txtEstadoAprob.setEnabled(false);
            txtEstadoAprob.setLayoutParams(layoutEstadoAprob);

            txtObservacion.setImageResource(R.drawable.ver);
            txtObservacion.setId(this.inicio + i);
            txtObservacion.setBackgroundResource(R.drawable.tabla_celda);
            txtObservacion.setLayoutParams(layoutObservacion);
            txtObservacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index_info = v.getId();
                    circuloProgreso = ProgressDialog.show(getActivity(), "", "Espere por favor ...", true);
                    dialog = new Informacion_Elementos(actividad, rootView, v.getId(), elementos);
                    dialog.show();
                    circuloProgreso.dismiss();
                }
            });

            txtDetalle.setImageResource(R.drawable.modificar);
            txtDetalle.setId(this.inicio + i);
            txtDetalle.setBackgroundResource(R.drawable.tabla_celda);
            txtDetalle.setLayoutParams(layoutDetalle);
            txtDetalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circuloProgreso = ProgressDialog.show(getActivity(), "", "Espere por favor ...", true);
                    index_obser = v.getId();
                    thread = new Thread() {
                        public void run() {

                            Looper.prepare();

                            String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                            observaciones = new WS_Observaciones();
                            observaciones.startWebAccess(elementos.getId_elemento().get(index_obser), new Login().getUsuarioSesion(), id_dispositivo);

                            handler.post(Obser);
                        }
                    };

                    thread.start();

                }
            });

            fila.addView(txtPlaca);
            fila.addView(txtDescripcion);
            fila.addView(txtEstadoAprob);
            fila.addView(txtObservacion);
            fila.addView(txtDetalle);

            tabla.addView(fila);
        }
    }

    public void cargarElementos() {

        tabla = (TableLayout) vista.findViewById(R.id.tabla_elementos_inventario);
        cabecera = (TableLayout) vista.findViewById(R.id.cabecera_elementos_inventario);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        layoutPlaca = new TableRow.LayoutParams((int) (tamanoPantalla * f1), TableRow.LayoutParams.MATCH_PARENT);
        layoutDescripcion = new TableRow.LayoutParams((int) (tamanoPantalla * f2), TableRow.LayoutParams.MATCH_PARENT);
        layoutEstadoAprob = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);
        layoutObservacion = new TableRow.LayoutParams((int) (tamanoPantalla * f4), TableRow.LayoutParams.MATCH_PARENT);
        layoutDetalle = new TableRow.LayoutParams((int) (tamanoPantalla * f5), TableRow.LayoutParams.MATCH_PARENT);

        tabla.removeAllViews();

    }

    public void bajar() {

        if (this.inicio <= (id_elementos.size() - (factor + 1))) {
            cargarElementos();
            this.inicio++;
            agregarFilasTabla();
        }
    }

    public void subir() {

        if (this.inicio > 0) {
            cargarElementos();
            this.inicio--;
            agregarFilasTabla();
        }
    }

    public void borrarTabla() {

        tabla = (TableLayout) vista.findViewById(R.id.tabla_elementos_inventario);
        cabecera = (TableLayout) vista.findViewById(R.id.cabecera_elementos_inventario);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        layoutPlaca = new TableRow.LayoutParams((int) (tamanoPantalla * f1), TableRow.LayoutParams.MATCH_PARENT);
        layoutDescripcion = new TableRow.LayoutParams((int) (tamanoPantalla * f2), TableRow.LayoutParams.MATCH_PARENT);
        layoutEstadoAprob = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);
        layoutObservacion = new TableRow.LayoutParams((int) (tamanoPantalla * f4), TableRow.LayoutParams.MATCH_PARENT);
        layoutDetalle = new TableRow.LayoutParams((int) (tamanoPantalla * f5), TableRow.LayoutParams.MATCH_PARENT);

        tabla.removeAllViews();
        cabecera.removeAllViews();
    }
}

