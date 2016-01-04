package com.arkamovil.android.borrar;

import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.arkamovil.android.R;

import java.util.List;

public class TablaModificarInventario {

    public void cerrarDialog() {
        dialog.dismiss();
    }

    private double f1 = 0.23;
    private double f2 = 0.4;
    private double f3 = 0.27;

    private static Modificar_Informacion_Elementos dialog;

    private static TableLayout tabla;
    private static TableLayout cabecera;

    private static TableRow.LayoutParams layoutFila;
    private static TableRow.LayoutParams layoutId;
    private static TableRow.LayoutParams layoutTexto;
    private static TableRow.LayoutParams layoutMod;

    private static Activity actividad;
    private static View rootView;

    private static int estado = 0;
    private static int id = 0;

    private Thread thread_estado;

    private Handler handler = new Handler();


    private static final int factor = 5;


    private static List<String> id_elemento;
    private static List<String> descripcion;
    private static List<String> placa;
    private static boolean[] arr;

    private static int inicio;

    private static int tamanoPantalla;


    private static int MAX_FILAS = 0;


    public void crear(View rootView, Activity actividad, List<String> id, List<String> desc, List<String> placa) {

        this.actividad = actividad;
        this.rootView = rootView;

        this.tamanoPantalla = rootView.getWidth();


        this.id_elemento = id;
        this.descripcion = desc;
        this.placa = placa;

        if (id_elemento.size() < this.factor) {
            this.MAX_FILAS = id_elemento.size();
        } else {
            this.MAX_FILAS = this.factor;
        }

        this.inicio = 0;

        cargarElementos();

        if (id_elemento.size() > 0) {
            agregarCabecera();
            agregarFilasTabla();
        } else {
            Toast.makeText(actividad, "No registran elementos para los criterios de busqueda.", Toast.LENGTH_LONG).show();
            ImageView bajar = (ImageView) rootView.findViewById(R.id.bajar_c6);
            ImageView subir = (ImageView) rootView.findViewById(R.id.subir_c6);
            bajar.setVisibility(View.GONE);
            subir.setVisibility(View.GONE);
        }
    }

    public void agregarCabecera() {
        TableRow fila;
        TextView txtId;
        TextView txtDescripcion;
        TextView txtInfo;


        fila = new TableRow(actividad);
        fila.setLayoutParams(layoutFila);

        txtId = new TextView(actividad);
        txtDescripcion = new TextView(actividad);
        txtInfo = new TextView(actividad);

        txtId.setText("Placa");
        txtId.setGravity(Gravity.CENTER_HORIZONTAL);
        txtId.setTextAppearance(actividad, R.style.etiqueta);
        txtId.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtId.setLayoutParams(layoutId);

        txtDescripcion.setText("Descripción");
        txtDescripcion.setGravity(Gravity.CENTER_HORIZONTAL);
        txtDescripcion.setTextAppearance(actividad, R.style.etiqueta);
        txtDescripcion.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtDescripcion.setLayoutParams(layoutTexto);

        txtInfo.setText("Modificar");
        txtInfo.setGravity(Gravity.CENTER_HORIZONTAL);
        txtInfo.setTextAppearance(actividad, R.style.etiqueta);
        txtInfo.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtInfo.setLayoutParams(layoutMod);

        fila.addView(txtId);
        fila.addView(txtDescripcion);
        fila.addView(txtInfo);
        cabecera.addView(fila);
    }

    public void agregarFilasTabla() {

        TableRow fila;
        TextView txtId;
        TextView txtDescripcion;
        ImageView txtMod;

        for (int i = 0; i < MAX_FILAS; i++) {
            fila = new TableRow(actividad);
            fila.setLayoutParams(layoutFila);

            txtId = new TextView(actividad);
            txtDescripcion = new TextView(actividad);
            txtMod = new ImageView(actividad);

            txtId.setText(placa.get(this.inicio + i));
            txtId.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtId.setTextAppearance(actividad, R.style.etiqueta);
            txtId.setBackgroundResource(R.drawable.tabla_celda);
            txtId.setLayoutParams(layoutId);

            txtDescripcion.setText(descripcion.get(this.inicio + i));
            txtDescripcion.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtDescripcion.setTextAppearance(actividad, R.style.etiqueta);
            txtDescripcion.setBackgroundResource(R.drawable.tabla_celda);
            txtDescripcion.setLayoutParams(layoutTexto);

            //txtMod.setText("ver");

            txtMod.setId(this.inicio + i);
            txtMod.setImageResource(R.drawable.modificar);
            txtMod.setPadding(30, 30, 30, 30);
            txtMod.setBackgroundResource(R.drawable.tabla_celda);
            txtMod.setLayoutParams(layoutMod);

            txtMod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    thread_estado = new Thread() {
                        public void run() {

                            id = v.getId();

                            WS_Asignaciones ws_asignaciones = new WS_Asignaciones();
                            ws_asignaciones.startWebAccess(actividad, id_elemento.get(v.getId()), id);


                            handler.post(createESTADO);
                        }

                    };

                    thread_estado.start();

                }
            });

            fila.addView(txtId);
            fila.addView(txtDescripcion);
            fila.addView(txtMod);

            tabla.addView(fila);

        }
    }

    public void cargarElementos() {

        tabla.removeAllViews();

        tabla = (TableLayout) rootView.findViewById(R.id.tabla_c6);
        cabecera = (TableLayout) rootView.findViewById(R.id.cabecera_c6);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        layoutId = new TableRow.LayoutParams((int) (tamanoPantalla * f1), TableRow.LayoutParams.MATCH_PARENT);
        layoutTexto = new TableRow.LayoutParams((int) (tamanoPantalla * f2), TableRow.LayoutParams.MATCH_PARENT);
        layoutMod = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);

    }

    public void bajar(View rootView, Activity actividad) {

        if (this.inicio <= (id_elemento.size() - (factor + 1))) {
            cargarElementos();
            this.inicio++;
            agregarFilasTabla();
        }
    }

    public void subir(View rootView, Activity actividad) {

        if (this.inicio > 0) {
            cargarElementos();
            this.inicio--;
            agregarFilasTabla();
        }
    }

    public void borrarTabla(View rootView, Activity actividad) {


        tabla = (TableLayout) rootView.findViewById(R.id.tabla_c6);
        cabecera = (TableLayout) rootView.findViewById(R.id.cabecera_c6);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        layoutId = new TableRow.LayoutParams((int) (tamanoPantalla * f1), TableRow.LayoutParams.MATCH_PARENT);
        layoutTexto = new TableRow.LayoutParams((int) (tamanoPantalla * f2), TableRow.LayoutParams.MATCH_PARENT);
        layoutMod = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);

        tabla.removeAllViews();
        cabecera.removeAllViews();
    }

    final Runnable createESTADO = new Runnable() {

        public void run() {
            WS_Asignaciones ws_asignaciones = new WS_Asignaciones();
            dialog = new Modificar_Informacion_Elementos(actividad, id);
            dialog.show();
        }
    };
}
