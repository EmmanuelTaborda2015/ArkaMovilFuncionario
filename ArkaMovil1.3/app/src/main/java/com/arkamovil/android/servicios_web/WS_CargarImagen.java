package com.arkamovil.android.servicios_web;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WS_CargarImagen {

    private final String NAMESPACE = "urn:arka";
    //private final String URL = "http://10.0.2.2/ws/servicio.php?wsdl";
    private String URL; //= "http://10.20.0.38/WS_ARKA/servicio/servicio.php";
    private final String SOAP_ACTION = "urn:arka/asignar_imagen";
    private final String METHOD_NAME = "asignar_imagen";

    private String webResponse = "";


    public String startWebAccess(String id_elemento, String imagen, String usuario, String dispositivo) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("id_elemento", id_elemento);
        request.addProperty("imagen", imagen);
        request.addProperty("usuario", usuario);
        request.addProperty("dispositivo", dispositivo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(URL);

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            try{
                Object response = envelope.getResponse();
                webResponse = response.toString();
            }catch (NullPointerException ex){
                webResponse = "";
            }
        } catch (Exception exception) {
        }
        return webResponse;
    }

}