package com.arkamovil.android.borrar;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
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
import com.arkamovil.android.servicios_web.WS_ConsultarPlacaImagen;
import com.arkamovil.android.servicios_web.WS_Dependencia;
import com.arkamovil.android.servicios_web.WS_ElementoPlaca;
import com.arkamovil.android.servicios_web.WS_Funcionario;
import com.arkamovil.android.servicios_web.WS_Sede;
import com.arkamovil.android.servicios_web.WS_Ubicacion;

import java.util.ArrayList;
import java.util.List;

public class CasoUso5 extends Fragment {

    private AutoCompleteTextView sede;
    private AutoCompleteTextView dependencia;
    private AutoCompleteTextView ubicacion;
    private AutoCompleteTextView funcionario;
    private ImageView bajar;
    private ImageView subir;

    private View rootView;

    private static List<String> lista_sede;
    private static List<String> lista_id_sede;
    private static List<String> lista_dependencia;
    private static List<String> lista_id_dependencia;
    private static List<String> lista_ubicacion;
    private static List<String> lista_id_ubicacion;
    private List<String> lista_funcionario = new ArrayList<String>();
    private List<String> lista_documentos = new ArrayList<String>();
    private static List<String> id_elemento = new ArrayList<String>();
    private static List<String> lista_elemento_funcionario = new ArrayList<String>();

    private static WS_Elemento_funcionario elem_fun;

    private static WS_Elemento_dependencia elem_dep;

    private static String string_sede;
    private static String string_dependencia;
    private static String string_funcionario;


    private int seleccion = -1;
    private int seleccion1 = -1;
    private int seleccion2 = -1;
    private int seleccion3 = -1;

    private static int funcion = 0;

    public static int getFuncion() {
        return funcion;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fm_casouso5, container, false);

        sede = (AutoCompleteTextView) rootView.findViewById(R.id.sede_c5);
        dependencia = (AutoCompleteTextView) rootView.findViewById(R.id.dependencia_c5);
        ubicacion = (AutoCompleteTextView) rootView.findViewById(R.id.ubicacion_c5);
        funcionario = (AutoCompleteTextView) rootView.findViewById(R.id.funcionario_c5);
        bajar = (ImageView) rootView.findViewById(R.id.bajar_c5);
        subir = (ImageView) rootView.findViewById(R.id.subir_c5);

        dependencia.setEnabled(false);
        ubicacion.setEnabled(false);

        bajar.setVisibility(View.GONE);
        subir.setVisibility(View.GONE);

        String id_dispositivo = Settings.Secure.getString(rootView.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        WS_Sede ws_sede = new WS_Sede();
        ws_sede.startWebAccess(getActivity(), sede, new Login().getUsuarioSesion(), id_dispositivo);
        lista_sede = ws_sede.getSede();
        lista_id_sede = ws_sede.getId_sede();

        final LinearLayout l1 = (LinearLayout) rootView.findViewById(R.id.dep_c5);
        l1.setVisibility(View.GONE);

        final LinearLayout l2 = (LinearLayout) rootView.findViewById(R.id.fun_c5);
        l2.setVisibility(View.GONE);

        //

        Button des1 = (Button) rootView.findViewById(R.id.des_dep_c5);
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

        Button des2 = (Button) rootView.findViewById(R.id.des_fun_c5);
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

        new Despliegue(funcionario);

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
                ubicacion.setText("");
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

        Button con_dep = (Button) rootView.findViewById(R.id.con_dep_c5);
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
                            elem_dep.startWebAccess(rootView, getActivity(), lista_id_dependencia.get(seleccion1), 1);
                            id_elemento = elem_dep.getId_elemento();
                            lista_elemento_funcionario = elem_dep.getFuncionario();
                            string_dependencia = String.valueOf(dependencia.getText());
                            funcion = 1;
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

        Button con_fun = (Button) rootView.findViewById(R.id.con_fun_c5);
        con_fun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                limpiarTabla();

                seleccion3 = -1;

                for (int i = 0; i < lista_funcionario.size(); i++) {
                    if (String.valueOf(funcionario.getText()).equalsIgnoreCase(lista_documentos.get(i))) {
                        seleccion3 = i;
                    }
                }

                if (seleccion3 > -1) {
                    elem_fun = new WS_Elemento_funcionario();
                    elem_fun.startWebAccess(rootView, getActivity(), lista_documentos.get(seleccion3), 1);
                    id_elemento = elem_fun.getId_elemento();
                    lista_elemento_funcionario = elem_fun.getFuncionario();
                    string_funcionario = String.valueOf(funcionario.getText());
                    funcion = 2;

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

        //boton para bajar los elementos
        bajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TablaConsultarInventario baj = new TablaConsultarInventario();
                baj.bajar(rootView, getActivity());
            }
        });
        //llamar metodo en clase CrearTablas para ir hacia los primeros registros mostrados en la tabla
        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TablaConsultarInventario sub = new TablaConsultarInventario();
                sub.subir(rootView, getActivity());
            }
        });

        return rootView;
    }

    public void limpiarTabla() {

        TablaConsultarInventario borrar = new TablaConsultarInventario();
        borrar.borrarTabla(rootView, getActivity());
        bajar.setVisibility(View.GONE);
        subir.setVisibility(View.GONE);
    }

}
