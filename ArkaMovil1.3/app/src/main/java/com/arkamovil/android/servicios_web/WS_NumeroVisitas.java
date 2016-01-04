package com.arkamovil.android.servicios_web;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WS_NumeroVisitas {

    private final String NAMESPACE = "arkaurn:arka";
    //private final String URL = "http://10.0.2.2/ws/servicio.php?wsdl";
    //private final String URL = "http://10.20.0.38/ws_arka_android/servicio.php?wsdl";

    private String URL;

    private final String SOAP_ACTION = "arkaurn:arka/consultar_visita";
    private final String METHOD_NAME = "consultar_visita";

    private String webResponse = "";


    public String startWebAccess(String usuario, String dispositivo) {


        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

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