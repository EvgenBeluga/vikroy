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

        // --- Вертикаль А-А1-Т-Н ---
        double ax = 0, ay = 0;
        double a1y = ay - (tall ? 2.0 : 1.5);
        double ty = ay - m.getDtc();
        double tn = m.getVsd() / 2;          // расстояние от талии до низа
        double hy = ty - tn;

        // --- Горловина ---
        double a2x = -(m.getBust() / 16 + 1);
        double a2y = ay;

        // --- Линия груди ---
        double gy = a1y - (m.getBust() / 6 - (tall ? 0 : 1));
        double g1x = -(m.getBust() / 4);
        double g1y = gy;
        double g2x = g1x + (m.getBust() / 16);
        double g2y = gy;
        double px = g2x;
        double py = a1y;
        double p1x = px - (tall ? 2.0 : 1.5);
        double p1y = py;

        // --- Талия ---
        double t1x = -(m.getWaist() / 4);
        double t1y = ty;
        double t2x = t1x + 1.0;
        double t2y = ty;

        // --- Трусы ---
        // Б2 (окат спереди): от Т1 вправо 3 см, вниз Вт, но не ниже H
        double b2x = t1x + 3.0;
        double b2y = ty - m.getVt();
        if (b2y < hy + 1) b2y = hy + 1;   // не ниже низа

        // Б3 (пересечение оката с боковым швом): от Б2 вниз 1.5 см, но не ниже H
        double b3x = b2x;
        double b3y = b2y - 1.5;
        if (b3y < hy + 0.5) b3y = hy + 0.5;

        // Б4 (убавка по спинке): от Б3 вправо 2 см
        double b4x = b3x + 2.0;
        double b4y = b3y;

        // Н2 (шаговый шов): от Н влево на 1/2 ластовицы * 1.3
        double latWidth = m.getHips() / 10;
        double h2x = 0 - (latWidth / 2) * 1.3;
        double h2y = hy;

        // Н1 (ластовица): от Н вверх 6-7 см, влево на 1/5 ББ1
        double h1y = hy + (tall ? 7 : 6);
        double h1x = 0 - (m.getHips() / 20);

        // Н3 (перпендикуляр от середины Н2Б4 вниз 1 см)
        double h3x = (h2x + b4x) / 2;
        double h3y = (h2y + b4y) / 2 - 1.0;

        // --- Контур (ломаная, для диагностики) ---
        p.moveTo(a2x, a2y);        // А2
        p.lineTo(0, a1y);          // А1
        p.lineTo(p1x, p1y);        // П1
        p.lineTo(g1x, g1y);        // Г1
        p.lineTo(t1x, t1y);        // Т1
        p.lineTo(t2x, t2y);        // Т2
        p.lineTo(0, ty);           // Т
        p.lineTo(b4x, b4y);        // Б4
        p.lineTo(h3x, h3y);        // Н3
        p.lineTo(h2x, h2y);        // Н2
        p.lineTo(0, hy);           // Н
        p.lineTo(h1x, h1y);        // Н1
        p.lineTo(b2x, b2y);        // Б2
        p.lineTo(t1x, t1y);        // обратно к Т1
        p.lineTo(0, ay);           // А
        p.lineTo(a2x, a2y);        // А2

        // --- Отладка ---
        System.out.println("=== BACK POINTS (FIXED) ===");
        System.out.printf("A: (%.2f, %.2f)%n", ax, ay);
        System.out.printf("A1: (%.2f, %.2f)%n", 0.0, a1y);
        System.out.printf("A2: (%.2f, %.2f)%n", a2x, a2y);
        System.out.printf("P1: (%.2f, %.2f)%n", p1x, p1y);
        System.out.printf("G1: (%.2f, %.2f)%n", g1x, g1y);
        System.out.printf("T1: (%.2f, %.2f)%n", t1x, t1y);
        System.out.printf("T2: (%.2f, %.2f)%n", t2x, t2y);
        System.out.printf("T:  (%.2f, %.2f)%n", 0.0, ty);
        System.out.printf("B2: (%.2f, %.2f)%n", b2x, b2y);
        System.out.printf("B3: (%.2f, %.2f)%n", b3x, b3y);
        System.out.printf("B4: (%.2f, %.2f)%n", b4x, b4y);
        System.out.printf("H:  (%.2f, %.2f)%n", 0.0, hy);
        System.out.printf("H1: (%.2f, %.2f)%n", h1x, h1y);
        System.out.printf("H2: (%.2f, %.2f)%n", h2x, h2y);
        System.out.printf("H3: (%.2f, %.2f)%n", h3x, h3y);

        return p;
    }
}