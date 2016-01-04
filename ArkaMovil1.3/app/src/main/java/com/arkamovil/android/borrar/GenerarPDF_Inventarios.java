package com.arkamovil.android.borrar;


import harmony.java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import com.arkamovil.android.R;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class GenerarPDF_Inventarios{

    private final static String NOMBRE_DIRECTORIO = "Inventarios";
    private static String NOMBRE_DOCUMENTO = "prueba.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static Activity actividad;
    private static List<String> id_elemento;


    public  void generar(Resources resources, Activity actividad, List<String> elem, String sede, String dependencia, String funcionario){

        this.actividad = actividad;
        this.id_elemento = elem;

        NOMBRE_DOCUMENTO = funcionario +".pdf";

        // Creamos el documento.
        Document documento = new Document();

        try {

            // Creamos el fichero con el nombre que deseemos.
            File f = crearFichero(NOMBRE_DOCUMENTO);

            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            FileOutputStream ficheroPdf = new FileOutputStream(
                    f.getAbsolutePath());

            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);

            // Incluimos el p�e de p�gina y una cabecera
            HeaderFooter cabecera = new HeaderFooter(new Phrase(
                    "Cabecera"), false);
            HeaderFooter pie = new HeaderFooter(new Phrase(
                    "Oficina Asesora de Sistemas"), false);


            documento.setFooter(pie);

            // Abrimos el documento.
            documento.open();

            Paragraph text1 = new Paragraph("\n");
            Paragraph text = new Paragraph("\n\n\n");

            // se añade el titulo

            Paragraph titulo = new Paragraph("\n UNIVERSIDAD DISTRITAL \"FRANCISCO JOSE DE CALDAS\" \n\n");

            Font font1 = FontFactory.getFont(FontFactory.HELVETICA, 40,
                    Font.BOLD, Color.BLACK);

            titulo.setAlignment(Element.ALIGN_CENTER);

            titulo.setFont(font1);


            // se añade el subtitulo

            Paragraph subtitulo2 = new Paragraph("LEVANTAMIENTO FISICO DE INVENTARIOS");


            Font font2 = FontFactory.getFont(FontFactory.HELVETICA, 38,
                    Font.BOLD, Color.BLACK);

            subtitulo2.setAlignment(Element.ALIGN_CENTER);

            subtitulo2.setFont(font2);


            Paragraph texto1 = new Paragraph("Fecha de Realización:");
            Paragraph texto2 = new Paragraph("Sede:");
            Paragraph texto3 = new Paragraph("Dependencia:");
            Paragraph texto4 = new Paragraph("Funcionario:");

            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();


            // Insertamos una imagen que se encuentra en los recursos de la
            Bitmap bitmap = BitmapFactory.decodeResource(resources,
                    R.drawable.ud);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 5, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            imagen.scaleAbsolute(45, 60);
            imagen.setAbsolutePosition(175, 748);

            //imagen.setAlignment(Element.ALIGN_CENTER);

            documento.add(imagen);

            PdfPTable tablaTitulo = new PdfPTable(2);

            PdfPCell cell;
            cell = new PdfPCell(new Phrase());
            cell.setRowspan(1);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            tablaTitulo.addCell(cell);

            cell = new PdfPCell(new Phrase(titulo));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            tablaTitulo.addCell(cell);

            cell = new PdfPCell(new Phrase(subtitulo2));
            cell.setColspan(2);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            tablaTitulo.addCell(cell);

            //----------------------------------------//

            documento.add(tablaTitulo);

            PdfPTable tablaDatos = new PdfPTable(2);

            //-------------PRIMER CAMPO---------------//

            cell = new PdfPCell(new Phrase(texto1));
            cell.setRowspan(1);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaDatos.addCell(cell);

            cell = new PdfPCell(new Phrase(today.monthDay + "/" + (today.month + 1) + "/" + today.year));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaDatos.addCell(cell);

            //----------------------------------------//

            //-------------SEGUNDO CAMPO---------------//

            cell = new PdfPCell(new Phrase(texto2));
            cell.setRowspan(1);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaDatos.addCell(cell);

            cell = new PdfPCell(new Phrase(sede));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaDatos.addCell(cell);

            //----------------------------------------//

            //-------------TERCER CAMPO---------------//

            cell = new PdfPCell(new Phrase(texto3));
            cell.setRowspan(1);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaDatos.addCell(cell);

            cell = new PdfPCell(new Phrase(dependencia));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaDatos.addCell(cell);

            //----------------------------------------//

            //-------------CUARTO CAMPO---------------//

            cell = new PdfPCell(new Phrase(texto4));
            cell.setRowspan(1);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaDatos.addCell(cell);

            cell = new PdfPCell(new Phrase(funcionario));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaDatos.addCell(cell);

            //----------------------------------------//

            //-------------QUINTO CAMPO---------------//

            cell = new PdfPCell(new Phrase("\n"));
            cell.setColspan(2);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaDatos.addCell(cell);

            //----------------------------------------//

            //-------------SEXTO CAMPO---------------//

            cell = new PdfPCell(new Phrase("Elementos asignados durante el Levantamiento Físico"));
            cell.setColspan(2);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaDatos.addCell(cell);

            //----------------------------------------//

            //-------------SEPTIMO CAMPO---------------//

            cell = new PdfPCell(new Phrase("\n"));
            cell.setColspan(2);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaDatos.addCell(cell);

            //----------------------------------------//

            documento.add(tablaDatos);


            PdfPTable tablaElementos = new PdfPTable(5);

            //-----------------TABLA-----------------//

            cell = new PdfPCell(new Phrase("Id Elemento"));
            cell.setRowspan(4);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            tablaElementos.addCell(cell);

            cell = new PdfPCell(new Phrase("Placa"));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            tablaElementos.addCell(cell);

            cell = new PdfPCell(new Phrase("Estado Actualización"));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            tablaElementos.addCell(cell);

            cell = new PdfPCell(new Phrase("Estado Elemento"));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            tablaElementos.addCell(cell);

            cell = new PdfPCell(new Phrase("Observaciones"));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            tablaElementos.addCell(cell);

            //----------------------------------------//

            documento.add(tablaElementos);

            //---AQUI SE GENERA EL FOR PARA CARGAR---//
            //--------LOS ELEMENTOS EN LA TABLA------//




            //-----------------TABLA-----------------//

            WS_Asignaciones ws_asignaciones = new WS_Asignaciones();

            for(int i=0; i< id_elemento.size(); i++) {

                ws_asignaciones.startWebAccess(this.actividad, id_elemento.get(i), 0);

                if (ws_asignaciones.getId_elemento().size() > 0) {

                    String id = ws_asignaciones.getId_elemento().get(0);
                    String placa = ws_asignaciones.getPlaca().get(0);
                    String estado_act = ws_asignaciones.getEstado_Actualizacion().get(0);
                    String estado = ws_asignaciones.getEstado().get(0);
                    String observaciones = ws_asignaciones.getObservaciones().get(0);

                    PdfPTable tablaCargada = new PdfPTable(5);

                    cell = new PdfPCell(new Phrase(id));
                    cell.setRowspan(4);
                    cell.setPaddingBottom(5);
                    cell.setPaddingTop(5);
                    cell.setPaddingLeft(5);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);

                    tablaCargada.addCell(cell);

                    cell = new PdfPCell(new Phrase(placa));
                    cell.setPaddingBottom(5);
                    cell.setPaddingTop(5);
                    cell.setPaddingLeft(5);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);

                    tablaCargada.addCell(cell);

                    cell = new PdfPCell(new Phrase(estado_act));
                    cell.setPaddingBottom(5);
                    cell.setPaddingTop(5);
                    cell.setPaddingLeft(5);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);

                    tablaCargada.addCell(cell);

                    cell = new PdfPCell(new Phrase(estado));
                    cell.setPaddingBottom(5);
                    cell.setPaddingTop(5);
                    cell.setPaddingLeft(5);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);

                    tablaCargada.addCell(cell);

                    cell = new PdfPCell(new Phrase(observaciones));
                    cell.setPaddingBottom(5);
                    cell.setPaddingTop(5);
                    cell.setPaddingLeft(5);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);

                    tablaCargada.addCell(cell);

                    documento.add(tablaCargada);

                }
            }
            //----------------------------------------//

            //-------------///////////---------------//

            PdfPTable tablaFirmas = new PdfPTable(3);

            cell = new PdfPCell(new Phrase("\n\n\n"));
            cell.setColspan(3);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);

            tablaFirmas.addCell(cell);

            //----------------------------------------//

            //-------------CAMPOS FIRMAS---------------//

            cell = new PdfPCell(new Phrase("FIRMA ENCARGADO DE LEVANTAMIENTO"));
            cell.setRowspan(2);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            tablaFirmas.addCell(cell);

            cell = new PdfPCell(new Phrase(""));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            tablaFirmas.addCell(cell);

            cell = new PdfPCell(new Phrase("FIRMA FUNCIONARIO ASIGNADO"));
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setPaddingLeft(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            tablaFirmas.addCell(cell);
            //----------------------------------------//

            documento.add(tablaFirmas);

        } catch (DocumentException e) {

            Log.e(ETIQUETA_ERROR, e.getMessage());

        } catch (IOException e) {

            Log.e(ETIQUETA_ERROR, e.getMessage());

        } finally {

            // Cerramos el documento.
            documento.close();
        }
    }

    public static File crearFichero(String nombreFichero) throws IOException {
        File ruta = getRuta();
        File fichero = null;
        if (ruta != null)
            fichero = new File(ruta, nombreFichero);
        return fichero;
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



}