package com.arkamovil.android.servicios_web;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.arkamovil.android.borrar.Modificar_Informacion_Elementos_Scanner;
import com.arkamovil.android.borrar.WS_Asignaciones;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WS_Placa {

    private final String NAMESPACE = "urn:arka";
    //private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";

    private String URL;

    private final String SOAP_ACTION = "urn:arka/consultar_placa";
    private final String METHOD_NAME = "consultar_placa";

    private static Modificar_Informacion_Elementos_Scanner dialog;

    public Thread getThread() {
        return thread;
    }

    private Thread thread;
    private Thread thread_estado;

    private Handler handler = new Handler();

    private Activity act;
    private View rootView;

    private static List<String> descripcion = new ArrayList<String>();
    private static List<String> id_elemento = new ArrayList<String>();
    private static List<String> nivel = new ArrayList<String>();
    private static List<String> marca = new ArrayList<String>();
    private static List<String> placa = new ArrayList<String>();
    private static List<String> serie = new ArrayList<String>();
    private static List<String> valor = new ArrayList<String>();
    private static List<String> subtotal = new ArrayList<String>();
    private static List<String> iva = new ArrayList<String>();
    private static List<String> total = new ArrayList<String>();


    public static List<String> getTotal() {
        return total;
    }

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

    public static List<String> getValor() {
        return valor;
    }

    public static List<String> getSubtotal() {
        return subtotal;
    }

    public static List<String> getIva() {
        return iva;
    }

    public static List<String> getDescripcion() {
        return descripcion;
    }

    public static List<String> getId_elemento() {
        return id_elemento;
    }

    public void startWebAccess(View rootView, Activity actividad, final String numplaca, final String usuario, final String dispositivo) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        this.rootView = rootView;
        this.act = actividad;

        descripcion = new ArrayList<String>();
        id_elemento = new ArrayList<String>();
        nivel = new ArrayList<String>();
        marca = new ArrayList<String>();
        placa = new ArrayList<String>();
        serie = new ArrayList<String>();
        valor = new ArrayList<String>();
        subtotal = new ArrayList<String>();
        iva = new ArrayList<String>();
        total = new ArrayList<String>();

        thread = new Thread() {
            public void run() {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("placa", numplaca);
                request.addProperty("usuario", usuario);
                request.addProperty("dispositivo", dispositivo);

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
                        try{
                            obj3 = (SoapObject) obj2.getProperty(7);
                            valor.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            valor.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(9);
                            subtotal.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            subtotal.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(11);
                            iva.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            iva.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(11);
                            total.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            total.add("");
                        }
                    }

                } catch (Exception exception) {
                }
                handler.post(createUI);
            }
        };

        thread.start();
    }

    final Runnable createUI = new Runnable() {

        public void run() {
            if (id_elemento.size() > 0) {

                thread_estado = new Thread() {
                    public void run() {
                        WS_Asignaciones ws_asignaciones = new WS_Asignaciones();
                        ws_asignaciones.startWebAccess(act, id_elemento.get(0), 0);

                        handler.post(createESTADO);
                    }

                };

                thread_estado.start();

            } else {
                Toast.makeText(act, "No se ha encontrado ningún registro con la placa escaneada", Toast.LENGTH_LONG).show();
            }
        }
    };

    final Runnable createESTADO = new Runnable() {

        public void run() {
            WS_Asignaciones ws_asignaciones = new WS_Asignaciones();
            dialog = new Modificar_Informacion_Elementos_Scanner(act);
            dialog.show();
        }
    };

    public void cerrarDialog() {
        dialog.dismiss();
    }
}
