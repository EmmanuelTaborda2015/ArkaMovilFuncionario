package com.arkamovil.android;

import android.content.Context;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.Settings;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.arkamovil.android.herramientas.AppStatus;
import com.arkamovil.android.herramientas.Simplesamlphp;
import com.arkamovil.android.menu_desplegable.CasosUso;
import com.arkamovil.android.servicios_web.WS_CerrarSesion;
import com.arkamovil.android.servicios_web.WS_ValidarConexion;

public class Login extends ActionBarActivity {

    private AlertDialog alertaConexion;
    private Context context = this;
    private Thread thread;

    private Button boton;

    private Thread thread_cerrarSesion;
    private Handler handler_cerrarSesion = new Handler();
    private Handler handler_conexion = new Handler();
    private String webResponse_cerrarSesion;
    private Thread thread_validarConexion;
    private String webResponse_conexion;

    public int getSalir() {
        return salir;
    }

    public void Salir(int salir) {
        this.salir = 1;

    }

    private int salir = 0;

    private ProgressDialog circuloProgreso;

    static public String getUsuarioSesion() {
        return usuarioSesion;
    }

    public static void setUsuarioSesion(String usuarioSesion) {
        Login.usuarioSesion = usuarioSesion;
    }

    private static String usuarioSesion = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try{
            salir = CasosUso.getSalir();
        }catch(Exception salida){
        }

        if(salir  == 1){
            System.exit(0);
        }

        boton = (Button) findViewById(R.id.botonlogin);

        boton.setTextColor(getResources().getColor(R.color.GRIS2));

        boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                boton.setEnabled(false);
                boton.setTextColor(getResources().getColor(R.color.BLANCO));

                int contador = 0;

                if (contador == 0) {
                    AppStatus status = new AppStatus();
                    if (status.getInstance(getApplication()).isOnline()) {

                        thread_validarConexion = new Thread() {
                            public void run() {
                                Looper.prepare();
                                WS_ValidarConexion validarConexion = new WS_ValidarConexion();
                                webResponse_conexion = validarConexion.startWebAccess();
                                handler_conexion.post(conexion);
                            }
                        };

                        thread_validarConexion.start();

                        thread_cerrarSesion = new Thread() {
                            public void run() {
                                Looper.prepare();
                                String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                                WS_CerrarSesion ws_cerrarSesion = new WS_CerrarSesion();
                                webResponse_cerrarSesion = ws_cerrarSesion.startWebAccess(new Login().getUsuarioSesion(), id_dispositivo);
                                handler_cerrarSesion.post(cerrarSesion);
                            }
                        };
                        thread_cerrarSesion.start();

                    } else {

                        // wifi connection was lost
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Sin Conexión a Internet");
                        builder.setMessage("No se ha podido iniciar sesión debido a que no hay conexión a internet. \nPor favor conectese a una red Wi-Fi o Movil e inicie sesión.");
                        builder.setPositiveButton("Entendido",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        //builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.setCancelable(false);
                        alertaConexion = builder.create();
                        alertaConexion.show();
                        boton.setEnabled(true);
                        boton.setTextColor(getResources().getColor(R.color.GRIS2));
                    }
                } else {
                    boton.setEnabled(true);
                    boton.setTextColor(getResources().getColor(R.color.GRIS2));
                }
            }
        });
    }

    final Runnable cerrarSesion = new Runnable() {

        public void run() {

            thread = new Thread() {
                public void run() {
                    //Se crea el objeto de la clase (Simplesamlphp), para iniciar la autenticación
                    Intent i = new Intent (Login.this, Simplesamlphp.class) ;
                    startActivity(i);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boton.setEnabled(true);
                            boton.setTextColor(getResources().getColor(R.color.GRIS2));
                        }
                    });
                }
            };

            thread.start();

        }
    };

    final Runnable conexion = new Runnable() {

        public void run() {
            if("false".equals(webResponse_conexion)){
                // wifi connection was lost
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Problemas en la Conexión a Internet");
                builder.setMessage("La conexión a internet mediante la cual esta tratando de acceder no es valida. \nPor favor verifique la configuración del proxy o intente nuevamente conectandose a otra red Wi-Fi o Movil.");
                builder.setPositiveButton("Entendido",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent (Login.this, Login.class) ;
                                startActivity(i);
                                dialog.cancel();
                            }
                        });
                //builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setCancelable(false);
                alertaConexion = builder.create();
                alertaConexion.show();
            }
        }
    };

}