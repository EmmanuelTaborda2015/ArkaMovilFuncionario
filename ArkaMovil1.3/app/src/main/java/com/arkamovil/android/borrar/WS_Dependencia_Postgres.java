package com.arkamovil.android.borrar;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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

public class WS_Dependencia_Postgres {

    private final String NAMESPACE = "arkaurn:arka";
    //private final String URL = "http://10.0.2.2/ws/servicio.php?wsdl";
    private final String URL = "http://10.20.0.38/ws_arka_android/servicio.php?wsdl";
    private final String SOAP_ACTION = "arkaurn:arka/consultar_dependencias";
    private final String METHOD_NAME = "consultar_dependencias";

    private Thread thread;
    private Handler handler = new Handler();

    private Activity act;
    private AutoCompleteTextView spin;

    private List<String> dependencia = new ArrayList<String>();
    private List<String> id_dependencia = new ArrayList<String>();


    public void startWebAccess(final Activity act, final AutoCompleteTextView spin) {

        this.act = act;
        this.spin = spin;

        thread = new Thread() {
            public void run() {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);

                try {

                    httpTransport.call(SOAP_ACTION, envelope);
                    SoapObject response = (SoapObject) envelope.getResponse();
                    for (int i = 0; i < response.getPropertyCount(); i++) {
                        dependencia.add(response.getProperty(i).toString());
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(act, android.R.layout.simple_spinner_item, dependencia);
            spin.setAdapter(adapter);

            if(dependencia.size() == 0){
                Toast.makeText(act, "La sede seleccionada no tiene dependencias relacionadas", Toast.LENGTH_LONG).show();
                spin.setText("No existen dependencias relacionadas");
                spin.setEnabled(false);
                spin.setTextColor(act.getResources().getColor(R.color.GRIS));
            }else{
                spin.setEnabled(true);
                spin.setTextColor(act.getResources().getColor(R.color.NEGRO));
            }
        }
    };

    public List<String> getDependencia() {
        return dependencia;
    }
}