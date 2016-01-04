package com.arkamovil.android.casos_uso;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.herramientas.Despliegue;
import com.arkamovil.android.menu_desplegable.CasosUso;
import com.arkamovil.android.procesos.FinalizarSesion;
import com.arkamovil.android.servicios_web.WS_Dependencia;
import com.arkamovil.android.servicios_web.WS_Funcionario;
import com.arkamovil.android.servicios_web.WS_InventarioTipoConfirmacion;
import com.arkamovil.android.servicios_web.WS_Login;
import com.arkamovil.android.servicios_web.WS_Sede;
import com.arkamovil.android.servicios_web.WS_ValidarSesion;

import java.util.ArrayList;
import java.util.List;

public class CriteriosLevantamientoFisico extends Fragment {

    private AutoCompleteTextView sede;
    private AutoCompleteTextView dependencia;
    private AutoCompleteTextView funcionario;

    private Thread thread_WS_Fucncionario;
    private Handler handler2 = new Handler();
    private ProgressDialog circuloProgreso;

    private int focus = 0;
    private int selecciono = 0;

    WS_InventarioTipoConfirmacion inventario;


    private static String string_sede;
    private static String string_funcionario;

    private Thread thread_validarSesion;
    private Handler handler_validarSesion = new Handler();
    private String webResponse_sesion;

    private List<String> lista_sede = new ArrayList<String>();
    private List<String> lista_id_sede = new ArrayList<String>();

    private List<String> lista_dependencia = new ArrayList<String>();
    private List<String> lista_id_dependencia = new ArrayList<String>();

    private static List<String> lista_funcionario = new ArrayList<String>();
    private List<String> lista_documento = new ArrayList<String>();


    private  WS_Funcionario ws_funcionario;
    private static View rootView;


    private int seleccion = -1;
    private int seleccion1 = -1;
    private int seleccion2 = -1;
    private int seleccion3 = -1;

