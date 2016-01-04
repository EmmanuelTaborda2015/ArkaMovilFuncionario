package com.arkamovil.android.Informacion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.servicios_web.WS_ElementoPlaca;
import com.arkamovil.android.servicios_web.WS_ElementosInventario;
import com.arkamovil.android.servicios_web.WS_GuardarObservaciones;
import com.arkamovil.android.servicios_web.WS_Imagen;
import com.arkamovil.android.servicios_web.WS_Observaciones;
import com.arkamovil.android.servicios_web.WS_Periodo;

public class Observaciones extends Dialog {


    private Activity c;

    private WS_Observaciones datos;
    WS_ElementosInventario elementos;

    private String id_elemento;
    private String id_levantamiento;
    private String funcionario;
    private  int indexObservacion;
    private String getId_levantamientoGenerado;


    private Thread thread;
    private Handler handler = new Handler();

    public Observaciones(Activity a, WS_Observaciones observaciones, WS_ElementosInventario elementos, String funcionario, int indexObservacion) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;

        this.datos = observaciones;
        this.elementos = elementos;

        this.id_elemento = elementos.getId_elemento().get(indexObservacion);
        this.funcionario = funcionario;
        this.indexObservacion = indexObservacion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dl_observaciones);
        final Button cerrar = (Button) findViewById(R.id.cerrar_obser);
        final Button guardar = (Button) findViewById(R.id.guardar_obser);

        EditText obs_fun = (EditText) findViewById(R.id.obser_funcionario);
        final EditText observaciones_almacen = (EditText) findViewById(R.id.observaciones_almacen);
        final EditText obs_almacen = (EditText) findViewById(R.id.obser_almacen);

        //final Spinner tipo_movimiento = (Spinner) findViewById(R.id.tipo_movimiento);
        //String[] opciones = {"Seleccione ...", "Faltante por hurto", "Faltante por dependencia", "Traslado", "Baja"};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(c.getApplication(), R.layout.spinner_item, opciones);
        //tipo_movimiento.setAdapter(adapter);

        int obsfun = 0;
        int obsalm = 0;

        for(int i = 0; i<datos.getId_levantamiento().size(); i++) {
            if("0".equals(datos.getCreador_observacion().get(i))){
                obs_fun.setText(obs_fun.getText() + datos.getFecha_registro().get(i) + ", " + datos.getObservacion().get(i)+ "\n");
                obsfun++;
            }else if ("1".equals(datos.getCreador_observacion().get(i))){
                String movimiento = "";
                String observacion = "";
                if(!"".equals(datos.getTipo_movimiento().get(i))){
                    if("0".equals(datos.getTipo_movimiento().get(i))){
                        movimiento= ", Posible movimiento: Faltante por hurto";
                    }else if ("1".equals(datos.getTipo_movimiento().get(i))){
                        movimiento= ", Posible movimiento: Faltante por dependencia";
                    }else if ("2".equals(datos.getTipo_movimiento().get(i))){
                        movimiento= ", Posible movimiento: Traslado";
                    }else if ("3".equals(datos.getTipo_movimiento().get(i))){
                        movimiento= ", Posible movimiento: Baja";
                    }
                }
                if(!"".equals(datos.getObservacion().get(i))){
                    observacion = ", " + datos.getObservacion().get(i);
                }
                observaciones_almacen.setText(observaciones_almacen.getText() + datos.getFecha_registro().get(i) + observacion +  movimiento +"\n");
                obsalm++;
            }
        }

        if(datos.getId_levantamiento().size()==0){
            obs_fun.setText("Sin observaciones");
            observaciones_almacen.setText("Sin observaciones");
        }
        if(obsfun==0){
            obs_fun.setText("Sin observaciones");
        }
        if(obsalm==0){
            observaciones_almacen.setText("Sin observaciones");
        }

         cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
         });

            guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guardar.setEnabled(false);
                    cerrar.setEnabled(false);
                    WS_Periodo ws_periodo = new WS_Periodo();
                    if(ws_periodo.getFecha_valida()==true) {
                        if (!"".equals(String.valueOf(obs_almacen.getText())) /*|| tipo_movimiento.getSelectedItemPosition() > 0*/) {
                            thread = new Thread() {
                                public void run() {

                                    Looper.prepare();

                                    String id_dispositivo = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
                                    WS_GuardarObservaciones ws_guardarObservaciones = new WS_GuardarObservaciones();
                                    getId_levantamientoGenerado = ws_guardarObservaciones.startWebAccess(id_elemento, id_levantamiento, funcionario, String.valueOf(obs_almacen.getText()), String.valueOf(0) /*String.valueOf(tipo_movimiento.getSelectedItemPosition() - 1)*/, new Login().getUsuarioSesion(), id_dispositivo);
                                    handler.post(createUI);
                                }
                            };

                            thread.start();

                        } else {
                            Toast.makeText(c, "No ha ingresado ninguna observación.", Toast.LENGTH_LONG).show();
                            guardar.setEnabled(true);
                            cerrar.setEnabled(true);
                        }
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(c);
                        builder.setTitle("Fuera de Período de Levantamiento");
                        builder.setMessage("No se Puede Registrar la Observación Porque Se Encuentra Fuera del Período de Levantamiento Físico");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                guardar.setEnabled(true);
                                cerrar.setEnabled(true);
                                dialog.cancel();
                            }
                        });
                        //builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.show();
                    }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(c, "Oprima el botón \"CERRAR\" cuando este activo", Toast.LENGTH_LONG).show();
    }

    final Runnable createUI = new Runnable() {

        public void run() {
            Toast.makeText(c, "Ha sido registrada su observación", Toast.LENGTH_LONG).show();
            dismiss();
        }
    };
}