package com.arkamovil.android.Informacion;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.servicios_web.WS_ElementoPlaca;
import com.arkamovil.android.servicios_web.WS_ElementosInventario;
import com.arkamovil.android.servicios_web.WS_Imagen;

public class Informacion_Elementos extends Dialog {


    private Activity c;
    private View vista;
    private int i;
    private Thread thread;
    private Handler handler = new Handler();
    private String img;
    private Button cerrar;
    private WS_ElementosInventario datos;

    public Informacion_Elementos(Activity a, View vista, int i, WS_ElementosInventario datos) {
        super(a);
        // TODO Auto-generated constructor stub
        this.vista = vista;
        this.c = a;
        this.i = i;
        this.datos = datos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dl_informacion_elemento);


        cerrar = (Button) findViewById(R.id.cerrar_infoelem);

        cerrar.setEnabled(false);

        TextView marca = (TextView) findViewById(R.id.info_marca);
        TextView serie = (TextView) findViewById(R.id.info_serie);
        TextView nivel = (TextView) findViewById(R.id.info_nivel);
        TextView ubicacion = (TextView) findViewById(R.id.info_ubicacion);
        TextView valor = (TextView) findViewById(R.id.info_valor);
        TextView tipo_bien = (TextView) findViewById(R.id.info_tipobien);
        TextView fecha_salida = (TextView) findViewById(R.id.info_fechasalida);

        marca.setText(datos.getMarca().get(i));
        serie.setText(datos.getSerie().get(i));
        nivel.setText(datos.getNivel().get(i));
        ubicacion.setText(datos.getUbicacion_esp().get(i));
        valor.setText(datos.getTotal().get(i));
        tipo_bien.setText(datos.getTipo_bien().get(i));
        fecha_salida.setText(datos.getFecha_asig().get(i));

        thread = new Thread() {
            public void run() {

                Looper.prepare();

                String id_dispositivo = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
                WS_Imagen ws_imagen = new WS_Imagen();
                img = ws_imagen.startWebAccess(datos.getId_elemento().get(i), new Login().getUsuarioSesion(), id_dispositivo);

                handler.post(createUI);
            }
        };

