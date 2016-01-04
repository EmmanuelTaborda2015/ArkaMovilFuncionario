package com.arkamovil.android.borrar;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.arkamovil.android.R;

import java.util.List;

public class TablaInventarios{

    ImageView txtverInventario;

    private double f1 = 0.18;
    private double f2 = 0.35;
    private double f3 = 0.35;
    private double f4 = 0.12;

    private static TableLayout tabla;
    private static TableLayout cabecera;

    private static TableRow.LayoutParams layoutFila;
    private static TableRow.LayoutParams layoutId;
    private static TableRow.LayoutParams layoutTexto;
    private static TableRow.LayoutParams layoutFuncionario;
    private static TableRow.LayoutParams layoutMod;

    private static Activity actividad;
    private static View vista;

    private static final int factor = 5;


    private static List<String> nom_fun;
    private static List<String> doc_fun;
    private static List<String> sede;
    private static List<String> dependencia;
    private static List<String> ubicacion;

    private static int inicio;

    private static int tamanoPantalla;


    private static int MAX_FILAS = 0;


    public void crear(View rootView, Activity actividad, List<String> doc_fun, List<String> sede, List<String> dependencia, List<String> ubicacion) {

        this.actividad = actividad;
        this.vista = rootView;

        this.doc_fun = doc_fun;
        this.sede = sede;
        this.dependencia = dependencia;
        this.ubicacion = ubicacion;

        this.tamanoPantalla = rootView.getWidth();

        if (doc_fun.size() < this.factor) {
            this.MAX_FILAS = doc_fun.size();
        } else {
            this.MAX_FILAS = this.factor;
        }

        this.inicio = 0;

        cargarElementos();

        if (doc_fun.size() > 0) {
            agregarCabecera();
            agregarFilasTabla();
        } else {
            Toast.makeText(actividad, "No existen inventarios para los criterios seleccionados.", Toast.LENGTH_LONG).show();
        }


    }

    public void agregarCabecera() {

        TableRow fila;
        TextView txtNombreFuncionario;
        TextView txtDocumentoFuncionario;
        TextView txtDependencia;
        TextView txtInformacion;


        fila = new TableRow(actividad);
        fila.setLayoutParams(layoutFila);

        txtNombreFuncionario = new TextView(actividad);
        txtDocumentoFuncionario = new TextView(actividad);
        txtDependencia = new TextView(actividad);
        txtInformacion = new TextView(actividad);

        txtNombreFuncionario.setText("Documento");
        txtNombreFuncionario.setGravity(Gravity.CENTER_HORIZONTAL);
        txtNombreFuncionario.setTextAppearance(actividad, R.style.etiqueta);
        txtNombreFuncionario.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtNombreFuncionario.setLayoutParams(layoutId);

        txtDocumentoFuncionario.setText("Nombre");
        txtDocumentoFuncionario.setGravity(Gravity.CENTER_HORIZONTAL);
        txtDocumentoFuncionario.setTextAppearance(actividad, R.style.etiqueta);
        txtDocumentoFuncionario.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtDocumentoFuncionario.setLayoutParams(layoutTexto);

        txtDependencia.setText("Ubicación");
        txtDependencia.setGravity(Gravity.CENTER_HORIZONTAL);
        txtDependencia.setTextAppearance(actividad, R.style.etiqueta);
        txtDependencia.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtDependencia.setLayoutParams(layoutFuncionario);

        txtInformacion.setText("Detalles");
        txtInformacion.setGravity(Gravity.CENTER_HORIZONTAL);
        txtInformacion.setTextAppearance(actividad, R.style.etiqueta);
        txtInformacion.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtInformacion.setLayoutParams(layoutMod);

        fila.addView(txtNombreFuncionario);
        fila.addView(txtDocumentoFuncionario);
        fila.addView(txtDependencia);
        fila.addView(txtInformacion);
        cabecera.addView(fila);
    }

    public void agregarFilasTabla() {

        TableRow fila;
        TextView txtnombre;
        TextView txtdocumento;
        TextView txtdependencia;

        for (int i = 0; i < MAX_FILAS; i++) {
            fila = new TableRow(actividad);
            fila.setLayoutParams(layoutFila);

            txtnombre = new TextView(actividad);
            txtdocumento = new TextView(actividad);
            txtdependencia = new TextView(actividad);
            txtverInventario = new ImageView(actividad);

            txtnombre.setText(doc_fun.get(inicio + i));
            txtnombre.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtnombre.setTextAppearance(actividad, R.style.etiqueta);
            txtnombre.setBackgroundResource(R.drawable.tabla_celda);
            txtnombre.setLayoutParams(layoutId);

            txtdocumento.setText(doc_fun.get(inicio + i));
            txtdocumento.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtdocumento.setTextAppearance(actividad, R.style.etiqueta);
            txtdocumento.setBackgroundResource(R.drawable.tabla_celda);
            txtdocumento.setLayoutParams(layoutTexto);

            txtdependencia.setText(dependencia.get(inicio + i));
            txtdependencia.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtdependencia.setTextAppearance(actividad, R.style.etiqueta);
            txtdependencia.setBackgroundResource(R.drawable.tabla_celda);
            txtdependencia.setLayoutParams(layoutFuncionario);

            txtverInventario.setImageResource(R.drawable.ver);
            txtverInventario.setId(this.inicio + i);
            txtverInventario.setBackgroundResource(R.drawable.tabla_celda);
            txtverInventario.setLayoutParams(layoutMod);
            txtverInventario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //dialog = new Informacion_Elementos_Cedula(actividad, v.getId());
                    //dialog.show();

                }
            });


            fila.addView(txtnombre);
            fila.addView(txtdocumento);
            fila.addView(txtdependencia);
            fila.addView(txtverInventario);

            tabla.addView(fila);
        }
    }

    public void cargarElementos() {

        tabla = (TableLayout) vista.findViewById(R.id.tabla_levantamiento_fisico);
        cabecera = (TableLayout) vista.findViewById(R.id.cabecera_levantamiento_fisico);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        layoutId = new TableRow.LayoutParams((int) (tamanoPantalla * f1), TableRow.LayoutParams.MATCH_PARENT);
        layoutTexto = new TableRow.LayoutParams((int) (tamanoPantalla * f2), TableRow.LayoutParams.MATCH_PARENT);
        layoutFuncionario = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);
        layoutMod = new TableRow.LayoutParams((int) (tamanoPantalla * f4), TableRow.LayoutParams.MATCH_PARENT);

        tabla.removeAllViews();

    }

    public void bajar() {

        if (this.inicio <= (doc_fun.size() - (factor + 1))) {
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

        tabla = (TableLayout) vista.findViewById(R.id.tabla_levantamiento_fisico);
        cabecera = (TableLayout) vista.findViewById(R.id.cabecera_levantamiento_fisico);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        layoutId = new TableRow.LayoutParams((int) (tamanoPantalla * f1), TableRow.LayoutParams.MATCH_PARENT);
        layoutTexto = new TableRow.LayoutParams((int) (tamanoPantalla * f2), TableRow.LayoutParams.MATCH_PARENT);
        layoutFuncionario = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);
        layoutMod = new TableRow.LayoutParams((int) (tamanoPantalla * f4), TableRow.LayoutParams.MATCH_PARENT);

        tabla.removeAllViews();
        cabecera.removeAllViews();
    }


}
