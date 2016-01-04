package com.arkamovil.android.casos_uso;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.herramientas.Despliegue;
import com.arkamovil.android.procesos.FinalizarSesion;
import com.arkamovil.android.procesos.GenerarPDF_ActaVisita;
import com.arkamovil.android.servicios_web.WS_CerrarSesion;
import com.arkamovil.android.servicios_web.WS_Dependencia;
import com.arkamovil.android.servicios_web.WS_ElementoPlaca;
import com.arkamovil.android.servicios_web.WS_Funcionario;
import com.arkamovil.android.servicios_web.WS_NumeroVisitas;
import com.arkamovil.android.servicios_web.WS_RegistroActaVisita;
import com.arkamovil.android.servicios_web.WS_Sede;
import com.arkamovil.android.servicios_web.WS_Ubicacion;
import com.arkamovil.android.servicios_web.WS_ValidarConexion;
import com.arkamovil.android.servicios_web.WS_ValidarSesion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ActaVisita extends Fragment {

    private static int year1;
    private static int month1;
    private static int day1;
    private static int year2;
    private static int month2;
    private static int day2;
    private TextView tvDisplayDate1;
    private TextView tvDisplayDate2;

    private  WS_Funcionario ws_funcionario;

    private Thread thread_WS_Fucncionario;
    private Thread thread_date;
    private Handler handler2 = new Handler();

    private Thread thread_validarSesion;
    private Handler handler_validarSesion = new Handler();
    private String webResponse_sesion;

    private AutoCompleteTextView sede;
    private AutoCompleteTextView dependencia;
    private AutoCompleteTextView ubicacion;
    private AutoCompleteTextView funcionario;
    private AutoCompleteTextView docRes;
    private EditText observacion;
    private AutoCompleteTextView numVisita;
    private TextView proximaVis;

    private ProgressDialog circuloProgreso;



    private String proxVisita;
    private String fecha;
    private String sede_s;
    private String dependencia_s;
    private String ubicacion_s;
    private String nombRes_s;
    private String docRes_s;
    private String observacion_s;
    private String numVisita_s;

    private Thread thread_registrarActa;
    private Thread thread_numvisitas;

    private Handler handler = new Handler();
    private String webResponse;

    private View rootView;

    private int contador = 0;
    private int validador = 0;

    private String id_dispositivo;


    private List<String> lista_sede = new ArrayList<String>();
    private List<String> lista_id_sede = new ArrayList<String>();
    private List<String> lista_ubicacion = new ArrayList<String>();
    private List<String> lista_id_ubicacion = new ArrayList<String>();
    private List<String> lista_dependencia = new ArrayList<String>();
    private List<String> lista_id_dependencia = new ArrayList<String>();
    private List<String> lista_funcionario = new ArrayList<String>();
    private List<String> lista_documento = new ArrayList<String>();

    private int seleccion = 0;
    private int seleccion1 = 0;
    private int seleccion2 = 0;
    private int seleccion3 = 0;

    private int focus = 0;

    private Thread thread_validarConexion;
    private String webResponse_conexion;
    private AlertDialog alertaConexion;
    private Handler handler_conexion = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fm_acta_visita, container, false);

        id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        //Se definen los campos a utilizar en la clase.
        establecerCampos();

        thread_validarConexion = new Thread() {
            public void run() {
                Looper.prepare();
                WS_ValidarConexion validarConexion = new WS_ValidarConexion();
                webResponse_conexion = validarConexion.startWebAccess();
                handler_conexion.post(conexion);
            }
        };

        thread_validarConexion.start();

        dependencia.setEnabled(false);
        ubicacion.setEnabled(false);

        //Se cargar los datos del web service sede.
        WS_Sede ws_sede = new WS_Sede();
        ws_sede.startWebAccess(getActivity(), sede, new Login().getUsuarioSesion(), id_dispositivo);
        lista_sede = ws_sede.getSede();
        lista_id_sede = ws_sede.getId_sede();


        //Se despliegan los datos obtenidos por del web service en el campo Atocomplete sede.
        new Despliegue(sede);

        //se genera la funció que permite generar un evento al seleccionar un item de las sedes.
        sede.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < lista_sede.size(); i++) {
                    if (String.valueOf(sede.getText()).equals(lista_sede.get(i))) {
                        seleccion = i;
                    }
                }
                WS_Dependencia ws_dependencia = new WS_Dependencia();
                ws_dependencia.startWebAccess(getActivity(), dependencia, lista_id_sede.get(seleccion), new Login().getUsuarioSesion(), id_dispositivo);

                lista_dependencia = ws_dependencia.getDependencia();
                lista_id_dependencia = ws_dependencia.getId_dependencia();

                //Se eliminan los items seleccionados anteriormente.

                dependencia.setText("");
                funcionario.setText("");
                docRes.setText("");
                dependencia.requestFocus();

                //Se despliegan los datos obtenidos de la dependencia.
                new Despliegue(dependencia);

                thread_numvisitas = new Thread() {
                    public void run() {

                        Looper.prepare();

                        WS_NumeroVisitas visitas = new WS_NumeroVisitas();
                        webResponse = visitas.startWebAccess(new Login().getUsuarioSesion(), id_dispositivo);

                        handler.post(createUI);
                    }
                };

                thread_numvisitas.start();
            }
        });

        dependencia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < lista_dependencia.size(); i++) {
                    if (String.valueOf(dependencia.getText()).equals(lista_dependencia.get(i))) {
                        seleccion1 = i;
                    }
                }

                WS_Ubicacion ws_ubicacion = new WS_Ubicacion();
                ws_ubicacion.startWebAccess(getActivity(), ubicacion, lista_id_dependencia.get(seleccion1), new Login().getUsuarioSesion(), id_dispositivo);

                lista_ubicacion = ws_ubicacion.getUbicacion();
                lista_id_ubicacion = ws_ubicacion.getId_ubicacion();

                //Se eliminan los items seleccionados anteriormente.

                ubicacion.setText("");
                funcionario.setText("");
                docRes.setText("");
                ubicacion.requestFocus();

                //Se despliegan los datos obtenidos de la dependencia.
                new Despliegue(ubicacion);

            }
        });

        ubicacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < lista_ubicacion.size(); i++) {
                    if (String.valueOf(ubicacion.getText()).equals(lista_ubicacion.get(i))) {
                        seleccion2 = i;
                    }
                }

                funcionario.setText("");
                docRes.setText("");
                docRes.requestFocus();
            }
        });

        docRes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < lista_documento.size(); i++) {
                    if (String.valueOf(docRes.getText()).equals(String.valueOf(lista_documento.get(i))+" - " + String.valueOf(lista_funcionario.get(i)))) {
                        seleccion3 = i;
                    }
                }

                funcionario.setText(lista_funcionario.get(seleccion3));

                observacion.requestFocus();

            }
        });

        docRes.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final String text = s.toString();
                if (s.length() >= 3 && (focus == (s.length() + 1) || focus == (s.length() - 1) || focus == s.length())) {
                    thread_WS_Fucncionario = new Thread() {
                        public void run() {

                            Looper.prepare();

                            ws_funcionario = new WS_Funcionario();
                            ws_funcionario.startWebAccess(text, new Login().getUsuarioSesion(), id_dispositivo);

                            handler2.post(Funcionario);

                        }
                    };
                    thread_WS_Fucncionario.start();
                }

                focus = s.length();

            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                new Despliegue(docRes);
            }
        });

        docRes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                            public void onFocusChange(View v, boolean hasFocus) {
                                                if (hasFocus) {
                                                    try {
                                                        docRes.setText("");
                                                        docRes.setTextColor(getResources().getColor(R.color.NEGRO));
                                                    } catch (NumberFormatException e) {
                                                    }
                                                }
                                            }
                                        }
        );


        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        String val1 = "";
        String val2 = "";

        if (today.monthDay < 10) {
            val1 = "0";
        }

        if ((today.month + 1) < 10) {
            val2 = "0";
        }

        year1 = today.year;
        month1 = today.month;
        day1 = today.monthDay;


        tvDisplayDate1 = (TextView) rootView.findViewById(R.id.fecha);
        tvDisplayDate2 = (TextView) rootView.findViewById(R.id.proxvis_c1);

        tvDisplayDate1.setTextColor(getResources().getColor(R.color.NEGRO));
        tvDisplayDate1.setText(val1 + day1 + "/" + val2 + (month1 + 1) + "/" + year1);

        contador++;

        tvDisplayDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                year2 = c.get(Calendar.YEAR);
                month2 = c.get(Calendar.MONTH);
                day2 = c.get(Calendar.DAY_OF_MONTH);

                //if (contador > 0) {

                        DatePickerDialog dialog = new DatePickerDialog(getActivity(), datePickerListener,
                                year1, month1, day1);
                        dialog.show();

                //} else {
                    //Toast.makeText(getActivity(), "Porfavor ingrese los datos en orden", Toast.LENGTH_LONG).show();
                //}
            }
        });


        //////////////////////////////////////////////////////////////////////////////////////////////////
        Button descargarPDF = (Button) rootView.findViewById(R.id.descargar);
        descargarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validar();


                if (validador == 0) {

                    fecha = String.valueOf(day1) + "/" + String.valueOf(month1 + 1) + "/" + String.valueOf(year1);
                    proxVisita = String.valueOf(day2) + "/" + String.valueOf(month2 + 1) + "/" + String.valueOf(year2);
                    sede_s = String.valueOf(sede.getText());
                    dependencia_s = String.valueOf(dependencia.getText());
                    nombRes_s = String.valueOf(funcionario.getText());
                    docRes_s = String.valueOf(docRes.getText());
                    observacion_s = String.valueOf(observacion.getText());
                    numVisita_s = String.valueOf(numVisita.getText());
                    circuloProgreso = ProgressDialog.show(getActivity(), "", "Generando PDF ...", true);
                    thread_registrarActa = new Thread() {
                        public void run() {
                            Looper.prepare();

                            WS_RegistroActaVisita enviar = new WS_RegistroActaVisita();
                            enviar.startWebAccess(lista_id_sede.get(seleccion), lista_id_dependencia.get(seleccion1), lista_documento.get(seleccion3), observacion_s, fecha, proxVisita, lista_id_ubicacion.get(seleccion2), new Login().getUsuarioSesion(), id_dispositivo);
                        }
                    };

                    thread_registrarActa.start();
                    circuloProgreso.dismiss();
                    GenerarPDF_ActaVisita generar = new GenerarPDF_ActaVisita();
                    generar.generar(getResources(), fecha, sede_s, dependencia_s, lista_funcionario.get(seleccion3), lista_documento.get(seleccion3), observacion_s, numVisita_s, proxVisita);

                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getActivity());

                    dialogo.setTitle("ACTA DE VISITA GENERADA");
                    dialogo.setMessage("Se ha generado el acta de visita en la ruta -> Download -> Acta de Visita -> Actavisita" + numVisita_s);
                    dialogo.setPositiveButton("Entendido",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    //builder.setIcon(android.R.drawable.ic_dialog_alert);
                    dialogo.setCancelable(true);
                    dialogo.create();
                    dialogo.show();

                    limpiar();

                    sede.requestFocus();

                    Toast.makeText(getActivity(), "Se ha generado el acta de visita en -> Download -> Acta de Visita -> Actavisita" + numVisita_s, Toast.LENGTH_LONG).show();
                }

            }
        });


        return rootView;
    }


    //Función que permite asignar la fecha seleccionada en el calendario al campo

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {



            month2 = selectedMonth;
            day2 = selectedDay;
            year2 = selectedYear;

            if (verificarFechas() == true) {


                String val1 = "";
                String val2 = "";

                if (day2 < 10) {
                    val1 = "0";
                }

                if ((month2 + 1) < 10) {
                    val2 = "0";
                }

                tvDisplayDate2.setText(val1 + day2 + "/" + val2 + (month2 + 1) + "/" + year2);
                tvDisplayDate2.setTextColor(getResources().getColor(R.color.NEGRO));

            } else {
                Toast.makeText(getActivity(), "La fecha de la próxima visita no es valida, por favor verifiquela e ingresela de nuevo", Toast.LENGTH_LONG).show();
            }

        }
    };



    //Se verifica si la fecha de visita es previa a la fecha de la proxima visita

    public boolean verificarFechas() {

        boolean verificar = false;

        if (year1 <= year2) {
            if (month1 <= month2) {
                if (day1 <= day2) {
                    verificar = true;
                }
            }
        }
        return verificar;
    }

    final Runnable createUI = new Runnable() {

        public void run() {
            AutoCompleteTextView numVisita = (AutoCompleteTextView) rootView.findViewById(R.id.numerovisita);
            if ("anyType{}".equalsIgnoreCase(String.valueOf(webResponse))) {
                numVisita.setText("1");
            } else {
                numVisita.setText(String.valueOf(Integer.parseInt(webResponse) + 1));
            }
        }
    };

    public void establecerCampos() {

        sede = (AutoCompleteTextView) rootView.findViewById(R.id.sede_c1);
        dependencia = (AutoCompleteTextView) rootView.findViewById(R.id.dependencia_c1);
        ubicacion = (AutoCompleteTextView) rootView.findViewById(R.id.ubicacion_c1);
        funcionario = (AutoCompleteTextView) rootView.findViewById(R.id.nombreresponsable_c1);
        docRes = (AutoCompleteTextView) rootView.findViewById(R.id.cedularesponsable_c1);
        observacion = (EditText) rootView.findViewById(R.id.observacion_c1);
        numVisita = (AutoCompleteTextView) rootView.findViewById(R.id.numerovisita);
        proximaVis = (TextView) rootView.findViewById(R.id.proxvis_c1);

    }

    public void validar() {

        validador = 0;

        if ("".equals(String.valueOf(sede.getText()))) {
            Toast.makeText(getActivity(), "Porfavor ingrese la Sede", Toast.LENGTH_LONG).show();
            validador++;
        } else if ("".equals(String.valueOf(dependencia.getText())) && validador == 0) {
            Toast.makeText(getActivity(), "Porfavor ingrese la Dependencia", Toast.LENGTH_LONG).show();
            validador++;
        } else if (("".equals(String.valueOf(funcionario.getText())) || "no existen funcionarios relacionados".equalsIgnoreCase(String.valueOf(funcionario.getText()))) && validador == 0) {
            Toast.makeText(getActivity(), "Porfavor ingrese el Nombre del responsable", Toast.LENGTH_LONG).show();
            validador++;
        } else if ("".equals(String.valueOf(docRes.getText())) && validador == 0) {
            Toast.makeText(getActivity(), "Porfavor ingrese la Cédula del responsable", Toast.LENGTH_LONG).show();
            validador++;
        } else if ("".equals(String.valueOf(observacion.getText())) && validador == 0) {
            Toast.makeText(getActivity(), "Porfavor ingrese las Observaciones", Toast.LENGTH_LONG).show();
            validador++;
        } else if ("".equals(String.valueOf(numVisita.getText())) && validador == 0) {
            Toast.makeText(getActivity(), "Porfavor ingrese el Número de visita", Toast.LENGTH_LONG).show();
            validador++;
        } else if ("dd:mm:aa".equals(String.valueOf(proximaVis.getText())) && validador == 0) {
            Toast.makeText(getActivity(), "Porfavor ingrese la fecha de la próxima visita", Toast.LENGTH_LONG).show();
            validador++;
        }

    }

    public void limpiar() {
        sede.setText("");
        dependencia.setText("");
        ubicacion.setText("");
        funcionario.setText("");
        docRes.setText("");
        observacion.setText("");
        numVisita.setText("");
        proximaVis.setText("");
    }

    final Runnable Funcionario = new Runnable() {
        public void run() {

            ws_funcionario.cargarListaFuncionario(getActivity(), docRes, "null");
            lista_funcionario = ws_funcionario.getFun_nombre();
            lista_documento = ws_funcionario.getFun_identificacion();
        }
    };

    final Runnable conexion = new Runnable() {

        public void run() {
            if("false".equals(webResponse_conexion)){
                // wifi connection was lost
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Problemas en la Conexión a Internet");
                builder.setMessage("Se ha cerrado la sesión debido a que la conexión a internet mediante la cual esta tratando de acceder no es valida. \nPor favor verifique la configuración del proxy o intente nuevamente conectandose a otra red Wi-Fi o Movil.");
                builder.setPositiveButton("Entendido",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                //builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setCancelable(false);
                alertaConexion = builder.create();
                alertaConexion.show();
            }
        }
    };
}

