package com.vikroy.service;

import com.vikroy.dto.PatternResponse;
import com.vikroy.entity.MeasurementProfile;
import com.vikroy.model.PathCommand;
import com.vikroy.model.PatternPath;
import org.springframework.stereotype.Service;

@Service
public class SvgExportService {

    private static final double CM = 28.35; // 1 см = 28.35 pt (для SVG)

    private final PatternService patternService;

    public SvgExportService(PatternService patternService) {
        this.patternService = patternService;
    }

    public String generateSvg(MeasurementProfile profile) {
        if (profile == null) {
            return "<svg xmlns=\"http://www.w3.org/2000/svg\"><text x=\"10\" y=\"20\">Нет мерок</text></svg>";
        }

        PatternResponse data = patternService.build(profile);

        // Сдвиг для красивого размещения
        double offsetX = 300;
        double offsetY = 800;

        StringBuilder path = new StringBuilder();

        if (data.frontPath() != null) {
            appendPath(path, data.frontPath(), offsetX + 200, offsetY, "blue");
        }
        if (data.backPath() != null) {
            appendPath(path, data.backPath(), offsetX, offsetY, "black");
        }

        // --- Генерация SVG с сеткой ---
        String svg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"800\" height=\"1000\" viewBox=\"0 0 800 1000\">\n" +
                "  <rect width=\"100%\" height=\"100%\" fill=\"white\"/>\n" +
                "  <defs>\n" +
                "    <pattern id=\"grid\" width=\"" + CM * 5 + "\" height=\"" + CM * 5 + "\" patternUnits=\"userSpaceOnUse\">\n" +
                "      <path d=\"M " + CM * 5 + " 0 L 0 0 0 " + CM * 5 + "\" fill=\"none\" stroke=\"#eee\" stroke-width=\"0.5\"/>\n" +
                "    </pattern>\n" +
                "  </defs>\n" +
                "  <rect width=\"100%\" height=\"100%\" fill=\"url(#grid)\"/>\n" +
                "  " + path.toString() +
                "</svg>";

        return svg;
    }

    private void appendPath(StringBuilder sb, PatternPath path, double offsetX, double offsetY, String color) {
        sb.append("<path d=\"");
        for (PathCommand cmd : path.getCommands()) {
            switch (cmd.getType()) {
                case MOVE_TO -> sb.append("M ").append(cx(cmd.getX1(), offsetX)).append(",").append(cy(cmd.getY1(), offsetY)).append(" ");
                case LINE_TO -> sb.append("L ").append(cx(cmd.getX1(), offsetX)).append(",").append(cy(cmd.getY1(), offsetY)).append(" ");
                case CURVE_TO -> sb.append("C ")
                        .append(cx(cmd.getX1(), offsetX)).append(",").append(cy(cmd.getY1(), offsetY)).append(" ")
                        .append(cx(cmd.getX2(), offsetX)).append(",").append(cy(cmd.getY2(), offsetY)).append(" ")
                        .append(cx(cmd.getX3(), offsetX)).append(",").append(cy(cmd.getY3(), offsetY)).append(" ");
            }
        }
        sb.append("\" fill=\"none\" stroke=\"").append(color).append("\" stroke-width=\"1.5\"/>\n");
    }

    private String cx(double xCm, double offsetX) {
        return String.format(java.util.Locale.US, "%.2f", offsetX + xCm * CM);
    }

    private String cy(double yCm, double offsetY) {
        return String.format(java.util.Locale.US, "%.2f", offsetY - yCm * CM);
    }
}