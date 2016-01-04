package com.arkamovil.android.borrar;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.herramientas.Despliegue;
import com.arkamovil.android.servicios_web.WS_Dependencia;
import com.arkamovil.android.servicios_web.WS_ElementoPlaca;
import com.arkamovil.android.servicios_web.WS_Funcionario;
import com.arkamovil.android.servicios_web.WS_Sede;
import com.arkamovil.android.servicios_web.WS_Ubicacion;

import java.util.ArrayList;
import java.util.List;

public class CasoUso4 extends Fragment {

    private AutoCompleteTextView sede;
    private AutoCompleteTextView dependencia;
    private AutoCompleteTextView ubicacion;
    private AutoCompleteTextView funcionario;
    private Button scanear;
    private Button pdf;

    private static WS_Elemento_funcionario elem_fun;

    private static WS_Elemento_dependencia elem_dep;

    private static String string_sede;
    private static String string_dependencia;
    private static String string_ubicacion;
    private static String string_funcionario;

    private int opcion = 0;

    public static String getString_dependencia() {
        return string_dependencia;
    }

    private Thread thread_actualizarregistro;
    private Thread thread_actualizar_fun;
    private Thread thread_actualizar_dep;

    private static int actualizacion = 0;

    public static void setActualizacion(int actualizacion) {
        CasoUso4.actualizacion = actualizacion;
    }


    private Handler handler = new Handler();

    private static List<String> lista_sede;
    private static List<String> lista_id_sede;
    private static List<String> lista_dependencia;
    private static List<String> lista_id_dependencia;
    private static List<String> lista_ubicacion;
    private static List<String> lista_id_ubicacion;
    private static List<String> lista_funcionario;
    private static List<String> lista_elemento_funcionario;
    private static List<String> lista_documentos;
    private static List<String> id_elemento;


    public static List<String> getLista_documentos() {
        return lista_documentos;
    }

    public static List<String> getLista_elemento_funcionario() {
        return lista_elemento_funcionario;
    }

    public static List<String> getLista_funcionario() {
        return lista_funcionario;
    }

    private ImageView bajar;
    private ImageView subir;

    private static View rootView;

