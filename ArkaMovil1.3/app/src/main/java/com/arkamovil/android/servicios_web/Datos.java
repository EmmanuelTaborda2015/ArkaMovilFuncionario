package com.arkamovil.android.servicios_web;

/**
 * Created by emmanuel on 5/11/15.
 */
public class Datos {
    public String getHOST() {
        return HOST;
    }

    public String getSERVICE() {
        return SERVICE;
    }

    public String getLOGIN() {
        return LOGIN;
    }

    public String getLOGOUT() {
        return LOGOUT;
    }

    //private String URL = "https://oas.udistrital.edu.co/WS_ARKA/servicio/servicio.php";
    private String HOST = "http://10.20.0.38/";
    private String SERVICE = HOST+"arkamovil/webservice/funcionario/servicio/servicio.php";
    private String LOGIN = HOST+"arkamovil/webservice/funcionario/aplicativo/simpleSAMLWS.php";
    private String LOGOUT = HOST+"arkamovil/webservice/funcionario/aplicativo/cerrarSesionSimpleSAML.php";

}
