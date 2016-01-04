package com.arkamovil.android.borrar;

import android.text.format.Time;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WS_EnviarElementosAsignar {

    private final String NAMESPACE = "urn:arka";
    private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";
    private final String SOAP_ACTION = "urn:arka/asignar_elementos_funcionario";
    private final String METHOD_NAME = "asignar_elementos_funcionario";

    private String webResponse = "";


    public String startWebAccess(String sede, String dependencia, String funcionario, String observacion, String id_elemento, String ubicacion) {


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("sede", sede);
        request.addProperty("dependencia", dependencia);
        request.addProperty("funcionario", funcionario);
        request.addProperty("id_elemento", id_elemento);
        request.addProperty("observaciones", observacion);

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        request.addProperty("fecha_registro", today.monthDay + "/" + (today.month + 1) + "/" + today.year);
        request.addProperty("ubicacion", ubicacion);

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