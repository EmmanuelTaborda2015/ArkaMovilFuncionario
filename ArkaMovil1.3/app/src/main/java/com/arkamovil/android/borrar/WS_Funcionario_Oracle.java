package com.arkamovil.android.borrar;

import android.app.Activity;
import android.os.Handler;
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

public class WS_Funcionario_Oracle {

    private final String NAMESPACE = "arkaurn:arka";
    private final String URL = "http://10.20.0.38/ws_arka_android/servicio.php?wsdl";
    private final String SOAP_ACTION = "arkaurn:arka/consultar_funcionarios_oracle";
    private final String METHOD_NAME = "consultar_funcionarios_oracle";

    private Thread thread;
    private Handler handler = new Handler();

    private Activity act;
    private AutoCompleteTextView spin;

     private List<String> fun_identificacion = new ArrayList<String>();
     private List<String> fun_nombre = new ArrayList<String>();

    public List<String> getFun_nombre() {
        return fun_nombre;
    }

    public List<String> getFun_identificacion() {
        return fun_identificacion;
    }

    public void startWebAccess(final Activity act, final AutoCompleteTextView spin, final String dependencia) {

        this.act = act;
        this.spin = spin;

        thread = new Thread() {
            public void run() {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("id_objeto", dependencia);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);

                try {

                    httpTransport.call(SOAP_ACTION, envelope);

                    SoapObject obj1 = (SoapObject) envelope.getResponse();

                    for (int i = 0; i < obj1.getPropertyCount(); i++) {
                        SoapObject obj2 = (SoapObject) obj1.getProperty(i);

                        fun_identificacion.add(obj2.getProperty("id").toString());
                        fun_nombre.add(obj2.getProperty("nombre").toString());
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(act, android.R.layout.simple_spinner_item, fun_identificacion);
            spin.setAdapter(adapter);

            if(fun_identificacion.size() == 0){
                Toast.makeText(act, "VERIFIQUE SU CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        }
    };

}