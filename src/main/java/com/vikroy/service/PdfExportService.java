package com.vikroy.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.vikroy.dto.PatternResponse;
import com.vikroy.entity.MeasurementProfile;
import com.vikroy.model.PatternPoint;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfExportService {

    /** 1 см = 28.35 pt */
    private static final float CM = 28.35f;
    private static final float MARGIN = 30f;

    private final PatternService patternService;

    public PdfExportService(PatternService patternService) {
        this.patternService = patternService;
    }

    /** Генерирует PDF с выкройкой и возвращает байты (для HTTP-ответа) */
    public byte[] generatePdf(MeasurementProfile profile) {
        PatternResponse data = patternService.build(profile);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, MARGIN, MARGIN, MARGIN, MARGIN);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            PdfContentByte canvas = writer.getDirectContent();

            float pageHeight = PageSize.A4.getHeight();
            float centerFrontX = MARGIN + 200;
            float centerBackX = MARGIN + 50;
            float topY = pageHeight - MARGIN - 100;

            // Перед (синий)
            drawPart(canvas, data.frontPoints(), centerFrontX, topY, 0, 100, 255);
            // Спинка (чёрный)
            drawPart(canvas, data.backPoints(), centerBackX, topY, 0, 0, 0);
            // Рукав (зелёный)
            drawPart(canvas, data.sleevePoints(), MARGIN + 50, topY - 400, 0, 128, 0);
            // Юбки (фиолетовый)
            drawPart(canvas, data.frontSkirtPoints(), MARGIN + 200, topY - 400, 128, 0, 128);
            drawPart(canvas, data.backSkirtPoints(), MARGIN + 350, topY - 400, 128, 0, 128);

            // Контрольный квадрат 10×10 см
            drawControlSquare(canvas, MARGIN + 10, MARGIN + 10);

            document.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("Ошибка генерации PDF", e);
        }

        return out.toByteArray();
    }

    private void drawPart(PdfContentByte canvas, List<PatternPoint> points,
                          float offsetX, float offsetY, int r, int g, int b) {
        if (points == null || points.size() < 2) return;

        canvas.saveState();
        canvas.setLineWidth(1.5f);
        canvas.setRGBColorStroke(r, g, b);

        canvas.moveTo(offsetX + (float) points.get(0).getX(),
                offsetY + (float) points.get(0).getY());
        for (int i = 1; i < points.size(); i++) {
            canvas.lineTo(offsetX + (float) points.get(i).getX(),
                    offsetY + (float) points.get(i).getY());
        }
        canvas.stroke();
        canvas.restoreState();
    }

    private void drawControlSquare(PdfContentByte canvas, float x, float y) {
        canvas.saveState();
        canvas.setLineWidth(1f);
        canvas.setRGBColorStroke(0, 0, 0);

        float sqSize = 10 * CM;
        canvas.rectangle(x, y, sqSize, sqSize);
        canvas.stroke();

        canvas.beginText();
        canvas.setFontAndSize(
                com.lowagie.text.FontFactory
                        .getFont(com.lowagie.text.FontFactory.HELVETICA, 10)
                        .getBaseFont(),
                10
        );
        canvas.showTextAligned(
                com.lowagie.text.Element.ALIGN_LEFT,
                "10 см",
                x,
                y + sqSize + 5,
                0
        );
        canvas.endText();
        canvas.restoreState();
    }
}