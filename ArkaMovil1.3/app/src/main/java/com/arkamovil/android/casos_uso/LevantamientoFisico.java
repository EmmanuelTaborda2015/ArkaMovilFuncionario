package com.arkamovil.android.casos_uso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.servicios_web.WS_InventarioTipoConfirmacion;

import java.util.List;

public class LevantamientoFisico extends Fragment {

    private ProgressDialog circuloProgreso;

    private Thread thread;
    private Handler handler = new Handler();

    private String estado;

    private WS_InventarioTipoConfirmacion inventario;

    private Thread thread_validarSesion;
    private Handler handler_validarSesion = new Handler();
    private String webResponse_sesion;

    private  int iniciar = 0;
    private View rootView;

    public static int getOffset() {
        return offset;
    }

    private static int offset = 0;

    public int getLimit() {
        return limit;
    }

    private int limit = 10;

    ImageView subir;
    ImageView bajar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fm_levantamiento_fisico, container, false);

        estado  = getArguments().getString("estado");
        final String criterio  = getArguments().getString("criterio");
        final String dato  = getArguments().getString("dato");

        circuloProgreso = ProgressDialog.show(getActivity(), "", "Espere por favor ...", true);

        thread = new Thread() {
            public void run() {

                Looper.prepare();

                String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                inventario = new WS_InventarioTipoConfirmacion();
                inventario.startWebAccess(estado, criterio, dato, offset, limit, new Login().getUsuarioSesion(), id_dispositivo);
                handler.post(createUI);
            }
        };

        thread.start();

        bajar = (ImageView) rootView.findViewById(R.id.bajar_levantamiento_fisico);
        subir = (ImageView) rootView.findViewById(R.id.subir_levantamiento_fisico);

        bajar.setVisibility(View.GONE);
        subir.setVisibility(View.GONE);

        bajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bajar();
                bajar.setEnabled(false);
                offset++;
                thread = new Thread() {
                    public void run() {

                        Looper.prepare();
                        String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                        inventario = new WS_InventarioTipoConfirmacion();
                        inventario.startWebAccess(estado, criterio, dato, offset, limit, new Login().getUsuarioSesion(), id_dispositivo);

                        handler.post(createUI);
                    }
                };

                thread.start();
            }
        });

        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //subir();
                subir.setEnabled(false);
                if (offset > 0) {
                    offset--;
                    thread = new Thread() {
                        public void run() {

                            Looper.prepare();
                            String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                            inventario = new WS_InventarioTipoConfirmacion();
                            inventario.startWebAccess(estado, criterio, dato, offset, limit, new Login().getUsuarioSesion(), id_dispositivo);

                            handler.post(createUI);
                        }
                    };

                    thread.start();
                }
            }
        });

        return rootView;
    }

    final Runnable createUI = new Runnable() {

        public void run() {
            circuloProgreso.dismiss();
            if("0".equals(estado)){
                getActivity().setTitle("Levantamiento Físico de Inventarios - Sin Verificar");
            }else if("1".equals(estado)){
                getActivity().setTitle("Levantamiento Físico de Inventarios - Aprobados");
            }else if("2".equals(estado)){
                getActivity().setTitle("Levantamiento Físico de Inventarios - No Aprobados");
            }else if("3".equals(estado)){
                getActivity().setTitle("Levantamiento Físico de Inventarios - Radicados");
            }else if("4".equals(estado)){
                getActivity().setTitle("Levantamiento Físico de Inventarios - No Radicados");
            }
            if(iniciar==0){
                iniciar++;
            }else{
                borrarTabla();
            }
            crear(rootView, getActivity(), inventario.getNomb_fun(), inventario.getDoc_fun(), inventario.getId_sede(), inventario.getSede(), inventario.getId_dependencia(), inventario.getDependencia());
            bajar.setEnabled(true);
            subir.setEnabled(true);
        }
    };

    public void limpiarTabla() {
        borrarTabla();
    }


    //************DESDE AQUÍ SE CREAN LOS EVENTOS DE LA TABLA************//

    private double f1 = 0.15;
    private double f2 = 0.30;
    private double f3 = 0.20;
    private double f4 = 0.12;
    private double f5 = 0.18;

    private static TableLayout tabla;
    private static TableLayout cabecera;

    private static TableRow.LayoutParams layoutFila;
    private static TableRow.LayoutParams layoutId;
    private static TableRow.LayoutParams layoutTexto;
    private static TableRow.LayoutParams layoutSede;
    private static TableRow.LayoutParams layoutDependencia;
    private static TableRow.LayoutParams layoutMod;

    private static Activity actividad;
    private static View vista;

    private static final int factor = new LevantamientoFisico().getLimit();


    private static List<String> id_elemento;
    private static List<String> nom_fun;
    private static List<String> doc_fun;
    private static List<String> id_sede;
    private static List<String> sede;
    private static List<String> id_dependencia;
    private static List<String> dependencia;
    private static List<String> id_espacio;
    private static List<String> espacio;

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
        this.id_espacio=id_espacio;
        this.espacio=espacio;

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
            Fragment fragment = new CriteriosLevantamientoFisico();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }


        if(doc_fun.size() == factor){
            bajar.setVisibility(View.VISIBLE);
        }else{
            bajar.setVisibility(View.INVISIBLE);
        }
        if(new LevantamientoFisico().getOffset() > 0){
            subir.setVisibility(View.VISIBLE);
        }else{
            subir.setVisibility(View.INVISIBLE);
        }


    }

    public void agregarCabecera() {

        TableRow fila;
        TextView txtPlaca;
        TextView txtDescripcion;
        TextView txtUbicacion;
        TextView txtSede;
        TextView txtDetalle;

        fila = new TableRow(actividad);
        fila.setLayoutParams(layoutFila);

        txtPlaca = new TextView(actividad);
        txtDescripcion = new TextView(actividad);
        txtSede = new TextView(actividad);
        txtUbicacion = new TextView(actividad);
        txtDetalle = new TextView(actividad);

        txtPlaca.setText("Documento");
        txtPlaca.setGravity(Gravity.CENTER_HORIZONTAL);
        txtPlaca.setTextAppearance(actividad, R.style.etiqueta);
        txtPlaca.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtPlaca.setLayoutParams(layoutId);

        txtDescripcion.setText("Nombre");
        txtDescripcion.setGravity(Gravity.CENTER_HORIZONTAL);
        txtDescripcion.setTextAppearance(actividad, R.style.etiqueta);
        txtDescripcion.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtDescripcion.setLayoutParams(layoutTexto);

        txtSede.setText("Sede");
        txtSede.setGravity(Gravity.CENTER_HORIZONTAL);
        txtSede.setTextAppearance(actividad, R.style.etiqueta);
        txtSede.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtSede.setLayoutParams(layoutDependencia);

        txtUbicacion.setText("Dependencia");
        txtUbicacion.setGravity(Gravity.CENTER_HORIZONTAL);
        txtUbicacion.setTextAppearance(actividad, R.style.etiqueta);
        txtUbicacion.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtUbicacion.setLayoutParams(layoutDependencia);

        txtDetalle.setText("Detalles");
        txtDetalle.setGravity(Gravity.CENTER_HORIZONTAL);
        txtDetalle.setTextAppearance(actividad, R.style.etiqueta);
        txtDetalle.setBackgroundResource(R.drawable.tabla_celda_cabecera);
        txtDetalle.setLayoutParams(layoutMod);

        fila.addView(txtPlaca);
        fila.addView(txtDescripcion);
        fila.addView(txtSede);
        fila.addView(txtUbicacion);
        fila.addView(txtDetalle);
        cabecera.addView(fila);
    }

    public void agregarFilasTabla() {

        TableRow fila;
        TextView txtnombre;
        TextView txtdocumento;
        TextView txtdependencia;
        TextView txtsede;
        ImageView txtverInventario;

        for (int i = 0; i < MAX_FILAS; i++) {
            fila = new TableRow(actividad);
            fila.setLayoutParams(layoutFila);

            txtnombre = new TextView(actividad);
            txtdocumento = new TextView(actividad);
            txtdependencia = new TextView(actividad);
            txtsede = new TextView(actividad);
            txtverInventario = new ImageView(actividad);

            txtnombre.setText(doc_fun.get(inicio + i));
            txtnombre.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtnombre.setTextAppearance(actividad, R.style.etiqueta);
            txtnombre.setBackgroundResource(R.drawable.tabla_celda);
            txtnombre.setLayoutParams(layoutId);

            txtdocumento.setText(nom_fun.get(inicio + i));
            txtdocumento.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtdocumento.setTextAppearance(actividad, R.style.etiqueta);
            txtdocumento.setBackgroundResource(R.drawable.tabla_celda);
            txtdocumento.setLayoutParams(layoutTexto);

            txtsede.setText(sede.get(inicio + i));
            txtsede.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtsede.setTextAppearance(actividad, R.style.etiqueta);
            txtsede.setBackgroundResource(R.drawable.tabla_celda);
            txtsede.setLayoutParams(layoutDependencia);

            txtdependencia.setText(dependencia.get(inicio + i));
            txtdependencia.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txtdependencia.setTextAppearance(actividad, R.style.etiqueta);
            txtdependencia.setBackgroundResource(R.drawable.tabla_celda);
            txtdependencia.setLayoutParams(layoutDependencia);

            txtverInventario.setImageResource(R.drawable.ver);
            txtverInventario.setId(this.inicio + i);
            txtverInventario.setBackgroundResource(R.drawable.tabla_celda);
            txtverInventario.setLayoutParams(layoutMod);
            txtverInventario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Fragment fragment = new ElementosInventario();
                    Bundle parametro = new Bundle();
                    parametro.putString("doc_fun", doc_fun.get(v.getId()));
                    parametro.putString("id_dep", id_dependencia.get(v.getId()));
                    fragment.setArguments(parametro);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });


            fila.addView(txtnombre);
            fila.addView(txtdocumento);
            fila.addView(txtsede);
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
        layoutDependencia = new TableRow.LayoutParams((int) (tamanoPantalla * f5), TableRow.LayoutParams.MATCH_PARENT);
        layoutDependencia = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);
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
        layoutDependencia = new TableRow.LayoutParams((int) (tamanoPantalla * f3), TableRow.LayoutParams.MATCH_PARENT);
        layoutMod = new TableRow.LayoutParams((int) (tamanoPantalla * f4), TableRow.LayoutParams.MATCH_PARENT);

        tabla.removeAllViews();
        cabecera.removeAllViews();
    }
}

