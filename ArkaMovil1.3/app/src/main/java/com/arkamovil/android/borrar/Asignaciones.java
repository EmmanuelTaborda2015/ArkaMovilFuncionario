package com.arkamovil.android.borrar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arkamovil.android.R;
import com.arkamovil.android.herramientas.Despliegue;

public class Asignaciones extends Dialog {


    private Activity c;
    private View roorView;

    private AutoCompleteTextView funcionario;

    private EditText elemento;
    private EditText placa;
    private EditText estadoAct;
    private EditText estado;
    private EditText observacion;

    private Button modificar;
    private Button cerrar;

    private int seleccion2;
    private String documento;

    private Thread thread;
    private Handler handler = new Handler();

    private WS_Asignaciones datos;
    private WS_Elemento_funcionario elementos;

    private int i;

    public Asignaciones(View rootView, Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.roorView = rootView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dl_asignaciones);

        funcionario = (AutoCompleteTextView) findViewById(R.id.funcionario_c4_dl);
        elemento = (EditText) findViewById(R.id.elemento_c4);
        placa = (EditText) findViewById(R.id.placa_c4);
        estadoAct = (EditText) findViewById(R.id.estadoactualizacion_c4);
        estado = (EditText) findViewById(R.id.estado_c4);
        observacion = (EditText) findViewById(R.id.observacion_c4);


        funcionario.setEnabled(false);

        modificar = (Button) findViewById(R.id.modificar_c4);
        cerrar = (Button) findViewById(R.id.cancelar_c4);

        datos = new WS_Asignaciones();

        final CasoUso4 casoUso4 = new CasoUso4();

        funcionario.setText(casoUso4.getLista_elemento_funcionario().get(datos.getSeleccion()));
        elemento.setText(datos.getId_elemento().get(0));
        placa.setText(datos.getPlaca().get(0));
        estado.setText(datos.getEstado().get(0));
        estadoAct.setText(datos.getEstado_Actualizacion().get(0));
        observacion.setText(datos.getObservaciones().get(0));

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TablaConsultarInventariosAsignados cerrarDialog = new TablaConsultarInventariosAsignados();
                cerrarDialog.cerrarDialog();
            }
        });

        funcionario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < casoUso4.getLista_funcionario().size(); i++) {
                    if (String.valueOf(funcionario.getText()).equals(casoUso4.getLista_funcionario().get(i))) {
                        seleccion2 = i;
                    }
                }

                documento = casoUso4.getLista_documentos().get(seleccion2);

                final InputMethodManager imm = (InputMethodManager) c.getSystemService(c.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(roorView.getWindowToken(), 0);
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("MODIFICAR".equalsIgnoreCase(String.valueOf(modificar.getText()))) {
                    funcionario.setEnabled(true);
                    funcionario.requestFocus();
                    funcionario.setText("");

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, casoUso4.getLista_documentos());
                    funcionario.setAdapter(adapter);

                    modificar.setText("Guardar");
                    cerrar.setText("Cancelar");

                    new Despliegue(funcionario);

                } else if ("GUARDAR".equalsIgnoreCase(String.valueOf(modificar.getText()))) {

                    if (!"".equalsIgnoreCase(String.valueOf(funcionario.getText()))) {
                        for (int i = 0; i < casoUso4.getLista_funcionario().size(); i++) {
                            if (casoUso4.getLista_documentos().get(i).equals(String.valueOf(funcionario.getText()))) {
                                thread = new Thread() {
                                    public void run() {
                                        WS_EnviarElementosAsignar ws_enviarElementosAsignar = new WS_EnviarElementosAsignar();

                                        casoUso4.getString_sede();

                                        documento = String.valueOf(funcionario.getText());

                                        ws_enviarElementosAsignar.startWebAccess(String.valueOf(elementos.getSede().get(datos.getSeleccion())), String.valueOf(elementos.getDependencia().get(datos.getSeleccion())), documento, datos.getObservaciones().get(0),  String.valueOf(elemento.getText()), "Ubicacion");


                                        casoUso4.setActualizacion(1);

                                        handler.post(createUI);
                                    }
                                };

                                thread.start();


                                i = casoUso4.getLista_funcionario().size();

                            } else if (i == casoUso4.getLista_funcionario().size() - 1) {
                                Toast.makeText(c, "El funcionario ingresado no es valido", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(c, "Seleccione el funcionario a asignar el elemento", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    final Runnable createUI = new Runnable() {

        public void run() {
            Toast.makeText(c, "Ha sido Reasignado el elemento", Toast.LENGTH_LONG).show();
            TablaConsultarInventariosAsignados tablaConsultarInventariosAsignados = new TablaConsultarInventariosAsignados();
            tablaConsultarInventariosAsignados.cerrarDialog();
            Button actualizar = (Button) roorView.findViewById(R.id.actualizar_c4);
            actualizar.setText("Listo");
        }
    };

}