package com.arkamovil.android.borrar;

import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.arkamovil.android.R;

import java.util.List;

public class TablaConsultarInventariosAsignados {

    private static TableLayout tabla;
    private static TableLayout cabecera;

    private double f1 = 0.3;
    private double f2 = 0.4;
    private double f3 = 0.2;

    private static TableRow.LayoutParams layoutFila;
    private static TableRow.LayoutParams layoutId;
    private static TableRow.LayoutParams layoutTexto;
    private static TableRow.LayoutParams layoutVer;

    private static Asignaciones dialog;

    public Thread getThread() {
        return thread;
    }

    private Thread thread;
    private Handler handler = new Handler();

    private static Activity actividad;
    private static View rootView;

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
        this.placa = placa;

        this.tamanoPantalla = rootView.getWidth();


        this.id_elemento = id;
        this.descripcion = desc;

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
            Toast.makeText(actividad, "No registran elementos para el criterio de busqueda.", Toast.LENGTH_LONG).show();
            ImageView bajar = (ImageView) rootView.findViewById(R.id.bajar_c4);
            ImageView subir = (ImageView) rootView.findViewById(R.id.subir_c4);
            Button pdf = (Button) rootView.findViewById(R.id.generarpdf_c4);
            bajar.setVisibility(View.GONE);
            subir.setVisibility(View.GONE);
            pdf.setVisibility(View.GONE);
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

        txtInfo.setText("Ver");
        txtInfo.setGravity(Gravity.CENTER_HORIZONTAL);
        txtInfo.setTextAppearance(actividad, R.style.etiqueta);
        txtInfo.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtInfo.setLayoutParams(layoutVer);

        fila.addView(txtId);
        fila.addView(txtDescripcion);
        fila.addView(txtInfo);
        cabecera.addView(fila);
    }

    public void agregarFilasTabla() {

        TableRow fila;
        TextView txtId;
        TextView txtDescripcion;
        ImageView txtVer;

        for (int i = 0; i < MAX_FILAS; i++) {
            fila = new TableRow(actividad);
            fila.setLayoutParams(layoutFila);

            txtId = new TextView(actividad);
            txtDescripcion = new TextView(actividad);
            txtVer = new ImageView(actividad);

            txtId.setText(placa.get(this.inicio + i));
            txtId.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtId.setTextAppearance(actividad, R.style.etiqueta);
            txtId.setBackgroundResource(R.drawable.tabla_celda);
            txtId.setLayoutParams(layoutId);

            txtDescripcion.setText(descripcion.get(this.inicio + i));
            txtId.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtDescripcion.setTextAppearance(actividad, R.style.etiqueta);
            txtDescripcion.setBackgroundResource(R.drawable.tabla_celda);
            txtDescripcion.setLayoutParams(layoutTexto);

            txtVer.setImageResource(R.drawable.ver);
            txtVer.setId(this.inicio + i);
            txtVer.setBackgroundResource(R.drawable.tabla_celda);
            txtVer.setLayoutParams(layoutVer);
            txtVer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    thread = new Thread() {
                        public void run() {
                            WS_Asignaciones ws_asignaciones = new WS_Asignaciones();
                            ws_asignaciones.startWebAccess(actividad, id_elemento.get(v.getId()), v.getId());

                            handler.post(createUI);
                        }
                    };

                    thread.start();
                }
            });

            fila.addView(txtId);
            fila.addView(txtDescripcion);
            fila.addView(txtVer);

            tabla.addView(fila);

        }
    }

    public void cargarElementos() {

        tabla.removeAllViews();
        cabecera.removeAllViews();

        tabla = (TableLayout) rootView.findViewById(R.id.tabla_c4);
        cabecera = (TableLayout) rootView.findViewById(R.id.cabecera_c4);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        layoutId = new TableRow.LayoutParams((int) (tamanoPantalla * f1), TableRow.LayoutParams.MATCH_PARENT);
        layoutTexto = new TableRow.LayoutParams((int) (tamanoPantalla * f2), TableRow.LayoutParams.MATCH_PARENT);
        layoutVer = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);
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


        tabla = (TableLayout) rootView.findViewById(R.id.tabla_c4);
        cabecera = (TableLayout) rootView.findViewById(R.id.cabecera_c4);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        layoutId = new TableRow.LayoutParams((int) (tamanoPantalla * f1), TableRow.LayoutParams.MATCH_PARENT);
        layoutTexto = new TableRow.LayoutParams((int) (tamanoPantalla * f2), TableRow.LayoutParams.MATCH_PARENT);
        layoutVer = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);

        tabla.removeAllViews();
        cabecera.removeAllViews();
    }

    final Runnable createUI = new Runnable() {

        public void run() {
            WS_Asignaciones ws_asignaciones = new WS_Asignaciones();
            if (ws_asignaciones.getId_elemento().size() > 0) {
                dialog = new Asignaciones(rootView, actividad);
                dialog.show();
            } else {
                Toast.makeText(actividad, "No se han generado actualizaciones para este elemento", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void cerrarDialog() {
        dialog.dismiss();
    }

}
