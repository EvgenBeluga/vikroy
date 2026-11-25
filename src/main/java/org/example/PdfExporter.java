package org.example;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfExporter {

    public static void export(Measurements m) {
        String userHome = System.getProperty("user.home");
        String folderPath = userHome + File.separator + "Documents" + File.separator + "Выкройка";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();
        String filePath = folderPath + File.separator + "pattern_pro.pdf";

        float cm = 28.35f;
        float margin = 30;
        float pageWidth = PageSize.A4.getWidth();
        float pageHeight = PageSize.A4.getHeight();

        try {
            Document document = new Document(PageSize.A4, margin, margin, margin, margin);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            PdfContentByte canvas = writer.getDirectContent();

            PatternBuilder builder = new PatternBuilder();
            PatternData data = builder.build(m);

            // === ПОЗИЦИОНИРОВАНИЕ ===
            float centerFrontX = margin + 200;
            float centerBackX = margin + 50;
            float topY = pageHeight - margin - 100;

            // === РИСУЕМ ПЕРЕД ===
            drawPart(canvas, data.frontPoints, centerFrontX, topY, 0, 100, 255);

            // === РИСУЕМ СПИНКУ ===
            drawPart(canvas, data.backPoints, centerBackX, topY, 0, 0, 0);

            // === КОНТРОЛЬНЫЙ КВАДРАТ ===
            drawControlSquare(canvas, margin + 10, margin + 10, cm);

            document.close();
            System.out.println("PDF сохранён по пути: " + filePath);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawPart(PdfContentByte canvas, List<PatternPoint> points,
                                 float offsetX, float offsetY,
                                 int r, int g, int b) {
        if (points == null || points.size() < 2) return;

        canvas.saveState();
        canvas.setLineWidth(1.5f);
        canvas.setRGBColorStroke(r, g, b);

        // Начинаем с первой точки
        canvas.moveTo(offsetX + (float) points.get(0).x, offsetY + (float) points.get(0).y);

        // Соединяем линиями (можно заменить на curveTo для плавности)
        for (int i = 1; i < points.size(); i++) {
            canvas.lineTo(offsetX + (float) points.get(i).x, offsetY + (float) points.get(i).y);
        }

        canvas.stroke();
        canvas.restoreState();
    }

    private static void drawControlSquare(PdfContentByte canvas, float x, float y, float cm) {
        canvas.saveState();
        canvas.setLineWidth(1f);
        canvas.setRGBColorStroke(0, 0, 0);
        float sqSize = 10 * cm;
        canvas.rectangle(x, y, sqSize, sqSize);
        canvas.stroke();
        canvas.beginText();
        canvas.setFontAndSize(com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA, 10).getBaseFont(), 10);
        canvas.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, "10 см", x, y + sqSize + 5, 0);
        canvas.endText();
        canvas.restoreState();
    }
}