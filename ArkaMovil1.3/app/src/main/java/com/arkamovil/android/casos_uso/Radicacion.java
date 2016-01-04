package com.arkamovil.android.casos_uso;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.procesos.FinalizarSesion;
import com.arkamovil.android.servicios_web.WS_ElementoPlaca;
import com.arkamovil.android.servicios_web.WS_Funcionario;
import com.arkamovil.android.servicios_web.WS_InventarioTipoConfirmacion;
import com.arkamovil.android.servicios_web.WS_Radicar;
import com.arkamovil.android.servicios_web.WS_ValidarSesion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Radicacion extends Fragment {

    private Thread thread;
    private Thread threadRadicar;
    private Handler handler = new Handler();

    private Thread thread_validarSesion;
    private Handler handler_validarSesion = new Handler();
    private String webResponse_sesion;

    Button botonRadicar;

    ImageView subir;
    ImageView bajar;

    private WS_InventarioTipoConfirmacion inventario;
    private AutoCompleteTextView funcionario;

    private List<String> lista_sede = new ArrayList<String>();
    private List<String> lista_id_sede = new ArrayList<String>();

    private List<String> lista_dependencia = new ArrayList<String>();
    private List<String> lista_id_dependencia = new ArrayList<String>();

    private static List<String> lista_funcionario = new ArrayList<String>();
    private static List<String> lista_documento = new ArrayList<String>();

    private int seleccion3 = -1;

    private View rootView;
    private int offset = 0;
    private int limit = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fm_radicacion, container, false);

        funcionario = (AutoCompleteTextView) rootView.findViewById(R.id.funcionario_radicado);

        final String estado = "5";
        final String criterio  = "2";

        WS_Funcionario ws_funcionario = new WS_Funcionario();

        ws_funcionario.cargarListaFuncionario(getActivity(), funcionario, "null");

        lista_funcionario = ws_funcionario.getFun_nombre();
        lista_documento = ws_funcionario.getFun_identificacion();

        final Button consultar = (Button) rootView.findViewById(R.id.con_fun_radicado);
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    for (int i = 0; i < lista_funcionario.size(); i++) {
                        if (String.valueOf(funcionario.getText()).equalsIgnoreCase(lista_documento.get(i) + " - " +lista_funcionario.get(i))) {
                            seleccion3 = i;
                        }
                    }
                    if (seleccion3 > -1) {

                        final String dato = lista_documento.get(seleccion3);

                        thread = new Thread() {
                            public void run() {

                                Looper.prepare();

                                String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                                inventario = new WS_InventarioTipoConfirmacion();
                                inventario.startWebAccess(estado, criterio, dato, offset, limit,  new Login().getUsuarioSesion(), id_dispositivo);

                                handler.post(createUI);
                            }
                        };

                        thread.start();

                    } else {
                        Toast.makeText(getActivity(), "El funcionario ingresado no es valido, por favor verifiquelo e intente de nuevo.", Toast.LENGTH_LONG).show();
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

        botonRadicar = (Button) rootView.findViewById(R.id.boton_radicado);
        subir = (ImageView) rootView.findViewById(R.id.subir_radicado);
        bajar = (ImageView) rootView.findViewById(R.id.bajar_radicado);


        subir.setVisibility(View.GONE);
        bajar.setVisibility(View.GONE);
        botonRadicar.setVisibility(View.GONE);

        botonRadicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> dep = new ArrayList<String>();
                for (int i=0; i < posicionRadicado.size(); i++) {
                    dep.add(id_dependencia.get(posicionRadicado.get(i)));
                }

                String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                WS_Radicar radicar = new WS_Radicar();
                radicar.startWebAccess(doc_fun.get(0), dep, new Login().getUsuarioSesion(), id_dispositivo);
            }
        });

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
            crear(rootView, getActivity(), inventario.getNomb_fun(), inventario.getDoc_fun(), inventario.getId_sede(), inventario.getSede(), inventario.getId_dependencia(), inventario.getDependencia());
        }
    };

    final Runnable RadicacionUI = new Runnable() {

        public void run() {
            Toast.makeText(getActivity(), "Han sido radicados los elementos.", Toast.LENGTH_LONG).show();
        }
    };

    public void limpiarTabla() {
        borrarTabla();
    }


    //************DESDE AQUÍ SE CREAN LOS EVENTOS DE LA TABLA************//

    private double f1 = 0.18;
    private double f2 = 0.35;
    private double f3 = 0.75;
    private double f4 = 0.20;

    private static TableLayout tabla;
    private static TableLayout cabecera;

    private static TableRow.LayoutParams layoutFila;

    private static TableRow.LayoutParams layoutDependencia;
    private static TableRow.LayoutParams layoutRadicar;

    private static Activity actividad;
    private static View vista;

    private static final int factor = 5;


    private static List<String> id_elemento;
    private static List<String> nom_fun;
    private static List<String> doc_fun;
    private static List<String> id_sede;
    private static List<String> sede;
    private static List<String> id_dependencia;
    private static List<String> dependencia;
    private static List<String> radicado;

    private List<Boolean> arrayRadicado = new ArrayList<Boolean>();
    private List<Integer> posicionRadicado = new ArrayList<Integer>();


    private static int inicio;

    private static int tamanoPantalla;


    private static int MAX_FILAS = 0;


    public void crear(View rootView, Activity actividad, List<String> nom_fun, List<String> doc_fun, List<String> id_sede, List<String> sede, List<String> id_dependencia, List<String> dependencia) {

        this.actividad = actividad;
        this.vista = rootView;


        this.nom_fun=nom_fun;
        this.doc_fun=doc_fun;
        this.id_sede=id_sede;
        this.sede=sede;
        this.id_dependencia=id_dependencia;
        this.dependencia=dependencia;
        this.radicado = radicado;

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
            botonRadicar.setVisibility(View.VISIBLE);
            if(doc_fun.size() > factor){
                subir.setVisibility(View.VISIBLE);
                bajar.setVisibility(View.VISIBLE);
            }

        } else {
            Toast.makeText(actividad, "No existen inventarios por radicar para los criterios seleccionados.", Toast.LENGTH_LONG).show();
        }


    }

    public void agregarCabecera() {

        TableRow fila;
        TextView txtUbicacion;
        TextView txtDetalle;

        fila = new TableRow(actividad);
        fila.setLayoutParams(layoutFila);

        txtUbicacion = new TextView(actividad);
        txtDetalle = new TextView(actividad);

        txtUbicacion.setText("Ubicación del Inventario");
        txtUbicacion.setGravity(Gravity.CENTER_HORIZONTAL);
        txtUbicacion.setTextAppearance(actividad, R.style.etiqueta);
        txtUbicacion.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtUbicacion.setLayoutParams(layoutDependencia);

        txtDetalle.setText("Radicar");
        txtDetalle.setGravity(Gravity.CENTER_HORIZONTAL);
        txtDetalle.setTextAppearance(actividad, R.style.etiqueta);
        txtDetalle.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtDetalle.setLayoutParams(layoutRadicar);

        fila.addView(txtUbicacion);
        fila.addView(txtDetalle);
        cabecera.addView(fila);
    }


    public void agregarFilasTabla() {

        TableRow fila;
        TextView txtdependencia;
        CheckBox txtverInventario;


        for (int i = 0; i < MAX_FILAS; i++) {
            fila = new TableRow(actividad);
            fila.setLayoutParams(layoutFila);

            txtdependencia = new TextView(actividad);
            txtverInventario = new CheckBox(actividad);

            txtdependencia.setText(dependencia.get(inicio + i));
            txtdependencia.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtdependencia.setTextAppearance(actividad, R.style.etiqueta);
            txtdependencia.setBackgroundResource(R.drawable.tabla_celda);
            txtdependencia.setLayoutParams(layoutDependencia);

            txtverInventario.setId(this.inicio + i);
            txtverInventario.setBackgroundResource(R.drawable.tabla_celda);
            if ("t".equals(radicado.get(inicio + i))) {
                txtverInventario.setChecked(true);
                txtverInventario.setEnabled(false);
            }
            final CheckBox finalTxtverInventario = txtverInventario;
            txtverInventario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finalTxtverInventario.isChecked()){
                        for (int i=0; i<posicionRadicado.size(); i++){
                            if(posicionRadicado.get(i) == v.getId()){
                                posicionRadicado.remove(i);
                                arrayRadicado.remove(i);
                            }
                        }
                        posicionRadicado.add(v.getId());
                        arrayRadicado.add(true);
                    }else{
                        for (int i=0; i<posicionRadicado.size(); i++){
                            if(posicionRadicado.get(i) == v.getId()){
                                posicionRadicado.remove(i);
                                arrayRadicado.remove(i);
                            }
                        }

                    }
                }
            });

            txtverInventario.setLayoutParams(layoutRadicar);
            fila.addView(txtdependencia);
            fila.addView(txtverInventario);

            tabla.addView(fila);
        }
    }

    public void cargarElementos() {

        tabla = (TableLayout) vista.findViewById(R.id.tabla_radicado);
        cabecera = (TableLayout) vista.findViewById(R.id.cabecera_radicado);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        layoutDependencia = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);
        layoutRadicar = new TableRow.LayoutParams((int) (tamanoPantalla * f4), TableRow.LayoutParams.MATCH_PARENT);

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

        tabla = (TableLayout) vista.findViewById(R.id.tabla_radicado);
        cabecera = (TableLayout) vista.findViewById(R.id.cabecera_radicado);
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        layoutDependencia = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);
        layoutRadicar = new TableRow.LayoutParams((int) (tamanoPantalla * f4), TableRow.LayoutParams.MATCH_PARENT);

        tabla.removeAllViews();
        cabecera.removeAllViews();
    }
}

