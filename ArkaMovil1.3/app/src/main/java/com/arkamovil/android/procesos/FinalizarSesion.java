package com.arkamovil.android.procesos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.arkamovil.android.Login;
import com.arkamovil.android.servicios_web.WS_CerrarSesion;
import com.arkamovil.android.servicios_web.WS_Sede;

/**
 * Created by emmanuel on 13/09/15.
 */
public class FinalizarSesion {

    private static Thread thread_cerrarSesion;
    private static Handler handler_cerrarSesion = new Handler();
    private static String webResponse_cerrarSesion;

    private static Activity act;

    public static void sesionExpirada(final Activity actividad){

        act = actividad;

        AlertDialog.Builder dialogo = new AlertDialog.Builder(act);

        dialogo.setTitle("SESIÓN CADUCADA");
        dialogo.setMessage("La sesión ha caducado, Por favor inicie sesión nuevamente ");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                thread_cerrarSesion = new Thread() {
                    public void run() {
                        Looper.prepare();

                        String id_dispositivo = Settings.Secure.getString(act.getContentResolver(), Settings.Secure.ANDROID_ID);
                        Log.v("Datos", id_dispositivo);
                        WS_CerrarSesion ws_cerrarSesion = new WS_CerrarSesion();
                        webResponse_cerrarSesion = ws_cerrarSesion.startWebAccess(new Login().getUsuarioSesion(), id_dispositivo);
                        handler_cerrarSesion.post(cerrarSesion);
                    }
                };
                thread_cerrarSesion.start();
            }
        });
        dialogo.setCancelable(false);
        dialogo.create();
        dialogo.show();


    }

    public static void sesionInvalida(final Activity actividad){

        AlertDialog.Builder dialogo = new AlertDialog.Builder(actividad);

        dialogo.setTitle("SESIÓN CADUCADA");
        dialogo.setMessage("La sesión presentas algunas inconsistencias, por favor inicie sesión nuevamente ");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(actividad, Login.class);
                actividad.startActivity(i);
            }
        });
        dialogo.setCancelable ( false);
        dialogo.create();
        dialogo.show();
    }

    final static  Runnable cerrarSesion = new Runnable() {

        public void run() {
            if("true".equals(webResponse_cerrarSesion)){

                Intent i = new Intent(act, Login.class);
                act.startActivity(i);

            }
        }
    };
}