    private  int estado_aprob = 0;
    private  int criterio = 2;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fm_verificar_inventarios, container, false);

        getActivity().setTitle("Levantamiento Físico de Inventarios");

        sede = (AutoCompleteTextView) rootView.findViewById(R.id.sede_c2);
        dependencia = (AutoCompleteTextView) rootView.findViewById(R.id.dependencia_c2);
        funcionario = (AutoCompleteTextView) rootView.findViewById(R.id.funcionario_c2);

        dependencia.setEnabled(false);

        final Button des1 = (Button) rootView.findViewById(R.id.filtro_tod_c2);
        final Button des2 = (Button) rootView.findViewById(R.id.filtro_dep_c2);
        final Button des3 = (Button) rootView.findViewById(R.id.filtro_fun_c2);

        des1.setVisibility(View.GONE);
        des2.setVisibility(View.GONE);
        des3.setVisibility(View.GONE);

        final LinearLayout l1 = (LinearLayout) rootView.findViewById(R.id.datos_todos);
        l1.setVisibility(View.GONE);

        final LinearLayout l2 = (LinearLayout) rootView.findViewById(R.id.datos_funcionario);
        l2.setVisibility(View.GONE);

        final LinearLayout l3 = (LinearLayout) rootView.findViewById(R.id.filtro);
        l3.setVisibility(View.GONE);



        Spinner tipo_confirmacion=(Spinner) rootView.findViewById(R.id.tipo_confirmacion);
        String []opciones={"Seleccione ...", "Sin verificar", "Aprobados", "No aprobados", "Radicados", "No radicados"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(),R.layout.spinner_item, opciones);
        tipo_confirmacion.setAdapter(adapter);

        final Spinner criterio_busqueda=(Spinner) rootView.findViewById(R.id.criterios_busqueda);
        String []opciones2={"Seleccione ...", "Todos", "Sede y/o Dependencia"};

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(rootView.getContext(),R.layout.spinner_item, opciones2);
        criterio_busqueda.setAdapter(adapter2);

        tipo_confirmacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                estado_aprob = i;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        criterio_busqueda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                criterio = i;
                if (i == 1) {
                    des1.setVisibility(View.VISIBLE);
                    l1.setVisibility(View.VISIBLE);

                    des2.setVisibility(View.GONE);
                    l2.setVisibility(View.GONE);

                    des3.setVisibility(View.GONE);
                    l3.setVisibility(View.GONE);
                } else if (i == 2) {
                    des1.setVisibility(View.GONE);
                    l1.setVisibility(View.GONE);

                    des2.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.VISIBLE);

                    des3.setVisibility(View.GONE);
                    l3.setVisibility(View.GONE);
                } else if (i == 3) {
                    des1.setVisibility(View.GONE);
                    l1.setVisibility(View.GONE);

                    des2.setVisibility(View.GONE);
                    l2.setVisibility(View.GONE);

                    des3.setVisibility(View.VISIBLE);
                    l3.setVisibility(View.VISIBLE);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        String id_dispositivo = Settings.Secure.getString(rootView.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        WS_Sede ws_sede = new WS_Sede();
        ws_sede.startWebAccess(getActivity(), sede, new Login().getUsuarioSesion(), id_dispositivo);

        lista_sede = ws_sede.getSede();
        lista_id_sede = ws_sede.getId_sede();

        des1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();

                if (on) {
                    l1.setVisibility(View.VISIBLE);
                } else {
                    l1.setVisibility(View.GONE);
                }
            }
        });

        des2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();
                if (on) {
                    l2.setVisibility(View.VISIBLE);
                } else {
                    l2.setVisibility(View.GONE);
                }
            }
        });

        des3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();
                if (on) {
                    l3.setVisibility(View.VISIBLE);
                } else {
                    l3.setVisibility(View.GONE);
                }
            }
        });


        new Despliegue(sede);

        sede.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < lista_sede.size(); i++) {
                    if (String.valueOf(sede.getText()).equals(lista_sede.get(i))) {
                        seleccion = i;
                    }
                }
                //Se envia parametros de vista y de campo AutoComplete al web service de dependencia.
                String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                WS_Dependencia ws_dependencia = new WS_Dependencia();
                ws_dependencia.startWebAccess(getActivity(), dependencia, lista_id_sede.get(seleccion), new Login().getUsuarioSesion(), id_dispositivo);

                lista_dependencia = ws_dependencia.getDependencia();
                lista_id_dependencia = ws_dependencia.getId_dependencia();

                string_sede = String.valueOf(sede.getText());

                dependencia.setText("");
                dependencia.requestFocus();

                //Se despliegan los datos obtenidos de la dependencia.
                new Despliegue(dependencia);
            }
        });

        dependencia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selecciono++;
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        //

        final Button consultar_tod = (Button) rootView.findViewById(R.id.con_tod_c2);
        consultar_tod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estado_aprob > 0) {

                    Fragment fragment = new LevantamientoFisico();
                    Bundle parametro = new Bundle();
                    parametro.putString("estado", (estado_aprob - 1) + "");
                    parametro.putString("criterio", (criterio - 1) + "");
                    parametro.putString("dato", "");

                    fragment.setArguments(parametro);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                } else {
                    Toast.makeText(getActivity(), "Por favor seleccione el estado de aprobación a consultar.", Toast.LENGTH_LONG).show();
                }

            }
        });
        final Button consultar_dep = (Button) rootView.findViewById(R.id.con_dep_c2);
        consultar_dep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( estado_aprob > 0 ){
                    seleccion = -1;
                    seleccion1 = -1;
                    seleccion2 = -1;

                    for (int i = 0; i < lista_sede.size(); i++) {
                        if (String.valueOf(sede.getText()).equalsIgnoreCase(lista_sede.get(i))) {
                            seleccion = i;
                        }
                    }

                    if (seleccion > -1) {
                        if(!String.valueOf(dependencia.getText()).equalsIgnoreCase("")){
                            for (int i = 0; i < lista_dependencia.size(); i++) {
                                if (String.valueOf(dependencia.getText()).equalsIgnoreCase(lista_dependencia.get(i))) {
                                    seleccion1 = i;
                                }
                            }
                            if (seleccion1 > -1) {

                                Fragment fragment = new LevantamientoFisico();
                                Bundle parametro = new Bundle();
                                parametro.putString("estado", (estado_aprob-1)+"");
                                parametro.putString("criterio", (criterio-1)+"");
                                parametro.putString("dato", lista_id_sede.get(seleccion)+ "," + lista_id_dependencia.get(seleccion1) );
                                fragment.setArguments(parametro);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.container, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            } else {
                                Toast.makeText(getActivity(), "La Dependencia ingresada no es valida, verifeque e intente de nuevo.", Toast.LENGTH_LONG).show();
                            }
                        }else{

                            Fragment fragment = new LevantamientoFisico();
                            Bundle parametro = new Bundle();
                            parametro.putString("estado", (estado_aprob-1)+"");
                            parametro.putString("criterio", (criterio-1)+"");
                            parametro.putString("dato", lista_id_sede.get(seleccion) );
                            fragment.setArguments(parametro);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();

                        }
                    } else {
                        Toast.makeText(getActivity(), "La Sede ingresada no es valida, verifeque e intente de nuevo.", Toast.LENGTH_LONG).show();
                    }

                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                }else{
                    Toast.makeText(getActivity(), "Por favor seleccione el estado de aprobación a consultar.", Toast.LENGTH_LONG).show();
                }

            }
        });


        final Button consultar_fun = (Button) rootView.findViewById(R.id.con_fun_c2);
        consultar_fun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( estado_aprob > 0 ){

                for (int i = 0; i < lista_funcionario.size(); i++) {
                    if (String.valueOf(funcionario.getText()).equalsIgnoreCase(lista_documento.get(i) + " - " +lista_funcionario.get(i))) {
                        seleccion3 = i;
                    }
                }
                if (seleccion3 > -1) {

                    Fragment fragment = new LevantamientoFisico();
                    Bundle parametro = new Bundle();
                    parametro.putString("estado", (estado_aprob-1)+"");
                    parametro.putString("criterio", (criterio-1)+"");
                    parametro.putString("dato", lista_documento.get(seleccion3) );
                    fragment.setArguments(parametro);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                } else {
                    Toast.makeText(getActivity(), "El funcionario ingresado no es valido, por favor verifiquelo e intente de nuevo.", Toast.LENGTH_LONG).show();
                }
                }else{
                    Toast.makeText(getActivity(), "Por favor seleccione el estado de aprobación a consultar.", Toast.LENGTH_LONG).show();
                }
            }

        });

        funcionario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        funcionario.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                final String text = s.toString();
                if (s.length() >= 3 && (focus == s.length()|| focus == (s.length() + 1) || focus == (s.length() - 1))) {
                    thread_WS_Fucncionario = new Thread() {
                        public void run() {
                            Looper.prepare();
                            String id_dispositivo = Settings.Secure.getString(rootView.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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
                new Despliegue(funcionario);
            }
        });


        funcionario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                 public void onFocusChange(View v, boolean hasFocus) {
                                                     if (hasFocus) {
                                                         try {
                                                             funcionario.setText("");
                                                             funcionario.setTextColor(getResources().getColor(R.color.NEGRO));
                                                         } catch (NumberFormatException e) {
                                                         }
                                                     }
                                                 }
                                             }
        );

        return rootView;
    }


    final Runnable Funcionario = new Runnable() {
        public void run() {
            ws_funcionario.cargarListaFuncionario(getActivity(), funcionario, "null");
            lista_funcionario = ws_funcionario.getFun_nombre();
            lista_documento = ws_funcionario.getFun_identificacion();
        }
    };

    final Runnable ValidarSesion = new Runnable() {

        public void run() {

           if("sesion_expirada".equals(webResponse_sesion)){
               new FinalizarSesion().sesionExpirada(getActivity());
               final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
           }else if("sesion_fraudulenta".equals(webResponse_sesion)){
               new FinalizarSesion().sesionInvalida(getActivity());
               final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
           }
        }
    };


}

