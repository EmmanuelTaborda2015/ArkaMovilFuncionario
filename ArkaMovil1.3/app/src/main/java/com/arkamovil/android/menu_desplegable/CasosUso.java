package com.arkamovil.android.menu_desplegable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.casos_uso.AsociarImagen;
import com.arkamovil.android.casos_uso.ActaVisita;
import com.arkamovil.android.casos_uso.CriteriosLevantamientoFisico;
import com.arkamovil.android.casos_uso.Radicacion;
import com.arkamovil.android.procesos.FinalizarSesion;
import com.arkamovil.android.servicios_web.WS_CerrarSesion;
import com.arkamovil.android.servicios_web.WS_Periodo;
import com.arkamovil.android.servicios_web.WS_ValidarConexion;
import com.arkamovil.android.servicios_web.WS_ValidarSesion;


public class CasosUso extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    int cont = 0;

    private Context context = this;
    private Thread thread_validarConexion;
    private String webResponse_conexion;


    private Thread thread_cerrarSesion;
    private Handler handler_cerrarSesion = new Handler();
    private String webResponse_cerrarSesion;
    private AlertDialog alertaConexion;

    private Thread thread_periodo;
    private Thread thread_validarSesion;
    private Handler handler_validarSesion = new Handler();
    private String webResponse_sesion;

    private Handler handler_conexion = new Handler();


    public static int getSalir() {
        return salir;
    }

    private static int salir = 0;

    private Button scanear;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

   // @Override
    //public void onBackPressed() {
        //Toast.makeText(c, "De clic en el botón \"CERRAR\" cuando este activo", Toast.LENGTH_LONG).show();
    //}

    public static final long DISCONNECT_TIMEOUT = 5*60*1000; // 5 min = 5 * 60 * 1000 ms

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            new FinalizarSesion().sesionExpirada(CasosUso.this);
            // Perform any required operation on disconnect
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casos_uso);

        String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);

        BroadcastReceiver broadcastReceiver  =new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                    if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){
                        //do stuff
                        alertaConexion.cancel();
                        Toast.makeText(getApplicationContext(), "Conectado a Internet!!!!", Toast.LENGTH_LONG).show();
                    } else {
                        // wifi connection was lost
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Sin Conexión a Internet");
                        builder.setMessage("Se ha cerrado la sesión debido a que se ha perdido la conexión a internet y esto puede causar errores en la aplicación. \nPor favor conectese a una red Wi-Fi o Movil e inicie una nueva sesión.");
                        builder.setPositiveButton("Entendido",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent (CasosUso.this, Login.class) ;
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
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        thread_periodo = new Thread() {
                    public void run() {
                        Looper.prepare();
                        WS_Periodo ws_periodo = new WS_Periodo();
                        String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                        ws_periodo.startWebAccess(new Login().getUsuarioSesion(),id_dispositivo);
                    }
                };
        thread_periodo.start();

        this.salir = 1;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    //En esta parte del código se redireccionan a cada uno de los fragmentos según la selección del usuario

    public void onSectionAttached(int number) {
        Fragment fragment = null;
        switch (number) {

            case 1:
                if (cont == 1) {
                    fragment = new CriteriosLevantamientoFisico();
                    this.setTitle("Levantamiento Físico de Inventarios");
                    cont = 0;
                }
                    thread_validarConexion = new Thread() {
                        public void run() {
                            Looper.prepare();
                            WS_ValidarConexion validarConexion = new WS_ValidarConexion();
                            webResponse_conexion = validarConexion.startWebAccess();
                            handler_conexion.post(conexion);
                    }
                };

                thread_validarConexion.start();
                cont++;


                break;

            case 2:
                fragment = new AsociarImagen();
                this.setTitle("Asociar Imagen a Elemento");
                thread_validarConexion = new Thread() {
                    public void run() {
                        Looper.prepare();
                        WS_ValidarConexion validarConexion = new WS_ValidarConexion();
                        webResponse_conexion = validarConexion.startWebAccess();
                        handler_conexion.post(conexion);
                    }
                };

                thread_validarConexion.start();

                break;

            case 3:
                thread_validarConexion = new Thread() {
                    public void run() {
                        Looper.prepare();
                        WS_ValidarConexion validarConexion = new WS_ValidarConexion();
                        webResponse_conexion = validarConexion.startWebAccess();
                        handler_conexion.post(conexion);
                    }
                };

                thread_validarConexion.start();
                this.salir = 1;
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

                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((CasosUso) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

    }

    final Runnable cerrarSesion = new Runnable() {

        public void run() {
            if("true".equals(webResponse_cerrarSesion)){
                Intent i = new Intent (CasosUso.this, Login.class) ;
                startActivity(i);
            }
        }
    };

    final Runnable ValidarSesion = new Runnable() {

        public void run() {
            new FinalizarSesion().sesionExpirada(CasosUso.this);
        }
    };

    final Runnable conexion = new Runnable() {

        public void run() {
            if("false".equals(webResponse_conexion)){
                // wifi connection was lost
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Problemas en la Conexión a Internet");
                builder.setMessage("Se ha cerrado la sesión debido a que la conexión a internet mediante la cual esta tratando de acceder no es valida. \nPor favor verifique la configuración del proxy o intente nuevamente conectandose a otra red Wi-Fi o Movil.");
                builder.setPositiveButton("Entendido",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent (CasosUso.this, Login.class) ;
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