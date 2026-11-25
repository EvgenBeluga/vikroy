package com.vikroy.service;

import com.vikroy.entity.MeasurementProfile;
import org.springframework.stereotype.Service;

@Service
public class SvgExportService {

    private static final double SCALE = 10.0;

    public String generateSvg(MeasurementProfile profile) {
        if (profile == null) {
            return "<svg xmlns=\"http://www.w3.org/2000/svg\"><text x=\"10\" y=\"20\">Нет мерок</text></svg>";
        }

        // --- Координаты (см) ---
        double ax = 0, ay = 0;
        double a1y = -1.5;
        double a2x = -4.7, a2y = 0;
        double g1x = -15.0, g1y = -10.5;
        double p1x = -2.5, p1y = -1.5;       // П1 — короткое плечо (было -12.7)
        double tx = 0, ty = -28.0;
        double t1x = -12.0, t1y = ty;
        double t2x = -11.0, t2y = ty;
        double hy = -52.0;
        double b2x = -11.7, b2y = -37.8;
        double b3x = -11.7, b3y = -38.6;
        double b4x = -10.2, b4y = -38.6;
        double h1x = -3.0, h1y = -46.7;
        double h2x = -4.0, h2y = -52.0;
        double h3x = -7.6, h3y = -45.8;

        // --- Сдвиг, чтобы всё было в положительной области ---
        double offsetX = 300;
        double offsetY = 600;

        // --- Построение пути (ломаная, только lineTo) ---
        StringBuilder path = new StringBuilder();
        path.append("M ").append(cx(a2x, offsetX)).append(",").append(cy(a2y, offsetY)).append(" ");
        path.append("L ").append(cx(ax, offsetX)).append(",").append(cy(a1y, offsetY)).append(" ");
        path.append("L ").append(cx(p1x, offsetX)).append(",").append(cy(p1y, offsetY)).append(" ");
        path.append("L ").append(cx(g1x, offsetX)).append(",").append(cy(g1y, offsetY)).append(" ");
        path.append("L ").append(cx(t1x, offsetX)).append(",").append(cy(t1y, offsetY)).append(" ");
        path.append("L ").append(cx(b2x, offsetX)).append(",").append(cy(b2y, offsetY)).append(" ");
        path.append("L ").append(cx(b3x, offsetX)).append(",").append(cy(b3y, offsetY)).append(" ");
        path.append("L ").append(cx(b4x, offsetX)).append(",").append(cy(b4y, offsetY)).append(" ");
        path.append("L ").append(cx(h3x, offsetX)).append(",").append(cy(h3y, offsetY)).append(" ");
        path.append("L ").append(cx(h2x, offsetX)).append(",").append(cy(h2y, offsetY)).append(" ");
        path.append("L ").append(cx(0, offsetX)).append(",").append(cy(hy, offsetY)).append(" ");
        path.append("L ").append(cx(h1x, offsetX)).append(",").append(cy(h1y, offsetY)).append(" ");
        path.append("L ").append(cx(t1x, offsetX)).append(",").append(cy(t1y, offsetY)).append(" ");
        path.append("L ").append(cx(t2x, offsetX)).append(",").append(cy(t2y, offsetY)).append(" ");
        path.append("L ").append(cx(tx, offsetX)).append(",").append(cy(ty, offsetY)).append(" ");
        path.append("L ").append(cx(ax, offsetX)).append(",").append(cy(ay, offsetY)).append(" ");
        path.append("L ").append(cx(a2x, offsetX)).append(",").append(cy(a2y, offsetY)).append(" Z");

        // --- Генерация SVG ---
        String svg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"800\" height=\"1000\" viewBox=\"0 0 800 1000\">\n" +
                "  <rect width=\"100%\" height=\"100%\" fill=\"white\"/>\n" +
                "  <defs>\n" +
                "    <pattern id=\"grid\" width=\"" + SCALE + "\" height=\"" + SCALE + "\" patternUnits=\"userSpaceOnUse\">\n" +
                "      <path d=\"M " + SCALE + " 0 L 0 0 0 " + SCALE + "\" fill=\"none\" stroke=\"#eee\" stroke-width=\"0.5\"/>\n" +
                "    </pattern>\n" +
                "  </defs>\n" +
                "  <rect width=\"100%\" height=\"100%\" fill=\"url(#grid)\"/>\n" +
                "  <path d=\"" + path.toString() + "\" fill=\"none\" stroke=\"black\" stroke-width=\"1.5\"/>\n" +
                "</svg>";

        // --- Отладка: вывести SVG в консоль ---
        System.out.println("=== SVG ===");
        System.out.println(svg);

        return svg;
    }

    private String cx(double xCm, double offsetX) {
        return String.format(java.util.Locale.US, "%.2f", offsetX + xCm * SCALE);
    }

    private String cy(double yCm, double offsetY) {
        return String.format(java.util.Locale.US, "%.2f", offsetY - yCm * SCALE);
    }
}