    private int seleccion = -1;
    private int seleccion1 = -1;
    private int seleccion2 = -1;
    private int seleccion3 = -1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fm_casouso4, container, false);

        lista_sede = new ArrayList<String>();
        lista_id_sede = new ArrayList<String>();
        lista_dependencia = new ArrayList<String>();
        lista_id_dependencia = new ArrayList<String>();
        lista_ubicacion = new ArrayList<String>();
        lista_id_ubicacion = new ArrayList<String>();
        lista_funcionario = new ArrayList<String>();
        lista_elemento_funcionario = new ArrayList<String>();
        lista_documentos = new ArrayList<String>();
        id_elemento = new ArrayList<String>();

        sede = (AutoCompleteTextView) rootView.findViewById(R.id.sede_c4);
        dependencia = (AutoCompleteTextView) rootView.findViewById(R.id.dependencia_c4);
        ubicacion = (AutoCompleteTextView) rootView.findViewById(R.id.ubicacion_c4);
        funcionario = (AutoCompleteTextView) rootView.findViewById(R.id.funcionario_c4);
        pdf = (Button) rootView.findViewById(R.id.generarpdf_c4);
        bajar = (ImageView) rootView.findViewById(R.id.bajar_c4);
        subir = (ImageView) rootView.findViewById(R.id.subir_c4);
        scanear = (Button) rootView.findViewById(R.id.escanear_c4);

        dependencia.setEnabled(false);
        ubicacion.setEnabled(false);

        bajar.setVisibility(View.GONE);
        subir.setVisibility(View.GONE);
        pdf.setVisibility(View.GONE);
        scanear.setVisibility(View.GONE);

        //Se envia parametros de vista y de dependencia seleccionada en el campo AutoComplete al web service de dependencias.

        String id_dispositivo = Settings.Secure.getString(rootView.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        WS_Sede ws_sede = new WS_Sede();
        ws_sede.startWebAccess(getActivity(), sede, new Login().getUsuarioSesion(), id_dispositivo);
        lista_sede = ws_sede.getSede();
        lista_id_sede = ws_sede.getId_sede();

        final LinearLayout l1 = (LinearLayout) rootView.findViewById(R.id.dep_c4);
        l1.setVisibility(View.GONE);

        final LinearLayout l2 = (LinearLayout) rootView.findViewById(R.id.fun_c4);
        l2.setVisibility(View.GONE);

        Button des1 = (Button) rootView.findViewById(R.id.des_dep_c4);
        des1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();

                if (on) {
                    l1.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.GONE);
                } else {
                    l1.setVisibility(View.GONE);
                }
            }
        });

        Button des2 = (Button) rootView.findViewById(R.id.des_fun_c4);
        des2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();
                if (on) {
                    l2.setVisibility(View.VISIBLE);
                    l1.setVisibility(View.GONE);
                } else {
                    l2.setVisibility(View.GONE);
                }
            }
        });

        new Despliegue(sede);

        WS_Funcionario ws_funcionario = new WS_Funcionario();

        ws_funcionario.cargarListaFuncionario(getActivity(), funcionario, "null");

        lista_funcionario = ws_funcionario.getFun_nombre();
        lista_documentos = ws_funcionario.getFun_identificacion();

        //new Despliegue(funcionario);


        sede.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < lista_sede.size(); i++) {
                    if (String.valueOf(sede.getText()).equals(lista_sede.get(i))) {
                        seleccion = i;
                    }
                }
                //Se envia parametros de vista y de campo AutoComplete al web service de facultad.
                String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                WS_Dependencia ws_dependencia = new WS_Dependencia();
                ws_dependencia.startWebAccess(getActivity(), dependencia, lista_id_sede.get(seleccion), new Login().getUsuarioSesion(), id_dispositivo);

                lista_dependencia = ws_dependencia.getDependencia();
                lista_id_dependencia = ws_dependencia.getId_dependencia();

                dependencia.setText("");
                dependencia.requestFocus();

                limpiarTabla();

                //Se despliegan los datos obtenidos de la dependencia.
                new Despliegue(dependencia);
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
                //Se envia parametros de vista y de campo AutoComplete al web service de facultad.

                String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                WS_Ubicacion ws_ubicacion = new WS_Ubicacion();
                ws_ubicacion.startWebAccess(getActivity(), ubicacion, lista_id_dependencia.get(seleccion1), new Login().getUsuarioSesion(), id_dispositivo);

                lista_ubicacion = ws_ubicacion.getUbicacion();
                lista_id_ubicacion = ws_ubicacion.getId_ubicacion();

                ubicacion.setText("");
                ubicacion.requestFocus();

                limpiarTabla();

                //Se despliegan los datos obtenidos de la dependencia.
                new Despliegue(ubicacion);
                //final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        ubicacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        funcionario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        Button con_dep = (Button) rootView.findViewById(R.id.con_dep_c4);
        con_dep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                limpiarTabla();

                seleccion = -1;
                seleccion1 = -1;
                seleccion2 = -1;

                for (int i = 0; i < lista_sede.size(); i++) {
                    if (String.valueOf(sede.getText()).equalsIgnoreCase(lista_sede.get(i))) {
                        seleccion = i;
                    }
                }

                if (seleccion > -1) {
                    string_sede = String.valueOf(sede.getText());
                    for (int i = 0; i < lista_dependencia.size(); i++) {
                        if (String.valueOf(dependencia.getText()).equalsIgnoreCase(lista_dependencia.get(i))) {
                            seleccion1 = i;
                        }
                    }
                    if (seleccion1 > -1) {
                        for (int i = 0; i < lista_ubicacion.size(); i++) {
                            if (String.valueOf(ubicacion.getText()).equalsIgnoreCase(lista_ubicacion.get(i))) {
                                seleccion2 = i;
                            }
                        }
                        if (seleccion2 > -1) {
                            //verificar ajustes
                            elem_dep = new WS_Elemento_dependencia();
                            elem_dep.startWebAccess(rootView, getActivity(), lista_id_dependencia.get(seleccion1), 3);
                            id_elemento = elem_dep.getId_elemento();
                            lista_elemento_funcionario = elem_dep.getFuncionario();
                            string_sede = String.valueOf(lista_id_sede.get(seleccion));
                            string_dependencia = String.valueOf(lista_id_dependencia.get(seleccion1));
                            opcion = 1;

//                        thread_actualizar_dep = new Thread() {
//                            public void run() {
//                                do {
//                                    if (actualizacion == 1) {
//                                        actualizarTabla();
//                                        actualizacion = 0;
//                                    }
//                                } while (true);
//                            }
//                        };
//
//                        thread_actualizar_dep.start();
                        } else {
                            Toast.makeText(getActivity(), "La Ubicación ingresada no es valida, verifeque e intente de nuevo.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "La Dependencia ingresada no es valida, verifeque e intente de nuevo.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "La Sede ingresada no es valida, verifeque e intente de nuevo.", Toast.LENGTH_LONG).show();
                }

                seleccion = -1;
                seleccion1 = -1;
                seleccion2 = -1;

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        Button con_fun = (Button) rootView.findViewById(R.id.con_fun_c4);
        con_fun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seleccion3 = -1;

                limpiarTabla();

                for (int i = 0; i < lista_funcionario.size(); i++) {
                    if (String.valueOf(funcionario.getText()).equalsIgnoreCase(lista_documentos.get(i))) {
                        seleccion3 = i;
                    }
                }
                Log.v("mensaje", funcionario.getText().toString());
                if (seleccion3 > -1) {
                    elem_fun = new WS_Elemento_funcionario();
                    elem_fun.startWebAccess(rootView, getActivity(), lista_documentos.get(seleccion3), 3);
                    id_elemento = elem_fun.getId_elemento();
                    lista_elemento_funcionario = elem_fun.getFuncionario();
                    string_funcionario = String.valueOf(funcionario.getText());

                    opcion = 2;

//                    // !importante¡ -> Hilo para actualizar la tabla del funcionario a la hora de reasignar elemento a otro funcionario.
//
//                    thread_actualizar_fun = new Thread() {
//                        public void run() {
//                            do {
//                                if (actualizacion == 1) {
//                                    actualizarTabla();
//                                    actualizacion = 0;
//                                }
//                            } while (true);
//                        }
//                    };
//
//                    thread_actualizar_fun.start();

                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                } else {
                    Toast.makeText(getActivity(), "El documento ingresado no es valido, por favor verifiquelo e intente de nuevo.", Toast.LENGTH_LONG).show();
                }

                seleccion3 = -1;

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        final Button actualizar = (Button) rootView.findViewById(R.id.actualizar_c4);
        actualizar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarTabla();
            }

            public void afterTextChanged(Editable s) {
            }
        });

        //boton para bajar los elementos
        bajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TablaConsultarInventariosAsignados baj = new TablaConsultarInventariosAsignados();
                baj.bajar(rootView, getActivity());
            }
        });
        //llamar metodo en clase CrearTablas para ir hacia los primeros registros mostrados en la tabla
        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TablaConsultarInventariosAsignados sub = new TablaConsultarInventariosAsignados();
                sub.subir(rootView, getActivity());
            }
        });

        //<!--IMPORTANTE--!>
        //El proceso que se encarga de leer el código de barras  se encuentra en la clase casos de uso ya que es la actividad principal (Super) y solo desde allí se pueden generar los procesos.


