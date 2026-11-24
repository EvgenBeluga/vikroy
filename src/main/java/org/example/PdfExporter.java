package org.example;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfExporter {

    public static void export(double bust, double waist, double hips, double torso, double seatHeight,
                              double backWidth, double armholeDepth, double shoulderLength, double neckCirc) {
        String folderPath = "C:\\Users\\Пользователь\\Documents\\Выкройка";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();
        String filePath = folderPath + "\\pattern_pro.pdf";

        float pageWidth = PageSize.A4.getWidth();
        float pageHeight = PageSize.A4.getHeight();
        float margin = 20;
        float usableWidth = pageWidth - 2 * margin;
        float usableHeight = pageHeight - 2 * margin;
        float cm = 28.35f;
        float overlap = 1 * cm;

        double stretch = 0.88;
        double chestWidthCm = (bust / 4.0) * stretch;
        double waistWidthCm = (waist / 4.0) * stretch;
        double hipWidthCm = (hips / 4.0) * stretch;
        double bodyHeightCm = torso;
        double seatHeightCm = seatHeight;
        double totalHeightCm = torso + seatHeight;

        double neckDepthCm = Math.min(chestWidthCm / 3.0, 7.0);
        double backNeckDepthCm = 2.0;

        double armholeDepthCm = armholeDepth;
        double shoulderLengthCm = shoulderLength;

        float chestWidth = (float) chestWidthCm * cm;
        float waistWidth = (float) waistWidthCm * cm;
        float hipWidth = (float) hipWidthCm * cm;
        float bodyHeight = (float) bodyHeightCm * cm;
        float seatHeightPt = (float) seatHeightCm * cm;
        float totalHeight = (float) totalHeightCm * cm;
        float neckDepth = (float) neckDepthCm * cm;
        float backNeckDepth = (float) backNeckDepthCm * cm;
        float armholeDepthPt = (float) armholeDepthCm * cm;
        float shoulderLengthPt = (float) shoulderLengthCm * cm;

        float maxHalfWidth = Math.max(chestWidth / 2, Math.max(waistWidth / 2, hipWidth / 2));
        float patternWidth = maxHalfWidth + 20; // только половина + отступ
        float patternHeight = totalHeight + 60;

        float effectiveWidth = usableWidth - overlap;
        float effectiveHeight = usableHeight - overlap;

        int cols = (int) Math.ceil(patternWidth / effectiveWidth);
        int rows = (int) Math.ceil(patternHeight / effectiveHeight);
        if (cols < 1) cols = 1;
        if (rows < 1) rows = 1;

        Document document = new Document(PageSize.A4, margin, margin, margin, margin);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            float shiftX = margin + 10; // левый отступ

            System.out.println("Рисуем ПЕРЕД...");
            drawPart(writer, document, "Перед", cols, rows, effectiveWidth, effectiveHeight,
                    patternWidth, patternHeight,
                    chestWidth, waistWidth, hipWidth, bodyHeight, seatHeightPt, totalHeight,
                    neckDepth, armholeDepthPt, shoulderLengthPt,
                    maxHalfWidth, // <-- ПЕРЕДАЁМ maxHalfWidth
                    cm, margin, pageWidth, pageHeight, shiftX, true);

            System.out.println("Рисуем СПИНКУ...");
            drawPart(writer, document, "Спинка", cols, rows, effectiveWidth, effectiveHeight,
                    patternWidth, patternHeight,
                    chestWidth, waistWidth, hipWidth, bodyHeight, seatHeightPt, totalHeight,
                    backNeckDepth, armholeDepthPt, shoulderLengthPt,
                    maxHalfWidth, // <-- ПЕРЕДАЁМ maxHalfWidth
                    cm, margin, pageWidth, pageHeight, shiftX, false);

            document.close();
            System.out.println("PDF сохранён по пути: " + filePath);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawPart(PdfWriter writer, Document document, String partName,
                                 int cols, int rows, float effectiveWidth, float effectiveHeight,
                                 float patternWidth, float patternHeight,
                                 float chestWidth, float waistWidth, float hipWidth,
                                 float bodyHeight, float seatHeightPt, float totalHeight,
                                 float neckDepth, float armholeDepth, float shoulderLength,
                                 float maxHalfWidth, // <-- НОВЫЙ ПАРАМЕТР
                                 float cm, float margin, float pageWidth, float pageHeight,
                                 float shiftX, boolean isFront) throws DocumentException {

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (writer.getPageNumber() > 0) document.newPage();
                PdfContentByte canvas = writer.getDirectContent();

                float offsetX = col * effectiveWidth;
                float offsetY = row * effectiveHeight;

                canvas.saveState();
                canvas.concatCTM(1, 0, 0, 1, -offsetX + shiftX, -offsetY);

                // --- Сетка (каждые 5 см) ---
                canvas.saveState();
                canvas.setLineWidth(0.3f);
                canvas.setRGBColorStroke(220, 220, 220);
                float gridStep = 5 * cm;
                for (float x = 0; x < patternWidth; x += gridStep) {
                    canvas.moveTo(x, 0);
                    canvas.lineTo(x, patternHeight);
                }
                for (float y = 0; y < patternHeight; y += gridStep) {
                    canvas.moveTo(0, y);
                    canvas.lineTo(patternWidth, y);
                }
                canvas.stroke();
                canvas.restoreState();

                // --- Рисуем контур ---
                float centerX = maxHalfWidth + 10; // осевая линия
                float bottomY = 30;
                float topY = bottomY + totalHeight;

                canvas.setLineWidth(1.5f);

                // 1. Плечо (от осевой линии влево)
                canvas.moveTo(centerX, topY);
                canvas.lineTo(centerX - shoulderLength, topY);

                // 2. Горловина
                canvas.curveTo(
                        centerX - shoulderLength + neckDepth / 4, topY - neckDepth / 3,
                        centerX - neckDepth / 2, topY - neckDepth * 0.8f,
                        centerX, topY - neckDepth
                );

                // 3. Пройма
                float armholeStartX = centerX - chestWidth / 2;
                canvas.moveTo(centerX - shoulderLength, topY);
                canvas.curveTo(
                        armholeStartX - 10, topY - armholeDepth / 2,
                        centerX - waistWidth / 2, topY - armholeDepth * 0.75f,
                        centerX - waistWidth / 2, topY - armholeDepth
                );

                // 4. Боковой шов
                canvas.lineTo(centerX - hipWidth / 2, bottomY + seatHeightPt * 0.3f);

                // 5. Ластовица
                canvas.curveTo(
                        centerX - hipWidth / 4, bottomY - 5,
                        centerX - hipWidth / 6, bottomY,
                        centerX, bottomY
                );

                // 6. Осевая
                canvas.moveTo(centerX, topY);
                canvas.lineTo(centerX, bottomY);

                canvas.stroke();

                // 7. Вытачка (только для переда)
                if (isFront) {
                    float dartY = topY - armholeDepth * 0.6f;
                    canvas.saveState();
                    canvas.setLineWidth(1f);
                    canvas.setLineDash(4, 4);
                    canvas.moveTo(centerX - chestWidth / 4, dartY);
                    canvas.lineTo(centerX - waistWidth / 4, dartY - 15);
                    canvas.stroke();
                    canvas.restoreState();
                }

                canvas.restoreState();

                // --- Метки для склейки ---
                canvas.saveState();
                canvas.setLineWidth(0.5f);
                canvas.setLineDash(3, 3);
                float markLen = 10;

                if (col > 0) {
                    canvas.moveTo(margin, pageHeight / 2 - markLen);
                    canvas.lineTo(margin, pageHeight / 2 + markLen);
                    canvas.stroke();
                }
                if (col < cols - 1) {
                    canvas.moveTo(pageWidth - margin, pageHeight / 2 - markLen);
                    canvas.lineTo(pageWidth - margin, pageHeight / 2 + markLen);
                    canvas.stroke();
                }
                if (row > 0) {
                    canvas.moveTo(pageWidth / 2 - markLen, margin);
                    canvas.lineTo(pageWidth / 2 + markLen, margin);
                    canvas.stroke();
                }
                if (row < rows - 1) {
                    canvas.moveTo(pageWidth / 2 - markLen, pageHeight - margin);
                    canvas.lineTo(pageWidth / 2 + markLen, pageHeight - margin);
                    canvas.stroke();
                }
                canvas.restoreState();

                // --- Название детали и номер листа ---
                canvas.beginText();
                canvas.setFontAndSize(FontFactory.getFont(FontFactory.HELVETICA, 10).getBaseFont(), 10);
                String title = partName + " — лист " + (row * cols + col + 1) + "/" + (rows * cols);
                canvas.showTextAligned(Element.ALIGN_LEFT, title, margin + 10, pageHeight - margin - 10, 0);
                canvas.endText();

                // --- Контрольный квадрат ---
                if (row == 0 && col == 0) {
                    canvas.saveState();
                    canvas.setLineWidth(1f);
                    float sqSize = 10 * cm;
                    canvas.rectangle(margin + 10, margin + 10, sqSize, sqSize);
                    canvas.stroke();
                    canvas.beginText();
                    canvas.setFontAndSize(FontFactory.getFont(FontFactory.HELVETICA, 10).getBaseFont(), 10);
                    canvas.showTextAligned(Element.ALIGN_LEFT, "10 см", margin + 10, margin + 10 + sqSize + 5, 0);
                    canvas.endText();
                    canvas.restoreState();
                }
            }
        }
    }
}