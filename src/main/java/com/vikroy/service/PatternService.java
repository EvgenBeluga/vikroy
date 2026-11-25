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

    public static final double CM = 28.35;
    private static final double STRETCH_FACTOR = 0.88;

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
                null, // перед пока не рисуем
                buildBack(m),
                null  // рукав пока не рисуем
        );
    }

    // ==================== СПИНКА (строго по методичке) ====================
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

        // --- Контур спинки ---
        p.moveTo(a2x, a2y);        // А2

        // 1. Горловина А2 -> А1 (плавная дуга)
        p.curveTo(
                a2x + 1.0, a2y - 0.2,   // cp1: вправо от А2
                ax - 0.5, a1y + 0.2,    // cp2: влево от А1
                ax, a1y                 // А1
        );

        // 2. Плечо А1 -> П1 (прямая)
        p.lineTo(p1x, p1y);

        // 3. Пройма П1 -> Г1 (плавная кривая)
        p.curveTo(
                p1x - 0.5, p1y - 4.0,   // cp1: вниз от П1
                g1x + 3.0, g1y + 3.0,   // cp2: вправо-вверх от Г1
                g1x, g1y                // Г1
        );

        // 4. Бок Г1 -> Т1 (прямая)
        p.lineTo(t1x, t1y);

        // 5. Окат спереди Т1 -> Б2 (плавная кривая)
        p.curveTo(
                t1x + 0.5, t1y - 3.0,   // cp1: вниз от Т1
                b2x - 0.5, b2y + 3.0,   // cp2: вверх от Б2
                b2x, b2y                // Б2
        );

        // 6. Б2 -> Б3 -> Б4 (прямые)
        p.lineTo(b3x, b3y);
        p.lineTo(b4x, b4y);

        // 7. Окат сзади Б4 -> Н3 -> Н2 (плавные кривые)
        p.curveTo(
                b4x + 1.0, b4y - 2.0,   // cp1: вправо-вниз от Б4
                h3x + 2.0, h3y + 2.0,   // cp2: вправо-вверх от Н3
                h3x, h3y                // Н3
        );
        p.curveTo(
                h3x - 1.0, h3y - 1.0,   // cp1: влево-вниз от Н3
                h2x + 1.0, h2y + 1.0,   // cp2: вправо-вверх от Н2
                h2x, h2y                // Н2
        );

        // 8. Н2 -> Н (низ, прямая)
        p.lineTo(0, hy);

        // 9. Н -> Н1 (ластовица, плавная кривая)
        p.curveTo(
                0 - 1.0, hy + 1.0,      // cp1: влево-вверх от Н
                h1x + 1.0, h1y - 1.0,   // cp2: вправо-вниз от Н1
                h1x, h1y                // Н1
        );

        // 10. Н1 -> Т1 (замыкание, плавная кривая)
        p.curveTo(
                h1x - 1.0, h1y + 3.0,   // cp1: влево-вверх от Н1
                t1x + 1.0, t1y - 3.0,   // cp2: вправо-вниз от Т1
                t1x, t1y                // Т1
        );

        // 11. Талия Т1 -> Т2 -> Т (прямые)
        p.lineTo(t2x, t2y);
        p.lineTo(tx, ty);

        // 12. Центр Т -> А (прямая)
        p.lineTo(ax, ay);

        // 13. Замыкание А -> А2 (прямая)
        p.lineTo(a2x, a2y);

        return p;
    }
}