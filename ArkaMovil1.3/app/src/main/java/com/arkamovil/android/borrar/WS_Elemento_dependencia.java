package com.arkamovil.android.borrar;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.arkamovil.android.R;
import com.arkamovil.android.borrar.TablaConsultarInventario;
import com.arkamovil.android.borrar.TablaConsultarInventariosAsignados;
import com.arkamovil.android.borrar.TablaInventarioCedula;
import com.arkamovil.android.borrar.TablaModificarInventario;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WS_Elemento_dependencia {

    private final String NAMESPACE = "urn:arka";
    private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";
    private final String SOAP_ACTION = "urn:arka/consultar_elementos_dependencia";
    private final String METHOD_NAME = "consultar_elementos_dependencia";

    public Thread getThread() {
        return thread;
    }

    private Thread thread;
    private Handler handler = new Handler();

    private Activity act;
    private View rootView;
    private int caso;

    private int contador = 0;

    private static List<String> descripcion = new ArrayList<String>();
    private static List<String> id_elemento = new ArrayList<String>();
    private static List<String> nivel = new ArrayList<String>();
    private static List<String> marca = new ArrayList<String>();
    private static List<String> placa = new ArrayList<String>();
    private static List<String> serie = new ArrayList<String>();
    private static List<String> valor = new ArrayList<String>();
    private static List<String> subtotal = new ArrayList<String>();
    private static List<String> iva = new ArrayList<String>();
    private static List<String> total = new ArrayList<String>();
    private static List<String> funcionario = new ArrayList<String>();

    public static List<String> getFuncionario() {
        return funcionario;
    }

    public static List<String> getTotal() {
        return total;
    }

    public static List<String> getNivel() {
        return nivel;
    }

    public static List<String> getMarca() {
        return marca;
    }

    public static List<String> getPlaca() {
        return placa;
    }

    public static List<String> getSerie() {
        return serie;
    }

    public static List<String> getValor() {
        return valor;
    }

    public static List<String> getSubtotal() {
        return subtotal;
    }

    public static List<String> getIva() {
        return iva;
    }

    public static List<String> getDescripcion() {
        return descripcion;
    }

    public static List<String> getId_elemento() {
        return id_elemento;
    }

    public void startWebAccess(View rootView, Activity actividad, final String dep, int caso) {

        this.rootView = rootView;
        this.act = actividad;
        this.caso = caso;

        descripcion = new ArrayList<String>();
        id_elemento = new ArrayList<String>();
        nivel = new ArrayList<String>();
        marca = new ArrayList<String>();
        placa = new ArrayList<String>();
        serie = new ArrayList<String>();
        valor = new ArrayList<String>();
        subtotal = new ArrayList<String>();
        iva = new ArrayList<String>();
        total = new ArrayList<String>();
        funcionario = new ArrayList<String>();

        thread = new Thread() {
            public void run() {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("dependencia", dep);

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
                            id_elemento.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            id_elemento.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(3);
                            descripcion.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            descripcion.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(5);
                            nivel.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            nivel.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(7);
                            marca.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            marca.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(9);
                            placa.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            placa.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(11);
                            serie.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            serie.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(13);
                            valor.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            valor.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(15);
                            subtotal.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            subtotal.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(17);
                            iva.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            iva.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(19);
                            total.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            total.add("");
                        }
                        try{
                            obj3 = (SoapObject) obj2.getProperty(21);
                            funcionario.add(obj3.getProperty("value").toString());
                        }catch (NullPointerException ex){
                            funcionario.add("");
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
            //Clase para crear Tablas, se envian como parametros la Vista, La Actividad y los valores para cada una de las columnas (ArrayList)

            if (caso == 1) {
                TablaConsultarInventario crear = new TablaConsultarInventario();
                crear.crear(rootView, act, id_elemento, descripcion, placa);
                if (id_elemento.size() > 0) {
                    ImageView bajar = (ImageView) rootView.findViewById(R.id.bajar_c5);
                    ImageView subir = (ImageView) rootView.findViewById(R.id.subir_c5);
                    bajar.setVisibility(View.VISIBLE);
                    subir.setVisibility(View.VISIBLE);
                }
            } else if (caso == 2) {
                TablaModificarInventario crear = new TablaModificarInventario();
                crear.crear(rootView, act, id_elemento, descripcion, placa);
                if (id_elemento.size() > 0) {
                    ImageView bajar = (ImageView) rootView.findViewById(R.id.bajar_c6);
                    ImageView subir = (ImageView) rootView.findViewById(R.id.subir_c6);
                    bajar.setVisibility(View.VISIBLE);
                    subir.setVisibility(View.VISIBLE);
                }
            } else if (caso == 3) {
                TablaConsultarInventariosAsignados crear = new TablaConsultarInventariosAsignados();
                crear.crear(rootView, act, id_elemento, descripcion, placa);
                if (id_elemento.size() > 0) {
                    ImageView bajar = (ImageView) rootView.findViewById(R.id.bajar_c4);
                    ImageView subir = (ImageView) rootView.findViewById(R.id.subir_c4);
                    //Button pdf = (Button) rootView.findViewById(R.id.generarpdf_c4);
                    bajar.setVisibility(View.VISIBLE);
                    subir.setVisibility(View.VISIBLE);
                    //pdf.setVisibility(View.VISIBLE);
                }
            } else if (caso == 4) {
                TablaInventarioCedula crear = new TablaInventarioCedula();
                crear.crear(rootView, act, id_elemento, descripcion, placa);
                if (id_elemento.size() > 0) {
                    ImageView bajar = (ImageView) rootView.findViewById(R.id.bajar_c7);
                    ImageView subir = (ImageView) rootView.findViewById(R.id.subir_c7);
                    bajar.setVisibility(View.VISIBLE);
                    subir.setVisibility(View.VISIBLE);
                }
            }


        }
    };


}