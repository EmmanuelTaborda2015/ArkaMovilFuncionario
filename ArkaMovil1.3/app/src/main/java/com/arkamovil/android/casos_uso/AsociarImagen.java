package com.arkamovil.android.casos_uso;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.arkamovil.android.Informacion.Informacion_Elemento_Placa;
import com.arkamovil.android.Informacion.Informacion_Elementos;
import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.procesos.FinalizarSesion;
import com.arkamovil.android.servicios_web.WS_CargarImagen;
import com.arkamovil.android.servicios_web.WS_ConsultarPlacaImagen;
import com.arkamovil.android.servicios_web.WS_ElementoPlaca;
import com.arkamovil.android.servicios_web.WS_ValidarSesion;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Emmanuel on 5/04/15.
 */
public class AsociarImagen extends Fragment {

    private View rootView;
    private String foto;
    private int opcion = 0;
    private final static String NOMBRE_DIRECTORIO = "ImagenesElementos";
    private Button btnCamara;
    private LinearLayout l1;
    private  Button consultar_placa;
    private Button scanear;
    private Button asignar;
    private static String imagen;
    private static String id_elemento;
    private static String id;
    private ProgressDialog circuloProgreso;
    private AlertDialog alerta;



    private Informacion_Elemento_Placa dialog;

    private Thread thread;
    private Thread thread_placa;
    private Thread thread_Informacion;
    private Handler handler = new Handler();

    private Thread thread_validarSesion;
    private Handler handler_validarSesion = new Handler();
    private String webResponse_sesion;

