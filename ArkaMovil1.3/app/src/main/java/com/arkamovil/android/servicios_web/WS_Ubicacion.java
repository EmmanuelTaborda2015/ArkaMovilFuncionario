package com.arkamovil.android.servicios_web;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.arkamovil.android.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WS_Ubicacion {

    private final String NAMESPACE = "urn:arka";
    //private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";

    private String URL;

    private final String SOAP_ACTION = "urn:arka/ubicacion";
    private final String METHOD_NAME = "ubicacion";

    private Thread thread;
    private Handler handler = new Handler();

    private Activity act;
    private AutoCompleteTextView spin;

    private List<String> ubicacion = new ArrayList<String>();
    private List<String> id_ubicacion = new ArrayList<String>();


    public void startWebAccess(final Activity act, final AutoCompleteTextView spin, final String dependencia, final String usuario, final String dispositivo) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        this.act = act;
        this.spin = spin;

        thread = new Thread() {
            public void run() {

                Looper.prepare();

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("dependencia", dependencia);
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
                            id_ubicacion.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            id_ubicacion.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(3);
                            ubicacion.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            ubicacion.add("");
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(act, android.R.layout.simple_spinner_item, ubicacion);
            spin.setAdapter(adapter);

            if (ubicacion.size() == 0) {
                Toast.makeText(act, "La dependencia seleccionada no tiene ubicaciones relacionadas", Toast.LENGTH_LONG).show();
                spin.setText("No existen ubicaciones relacionadas");
                spin.setEnabled(false);
                spin.setTextColor(act.getResources().getColor(R.color.GRIS));
            } else {
                spin.setEnabled(true);
                spin.setTextColor(act.getResources().getColor(R.color.NEGRO));
            }
        }
    };

    public List<String> getUbicacion() {
        return ubicacion;
    }
    public List<String> getId_ubicacion() {
        return id_ubicacion;
    }
}