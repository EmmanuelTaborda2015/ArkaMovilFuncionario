package com.arkamovil.android.herramientas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.arkamovil.android.Login;
import com.arkamovil.android.R;
import com.arkamovil.android.menu_desplegable.CasosUso;
import com.arkamovil.android.servicios_web.Datos;

import java.net.URLEncoder;

public class Simplesamlphp extends ActionBarActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplesamlphp);
        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);

        //android.webkit.CookieManager.getInstance().removeAllCookie();

        Datos url = new Datos();
        String urlLOGIN = url.getLOGIN();
        String urlLOGOUT = url.getLOGOUT();

        final String urlCerrarSesion =urlLOGOUT;
        final String urlIniciarSesion=urlLOGIN;

        StringBuffer buffer=new StringBuffer(urlCerrarSesion);

        mWebView.loadUrl(buffer.toString());

        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (urlCerrarSesion.equals(url)) {

                    String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                    StringBuffer buffer = new StringBuffer(urlIniciarSesion);
                    buffer.append("?dispositivo=" + URLEncoder.encode(id_dispositivo));
                    buffer.append("&=" + URLEncoder.encode(""));
                    buffer.append("&=" + URLEncoder.encode(""));
                    buffer.append("&=" + URLEncoder.encode(""));
                    mWebView.loadUrl(buffer.toString());
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);


            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.i("WEB_VIEW_TEST", "error code:" + errorCode);
                AlertDialog.Builder builder = new AlertDialog.Builder(Simplesamlphp.this);
                builder.setMessage(description).setPositiveButton("Aceptar", null).setTitle("ERROR");
                builder.show();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(this), "Android");
    }

    public class MyJavaScriptInterface {
        Activity activity;

        MyJavaScriptInterface(Activity activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void setParam(String usuario, String perfil) {
            new Login().setUsuarioSesion(usuario);
            finish();
            Intent intent = new Intent(getApplicationContext(), CasosUso.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public void closeActivity() {
            activity.finish();
        }
    }
}