    private WS_ElementoPlaca ws_elementoPlaca;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fm_cargarimagen, container, false);

        scanear = (Button) rootView.findViewById(R.id.escanear_c3);
        btnCamara = (Button) rootView.findViewById(R.id.camara);
        asignar = (Button) rootView.findViewById(R.id.asignarimagen);

        l1 = (LinearLayout) rootView.findViewById(R.id.linear_elemento);
        l1.setVisibility(View.GONE);

        //final LinearLayout l2 = (LinearLayout) rootView.findViewById(R.id.linear_ingresar_placa);
        //l2.setVisibility(View.GONE);

        //final Button esc = (Button) rootView.findViewById(R.id.esc_placa);
        //final Button ing_placa = (Button) rootView.findViewById(R.id.ingresar_placa);
        consultar_placa = (Button) rootView.findViewById(R.id.con_placa);

        l1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {



            //circuloProgreso = ProgressDialog.show(getActivity(), "", "Espere por favor ...", true);


            thread_Informacion = new Thread() {
                public void run() {

                    Looper.prepare();
                    String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                    ws_elementoPlaca = new WS_ElementoPlaca();
                    ws_elementoPlaca.startWebAccess(id_elemento, new Login().getUsuarioSesion(), id_dispositivo);

                    handler.post(Informacion);
                }
            };

            thread_Informacion.start();
        }
        });

        //ing_placa.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
                //boolean on = ((ToggleButton) v).isChecked();
                //if (on) {
                    //l2.setVisibility(View.VISIBLE);
                    //l1.setVisibility(View.GONE);
                //} else {
                   //l2.setVisibility(View.GONE);
                //}
            //}
        //});

        consultar_placa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AutoCompleteTextView txtPlaca = (AutoCompleteTextView) rootView.findViewById(R.id.ing_placa);
                final String cont = String.valueOf(txtPlaca.getText());
                consultar_placa.setEnabled(false);
                btnCamara.setEnabled(false);
                scanear.setEnabled(false);
                l1.setVisibility(View.GONE);

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                thread_placa = new Thread() {
                    public void run() {

                        Looper.prepare();

                        String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                        WS_ConsultarPlacaImagen placa = new WS_ConsultarPlacaImagen();
                        id = placa.startWebAccess(cont, new Login().getUsuarioSesion(), id_dispositivo);

                        handler.post(Elemento);
                    }
                };

                thread_placa.start();
            }
        });
        //btnCamara.setEnabled(false);
        //asignar.setEnabled(false);

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri output = Uri.fromFile(new File(foto));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                startActivityForResult(intent, 100);
                opcion = 1;
            }
        });

        asignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanear.setEnabled(false);
                btnCamara.setEnabled(false);
                asignar.setEnabled(false);

                thread = new Thread() {
                    public void run() {

                        Looper.prepare();

                        String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                        WS_CargarImagen ws_cargarImagen = new WS_CargarImagen();
                        ws_cargarImagen.startWebAccess(id_elemento, imagen, new Login().getUsuarioSesion(), id_dispositivo);

                        handler.post(createUI);
                    }
                };

                thread.start();

            }
        });

        scanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnCamara.setEnabled(false);
                asignar.setEnabled(false);
                final AutoCompleteTextView txtPlaca = (AutoCompleteTextView) rootView.findViewById(R.id.ing_placa);
                txtPlaca.setText("");
                //l2.setVisibility(View.GONE);

                try {

                    WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();

                    Intent intent = new Intent(
                            "com.google.zxing.client.android.SCAN");
                    //intent.putExtra("SCAN_WIDTH",display.getHeight());
                    //intent.putExtra("SCAN_HEIGHT", display.getWidth());
                    intent.putExtra("SCAN_FORMATS", "QR_CODE_MODE");
                    intent.putExtra("PROMPT_TITTLE", "ARKAMOVIL");
                    intent.putExtra("PROMPT_MESSAGE", "Ponga en el interior del recuadro el código de barras del elemento que desea consultar (ARKAMOVIL)");
                    startActivityForResult(intent,
                            IntentIntegrator.REQUEST_CODE);
                } catch (Exception e) {
                    Log.e("BARCODE_ERROR", e.getMessage());
                }

                //Intent intent = new Intent("com.arkamovil.android.SCAN");
                //startActivityForResult(intent, 0);
                opcion = 2;
            }
        });

        return rootView;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String strPath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strPath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(strPath, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (opcion == 1) {
            ImageView iv = (ImageView) rootView.findViewById(R.id.imageView1);
            iv.setImageBitmap(decodeSampledBitmapFromResource(foto, 300, 300));

            File file = new File(foto);
            if (file.exists()) {
                InputStream inputStream = null;

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                decodeSampledBitmapFromResource(foto, 300, 300).compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();

                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                imagen = encodedImage;

                asignar.setEnabled(true);

                file.delete();

                asignar.setEnabled(true);

            } else {
                Toast.makeText(getActivity(), "No se ha realizado la foto", Toast.LENGTH_SHORT).show();
                asignar.setEnabled(false);
            }
        }
        if (opcion == 2) {
            if (resultCode == getActivity().RESULT_OK) {
                final String contenido = intent.getStringExtra("SCAN_RESULT");
                String formato = intent.getStringExtra("SCAN_RESULT_FORMAT");
                scanear.setEnabled(false);
                thread_placa = new Thread() {
                    public void run() {

                        Looper.prepare();

                        String id_dispositivo = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                        WS_ConsultarPlacaImagen placa = new WS_ConsultarPlacaImagen();
                        id = placa.startWebAccess(contenido, new Login().getUsuarioSesion(), id_dispositivo);

                        handler.post(Elemento);
                    }
                };

                thread_placa.start();

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Se cancelo el escaneo de la placa", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static File getRuta() {

        // El fichero ser� almacenado en un directorio dentro del directorio
        // Descargas
        File ruta = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            ruta = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    NOMBRE_DIRECTORIO);

            if (ruta != null) {
                if (!ruta.mkdirs()) {
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }
        } else {
        }

        return ruta;
    }

    final Runnable createUI = new Runnable() {

        public void run() {
            Toast.makeText(getActivity(), "Ha sido cargado la imagen al elemento", Toast.LENGTH_SHORT).show();
            scanear.setEnabled(true);
            btnCamara.setEnabled(true);
            asignar.setEnabled(false);
        }
    };

    final Runnable Elemento = new Runnable() {

        public void run() {
            scanear.setEnabled(true);
            if ("false".equals(id) || "".equals(id) || "sesion_expirada".equals(id)) {
                Toast.makeText(getActivity(), "No se encontro ningun elemento con la placa escaneada o el elemento se encuentra asignado a otro funcionario", Toast.LENGTH_LONG).show();
                consultar_placa.setEnabled(true);
            } else {
                id_elemento = id;
                foto = Environment.getExternalStorageDirectory() + "/imagen" + id_elemento + ".jpg";
                btnCamara.setEnabled(true);
                consultar_placa.setEnabled(true);
                asignar.setEnabled(false);
                l1.setVisibility(View.VISIBLE);
            }
            scanear.setEnabled(true);
        }
    };

    final Runnable Informacion = new Runnable() {

        public void run() {
            if(id_elemento!="") {
                if(ws_elementoPlaca.getMarca().size()>0) {
                    dialog = new Informacion_Elemento_Placa(getActivity(), 0, ws_elementoPlaca);
                    dialog.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Sin Información");
                    builder.setMessage("El elemento escaneado no cuenta con información disponible.");
                    builder.setPositiveButton("Entendido",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    //builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setCancelable(false);
                    alerta = builder.create();
                    alerta.show();
                }
                    //circuloProgreso.dismiss();
            }else{
                Toast.makeText(getActivity(), "Este elemento aun no ha sido asignado o esta asignado a otro funcionario, no se puede mostrar la información", Toast.LENGTH_LONG).show();
            }

        }
    };
}