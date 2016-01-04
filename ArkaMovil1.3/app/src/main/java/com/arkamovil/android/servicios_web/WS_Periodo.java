package com.arkamovil.android.servicios_web;

import android.text.format.Time;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class WS_Periodo {

    private final String NAMESPACE = "urn:arka";
    //private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";

    private String URL;

    private final String SOAP_ACTION = "urn:arka/periodoLevantamiento";
    private final String METHOD_NAME = "periodoLevantamiento";

    private String fecha_inicio;
    private String fecha_final;
    private static boolean fecha_valida;



    public String getFecha_final() {
        return fecha_final;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public boolean getFecha_valida() {
        return fecha_valida;
    }

    public void startWebAccess(String usuario, String dispositivo) {

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
            SoapObject obj1 = (SoapObject) envelope.bodyIn;

            Vector<?> responseVector = (Vector<?>) obj1.getProperty(0);

            for (int i = 0; i < responseVector.size(); i++) {
                SoapObject obj2 = (SoapObject) responseVector.get(i);
                SoapObject obj3;
                try {
                    obj3 = (SoapObject) obj2.getProperty(1);
                    fecha_inicio=obj3.getProperty("value").toString();
                } catch (NullPointerException ex) {
                    fecha_inicio="";
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(3);
                    fecha_final=obj3.getProperty("value").toString();
                } catch (NullPointerException ex) {
                    fecha_final="";
                }
            }

            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();

           String fecha = today.year + "-" + (today.month +1)+ "-" + today.monthDay;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date fecha_actual;
            Date fecha_inicial;
            Date fecha_fin;
            try {
                fecha_actual = dateFormat.parse(fecha);
                fecha_inicial = dateFormat.parse(fecha_inicio);
                fecha_fin = dateFormat.parse(fecha_final);
                if(fecha_actual.after(fecha_inicial) && fecha_actual.before(fecha_fin)){
                    fecha_valida=true;
                }else{
                    fecha_valida=false;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.v("error",e.toString());
            }
        } catch (Exception exception) {
            Log.v("mensaje", exception.toString());
        }
    }
}