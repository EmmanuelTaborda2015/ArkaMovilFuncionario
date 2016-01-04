package com.arkamovil.android.borrar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arkamovil.android.R;
import com.arkamovil.android.procesos.LlenarListas;

public class Modificar_Informacion_Elementos extends Dialog {


    private Activity c;
    private int i;
    private static int estado = 0;
    private TextView elemento;
    private TextView placa;
    private TextView serie;
    private TextView observacion;
    private Spinner estadoSpin;

    private WS_Asignaciones datos;

    private Thread thread_actualizarregistro;

    private Handler handler = new Handler();

    public Modificar_Informacion_Elementos(Activity a, final int i) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.i = i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dg_modificar);

        elemento = (TextView) findViewById(R.id.elemento_61);
        placa = (TextView) findViewById(R.id.placa_61);
        serie = (TextView) findViewById(R.id.serial_61);
        observacion = (TextView) findViewById(R.id.observacion_61);

        estadoSpin = (Spinner) findViewById(R.id.estado_61);

        if (CasoUso6.getFuncion() == 1) {
            WS_Elemento_dependencia ws_elemento = new WS_Elemento_dependencia();
            elemento.setText(ws_elemento.getId_elemento().get(i));
            placa.setText(ws_elemento.getPlaca().get(i));
            serie.setText(ws_elemento.getSerie().get(i));
        } else if (CasoUso6.getFuncion() == 2) {
            WS_Elemento_funcionario ws_elemento = new WS_Elemento_funcionario();
            elemento.setText(ws_elemento.getId_elemento().get(i));
            placa.setText(ws_elemento.getPlaca().get(i));
            serie.setText(ws_elemento.getSerie().get(i));
        }

        datos = new WS_Asignaciones();

        if (datos.getEstado().size() > 0) {
            String est = datos.getEstado().get(0);

            if ("Existente y Activo".equalsIgnoreCase(est)) {
                estado = 1;
            } else if ("Sobrante".equalsIgnoreCase(est)) {
                estado = 2;
            } else if ("Faltante por Hurto".equalsIgnoreCase(est)) {
                estado = 3;
            } else if ("Faltante Dependencia".equalsIgnoreCase(est)) {
                estado = 4;
            } else if ("Baja".equalsIgnoreCase(est)) {
                estado = 5;
            }

            observacion.setText(datos.getObservaciones().get(0));

            LlenarListas estadoList = new LlenarListas();
            estadoList.llenarSpinnerEstado1(c, estadoSpin);

            estadoSpin.setSelection(estado);
        } else {
            LlenarListas estadoList = new LlenarListas();
            estadoList.llenarSpinnerEstado1(c, estadoSpin);
        }

        Button cancelar;
        cancelar = (Button) findViewById(R.id.cancelar_61);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TablaModificarInventario cerrarDialog = new TablaModificarInventario();
                cerrarDialog.cerrarDialog();
            }
        });

        Button guardar;
        guardar = (Button) findViewById(R.id.guardar_61);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String estadoGuardar = String.valueOf(estadoSpin.getSelectedItem());

                if ("--Seleccione una opción--".equals(estadoGuardar)) {
                    Toast.makeText(c, "Porfavor seleccione el estado", Toast.LENGTH_LONG).show();
                } else {
                    thread_actualizarregistro = new Thread() {
                        public void run() {
                            String id_elementoGuardar = String.valueOf(elemento.getText());
                            String elementoGuardar = String.valueOf(elemento.getText());
                            String placaGuardar = String.valueOf(placa.getText());
                            String serieGuardar = String.valueOf(serie.getText());
                            String estadoGuardar = String.valueOf(estadoSpin.getSelectedItem());
                            String observacionGuardar = String.valueOf(observacion.getText());
                            WS_ActualizarInventario ws_actualizarInventario = new WS_ActualizarInventario();
                            ws_actualizarInventario.startWebAccess(c, id_elementoGuardar, serieGuardar, placaGuardar, estadoGuardar, observacionGuardar);

                            handler.post(createUI);
                        }
                    };

                    thread_actualizarregistro.start();
                }
            }
        });

    }

    final Runnable createUI = new Runnable() {

        public void run() {
            Toast.makeText(c, "Se ha Actualizado el inventario Asignado", Toast.LENGTH_LONG).show();
            TablaModificarInventario cerrarDialog = new TablaModificarInventario();
            cerrarDialog.cerrarDialog();

        }
    };

}