package com.arkamovil.android.borrar;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.arkamovil.android.R;
import com.arkamovil.android.borrar.TablaAsignarInventarios;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WS_ElementosAsignar {

    private final String NAMESPACE = "urn:arka";
    private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";
    private final String SOAP_ACTION = "urn:arka/asignar_elementos";
    private final String METHOD_NAME = "asignar_elementos";

    public Thread getThread() {
        return thread;
    }

    private Thread thread;
    private Handler handler = new Handler();

    private Activity act;
    private View rootView;

    private static List<String> descripcion = new ArrayList<String>();
    private static List<String> id_elemento = new ArrayList<String>();
    private static List<String> nivel = new ArrayList<String>();
    private static List<String> marca = new ArrayList<String>();
    private static List<String> placa = new ArrayList<String>();
    private static List<String> serie = new ArrayList<String>();
    private static List<String> sede = new ArrayList<String>();
    private static List<String> dependencia = new ArrayList<String>();
    private static List<String> funcionario = new ArrayList<String>();

    public static List<String> getNivel() {
        return nivel;
    }

    public static List<String> getMarca() {
        return marca;
    }

    public static List<String> getPlaca() {
        return placa;
    }

    public static List<String> getSerie() {
        return serie;
    }

    public static List<String> getDescripcion() {
        return descripcion;
    }

    public static List<String> getId_elemento() {
        return id_elemento;
    }

    public void startWebAccess(View rootView, Activity actividad, final String fecha_inicial, final String fecha_final) {

        this.rootView = rootView;
        this.act = actividad;

        descripcion = new ArrayList<String>();
        id_elemento = new ArrayList<String>();
        nivel = new ArrayList<String>();
        marca = new ArrayList<String>();
        placa = new ArrayList<String>();
        serie = new ArrayList<String>();
        sede = new ArrayList<String>();
        dependencia = new ArrayList<String>();
        funcionario = new ArrayList<String>();

        thread = new Thread() {
            public void run() {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("fecha_inicio", fecha_inicial);
                request.addProperty("fecha_final", fecha_final);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);

                try {

                    httpTransport.call(SOAP_ACTION, envelope);

                    SoapObject obj1 = (SoapObject)envelope.bodyIn;

                    Vector<?> responseVector = (Vector<?>) obj1.getProperty(0);

                    for (int i = 0; i < responseVector.size(); i++) {
                        SoapObject obj2 = (SoapObject) responseVector.get(i);
                        SoapObject obj3;

                        try{
                            obj3 = (SoapObject) obj2.getProperty(1);
                            id_elemento.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            id_elemento.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(3);
                            descripcion.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            descripcion.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(5);
                            nivel.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            nivel.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(7);
                            marca.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            marca.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(9);
                            placa.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            placa.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(11);
                            serie.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            serie.add("");
                        }
                    }

                } catch (Exception exception) {
                    Log.v("mensaje", exception.toString());
                }
                handler.post(createUI);
            }
        };

        thread.start();
    }

    final Runnable createUI = new Runnable() {

        public void run() {
            TablaAsignarInventarios tablaAsignarInventarios = new TablaAsignarInventarios();
            tablaAsignarInventarios.crear(rootView, act, id_elemento, descripcion, placa);//funcionario y confirmacion

            if (id_elemento.size() > 0) {
//                ImageView bajar = (ImageView) rootView.findViewById(R.id.bajar_c2);
//                ImageView subir = (ImageView) rootView.findViewById(R.id.subir_c2);
//                Button asignar = (Button) rootView.findViewById(R.id.asignar_c2);

//                bajar.setVisibility(View.VISIBLE);
//                subir.setVisibility(View.VISIBLE);
//                asignar.setVisibility(View.VISIBLE);
            }

            Button consultar = (Button) rootView.findViewById(R.id.con_fun_c2);
            consultar.setEnabled(true);

        }
    };


}