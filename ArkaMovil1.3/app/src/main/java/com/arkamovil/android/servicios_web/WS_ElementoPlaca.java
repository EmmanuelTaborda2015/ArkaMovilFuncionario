package com.arkamovil.android.servicios_web;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WS_ElementoPlaca {

    private final String NAMESPACE = "urn:arka";
    //private final String URL = "http://10.20.0.38/WS_ARKA/servicio/servicio.php";

    private String URL;

    private final String SOAP_ACTION = "urn:arka/elementoFuncionarioPlaca";
    private final String METHOD_NAME = "elementoFuncionarioPlaca";

    private List<String> document_funcionario = new ArrayList<String>();
    private List<String> nom_funcionario = new ArrayList<String>();
    private List<String> placa = new ArrayList<String>();
    private List<String> descripcion = new ArrayList<String>();
    private List<String> estado = new ArrayList<String>();
    private List<String> marca = new ArrayList<String>();
    private List<String> serie = new ArrayList<String>();
    private List<String> nivel = new ArrayList<String>();
    private List<String> total = new ArrayList<String>();
    private List<String> tipo_bien = new ArrayList<String>();
    private List<String> fecha_asig = new ArrayList<String>();
    private List<String> ubicacion_esp = new ArrayList<String>();

    private List<String> id_elemento = new ArrayList<String>();

    public List<String> getUbicacion_esp() {
        return ubicacion_esp;
    }

    public List<String> getId_elemento() {
        return id_elemento;
    }

    public List<String> getFecha_asig() {
        return fecha_asig;
    }

    public List<String> getTipo_bien() {
        return tipo_bien;
    }

    public List<String> getTotal() {
        return total;
    }

    public List<String> getNivel() {
        return nivel;
    }

    public List<String> getSerie() {
        return serie;
    }

    public List<String> getMarca() {
        return marca;
    }

    public List<String> getEstado() {
        return estado;
    }

    public List<String> getDescripcion() {
        return descripcion;
    }

    public List<String> getPlaca() {
        return placa;
    }

    public List<String> getNom_funcionario() {
        return nom_funcionario;
    }

    public List<String> getDoc_funcionario() {
        return document_funcionario;
    }

    public void startWebAccess(final String id_elem, String usuario, String dispositivo) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("id_elemento", id_elem);
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
                    document_funcionario.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    document_funcionario.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(3);
                    nom_funcionario.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    nom_funcionario.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(5);
                    id_elemento.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    id_elemento.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(7);
                    placa.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    placa.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(9);
                    descripcion.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    descripcion.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(11);
                    estado.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    estado.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(13);
                    marca.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    marca.add("Sin Información");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(15);
                    serie.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    serie.add("Sin Información");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(17);
                    nivel.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    nivel.add("Sin Información");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(19);
                    total.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    total.add("Sin Información");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(21);
                    tipo_bien.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    tipo_bien.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(23);
                    fecha_asig.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    fecha_asig.add("Sin Información");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(25);
                    ubicacion_esp.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    ubicacion_esp.add("Sin Información");
                }
            }
        } catch (Exception exception) {
            Log.v("mensaje", exception.toString());
        }
    }
}