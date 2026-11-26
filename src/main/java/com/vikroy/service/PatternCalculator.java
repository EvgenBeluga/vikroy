package com.vikroy.service;

import com.vikroy.dto.PatternResponse;
import com.vikroy.entity.MeasurementProfile;
import com.vikroy.model.PatternPath;
import org.springframework.stereotype.Component;

/**
 * Строит выкройку по методике Lana (17 мерок).
 * Координаты в сантиметрах, ось Y направлена ВВЕРХ (PDF-координаты).
 */
@Component
public class PatternCalculator {

    // Для роста > 140 см используем одни прибавки, для <= 140 — другие
    private boolean isTall(MeasurementProfile m) {
        return m.getHeight() != null && m.getHeight() > 140;
    }

    public PatternResponse build(MeasurementProfile m) {
        return new PatternResponse(
                buildFront(m),
                buildBack(m),
                buildSleeve(m)
        );
    }

    // ==================== СПИНКА ====================
    private PatternPath buildBack(MeasurementProfile m) {
        PatternPath p = new PatternPath();
        boolean tall = isTall(m);

        // --- Перевод в сантиметры (мерки уже в см) ---
        double dtc = m.getDtc();
        double vsd = m.getVsd();
        double bust = m.getBust();
        double waist = m.getWaist();
        double hips = m.getHips();

        // --- Точка А (верх спинки) ---
        double ax = 0, ay = 0;

        // --- Точка А1 (линия плеча) ---
        double a1y = tall ? -2.0 : -1.5;
        double a1x = 0;

        // --- Точка Т (талия) ---
        double ty = a1y - dtc;
        double tx = 0;

        // --- Точка Н (низ) ---
        double hy = ty - (vsd / 2);
        double hx = 0;

        // --- Точка Г (линия груди) ---
        double gy = a1y - (bust / 6 - (tall ? 0 : 1));
        double gx = 0;

        // --- Точка А2 (горловина) ---
        double a2x = -(bust / 16 + 1);
        double a2y = 0;

        // --- Точка Г1 (пройма) ---
        double g1x = -(bust / 4);
        double g1y = gy;

        // --- Точка Г2 ---
        double g2x = g1x + (bust / 16);
        double g2y = gy;

        // --- Точка П (вершина проймы) ---
        double px = g2x;
        double py = a1y;

        // --- Точка П1 (конец плеча) ---
        double p1x = px - (tall ? 2.0 : 1.5);
        double p1y = py;

        // --- Точка Т1 (боковой шов) ---
        double t1x = tx - (waist / 4);
        double t1y = ty;

        // --- Точка Т2 (смещение) ---
        double t2x = t1x + 1.0;
        double t2y = ty;

        // --- Вытачка (Т3, Т4) ---
        double t3y = ty + (tall ? 2.0 : 1.5);
        double t4y = ty - (tall ? 2.5 : 2.0);

        // === Контур спинки ===
        p.moveTo(ax, ay); // А

        // Горловина А -> А2
        p.curveTo(
                ax - 1.0, ay - 0.5,
                a2x + 0.5, a2y - 0.5,
                a2x, a2y
        );

        // Плечо А2 -> П1
        p.lineTo(p1x, p1y);

        // Пройма П1 -> Г1
        p.curveTo(
                p1x - 0.5, p1y - 2.0,
                g1x + 1.0, g1y + 2.0,
                g1x, g1y
        );

        // Боковой шов Г1 -> Т1
        p.lineTo(t1x, t1y);

        // Талия Т1 -> Т2
        p.lineTo(t2x, t2y);

        // Ластовица Т2 -> Н
        p.curveTo(
                t2x - 0.5, t2y - 2.0,
                hx - 1.0, hy + 2.0,
                hx, hy
        );

        // Осевая Н -> А
        p.lineTo(ax, ay);

        return p;
    }

    // ==================== ПЕРЕД ====================
    private PatternPath buildFront(MeasurementProfile m) {
        PatternPath p = new PatternPath();
        boolean tall = isTall(m);

        double bust = m.getBust();
        double waist = m.getWaist();
        double hips = m.getHips();
        double dtc = m.getDtc();
        double vsd = m.getVsd();

        // --- Точка А2 (базовая для переда) ---
        double a2x = -(bust / 16 + 1);
        double a2y = 0;

        // --- Точка А3 (верх плеча спереди) ---
        double a3x = a2x;
        double a3y = a2y + (tall ? 1.0 : 0.5);

        // --- Точка А4 (низ горловины спереди) ---
        double a4x = a2x;
        double a4y = a3y - (tall ? 2.5 : 2.0);

        // --- Точка П2 (конец плеча переда) ---
        double p2x = a3x - (bust / 16 + 1);
        double p2y = a3y;

        // --- Точка Г1 (пройма) ---
        double g1x = -(bust / 4);
        double g1y = a3y - (bust / 6);

        // --- Точка Т1 (боковой шов) ---
        double t1x = -(waist / 4);
        double t1y = -dtc;

        // --- Точка Т2 (смещение) ---
        double t2x = t1x + 1.0;
        double t2y = t1y;

        // --- Точка Н (низ) ---
        double hy = t1y - (vsd / 2);
        double hx = 0;

        // === Контур переда ===
        p.moveTo(a3x, a3y); // А3

        // Горловина А3 -> А4
        p.curveTo(
                a3x + 1.5, a3y - 0.5,
                a4x + 1.5, a4y + 0.5,
                a4x, a4y
        );

        // Плечо А4 -> П2
        p.lineTo(p2x, p2y);

        // Пройма П2 -> Г1
        p.curveTo(
                p2x - 0.5, p2y - 2.0,
                g1x + 2.0, g1y + 2.0,
                g1x, g1y
        );

        // Бок Г1 -> Т1
        p.lineTo(t1x, t1y);

        // Талия Т1 -> Т2
        p.lineTo(t2x, t2y);

        // Ластовица Т2 -> Н
        p.curveTo(
                t2x - 0.5, t2y - 2.0,
                hx - 1.0, hy + 2.0,
                hx, hy
        );

        // Осевая Н -> А3
        p.lineTo(a3x, a3y);

        return p;
    }

    // ==================== РУКАВ ====================
    private PatternPath buildSleeve(MeasurementProfile m) {
        PatternPath p = new PatternPath();

        double sleeveLen = m.getSleeveLength();
        double upperArm = m.getUpperArmCirc();
        double wrist = m.getWristCirc();

        // Ширина рукава (с учётом растяжимости)
        double stretch = 0.88;
        double bicepW = (upperArm / 2) * stretch;
        double wristW = (wrist / 2) * stretch;

        double startX = 0;
        double startY = 0;

        // Окат
        p.moveTo(-bicepW / 2, startY);
        p.curveTo(
                -bicepW / 4, startY + sleeveLen * 0.15,
                bicepW / 4, startY + sleeveLen * 0.15,
                bicepW / 2, startY
        );

        // Боковые швы
        p.lineTo(wristW / 2, startY + sleeveLen);
        p.lineTo(-wristW / 2, startY + sleeveLen);
        p.lineTo(-bicepW / 2, startY);

        return p;
    }
}