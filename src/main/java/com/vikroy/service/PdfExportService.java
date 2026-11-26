package com.vikroy.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.vikroy.dto.PatternResponse;
import com.vikroy.entity.MeasurementProfile;
import com.vikroy.model.PathCommand;
import com.vikroy.model.PatternPath;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfExportService {

    private static final float CM = 28.35f; // 1 см = 28.35 pt
    private static final float MARGIN = 30f;

    private final PatternService patternService;

    public PdfExportService(PatternService patternService) {
        this.patternService = patternService;
    }

    public byte[] generatePdf(MeasurementProfile profile) {
        PatternResponse data = patternService.build(profile);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, MARGIN, MARGIN, MARGIN, MARGIN);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();
            PdfContentByte canvas = writer.getDirectContent();
            float pageH = PageSize.A4.getHeight();

            // --- Контрольный квадрат 10x10 см ---
            drawControlSquare(canvas, MARGIN, MARGIN, CM);

            // --- Перед (синий) ---
            float offsetXFront = MARGIN + 200;
            float offsetYFront = pageH - MARGIN - 100;
            if (data.frontPath() != null) {
                drawPath(canvas, data.frontPath(), offsetXFront, offsetYFront, 0, 0, 255);
            }

            // --- Спинка (чёрный) ---
            float offsetXBack = MARGIN + 50;
            float offsetYBack = pageH - MARGIN - 100;
            if (data.backPath() != null) {
                drawPath(canvas, data.backPath(), offsetXBack, offsetYBack, 0, 0, 0);
            }

            document.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("Ошибка генерации PDF", e);
        }

        return out.toByteArray();
    }

    private void drawPath(PdfContentByte canvas, PatternPath path,
                          float offsetX, float offsetY, int r, int g, int b) {
        canvas.saveState();
        canvas.setLineWidth(1.2f);
        canvas.setRGBColorStroke(r, g, b);

        for (PathCommand cmd : path.getCommands()) {
            switch (cmd.getType()) {
                case MOVE_TO -> canvas.moveTo(offsetX + (float) cmd.getX1() * CM,
                        offsetY + (float) cmd.getY1() * CM);
                case LINE_TO -> canvas.lineTo(offsetX + (float) cmd.getX1() * CM,
                        offsetY + (float) cmd.getY1() * CM);
                case CURVE_TO -> canvas.curveTo(
                        offsetX + (float) cmd.getX1() * CM,
                        offsetY + (float) cmd.getY1() * CM,
                        offsetX + (float) cmd.getX2() * CM,
                        offsetY + (float) cmd.getY2() * CM,
                        offsetX + (float) cmd.getX3() * CM,
                        offsetY + (float) cmd.getY3() * CM
                );
            }
        }
        canvas.stroke();
        canvas.restoreState();
    }

    private void drawControlSquare(PdfContentByte canvas, float x, float y, float cm) {
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