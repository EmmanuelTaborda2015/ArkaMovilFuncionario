package com.arkamovil.android.servicios_web;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
public class WS_GuardarObservaciones {

    private final String NAMESPACE = "urn:arka";
    //private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";

    private String URL;

    private final String SOAP_ACTION = "urn:arka/guardarObservacion";
    private final String METHOD_NAME = "guardarObservacion";

    private String webResponse = "";

    public String startWebAccess(String id_elemento, String id_levantamiento,  String funcionario, String observacion, String tipo_movimiento, String usuario, String dispositivo) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("id_levantamiento", id_levantamiento);
        request.addProperty("id_elemento", id_elemento);
        request.addProperty("funcionario", funcionario);
        request.addProperty("observacion", observacion);
        request.addProperty("tipo_movimiento", tipo_movimiento);
        request.addProperty("usuario", usuario);
        request.addProperty("dispositivo", dispositivo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(URL);
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            Object response = envelope.getResponse();
            webResponse = response.toString();
            Log.v("WebResponse", webResponse);
        } catch (Exception exception) {
        }
        return webResponse;
    }
}