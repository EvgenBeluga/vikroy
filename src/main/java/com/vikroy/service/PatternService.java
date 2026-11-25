package com.vikroy.service;

import com.vikroy.dto.MeasurementRequest;
import com.vikroy.dto.PatternResponse;
import com.vikroy.entity.MeasurementProfile;
import com.vikroy.entity.User;
import com.vikroy.model.PatternPath;
import com.vikroy.repository.MeasurementProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatternService {

    private final MeasurementProfileRepository measurementRepository;

    public PatternService(MeasurementProfileRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public MeasurementProfile saveMeasurements(User user, MeasurementRequest r) {
        MeasurementProfile p = MeasurementProfile.builder()
                .user(user)
                .height(r.height()).bust(r.bust()).waist(r.waist()).hips(r.hips())
                .neckCirc(r.neckCirc()).dtc(r.dtc()).vsd(r.vsd())
                .sleeveLength(r.sleeveLength()).upperArmCirc(r.upperArmCirc()).wristCirc(r.wristCirc())
                .vt(r.vt()).frontSkirtLength(r.frontSkirtLength()).backSkirtLength(r.backSkirtLength())
                .dtp(r.dtp()).ankleCirc(r.ankleCirc()).seatHeight(r.seatHeight())
                .build();
        return measurementRepository.save(p);
    }

    public PatternResponse build(MeasurementProfile m) {
        return new PatternResponse(
                buildFront(m),   // <-- перед
                buildBack(m),
                null
        );
    }

    // ==================== СПИНКА (по точкам с чертежа) ====================
    private PatternPath buildBack(MeasurementProfile m) {
        PatternPath p = new PatternPath();

        // --- Координаты (см) ---
        double ax = 0, ay = 0;
        double a1y = -1.5;
        double a2x = -4.7, a2y = 0;
        double g1x = -15.0, g1y = -10.5;
        double p1x = -12.7, p1y = -1.5;
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

        // --- Контур спинки (единый) ---
        // 1. А2 (горловина)
        p.moveTo(a2x, a2y);

        // 2. Горловина А2 -> А1 (плавная дуга, более пологая)
        p.curveTo(
                a2x + 2.0, a2y - 0.5,    // cp1: сильнее вправо
                ax - 1.5, a1y + 0.5,     // cp2: сильнее влево
                ax, a1y
        );

        // 3. Плечо А1 -> П1 (прямая)
        p.lineTo(p1x, p1y);

        // 4. Пройма П1 -> Г1 (плавная кривая)
        p.curveTo(
                p1x - 0.5, p1y - 4.0,
                g1x + 3.0, g1y + 3.0,
                g1x, g1y
        );

        // 5. Бок Г1 -> Т1 (прямая)
        p.lineTo(t1x, t1y);

        // 6. Окат спереди Т1 -> Б2 (плавная кривая)
        p.curveTo(
                t1x + 0.5, t1y - 3.0,
                b2x - 0.5, b2y + 3.0,
                b2x, b2y
        );

        // 7. Б2 -> Б3 -> Б4 (прямые)
        p.lineTo(b3x, b3y);
        p.lineTo(b4x, b4y);

        // 8. Окат сзади Б4 -> Н3 -> Н2 (плавнее)
        p.curveTo(
                b4x + 1.0, b4y - 2.0,
                h3x + 2.0, h3y + 1.0,    // cp2: ближе к Н3
                h3x, h3y
        );
        p.curveTo(
                h3x - 1.0, h3y - 1.0,
                h2x + 1.0, h2y + 1.0,
                h2x, h2y
        );

        // 9. Н2 -> Н (низ, прямая)
        p.lineTo(0, hy);

        // 10. Н -> Н1 (ластовица, плавная кривая)
        p.curveTo(
                0 - 1.0, hy + 1.0,
                h1x + 1.0, h1y - 1.0,
                h1x, h1y
        );

        // 11. Н1 -> Т1 (замыкание, плавная кривая)
        p.curveTo(
                h1x - 1.0, h1y + 3.0,
                t1x + 1.0, t1y - 3.0,
                t1x, t1y
        );

        // 12. Талия Т1 -> Т2 -> Т (прямые)
        p.lineTo(t2x, t2y);
        p.lineTo(tx, ty);

        // 13. Центр Т -> А (прямая)
        p.lineTo(ax, ay);

        // 14. Замыкание А -> А2 (прямая)
        p.lineTo(a2x, a2y);

        return p;
    }

    private PatternPath buildFront(MeasurementProfile m) {
        PatternPath p = new PatternPath();

        // --- Координаты переда (см) ---
        double a3x = -4.7, a3y = 0.5;           // А3 (вверх от А2 на 0.5)
        double a4x = -4.7, a4y = -3.8;          // А4 (вниз от А на 3.8)
        double p2x = -10.0, p2y = -3.8;         // П2 (укоротили плечо!)
        double g1x = -15.0, g1y = -10.5;        // Г1
        double t1x = -12.0, t1y = -28.0;        // Т1
        double t2x = -11.0, t2y = -28.0;        // Т2
        double tx = 0, ty = -28.0;              // Т
        double hy = -52.0;                      // Н
        double b2x = -11.7, b2y = -37.8;        // Б2
        double b3x = -11.7, b3y = -38.6;        // Б3
        double b4x = -10.2, b4y = -38.6;        // Б4
        double h1x = -3.0, h1y = -46.7;         // Н1
        double h2x = -4.0, h2y = -52.0;         // Н2
        double h3x = -7.6, h3y = -45.8;         // Н3

        // --- Контур переда ---
        p.moveTo(a3x, a3y);        // А3

        // 1. Горловина А3 -> А4 (плавная дуга)
        p.curveTo(
                a3x + 1.5, a3y - 0.5,
                a4x + 1.5, a4y + 0.5,
                a4x, a4y
        );

        // 2. Плечо А4 -> П2 (прямая, почти горизонтальная)
        p.lineTo(p2x, p2y);

        // 3. Пройма П2 -> Г1 (плавная кривая)
        p.curveTo(
                p2x - 0.5, p2y - 3.0,
                g1x + 3.0, g1y + 3.0,
                g1x, g1y
        );

        // 4. Бок Г1 -> Т1 (прямая)
        p.lineTo(t1x, t1y);

        // 5. Окат спереди Т1 -> Б2 (плавная кривая)
        p.curveTo(
                t1x + 0.5, t1y - 3.0,
                b2x - 0.5, b2y + 3.0,
                b2x, b2y
        );

        // 6. Б2 -> Б3 -> Б4 (прямые)
        p.lineTo(b3x, b3y);
        p.lineTo(b4x, b4y);

        // 7. Окат сзади Б4 -> Н3 -> Н2 (плавные кривые)
        p.curveTo(
                b4x + 1.0, b4y - 2.0,
                h3x + 2.0, h3y + 1.0,
                h3x, h3y
        );
        p.curveTo(
                h3x - 1.0, h3y - 1.0,
                h2x + 1.0, h2y + 1.0,
                h2x, h2y
        );

        // 8. Н2 -> Н (низ, прямая)
        p.lineTo(0, hy);

        // 9. Н -> Н1 (ластовица, плавная кривая)
        p.curveTo(
                0 - 1.0, hy + 1.0,
                h1x + 1.0, h1y - 1.0,
                h1x, h1y
        );

        // 10. Н1 -> Т1 (замыкание, плавная кривая)
        p.curveTo(
                h1x - 1.0, h1y + 3.0,
                t1x + 1.0, t1y - 3.0,
                t1x, t1y
        );

        // 11. Талия Т1 -> Т2 -> Т (прямые)
        p.lineTo(t2x, t2y);
        p.lineTo(tx, ty);

        // 12. Центр Т -> А3 (прямая)
        p.lineTo(a3x, a3y);

        return p;
    }
}