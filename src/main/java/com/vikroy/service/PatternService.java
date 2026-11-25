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
        boolean tall = m.getHeight() > 140;

        // --- Базовые точки (в см) ---
        double ax = 0, ay = 0;
        double a1x = ax;
        double a1y = ay - (tall ? 2.0 : 1.5);
        double tx = ax;
        double ty = a1y - m.getDtc();
        double hx = tx;
        double hy = ty - (m.getVsd() / 2);
        double gx = a1x;
        double gy = a1y - (m.getBust() / 6 - (tall ? 0 : 1));
        double a2x = ax - (m.getBust() / 16 + 1);
        double a2y = ay;
        double g1x = gx - (m.getBust() / 4);
        double g1y = gy;
        double g2x = g1x + (m.getBust() / 16);
        double g2y = gy;
        double px = g2x;
        double py = a1y;
        double p1x = px - (tall ? 2.0 : 1.5);
        double p1y = py;
        double t1x = tx - (m.getWaist() / 4);
        double t1y = ty;
        double t2x = t1x + 1.0;
        double t2y = ty;

        // --- Контур спинки: начинаем с А2 ---
        // 1. А2 (горловина)
        p.moveTo(a2x, a2y);

        // 2. А2 -> А1 (дуга горловины)
        p.curveTo(
                a2x + 0.2, a2y - 0.5,   // cp1
                a1x - 0.5, a1y + 0.2,   // cp2
                a1x, a1y                // конец
        );

        // 3. А1 -> П1 (плечо, прямая)
        p.lineTo(p1x, p1y);

        // 4. П1 -> Г1 (пройма, кривая)
        p.curveTo(
                p1x - 0.5, p1y - 1.5,   // cp1
                g1x + 1.0, g1y + 1.0,   // cp2
                g1x, g1y                // конец
        );

        // 5. Г1 -> Т1 (бок, прямая)
        p.lineTo(t1x, t1y);

        // 6. Т1 -> Т2 -> Т (талия, прямые)
        p.lineTo(t2x, t2y);
        p.lineTo(tx, ty);

        // 7. Т -> Н (низ, прямая)
        p.lineTo(hx, hy);

        // 8. Н -> А (центр спинки, прямая)
        p.lineTo(ax, ay);

        // 9. А -> А2 (замыкание по верху, прямая)
        p.lineTo(a2x, a2y);

        return p;
    }
}