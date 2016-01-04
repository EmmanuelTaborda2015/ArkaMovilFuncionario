package com.arkamovil.android.servicios_web;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WS_InventarioTipoConfirmacion {

    private final String NAMESPACE = "urn:arka";
    //private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";

    private String URL;

    private final String SOAP_ACTION = "urn:arka/tipoConfirmacionInventario";
    private final String METHOD_NAME = "tipoConfirmacionInventario";

    private List<String> doc_fun = new ArrayList<String>();
    private List<String> nomb_fun = new ArrayList<String>();

    private List<String> id_sede = new ArrayList<String>();
    private List<String> sede = new ArrayList<String>();

    private List<String> id_dependencia = new ArrayList<String>();
    private List<String> dependencia = new ArrayList<String>();

    public List<String> getDependencia() {
        return dependencia;
    }

    public List<String> getId_dependencia() {
        return id_dependencia;
    }

    public List<String> getSede() {
        return sede;
    }

    public List<String> getId_sede() {
        return id_sede;
    }

    public List<String> getNomb_fun() {
        return nomb_fun;
    }

    public List<String> getDoc_fun() {
        return doc_fun;
    }

    public void startWebAccess(final String estado, final String criterio, final String dato, final int offset, final int limit, String usuario, String dispositivo) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("estado", estado);
        request.addProperty("criterio", criterio);
        request.addProperty("dato", dato);
        request.addProperty("offset", offset);
        request.addProperty("limit", limit);
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
                    doc_fun.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    doc_fun.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(3);
                    nomb_fun.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    nomb_fun.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(5);
                    id_sede.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    id_sede.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(7);
                    sede.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    sede.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(9);
                    id_dependencia.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    id_dependencia.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(11);
                    dependencia.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    dependencia.add("");
                }
            }
        } catch (Exception exception) {
            Log.v("mensaje", exception.toString());
        }
    }
}