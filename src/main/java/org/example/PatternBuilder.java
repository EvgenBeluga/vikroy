package org.example;

import java.util.ArrayList;
import java.util.List;

public class PatternBuilder {

    private final double canvasScale = 6.0;  // для экрана
    private final double pdfScale = 28.35;   // для PDF (1 см = 28.35 pt)

    public PatternData buildForCanvas(Measurements m) {
        PatternData data = new PatternData();
        data.frontPoints = calculateHalfFront(m, canvasScale);
        data.backPoints = calculateHalfBack(m, canvasScale);
        data.sleevePoints = calculateSleeve(m, canvasScale);
        data.frontSkirtPoints = calculateSkirt(m, canvasScale, true);
        data.backSkirtPoints = calculateSkirt(m, canvasScale, false);
        return data;
    }

    public PatternData buildForPDF(Measurements m) {
        PatternData data = new PatternData();
        data.frontPoints = calculateHalfFront(m, pdfScale);
        data.backPoints = calculateHalfBack(m, pdfScale);
        data.sleevePoints = calculateSleeve(m, pdfScale);
        data.frontSkirtPoints = calculateSkirt(m, pdfScale, true);
        data.backSkirtPoints = calculateSkirt(m, pdfScale, false);
        return data;
    }

    // === ЛИФ: ПЕРЕД ===
    private List<PatternPoint> calculateHalfFront(Measurements m, double s) {
        List<PatternPoint> points = new ArrayList<>();
        double centerX = 400;
        double topY = 200;

        double chestW = (m.bust / 4.0) * m.stretchFactor * s;
        double waistW = (m.waist / 4.0) * m.stretchFactor * s;
        double hipW = (m.hips / 4.0) * m.stretchFactor * s;
        double bodyH = m.torso * s;
        double seatH = m.seatHeight * s;
        double armholeD = m.armholeDepth * s;
        double shoulderL = m.shoulderLength * s;
        double neckD = Math.min((m.neckCirc / 6.0) * m.stretchFactor, 7.0) * s;

        // 1. Плечо
        points.add(new PatternPoint(centerX, topY));
        points.add(new PatternPoint(centerX - shoulderL, topY));

        // 2. Горловина (плавная дуга из 3 точек)
        points.add(new PatternPoint(centerX - shoulderL + neckD / 2, topY - neckD / 2));
        points.add(new PatternPoint(centerX - neckD / 1.5, topY - neckD * 0.9));
        points.add(new PatternPoint(centerX, topY - neckD));

        // 3. Пройма (плавная дуга)
        double armholeStartX = centerX - chestW / 2;
        points.add(new PatternPoint(armholeStartX - 10, topY - armholeD / 2));
        points.add(new PatternPoint(centerX - waistW / 2, topY - armholeD * 0.75));
        points.add(new PatternPoint(centerX - waistW / 2, topY - armholeD));

        // 4. Боковой шов
        points.add(new PatternPoint(centerX - hipW / 2, topY - bodyH + seatH * 0.3));

        // 5. Ластовица (плавная кривая)
        points.add(new PatternPoint(centerX - hipW / 4, topY - bodyH + seatH * 0.1));
        points.add(new PatternPoint(centerX, topY - bodyH));

        // 6. Осевая
        points.add(new PatternPoint(centerX, topY));

        return points;
    }

    // === ЛИФ: СПИНКА ===
    private List<PatternPoint> calculateHalfBack(Measurements m, double s) {
        List<PatternPoint> points = new ArrayList<>();
        double centerX = 400;
        double topY = 200;

        double chestW = (m.bust / 4.0) * m.stretchFactor * s;
        double waistW = (m.waist / 4.0) * m.stretchFactor * s;
        double hipW = (m.hips / 4.0) * m.stretchFactor * s;
        double bodyH = m.torso * s;
        double seatH = m.seatHeight * s;
        double armholeD = m.armholeDepth * s;
        double shoulderL = m.shoulderLength * s;
        double backNeckD = 2.0 * s;

        points.add(new PatternPoint(centerX, topY));
        points.add(new PatternPoint(centerX - shoulderL, topY));
        points.add(new PatternPoint(centerX - shoulderL + backNeckD / 2, topY - backNeckD / 2));
        points.add(new PatternPoint(centerX, topY - backNeckD));

        double armholeStartX = centerX - chestW / 2;
        points.add(new PatternPoint(armholeStartX - 10, topY - armholeD / 2));
        points.add(new PatternPoint(centerX - waistW / 2, topY - armholeD * 0.75));
        points.add(new PatternPoint(centerX - waistW / 2, topY - armholeD));

        points.add(new PatternPoint(centerX - hipW / 2, topY - bodyH + seatH * 0.3));
        points.add(new PatternPoint(centerX - hipW / 4, topY - bodyH + seatH * 0.1));
        points.add(new PatternPoint(centerX, topY - bodyH));

        points.add(new PatternPoint(centerX, topY));

        return points;
    }

    // === РУКАВ ===
    private List<PatternPoint> calculateSleeve(Measurements m, double s) {
        List<PatternPoint> points = new ArrayList<>();
        double bicepW = (m.upperArmCirc / 2.0) * m.stretchFactor * s;
        double wristW = (m.wristCirc / 2.0) * m.stretchFactor * s;
        double sleeveH = m.sleeveLength * s;
        double capH = (m.armholeDepth * 0.8) * s;

        double startX = 800;
        double startY = 200;

        points.add(new PatternPoint(startX - bicepW / 2, startY + capH));
        points.add(new PatternPoint(startX, startY));
        points.add(new PatternPoint(startX + bicepW / 2, startY + capH));
        points.add(new PatternPoint(startX + wristW / 2, startY + sleeveH));
        points.add(new PatternPoint(startX - wristW / 2, startY + sleeveH));
        points.add(new PatternPoint(startX - bicepW / 2, startY + capH));

        return points;
    }

    // === ЮБКА ===
    private List<PatternPoint> calculateSkirt(Measurements m, double s, boolean isFront) {
        List<PatternPoint> points = new ArrayList<>();
        double waist = m.waist * m.stretchFactor;
        double length = isFront ? m.frontSkirtLength : m.backSkirtLength;

        double rWaist = (waist / (2 * Math.PI)) * s;
        double rBottom = rWaist + (length * s);

        double centerX = isFront ? 400 : 800;
        double centerY = 600;

        points.add(new PatternPoint(centerX, centerY));
        points.add(new PatternPoint(centerX + rWaist, centerY));
        points.add(new PatternPoint(centerX + rBottom, centerY));
        points.add(new PatternPoint(centerX, centerY + rBottom));
        points.add(new PatternPoint(centerX, centerY + rWaist));
        points.add(new PatternPoint(centerX, centerY));

        return points;
    }
}

class PatternData {
    List<PatternPoint> frontPoints;
    List<PatternPoint> backPoints;
    List<PatternPoint> sleevePoints;
    List<PatternPoint> frontSkirtPoints;
    List<PatternPoint> backSkirtPoints;
}