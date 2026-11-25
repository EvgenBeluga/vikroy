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

    private static final float PREVIEW_SCALE = 0.4f;
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
            float pageW = PageSize.A4.getWidth();

            drawGrid(canvas);

            if (data.backPath() != null) {
                // Вычисляем bounding box по всем точкам пути
                double[] bbox = computeBBox(data.backPath());
                double minX = bbox[0], minY = bbox[1], maxX = bbox[2], maxY = bbox[3];

                // Масштаб: переводим см в pt с учётом PREVIEW_SCALE
                float scale = (float) (PatternService.CM * PREVIEW_SCALE);

                // Сдвиг: левый край детали -> MARGIN, верхний край -> pageH - MARGIN
                float offsetX = MARGIN - (float) minX * scale;
                float offsetY = pageH - MARGIN - (float) maxY * scale;

                drawPath(canvas, data.backPath(), offsetX, offsetY, scale, 0, 0, 0);
            }

            document.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("Ошибка генерации PDF", e);
        }

        return out.toByteArray();
    }

    private double[] computeBBox(PatternPath path) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (PathCommand cmd : path.getCommands()) {
            // Для CURVE_TO учитываем все три точки, для остальных — одну
            double[] xs, ys;
            if (cmd.getType() == PathCommand.Type.CURVE_TO) {
                xs = new double[]{cmd.getX1(), cmd.getX2(), cmd.getX3()};
                ys = new double[]{cmd.getY1(), cmd.getY2(), cmd.getY3()};
            } else {
                xs = new double[]{cmd.getX1()};
                ys = new double[]{cmd.getY1()};
            }
            for (double x : xs) { minX = Math.min(minX, x); maxX = Math.max(maxX, x); }
            for (double y : ys) { minY = Math.min(minY, y); maxY = Math.max(maxY, y); }
        }
        return new double[]{minX, minY, maxX, maxY};
    }

    private void drawPath(PdfContentByte canvas, PatternPath path,
                          float offsetX, float offsetY, float scale,
                          int r, int g, int b) {
        canvas.saveState();
        canvas.setLineWidth(1.2f);
        canvas.setRGBColorStroke(r, g, b);

        for (PathCommand cmd : path.getCommands()) {
            switch (cmd.getType()) {
                case MOVE_TO -> canvas.moveTo(offsetX + (float) cmd.getX1() * scale,
                        offsetY + (float) cmd.getY1() * scale);
                case LINE_TO -> canvas.lineTo(offsetX + (float) cmd.getX1() * scale,
                        offsetY + (float) cmd.getY1() * scale);
                case CURVE_TO -> canvas.curveTo(
                        offsetX + (float) cmd.getX1() * scale,
                        offsetY + (float) cmd.getY1() * scale,
                        offsetX + (float) cmd.getX2() * scale,
                        offsetY + (float) cmd.getY2() * scale,
                        offsetX + (float) cmd.getX3() * scale,
                        offsetY + (float) cmd.getY3() * scale
                );
            }
        }
        canvas.stroke();
        canvas.restoreState();
    }

    private void drawGrid(PdfContentByte canvas) {
        canvas.saveState();
        canvas.setLineWidth(0.2f);
        canvas.setRGBColorStroke(220, 220, 220);
        float pageW = PageSize.A4.getWidth();
        float pageH = PageSize.A4.getHeight();
        float step = 28.35f * PREVIEW_SCALE;
        for (float x = MARGIN; x < pageW - MARGIN; x += step) {
            canvas.moveTo(x, MARGIN);
            canvas.lineTo(x, pageH - MARGIN);
        }
        for (float y = MARGIN; y < pageH - MARGIN; y += step) {
            canvas.moveTo(MARGIN, y);
            canvas.lineTo(pageW - MARGIN, y);
        }
        canvas.stroke();
        canvas.restoreState();
    }
}