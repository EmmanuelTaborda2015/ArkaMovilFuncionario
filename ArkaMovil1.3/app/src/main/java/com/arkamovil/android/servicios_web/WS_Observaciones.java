package com.arkamovil.android.servicios_web;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WS_Observaciones {

    private final String NAMESPACE = "urn:arka";
    //private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";

    private String URL;

    private final String SOAP_ACTION = "urn:arka/consultar_observacion";
    private final String METHOD_NAME = "consultar_observacion";


    private List<String> observacion = new ArrayList<String>();
    private List<String> tipo_movimiento = new ArrayList<String>();
    private List<String> id_levantamiento = new ArrayList<String>();
    private List<String> fecha_registro = new ArrayList<String>();
    private List<String> creador_observacion = new ArrayList<String>();

    public List<String> getCreador_observacion() {
        return creador_observacion;
    }

    public List<String> getFecha_registro() {
        return fecha_registro;
    }

    public List<String> getTipo_movimiento() {
        return tipo_movimiento;
    }

    public List<String> getObservacion() {
        return observacion;
    }

    public List<String> getId_levantamiento() {
        return id_levantamiento;
    }


    public void startWebAccess(String id_elemento, String usuario, String dispositivo) {


        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("id_elemento", id_elemento);
        request.addProperty("usuario", usuario);
        request.addProperty("dispositivo", dispositivo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(URL);

        try {

            httpTransport.call(SOAP_ACTION, envelope);
            SoapObject obj1 = (SoapObject) envelope.bodyIn;

            Vector<?> responseVector = (Vector<?>) obj1.getProperty(0);

            for (int i = 0; i < responseVector.size(); i++) {
                SoapObject obj2 = (SoapObject) responseVector.get(i);
                SoapObject obj3;
                try {
                    obj3 = (SoapObject) obj2.getProperty(1);
                    id_levantamiento.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    id_levantamiento.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(3);
                    observacion.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    observacion.add("Sin observaciones");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(5);
                    tipo_movimiento.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    tipo_movimiento.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(7);
                    fecha_registro.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    fecha_registro.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(9);
                    creador_observacion.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    creador_observacion.add("");
                }
            }
        } catch (Exception exception) {
            Log.v("mensaje", exception.toString());
        }
    }

}