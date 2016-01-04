package com.arkamovil.android.servicios_web;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WS_RegistroActaVisita {

    private final String NAMESPACE = "urn:arka";
    //private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";

    private String URL;

    private final String SOAP_ACTION = "urn:arka/registrarActaVisita";
    private final String METHOD_NAME = "registrarActaVisita";

    private String webResponse = "";


    public String startWebAccess(String sede, String dependencia, String responsable, String obser, String fecha, String proxVisita, String ubicacion, String usuario, String dispositivo) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("sede", sede);
        request.addProperty("dependencia", dependencia);
        request.addProperty("responsable", responsable);
        request.addProperty("observacion", obser);
        request.addProperty("fecha", fecha);
        request.addProperty("proxima_vis", proxVisita);
        request.addProperty("ubicacion", ubicacion);
        request.addProperty("usuario", usuario);
        request.addProperty("dispositivo", dispositivo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(URL);

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            Object response = envelope.getResponse();
            webResponse = response.toString();
        } catch (Exception exception) {
        }
        return webResponse;
    }
}


