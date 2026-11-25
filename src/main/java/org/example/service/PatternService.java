package org.example.service;

import org.example.Measurements;
import org.example.PatternPoint;
import org.example.entity.MeasurementProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatternService {

    private final double cm = 28.35; // 1 см = 28.35 pt (для PDF)

    public PatternData build(MeasurementProfile m) {
        PatternData data = new PatternData();
        data.frontPoints = buildFront(m);
        data.backPoints = buildBack(m);
        data.sleevePoints = buildSleeve(m);
        data.frontSkirtPoints = buildSkirt(m, true);
        data.backSkirtPoints = buildSkirt(m, false);
        return data;
    }

    class PatternData {
        List<PatternPoint> frontPoints;
        List<PatternPoint> backPoints;
        List<PatternPoint> sleevePoints;
        List<PatternPoint> frontSkirtPoints;
        List<PatternPoint> backSkirtPoints;
    }

    private List<PatternPoint> buildBack(Measurements m) {
        List<PatternPoint> points = new ArrayList<>();
        double s = cm;
        boolean tall = m.height > 140;

        // 1. Точка А (верх спинки)
        double ax = 0;
        double ay = 0;
        points.add(new PatternPoint(ax, ay));

        // 2. Точка А1 (линия плеча)
        double a1x = ax;
        double a1y = ay - (tall ? 2.0 : 1.5) * s;
        points.add(new PatternPoint(a1x, a1y));

        // 3. Точка Т (талия)
        double tx = ax;
        double ty = a1y - m.dtc * s;
        points.add(new PatternPoint(tx, ty));

        // 4. Точка Н (низ)
        double hx = tx;
        double hy = ty - (m.vsd / 2) * s;
        points.add(new PatternPoint(hx, hy));

        // 5. Точка Г (линия груди)
        double gx = a1x;
        double gy = a1y - (m.bust / 6 - (tall ? 0 : 1)) * s;
        points.add(new PatternPoint(gx, gy));

        // 6. Точка А2 (горловина)
        double a2x = ax - (m.bust / 16 + 1) * s;
        double a2y = ay;
        points.add(new PatternPoint(a2x, a2y));

        // 7. Точка Г1 (пройма)
        double g1x = gx - (m.bust / 4) * s;
        double g1y = gy;
        points.add(new PatternPoint(g1x, g1y));

        // 8. Точка Г2
        double g2x = g1x + (m.bust / 16) * s;
        double g2y = gy;
        points.add(new PatternPoint(g2x, g2y));

        // 9. Точка П (вершина проймы)
        double px = g2x;
        double py = a1y;
        points.add(new new PatternPoint(px, py));

        // 10. Точка П1 (конец плеча)
        double p1x = px - (tall ? 2.0 : 1.5) * s;
        double p1y = py;
        points.add(new PatternPoint(p1x, p1y));

        // 11. Точка Т1 (боковой шов)
        double t1x = tx - (m.waist / 4) * s;
        double t1y = ty;
        points.add(new PatternPoint(t1x, t1y));

        // 12. Точка Т2 (смещение)
        double t2x = t1x + 1.0 * s;
        double t2y = ty;
        points.add(new PatternPoint(t2x, t2y));

        // 13. Вытачка на спинке (Т3, Т4)
        double t3y = ty + (tall ? 2.0 : 1.5) * s;
        double t4y = ty - (tall ? 2.5 : 2.0) * s;
        points.add(new PatternPoint(t2x, t3y));
        points.add(new PatternPoint(t2x, t4y));

        return points;
    }

    // === ПОСТРОЕНИЕ ПЕРЕДА ===
    private List<PatternPoint> buildFront(Measurements m) {
        List<PatternPoint> points = new ArrayList<>();
        double s = cm;
        boolean tall = m.height > 140;

        // 1. Точка А2 (горловина спинки) — базовая для переда
        double a2x = -(m.bust / 16 + 1) * s;
        double a2y = 0;
        points.add(new PatternPoint(a2x, a2y));

        // 2. Точка А3 (верх плеча спереди)
        double a3x = a2x;
        double a3y = a2y + (tall ? 1.0 : 0.5) * s;
        points.add(new PatternPoint(a3x, a3y));

        // 3. Точка А1п (линия плеча переда)
        double a1py = a3y - (tall ? 1.0 : 0.5) * s;
        points.add(new PatternPoint(a3x, a1py));

        // 4. Точка А4 (низ горловины спереди)
        double a4x = a2x;
        double a4y = a1py - (tall ? 2.5 : 2.0) * s;
        points.add(new PatternPoint(a4x, a4y));

        // 5. Точка П2 (конец плеча переда)
        double p2x = a3x - (m.bust / 16 + 1) * s;
        double p2y = a3y;
        points.add(new PatternPoint(p2x, p2y));

        // 6. Точка Г1 (пройма)
        double g1x = -(m.bust / 4) * s;
        double g1y = a1py - (m.bust / 6) * s;
        points.add(new PatternPoint(g1x, g1y));

        // 7. Точка Т1 (боковой шов)
        double t1x = -(m.waist / 4) * s;
        double t1y = -m.dtc * s;
        points.add(new PatternPoint(t1x, t1y));

        // 8. Точка Т2 (смещение)
        double t2x = t1x + 1.0 * s;
        double t2y = t1y;
        points.add(new PatternPoint(t2x, t2y));

        return points;
    }

    // === РУКАВ ===
    private List<PatternPoint> buildSleeve(Measurements m) {
        List<PatternPoint> points = new ArrayList<>();
        double s = cm;

        double sleeveLen = m.sleeveLength * s;
        double bicepW = (m.upperArmCirc / 2) * m.stretchFactor * s;
        double wristW = (m.wristCirc / 2) * m.stretchFactor * s;

        double startX = 0;
        double startY = 0;

        points.add(new PatternPoint(startX - bicepW / 2, startY));
        points.add(new PatternPoint(startX, startY - sleeveLen * 0.3));
        points.add(new PatternPoint(startX + bicepW / 2, startY));
        points.add(new PatternPoint(startX + wristW / 2, startY + sleeveLen));
        points.add(new PatternPoint(startX - wristW / 2, startY + sleeveLen));
        points.add(new PatternPoint(startX - bicepW / 2, startY));

        return points;
    }

    // === ЮБКА ===
    private List<PatternPoint> buildSkirt(Measurements m, boolean isFront) {
        List<PatternPoint> points = new ArrayList<>();
        double s = cm;

        double length = isFront ? m.frontSkirtLength : m.backSkirtLength;
        double waist = m.waist * m.stretchFactor;

        double rWaist = (waist / (2 * Math.PI)) * s;
        double rBottom = rWaist + (length * s);

        double centerX = 0;
        double centerY = 0;

        points.add(new PatternPoint(centerX, centerY));
        points.add(new PatternPoint(centerX + rWaist, centerY));
        points.add(new PatternPoint(centerX + rBottom, centerY));
        points.add(new PatternPoint(centerX, centerY + rBottom));
        points.add(new PatternPoint(centerX, centerY + rWaist));
        points.add(new PatternPoint(centerX, centerY));

        return points;
    }
}


/// / Вспомогательные классы (можно сделать record'ами)
//class PatternData {
//    public List<new PatternPoint> frontPoints, backPoints, sleevePoints, frontSkirtPoints, backSkirtPoints;
//}
//class new PatternPoint {
//    public double x, y;
//    public new PatternPoint(double x, double y) { this.x = x; this.y = y; }
//}