        thread.start();

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Toast.makeText(c, "De clic en el botón \"CERRAR\" cuando este activo", Toast.LENGTH_LONG).show();
    }

    final Runnable createUI = new Runnable() {

        public void run() {

            ImageView imagen = (ImageView) findViewById(R.id.imagen_inf);
            imagen.setPadding(5, 5, 5, 5);

            try {
                byte[] byteData = Base64.decode(img, Base64.DEFAULT);
                Bitmap photo = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);

                imagen.setImageBitmap(photo);
            } catch (Exception e) {
                img = "iVBORw0KGgoAAAANSUhEUgAAANsAAAB4CAIAAAD0aRBXAAAAA3NCSVQICAjb4U/gAAAAGXRFWHRT\n" +
                        "b2Z0d2FyZQBnbm9tZS1zY3JlZW5zaG907wO/PgAAGzVJREFUeJztXc1TG8kV79HHjNAXHxJgSQYJ\n" +
                        "r43KNnIlxqldrw/Gh0324svahzg55//Iv7HJJbmkcthNqgJObZHsln1Yi1SAxAhMGBkjYRiB0fCh\n" +
                        "EaAZSTM5vNKrZkYSGLARw/wOlJiZ7umPX79+7/XrHkbTNGLBQsvAdtYFsGDhACxGWmgtWIy00Fqw\n" +
                        "GGmhtWAx0kJrwWKkhdaCxUgLrQWLkRZaCxYjLbQWLEZaaC1YjLTQWrAYaaG1YDHSQmvBYqQeVjDU\n" +
                        "2YKxOsBCS+FiyUhr+LU+LhYjGYbB3xY7WxMXi5EA4CLNTmIRtGVwgRhZrVY1TdM0TVVV411VVeEu\n" +
                        "PPbxi2cBcLEsm729PYfDUa1WbTYbzUubzVatVu12e6VS8Xg8Z1jC40HTNJ3IP79wnHUBPiq+//57\n" +
                        "QRBUVbXb7YQQIKXNZgPpyDBMOBx++PDhWRezGeqSzzR0JBdh1qYngXK5XKlU7Ha7zWYjtd5lGAau\n" +
                        "VKvVcrl8diVtCKhCXfXXfDCnjKQFCfyAmRrEYaVSUVXV6XSyLKuqqqqqlUqFTlKpVByOM2iZRpMv\n" +
                        "XDQ9FwHmZKSx8+x2u6ZpNpvNbrc7nU5VVW02m6IocIVhGJvNVi6X4bGPT0eU1vS/xt8XAeZkpA4w\n" +
                        "36mqGgwGQViCFATR6HQ6YSonhHR3d4OJ89FIoOMi4EJRUAdz2trQzVg1Ywej4DFKoI8sk5qIw/cq\n" +
                        "CYw0E1DZnDISuFipVAghTqezWq0WCgVZllVVrVarhBDga6PR+HFICWXAkQNqA2gUcKVarTocDjC5\n" +
                        "QJfQNI1lWa/X63K5ICGysFqtGmXteYRpZSR2syRJa2trMzMzGxsb4ACHDoaOh8m6RYDlsdvtsiwr\n" +
                        "iuLxeMDwstlswMiOjo6enp6BgYFPPvkEHmYYRlVVc9CRmFVGYt9Uq9WlpaVUKlUoFAghYNCArwfd\n" +
                        "kCd50WlJU+AiDhiXy+V2u8HwArbBKKpWqzs7O/l8Pp/PcxwXjUYxOT553nlpQkYC7QghDMOIovjm\n" +
                        "zZt8Pu90Op1Op45/upWb4wFdM8cmN9AR/PYOh6NcLsP0Db+hnDCbw2StKMrW1lYqlfL7/V1dXfDA\n" +
                        "CWvROjBPTWiAOCGErK+vr6+vcxzHsiy4eGjf3mnRkRwrUAPTongrl8uKooDvqVQqkRrRUcGoVqsw\n" +
                        "gzMMk8vlJEkihEBNIZ+TDIwWgQllJMxcIDZ2d3f39vZcLheuE9LBFsee43Qdf7y5G3MAtimKomla\n" +
                        "qVRyOBywwg6WGS4vORwOh8MB7GRZVlGU1lxhOiHMyUiaLg6HA3V/UrMDTqhy4bLeoX7sIwotVVXb\n" +
                        "2tquX78O6mNbW1ulUgHPKPwVRXFpaUmW5ba2NlVVZVkGeUkIQePMHMuMJmQkMbgbUa2Eu2DZnGTi\n" +
                        "ZmpAcQu2hbEMdEmIgbhgsgClKpXK8PCw0+msW5fl5eW5uTl4EswyfC+d/3mnIzErI3UrcmDr6GTV\n" +
                        "sbsQI4bo3yB6jW+ni6R7NQAmYrBXYM0drhAqRg5eJMtye3s7yk7ji0xAR2JWRtI4RXc3MAnYAPoo\n" +
                        "Knm6GZxQdEElgdT4Z8yTEMJxHJjYpMZFNFngMY7j4EWwyGkO/hlhfkaeHKgLAgWBhYqiAM/sdnu5\n" +
                        "XNao0HRweWJaVBIg2s04uWNEEnh5MKFOrILiyLIsWt/n3ayuC4uRDQETMbpmICZD0zRYjXQ6ne3t\n" +
                        "7T6fz+PxhEIhlmXRut/a2tre3pYkSRRFYBs4F2GdEN2l+Bbglt/vJwddQnRhbDab2+0GqSnLMu3J\n" +
                        "MhksRjYETMSog6KRbrPZOjs7I5FIf39/MBh0OBwcx5EamYCysixLkrSwsLC5uZnP52VZBveNUZ3F\n" +
                        "5emNjY1//vOfMAC02nYfELccx+3s7MiyDBLU7XarqqooisXIiwjac1Qul91ud39/fywWC4VCsIKC\n" +
                        "rk1M4nK5XC6Xz+fr6upaXV1dXFxcW1srlUrlchlkG01KZDzLsqurq6BK0j4j1FbBcy7LMi6EWrP2\n" +
                        "RQSaLJVKpb29/fLly/F4vKenB4IsQU3UPYwuJ7fbPTAw4Pf70+n0mzdvJEkCVtFmPmoFLMvCijYI\n" +
                        "SBCoyD8gLsQxaTWcUZN8WFiMbAgwIEBigefl6tWrN2/e9Hg8aGjn8/n19fVcLre7u1sul10ul8fj\n" +
                        "CQQCfX19gUCAEGK323t6etra2lwuF8/zW1tbEMVTLpdZliWElMtlsLIhnAytabB1wKxGMQxjgF5/\n" +
                        "OuM2+gCwGNkMsGRCCPF4PJFIJB6Pe71euLW9vc3zfC6XKxQKqqru7++rqgoLgIIgLC8v9/T0XL16\n" +
                        "tbOz0263e73eaDQKS5qQIbqQUDdFsYcKAJjbqDOQgwHntIL70RvmA8JiZH2gZwcc1729vTdu3Ojo\n" +
                        "6CCElEqlXC63uLi4srJSKpWq1arT6YS5FTxBsizv7u6Kolgqla5du9bT0+NwOILBYDweLxQKy8vL\n" +
                        "HMehFoizdt1iGNd4UC7SC0Jmgjljf04OWhR5vd4rV650d3eDFSyK4tTU1OLioizLhBC3241+StT2\n" +
                        "YA3m9evXMzMz+Xwecuvt7e3v73e73bC/Bxlpysn32LAYWQcMw0B8hizLdrs9EomEQiEwVnZ2dtLp\n" +
                        "9Pb2NqnN6TALw/xbqVRAIwSS7e3tLS0tLS4u7u7uQrZXrly5dOkSMBvFMD1fW+y0GNkQMCe6XK5I\n" +
                        "JOL1ekHhy2QymUwG151dLhceSQA8Bmc4XHG5XOVyOZPJrKysQBKfzxeJRJxOJ3iC6OVHE5vP7wWL\n" +
                        "kXUA/heYatva2rq6ulBAgmcRdpDBM4QQiH6gIzBwBZzjuEKhkM1mQUxqmtbe3u73++lFQvhtpjjw\n" +
                        "k8BqhfoAUoJPEZZkQIPc3NwEZ6HL5apWq6VSiWVZ3C8BczGykxAC27W2t7d3dnYIIQzD+Hy+trY2\n" +
                        "DBfSyUiLlxe9/k1gt9tLpVJnZyeuOG9vb+/v74Obulwuw+wMFKQPHYDfGA/Bsuze3t7W1hbc7ezs\n" +
                        "BDaDc5EO8zmVfT/nHRYj66CRh0WWZXAoYpQD+LHJwchfrbaNGkipKMr+/j4Y5qQWDUQO8g/tG0tG\n" +
                        "XvT61wWyTRenQ2p7YnAdBe/qlEJ6oQ/d7AjgH+0AR5ekZdxYjGwG2IEFv2H+BZ2SUIqmbssEbT4D\n" +
                        "GIbhOA4TkppARQqaz8t9EliMbAbYSwCkZBgGrBywrOnYHNpxQ4s6XBtsa2tra2uDB0qlUqlU0i0M\n" +
                        "fuR6tTIsRtYHsMRutxeLRTCTCSEdHR3t7e04mwPhdGuANDXRmvb7/WAeEUIKhUKxWCT1Drm0QCxG\n" +
                        "6kDPpABZlvf39+Fid3d3T08P7rpCWwTXWnRxjbgq09vb29nZCdeLxeL+/r4uicVIhMXIhgCqSZIk\n" +
                        "CAIssbAs29fXB8GR4B5HOuIEDbKTqZ1XpmlaMBjs6+sDn6WiKOvr66VSid6IAz8smwZgMfIAjPFg\n" +
                        "+/v7b9++zefzcLe3t/fatWt+vx9ifEAEMtTWMFIzegA+n29oaCgYDMKt9fX15eVlPCmFfq8lJgFW\n" +
                        "NNoBGAMQHQ6HJEnZbLa9vd3tdjudzlgsViqVKpXK1tYW7H2BeHLIAZa/wUnk8/l+8pOfxGIxjAbK\n" +
                        "ZrOFQgHkJc37JgFpFw0WIw+A1iPxaL9isZjJZNrb2+PxuM1m8/l8N27caG9v53l+bW1tf38f1mAg\n" +
                        "4JzUdsd2dXUlEon+/n6IFVdVNZ1OZ7NZ3JSN+BAC8vwKXYuR9YGkhD3/EITmdrsjkYjD4fB4PAMD\n" +
                        "Ax0dHW/fvn337t3W1tbu7i4c9+jxeLq6ui5fvgzWDCqUS0tLPM9vb2/jppkPqjieUzoSi5FNgGIS\n" +
                        "9sHkcjmWZcvl8uXLl10ul9Pp7O7u7uzslGUZtq5CrC7Lsg6Hw+v1oiAEOr58+RKOsYSLlh3TCBYj\n" +
                        "6wDpYqt91QG2N2Sz2b29vb29vStXrsCGGzgo1ePxGM0UTdPgvIB0Og0bt8vlMnzizqxbrU8FJmQk\n" +
                        "LX5O0vEwseIptyAg3717t7+//+7du8HBwf7+fnJwRyxtqququrq6+ubNG0EQJEmC/V8QZI6nT520\n" +
                        "qmaECRmpW1OmY2PfKwdYfYaoCPgNwY6SJEmStL6+HgwGe3t7vV4vnLICe7GLxeLW1tbm5qYoiqur\n" +
                        "q6VSSat9ZgG215zKBkLGcMqKafhtQkYacRIGMAePGWdqO2OKxWKxWBQEAXZ+oVcSpCCcU4Vxa1rt\n" +
                        "5HpyetQxqyZqQkbSU+exN68wBzdhabUPzNB3YZs2hE3o3gJX6IWZ0yIQHepmSlKak5F0UOPxus2Y\n" +
                        "imEYcIbjnn+cOmENhg7j1R2AeyrQDOdYWIw8N6CpcFqHNoFMAosEGY8nmGm1r0OQgzu5TpE0OnIz\n" +
                        "Jt0sZkJG0j13up/0oo/Uxy/OkppVjqeMYtDuB7I2tNopfh8i8zOHORkJ5MBD7pAiJ8wWzivD3YYY\n" +
                        "6QMnsdAcBZz6rEqvgJs1VsOEjCSU+o9H356cjlpt5yvG6oIU1Grf46Yl6GmRAxVT+JcWvU6nE4+z\n" +
                        "0rlCzzVMqIhg3zAMA+frgfGBd+kHjpiP8TedG61f6mz8E4pJ2qBGOkJdfvrTn/b19ZnPuDGhjES/\n" +
                        "oMPhcLvd169f39/fh6hEDBU7ueVxig4dHTTDxjEU81ApjuM++eSTK1euwMcS6XilD1GejwwzH32E\n" +
                        "Z+Du7OzMzc1tb28DU0ELxP47SUcaeXlazGAObnuAgcRxXDAYDAQCcBQR/VJ0SJ381WcLEzISugeC\n" +
                        "viCc7KxLdJpAxhuPGYJan23xTg5T9RYAuwesY52hjT9Od03vA8FoTaPIxLv0J8BMMHebkJEgNvDD\n" +
                        "qzgJ0L+Pbt+0DrCcUHj6pCGAORzm5py1GeoDsdhPRtdM69ddJyOJQb9E1w+tn5xrSWlCRpKmXXJ+\n" +
                        "/XbGkgMjccXIsmwsWDh9mEHzsEDjvIsYi5FmAM3C8z5xW7O2hdaCJSMttBYsRlpoLViMtNBasBhp\n" +
                        "obVgMdJCa8FipIXWgsVIC62Fhoy0/JSnhVPZ3tDiqFu741W8ISMxtOR9C2dBBzpssUnPNbp7LmBc\n" +
                        "KMIqN6l4XTSbtY0bnSwcD02akY4JPy8LgMa66AaVMfbq6FU7XI88L83UytAMJ/oRQ79+6FN3Twjd\n" +
                        "lkjdXV2Ivu6B96pX/bF7rkM+LXx8nCJh6shIzXDikYULDuNuEHLQcDlF+VWHkboNRJOTk7///e8F\n" +
                        "QTitV15Y0IN8bGxsbGyM7tSvv/56amrqzArXFE+fPh0dHSU100IQhN/97ne5XI40VpEFQfj666+P\n" +
                        "QZv6O7/onSjm2OF25miifkEL37lzJxwOn0XRDsfg4GATGjTixvE4c8hexEabpA7laKMHjkJu3TP0\n" +
                        "v01uHSX5SXDCfIx7uHRhtsPDw0d59bGLcZLyDw4OGi8e5YyaY7z6OLtjC4VCOp0eHBzM5/OvXr3S\n" +
                        "NG1oaCgWi0mSNDU1VSwWPR7PvXv3WJbVidiFhYXl5WVZlgkh0Wg0Ho/D14foos/Ozi4vL6uqOjAw\n" +
                        "EI1GeZ6Px+PwMQTIR5ZlnufhmWAwODQ05PP56EzS6bSmaeFweHZ2Np/Pcxw3NDR0FPEzPT196dIl\n" +
                        "TdPS6XSxWPR6vcPDw5g5tqkgCOl0WpIkj8cTj8dDodBRmlsUxVQqBY3zs5/9zNhJk5OT4XA4HA7j\n" +
                        "rUKh8OrVK1EUNU3D5kL33tramiAIw8PD09PTgiCwLHvt2rWBgQHjq3mez2Qy5XK5s7MzkUhgjeBF\n" +
                        "PM9rmhaJRFKplCiKLMsam4vneULI4OCgbmzwPM/zPMMwfX19t27dIhT56D2f+ENRFOCApmmBQCCR\n" +
                        "SODJHJjQ/tvf/rZ5U+ZyuVwuNzg4CDXRNG1zc/P58+eiKM7Pz7vd7nw+z/M8y7Lff/89fAxwdXX1\n" +
                        "7du3N27coH0Bo6Ojs7Ozdrsdvuu2uLi4srISj8fpjvnrX/+6sLDAsqzT6Zybm0un08vLy9Fo1O/3\n" +
                        "Q4kVRRkbG0un006n0+FwLC4uLiwsRKNR/Ho1IeTFixe5XO7ly5eSJLlcruXl5XQ6HQ6HdcQ1YnR0\n" +
                        "tFgsTk9PMwxTqVRWVlZ4nr9+/Tp9KkYymfzxxx93dnY8Hk8ul5ufn2cY5lBSLi0tjY2NSZLU1ta2\n" +
                        "tbX18uVLTdOAQ8iwv//9716vNxwOwxVZlr/55htBEKDY8/Pzr169unnzJmw4BCZNT0+Lovjq1Suf\n" +
                        "z7e9vT0/P68oSl9fH/3qb7/9dn5+XlVVlmUXFxd5nu/s7Ozo6EAGTExMCILw3//+t1gschz39u1b\n" +
                        "urngsWQyKYoizN2kJpLg1RzHVatVID32OCFEkiSQJj6fD/tudHSU53n45Mrr1695nu/r63O73YRi\n" +
                        "7XvLSOR+sVh88uQJfFPjT3/6UzKZHBkZAfE+OTn5n//8J5/PB4NBSJXJZNbW1j777DMYSdC1qVRq\n" +
                        "e3sbPvPLMEwqldrY2Hjw4AFkIori6Ogovg7+vnjxQpKkr776KhAIQLc9ffr0u++++9WvfkXL43w+\n" +
                        "PzIyEo/HYfz85S9/WVhYCIVCTeqFCR89egSZp1KpiYmJTCaDPSEIwszMTDweHxkZgSTPnz+fmpoK\n" +
                        "hULNZfDz588DgcDDhw9hTnj27BnP8/gVHCw5fQQAz/OyLD9+/LirqwsacHx8/M2bN/F4nC6zKIq/\n" +
                        "/OUv4ePdz549S6VSg4ODUH7oCFEUR0ZGgPrAiR9++OHXv/41zk6QyYMHD65duwbN/u2330JzGQW5\n" +
                        "Rh3iJUnSo0eP4COkr1+/fvbs2czMjE5S0mdvQN9B82qaVi6X//a3v42Pjz958oR+xXEiLeB9t2/f\n" +
                        "5jiOYRiWZYPBoN/vhyrBjKlpmqIopOYvCAaDn376KdKREBKLxTRNgy9NQ4l5no9EIqiyBAIBuvWx\n" +
                        "n+7du4ctznHcF198USwWs9ksTVyfzwdpGYYJBAKhUEiSpOaVYhgGVAXMPJFIqKq6u7tLaloRvOXz\n" +
                        "zz/HJPA7m802yRlmzOHhYSTB3bt3CanjVdYp6wzDrKysYHM9evQIGwQ10bt37+K35KEw6XQaT0HP\n" +
                        "ZrOBQADtEpZlh4eHy+UymsDQlT6fD/qOEAKf0IPmamSKQap4PA50ZBjm6tWrXV1d6XQaH4MS0m5/\n" +
                        "nufv3r0LdATafPnll5IkZTIZzF/TtOPokUAylMZwEUe8rhrwr8fjuXXrliiKoihKkiSKoiAIuh3v\n" +
                        "oiiCdo/ZRqPRVCqFD6yurhJCUqnU//73P91b8vl8NBrF4rW3t8OOel0xmtQIBgat1hBC4ERdTCuK\n" +
                        "YjgcpqULy7LhcHhzc7NJ5qIoqqoai8XwCsdxRoFNu4EZhhkYGJicnPzXv/41MTERDoej0SidA1MD\n" +
                        "fREKk8/n8XQ/Y5PC8/l8HiQCvFGnWcKprU1qBE9Cg+O8FIvFjN4r+Ao5IUQQBIZhZmdnQctHpkIh\n" +
                        "sTAMwxyHkfRAqVtW43CvVCrfffddLpeDW4FAIBwO04MDc25ul2m1wxtgVMCToPTQOrVm+JBRXZ+Z\n" +
                        "rkbGH03WmjFzKEmTzMnBFsOmN2ZIF8Dr9T5+/Hhubm5paUkQBEEQksnk/fv3UUyCIDS+SGfkEkNP\n" +
                        "oXpg7CbMRJd5805pVBfdLfoi/g6FQjAesImOfxKV7sBt/Lcuq2ZmZgRBePDgAZZgdXV1aWmJULX1\n" +
                        "+Xz5fJ5OWCwW6UxcLhfDMLdu3TI6Iw71LzS/i9D1hG6tmWVZFIfYgsViESf6uoD6FgoFtM80TZMk\n" +
                        "iTYdjO+ChJ9++ulnn30mSVIul3vx4sXExAQyElJhPoBCoYDSV9M0juMkSaLrXigUGIYxfk4Z86QH\n" +
                        "Bj3IiUGK63oHvBPkYF8gKziOI4QkEom6fUeP1SPpkYd256FzYi6XAx0Rm292dlY3yw8ODmYyGVEU\n" +
                        "4V9FUWAWwIEeCAR8Ph94IhCCIPzhD3+A9YP3LbaxnLokOhkZi8UKhYIgCFikXC4nSVJzmwnuoo5F\n" +
                        "CIFUukLq6Dg1NfXnP/8Zx+rg4GA8HgfHGV1BujUEQSgUCmhjwUyazWbpVFAMLLCOcORgo9HCDOU6\n" +
                        "PkxPcYqiLC0twauNeRJCAoGA1+tt3neQ5Egy0jh51RXsjZKDfsbzPJiliqKkUil6DQoyTyQSMzMz\n" +
                        "o6Oj8Xic47iFhQXoNnr+HR4efvbs2djY2NDQEMdxq6urs7OzPp+PduMdG8bkOnUqFov5/f7x8fHh\n" +
                        "4eFAILC5ufnvf/8brahG8Pl8iURiampKUZRoNCpJUjKZ1DliiaEBg8Hg5OQk1JRlWVEUFxYW0P4g\n" +
                        "taabnp4mhIRCoWKx+OOPPwaDQVoIJRKJhYWFp0+fJhIJt9u9vLycSqVisRj4QI6iajdRewRBGB8f\n" +
                        "HxoakiRpdnaWEEI7+Y2cuXPnzvPnz8fGxm7evOlyucBD5/f7wXeG3Xc4I+vqhe/V/Zqmff7558Vi\n" +
                        "ETXfSCTy5MmTb775JpPJ4Hh1Op2PHz9OJpOzs7Ng9gL/aI0NnLTJZPIf//gHNOilS5cePHhADIPy\n" +
                        "eNCpHLpmZVn2iy++SCaTyWQSroTD4ZGRESO9dADjOpVKgaGWSCRgKqCrpmvPaDR6586dqampXC4H\n" +
                        "xQiHw/fu3dOV9v79+9hE4XD45z//Of1AIBD4xS9+kUwmf/jhB7DSbt26dfv2bVJTtIxy8ei4ffv2\n" +
                        "3Nzc2NgYSJyHDx/S+oORM9B3ExMT4+PjIHFDodD9+/d1bd4sxufQ4tLTf/Nn4LcoirIs+3w+nZ0O\n" +
                        "2NzcBN8b3gIn3JMnT3TGICEE/Bderxd9H+Q9x8mhpW1yCz4f26gijUCnOvS9upoaU01NTU1PT//m\n" +
                        "N79RFGVjY8Pv9+sUUxrQ8t3d3fQn549Y7OZPQvF07tjmzbi2tgYGvq5GkKq+jKTtwSa56xTBJpWB\n" +
                        "H2ABINF1OYM796uvvuI4TtO0SqUyOTlJl5t+HpvgwPA6QVCIzgQ22qd0RZCLR3kpbbrVXTeqm1xX\n" +
                        "U9pqxqLCRafTGYlEjOYtna3R9jp6KzUfoqAvEUNHNMmtkdrdbNZu1DG6K7p+Ms5BuiLSTUl3P2Bo\n" +
                        "aGhsbOyPf/wjVBIUTXoaqlsqvcw/rozUZW7M1ljmRhUx5tyIsk04rbtCt6dOPzG2fN0qNB9jzRun\n" +
                        "bv/q5uW6BGiSf93rmqYdvv+jbmWIoQubzODGB3S6GurXEFuwsbFhs9kwLIBWZI/SebqLR5SaTUba\n" +
                        "e6VtcrcuJxqN6uZQVXV9fR0iLRoV4yi0OAoO7d9Grzieknr8XQ3vVeGj65rNH3gvihxxVj06mlP/\n" +
                        "fRMeMe375tnomSZ8PZV3NX/d0WHtXrDQWrDOtLDQWrAYaaG1YDHSQmvh/03npapfIp++AAAAAElF\n" +
                        "TkSuQmCC";

                byte[] byteData = Base64.decode(img, Base64.DEFAULT);
                Bitmap photo = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);

                imagen.setImageBitmap(photo);
            }

            final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            cerrar.setEnabled(true);

        }
    };
}