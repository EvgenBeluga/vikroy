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

    private static final float SCALE = 10f;
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

            float offsetX = MARGIN + 100;
            float offsetY = pageH - MARGIN - 100;

            if (data.frontPath() != null) {
                drawPath(canvas, data.frontPath(), offsetX + 200, offsetY, 0, 0, 255); // синий
            }
            if (data.backPath() != null) {
                drawPath(canvas, data.backPath(), offsetX, offsetY, 0, 0, 0); // чёрный
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
                case MOVE_TO -> canvas.moveTo(offsetX + (float) cmd.getX1() * SCALE,
                        offsetY + (float) cmd.getY1() * SCALE);
                case LINE_TO -> canvas.lineTo(offsetX + (float) cmd.getX1() * SCALE,
                        offsetY + (float) cmd.getY1() * SCALE);
                case CURVE_TO -> canvas.curveTo(
                        offsetX + (float) cmd.getX1() * SCALE,
                        offsetY + (float) cmd.getY1() * SCALE,
                        offsetX + (float) cmd.getX2() * SCALE,
                        offsetY + (float) cmd.getY2() * SCALE,
                        offsetX + (float) cmd.getX3() * SCALE,
                        offsetY + (float) cmd.getY3() * SCALE
                );
            }
        }
        canvas.stroke();
        canvas.restoreState();
    }
}