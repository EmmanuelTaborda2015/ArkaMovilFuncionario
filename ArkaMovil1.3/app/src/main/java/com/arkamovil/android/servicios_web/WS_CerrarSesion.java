package com.arkamovil.android.servicios_web;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
public class WS_CerrarSesion {
    private final String NAMESPACE = "urn:arka";
    //private final String URL = "http://10.0.2.2/ws/servicio.php?wsdl";
    //private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";

    private String URL;

    private final String SOAP_ACTION = "urn:arka/cerrarSesion";
    private final String METHOD_NAME = "cerrarSesion";
    private String webResponse = "";
    public String startWebAccess(String usuario, String id_dispositivo) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("usuario", usuario);
        request.addProperty("dispositivo", id_dispositivo);

        Log.v("usuario", usuario);
        Log.v("usuario", id_dispositivo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(URL);
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            Object response = envelope.getResponse();
            webResponse = response.toString();
            Log.v("mensaje", webResponse);

        } catch (Exception exception) {
        }
        return webResponse;
    }
}