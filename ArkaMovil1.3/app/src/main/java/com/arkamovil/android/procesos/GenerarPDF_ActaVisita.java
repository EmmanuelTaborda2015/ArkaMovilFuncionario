package com.arkamovil.android.procesos;


import harmony.java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.arkamovil.android.R;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.MultiColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

import org.w3c.dom.Text;

public class GenerarPDF_ActaVisita{

    private final static String NOMBRE_DIRECTORIO = "Actas";
    private static String NOMBRE_DOCUMENTO = "prueba.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";


    public  void generar(Resources resources, String fecha, String sede, String dependencia, String nomres, String cedres, String obser, String numvis, String proxVisita){

        NOMBRE_DOCUMENTO = "Actavisita"+numvis+".pdf";

        // Creamos el documento.
        Document documento = new Document();

        try {


            String fechaDia = "";
            String fechaMes = "";
            String fechaAnno = "";
            String proxvisDia = "";
            String proxvisMes = "";
            String proxvisAnno = "";

            char cFecha[] = fecha.toCharArray();
            int contador = 0;

            for(int i=0; i<cFecha.length; i++){
                if('/' == cFecha[i]){
                    contador++;
                }else{
                    if(contador == 0){
                        fechaDia += cFecha[i];
                    }else if (contador == 1){
                        fechaMes += cFecha[i];
                    }else if(contador == 2){
                        fechaAnno += cFecha[i];
                    }
                }
            }

            char cProximaVisita[] = proxVisita.toCharArray();
            contador = 0;

            for(int i=0; i<cProximaVisita.length; i++){
                if('/' == cProximaVisita[i]){
                    contador++;
                }else{
                    if(contador == 0){
                        proxvisDia+= cProximaVisita[i];
                    }else if (contador == 1){
                        proxvisMes += cProximaVisita[i];
                    }else if(contador == 2){
                        proxvisAnno += cProximaVisita[i];
                    }
                }


            }

            // Creamos el fichero con el nombre que deseemos.
            File f = crearFichero(NOMBRE_DOCUMENTO);

            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            Log.v("ficher",f.getAbsolutePath());
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

            Paragraph titulo = new Paragraph("UNIVERSIDAD DISTRITAL \"FRANCISCO JOSE DE CALDAS\" \n ALMACEN GENERAL E INVENTARIOS");

            Font font1 = FontFactory.getFont(FontFactory.HELVETICA, 40,
                    Font.BOLD, Color.BLACK);

            titulo.setAlignment(Element.ALIGN_CENTER);

            titulo.setFont(font1);


            // se añade el subtitulo

            Paragraph subtitulo2 = new Paragraph("VISITA DE LEVANTAMIENTO FISICO DE INVENTARIOS");


            Font font2 = FontFactory.getFont(FontFactory.HELVETICA, 38,
                    Font.BOLD, Color.BLACK);

            subtitulo2.setAlignment(Element.ALIGN_CENTER);

            subtitulo2.setFont(font2);

            // Insertamos una imagen que se encuentra en los recursos de la
            Bitmap bitmap = BitmapFactory.decodeResource(resources,
                    R.drawable.ud);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 5, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            imagen.scaleAbsolute(65, 90);

            imagen.setAlignment(Element.ALIGN_CENTER);

            //documento.add(imagen);

            PdfPTable tablaTitulo = new PdfPTable(2);

            PdfPCell cell;
            cell = new PdfPCell(new Phrase(titulo));
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaTitulo.addCell(cell);

            cell = new PdfPCell(new Phrase(subtitulo2));
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setPaddingBottom(5);
            cell.setPaddingTop(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaTitulo.addCell(cell);

            documento.add(tablaTitulo);

            documento.add(imagen);

            documento.add(text1);

            PdfPTable tablaFecha = new PdfPTable(4);
            PdfPCell dat;

            dat = new PdfPCell(new Phrase("FECHA"));
            dat.setRowspan(3);
            tablaFecha.addCell(dat);
            // we add the four remaining cells with addCell()
            tablaFecha.addCell("  Dia  " + fechaDia);
            tablaFecha.addCell("  Mes  " + fechaMes);
            tablaFecha.addCell("  Año  " + fechaAnno);

            documento.add(tablaFecha);

            PdfPTable tablaDatos = new PdfPTable(4);
            PdfPCell Datos;

            Datos = new PdfPCell(new Phrase("SEDE:" + "\n\n" + sede));
            Datos.setColspan(4);
            Datos.setPadding(5);
            tablaDatos.addCell(Datos);

            Datos = new PdfPCell(new Phrase("DEPENDENCIA:" + "\n\n" + dependencia));
            Datos.setColspan(4);
            Datos.setPadding(5);
            tablaDatos.addCell(Datos);

            Datos = new PdfPCell(new Phrase("NOMBRE DEL RESPONSABLE:" + "\n\n" + nomres));
            Datos.setColspan(4);
            Datos.setPadding(5);
            tablaDatos.addCell(Datos);

            Datos = new PdfPCell(new Phrase("CEDULA DEL RESPONSABLE:" + "\n\n" + cedres));
            Datos.setColspan(4);
            Datos.setPadding(5);
            tablaDatos.addCell(Datos);

            Datos = new PdfPCell(new Phrase("OBSERVACIONES:" + "\n\n" + obser));
            Datos.setColspan(4);
            Datos.setPadding(5);
            tablaDatos.addCell(Datos);

            Datos = new PdfPCell(new Phrase("VISITA No.:" + "\n\n" + numvis));
            Datos.setColspan(4);
            Datos.setPadding(5);
            tablaDatos.addCell(Datos);

            Datos = new PdfPCell(new Phrase("PROXIMA VISITA"));
            Datos.setRowspan(4);
            tablaDatos.addCell(Datos);
            // we add the four remaining cells with addCell()
            tablaDatos.addCell("  Dia  " + proxvisDia);
            tablaDatos.addCell("  Mes  " + proxvisMes);
            tablaDatos.addCell("  Año  " + proxvisAnno);

            documento.add(tablaDatos);

            documento.add(text);

            PdfPCell myCell1 = new PdfPCell(new Paragraph("FIRMA FUNCIONARIO DE ALMACEN") );
            myCell1.setBorder(Rectangle.TOP);

            PdfPCell myCell2 = new PdfPCell(new Paragraph("FIRMA RESPONSABLE DE INVENTARIO") );
            myCell2.setBorder(Rectangle.TOP);

            PdfPCell myCell3 = new PdfPCell(new Paragraph());
            myCell3.setBorder(0);

            PdfPTable tablaFirmas = new PdfPTable(3);

            //Datos.setColspan(2);
            tablaFirmas.addCell(myCell1);
            tablaFirmas.addCell(myCell3);
            tablaFirmas.addCell(myCell2);

            documento.add(tablaFirmas);

            // Agregar marca de agua
//            Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 42, Font.BOLD,
//                    Color.GRAY);
//            ColumnText.showTextAligned(writer.getDirectContentUnder(),
//                    Element.ALIGN_CENTER, new Paragraph(
//                            "Oficina Asesora de Sistemas", font3), 297.5f, 421,
//                    writer.getPageNumber() % 2 == 1 ? 45 : -45);

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