//        scanear = (Button) rootView.findViewById(R.id.escanear_c4);
//        scanear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent("com.arkamovil.android.SCAN");
//                startActivityForResult(intent, 0);
//            }
//        });

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdf.setEnabled(false);
                thread_actualizarregistro = new Thread() {
                    public void run() {
                        GenerarPDF_Inventarios generar = new GenerarPDF_Inventarios();
                        generar.generar(getResources(), getActivity(), id_elemento, String.valueOf(elem_fun.getSede().get(0)), String.valueOf(elem_fun.getDependencia().get(0)), String.valueOf(funcionario.getText()));

                        handler.post(createUI);
                    }
                };

                thread_actualizarregistro.start();
            }
        });

        return rootView;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    //En esta función se llama a la libreria encargada de leer y obtener la información de los códigos de barras despues de que se ha generado el intent.
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//
//        if (resultCode == getActivity().RESULT_OK) {
//            String contenido = intent.getStringExtra("SCAN_RESULT");
//            String formato = intent.getStringExtra("SCAN_RESULT_FORMAT");
//
//            //scanear.setText(contenido);
//
//            WS_Placa ws_placa = new WS_Placa();
//            ws_placa.startWebAccess(rootView, getActivity(), contenido);
//
//        } else if (resultCode == getActivity().RESULT_CANCELED) {
//            // Si se cancelo la captura.
//        }
//
//    }

    public void limpiarTabla() {

        TablaConsultarInventariosAsignados borrar = new TablaConsultarInventariosAsignados();
        borrar.borrarTabla(rootView, getActivity());
        bajar.setVisibility(View.GONE);
        subir.setVisibility(View.GONE);
        pdf.setVisibility(View.GONE);

    }

    //se Actualiza la tabla en dado caso que se reasigne un elemento
    public void actualizarTabla() {


        if (opcion == 1) {
            for (int i = 0; i < lista_sede.size(); i++) {
                if (String.valueOf(sede.getText()).equalsIgnoreCase(lista_sede.get(i))) {
                    seleccion = i;
                }
            }

            if (seleccion > -1) {
                for (int i = 0; i < lista_dependencia.size(); i++) {
                    if (String.valueOf(dependencia.getText()).equalsIgnoreCase(lista_dependencia.get(i))) {
                        seleccion1 = i;
                    }
                }
            }
            if (seleccion1 > -1) {
                for (int i = 0; i < lista_ubicacion.size(); i++) {
                    if (String.valueOf(dependencia.getText()).equalsIgnoreCase(lista_ubicacion.get(i))) {
                        seleccion2 = i;
                    }
                }
            }
            if (seleccion2 > -1) {
                limpiarTabla();

                lista_elemento_funcionario = new ArrayList<String>();
                //verificar ajustes
                elem_dep = new WS_Elemento_dependencia();
                elem_dep.startWebAccess(rootView, getActivity(), lista_id_dependencia.get(seleccion1), 3);
                id_elemento = elem_dep.getId_elemento();
                lista_elemento_funcionario = elem_dep.getFuncionario();
                string_sede = String.valueOf(lista_id_sede.get(seleccion));
                string_dependencia = String.valueOf(lista_id_dependencia.get(seleccion1));
            }
        } else if (opcion == 2) {
            for (int i = 0; i < lista_funcionario.size(); i++) {
                if (String.valueOf(funcionario.getText()).equalsIgnoreCase(lista_documentos.get(i))) {
                    seleccion3 = i;
                }
            }

            if (seleccion3 > -1) {
                limpiarTabla();
                elem_fun.startWebAccess(rootView, getActivity(), lista_documentos.get(seleccion3), 3);
            }
        }

        seleccion = -1;
        seleccion1 = -1;
        seleccion2 = -1;
        seleccion3 = -1;
    }

    final Runnable createUI = new Runnable() {

        public void run() {
            Toast.makeText(getActivity(), "Se ha generado el pdf en la carpeta -> Download -> Inventarios", Toast.LENGTH_LONG).show();
            pdf.setEnabled(true);
        }
    };

    public static String getString_sede() {
        return string_sede;
    }

    public static String getString_funcionario() {
        return string_funcionario;
    }
}

