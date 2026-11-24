package org.example;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
        float usableWidth = pageWidth - 2 * margin;
        float usableHeight = pageHeight - 2 * margin;

        // === РАСЧЁТЫ В СМ ===
        double stretch = 0.88;
        double chestW = (m.bust / 4.0) * stretch;
        double waistW = (m.waist / 4.0) * stretch;
        double hipW = (m.hips / 4.0) * stretch;
        double bodyH = m.torso;
        double seatH = m.seatHeight;
        double totalH = bodyH + seatH;
        double neckD = Math.min((m.neckCirc / 6.0) * stretch, 7.0);
        double backNeckD = 2.0;
        double armholeD = m.armholeDepth;
        double shoulderL = m.shoulderLength;

        // Перевод в пункты
        float cw = (float) chestW * cm;
        float ww = (float) waistW * cm;
        float hw = (float) hipW * cm;
        float bh = (float) bodyH * cm;
        float sh = (float) seatH * cm;
        float th = (float) totalH * cm;
        float nd = (float) neckD * cm;
        float bnd = (float) backNeckD * cm;
        float ad = (float) armholeD * cm;
        float sl = (float) shoulderL * cm;

        // Масштаб, чтобы вписать в лист (80% ширины/высоты)
        float maxW = Math.max(cw, Math.max(ww, hw));
        float maxH = th;
        float scaleX = (usableWidth * 0.8f) / maxW;
        float scaleY = (usableHeight * 0.8f) / maxH;
        float scale = Math.min(scaleX, scaleY);
        if (scale > 1.0f) scale = 1.0f;

        try {
            Document document = new Document(PageSize.A4, margin, margin, margin, margin);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            PdfContentByte canvas = writer.getDirectContent();

            // === ПЕРЕД (справа) ===
            float centerX = margin + usableWidth * 0.7f;
            float topY = margin + usableHeight * 0.85f;
            drawFront(canvas, centerX, topY, cw * scale, ww * scale, hw * scale, bh * scale, sh * scale, nd * scale, ad * scale, sl * scale);

            // === СПИНКА (слева) ===
            float backCenterX = centerX - maxW * scale - 30;
            drawBack(canvas, backCenterX, topY, cw * scale, ww * scale, hw * scale, bh * scale, sh * scale, bnd * scale, ad * scale, sl * scale);

            // === КОНТРОЛЬНЫЙ КВАДРАТ ===
            drawControlSquare(canvas, margin + 10, margin + 10, cm);

            document.close();
            System.out.println("PDF сохранён по пути: " + filePath);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawFront(PdfContentByte canvas, float centerX, float topY,
                                  float cw, float ww, float hw, float bh, float sh,
                                  float nd, float ad, float sl) {
        canvas.setLineWidth(1.5f);
        canvas.setRGBColorStroke(0, 0, 255); // Синий

        // 1. Плечо (от центра влево)
        canvas.moveTo(centerX, topY);
        canvas.lineTo(centerX - sl, topY);

        // 2. Горловина (плавная дуга от плеча к центру)
        canvas.curveTo(
                centerX - sl + nd / 2, topY - nd / 2,
                centerX - nd / 1.5f, topY - nd * 0.9f,
                centerX, topY - nd
        );

        // 3. Пройма (от конца плеча вниз к талии)
        float armholeStartX = centerX - cw / 2;
        canvas.moveTo(centerX - sl, topY);
        canvas.curveTo(
                armholeStartX - 5, topY - ad / 2,
                centerX - ww / 2, topY - ad * 0.75f,
                centerX - ww / 2, topY - ad
        );

        // 4. Боковой шов (от талии к бедру)
        canvas.lineTo(centerX - hw / 2, topY - bh + sh * 0.3f);

        // 5. Ластовица (плавная кривая к центру)
        canvas.curveTo(
                centerX - hw / 4, topY - bh + sh * 0.1f,
                centerX - hw / 6, topY - bh,
                centerX, topY - bh
        );

        // 6. Осевая линия (замыкаем)
        canvas.lineTo(centerX, topY);

        canvas.stroke();
    }

    private static void drawBack(PdfContentByte canvas, float centerX, float topY,
                                 float cw, float ww, float hw, float bh, float sh,
                                 float bnd, float ad, float sl) {
        canvas.setLineWidth(1.5f);
        canvas.setRGBColorStroke(0, 0, 0); // Чёрный

        // 1. Плечо
        canvas.moveTo(centerX, topY);
        canvas.lineTo(centerX - sl, topY);

        // 2. Горловина спинки (мелкая)
        canvas.curveTo(
                centerX - sl + bnd / 2, topY - bnd / 2,
                centerX - bnd / 1.5f, topY - bnd * 0.9f,
                centerX, topY - bnd
        );

        // 3. Пройма спинки
        float armholeStartX = centerX - cw / 2;
        canvas.moveTo(centerX - sl, topY);
        canvas.curveTo(
                armholeStartX - 5, topY - ad / 2,
                centerX - ww / 2, topY - ad * 0.75f,
                centerX - ww / 2, topY - ad
        );

        // 4. Боковой шов
        canvas.lineTo(centerX - hw / 2, topY - bh + sh * 0.3f);

        // 5. Ластовица
        canvas.curveTo(
                centerX - hw / 4, topY - bh + sh * 0.1f,
                centerX - hw / 6, topY - bh,
                centerX, topY - bh
        );

        // 6. Осевая
        canvas.lineTo(centerX, topY);

        canvas.stroke();
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