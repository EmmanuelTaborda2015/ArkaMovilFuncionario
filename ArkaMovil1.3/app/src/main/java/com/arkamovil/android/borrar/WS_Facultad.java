package com.arkamovil.android.borrar;

import android.app.Activity;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class WS_Facultad {

    private final String NAMESPACE = "arkaurn:arka";
    //private final String URL = "http://10.0.2.2/ws/servicio.php?wsdl";
    private final String URL = "http://10.20.0.38/ws_arka_android/servicio.php?wsdl";
    private final String SOAP_ACTION = "arkaurn:arka/consultar_facultad_oracle";
    private final String METHOD_NAME = "consultar_facultad_oracle";

    private Thread thread;
    private Handler handler = new Handler();

    Activity act;
    AutoCompleteTextView spin;

    List<String> facultad = new ArrayList<String>();

    public List<String> getFacultad() {
        return facultad;
    }

    public void startWebAccess(final Activity act, final AutoCompleteTextView spin, final String sede) {

        this.act = act;
        this.spin = spin;

        thread = new Thread() {
            public void run() {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("sede", sede);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);

                try {

                    httpTransport.call(SOAP_ACTION, envelope);
                    SoapObject response = (SoapObject) envelope.getResponse();
                    for (int i = 0; i < response.getPropertyCount(); i++) {
                        facultad.add(response.getProperty(i).toString());
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(act, android.R.layout.simple_spinner_item, facultad);
            spin.setAdapter(adapter);
        }
    };

}