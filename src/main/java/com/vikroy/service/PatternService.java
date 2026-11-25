package com.vikroy.service;

import com.vikroy.dto.MeasurementRequest;
import com.vikroy.dto.PatternResponse;
import com.vikroy.entity.MeasurementProfile;
import com.vikroy.entity.User;
import com.vikroy.model.PatternPoint;
import com.vikroy.repository.MeasurementProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PatternService {

    /** 1 см = 28.35 pt (для PDF) */
    private static final double CM = 28.35;
    /** Коэффициент растяжимости бифлекса */
    private static final double STRETCH_FACTOR = 0.88;

    private final MeasurementProfileRepository measurementRepository;

    public PatternService(MeasurementProfileRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    /** Сохраняет мерки пользователя и возвращает сохранённую сущность */
    public MeasurementProfile saveMeasurements(User user, MeasurementRequest r) {
        MeasurementProfile p = MeasurementProfile.builder()
                .user(user)
                .height(r.height())
                .bust(r.bust())
                .waist(r.waist())
                .hips(r.hips())
                .neckCirc(r.neckCirc())
                .dtc(r.dtc())
                .vsd(r.vsd())
                .sleeveLength(r.sleeveLength())
                .upperArmCirc(r.upperArmCirc())
                .wristCirc(r.wristCirc())
                .vt(r.vt())
                .frontSkirtLength(r.frontSkirtLength())
                .backSkirtLength(r.backSkirtLength())
                .dtp(r.dtp())
                .ankleCirc(r.ankleCirc())
                .seatHeight(r.seatHeight())
                .build();
        return measurementRepository.save(p);
    }

    /** Строит все детали выкройки по сохранённым меркам */
    @Transactional(readOnly = true)
    public PatternResponse build(MeasurementProfile m) {
        return new PatternResponse(
                buildFront(m),
                buildBack(m),
                buildSleeve(m),
                buildSkirt(m, true),
                buildSkirt(m, false)
        );
    }

    // ==================== СПИНКА ====================
    private List<PatternPoint> buildBack(MeasurementProfile m) {
        List<PatternPoint> points = new ArrayList<>();
        double s = CM;
        boolean tall = m.getHeight() > 140;

        double ax = 0, ay = 0;
        points.add(new PatternPoint(ax, ay));

        double a1x = ax;
        double a1y = ay - (tall ? 2.0 : 1.5) * s;
        points.add(new PatternPoint(a1x, a1y));

        double tx = ax;
        double ty = a1y - m.getDtc() * s;
        points.add(new PatternPoint(tx, ty));

        double hx = tx;
        double hy = ty - (m.getVsd() / 2) * s;
        points.add(new PatternPoint(hx, hy));

        double gx = a1x;
        double gy = a1y - (m.getBust() / 6 - (tall ? 0 : 1)) * s;
        points.add(new PatternPoint(gx, gy));

        double a2x = ax - (m.getBust() / 16 + 1) * s;
        double a2y = ay;
        points.add(new PatternPoint(a2x, a2y));

        double g1x = gx - (m.getBust() / 4) * s;
        double g1y = gy;
        points.add(new PatternPoint(g1x, g1y));

        double g2x = g1x + (m.getBust() / 16) * s;
        double g2y = gy;
        points.add(new PatternPoint(g2x, g2y));

        double px = g2x;
        double py = a1y;
        points.add(new PatternPoint(px, py));

        double p1x = px - (tall ? 2.0 : 1.5) * s;
        double p1y = py;
        points.add(new PatternPoint(p1x, p1y));

        double t1x = tx - (m.getWaist() / 4) * s;
        double t1y = ty;
        points.add(new PatternPoint(t1x, t1y));

        double t2x = t1x + 1.0 * s;
        double t2y = ty;
        points.add(new PatternPoint(t2x, t2y));

        double t3y = ty + (tall ? 2.0 : 1.5) * s;
        double t4y = ty - (tall ? 2.5 : 2.0) * s;
        points.add(new PatternPoint(t2x, t3y));
        points.add(new PatternPoint(t2x, t4y));

        return points;
    }

    // ==================== ПЕРЕД ====================
    private List<PatternPoint> buildFront(MeasurementProfile m) {
        List<PatternPoint> points = new ArrayList<>();
        double s = CM;
        boolean tall = m.getHeight() > 140;

        double a2x = -(m.getBust() / 16 + 1) * s;
        double a2y = 0;
        points.add(new PatternPoint(a2x, a2y));

        double a3x = a2x;
        double a3y = a2y + (tall ? 1.0 : 0.5) * s;
        points.add(new PatternPoint(a3x, a3y));

        double a1py = a3y - (tall ? 1.0 : 0.5) * s;
        points.add(new PatternPoint(a3x, a1py));

        double a4x = a2x;
        double a4y = a1py - (tall ? 2.5 : 2.0) * s;
        points.add(new PatternPoint(a4x, a4y));

        double p2x = a3x - (m.getBust() / 16 + 1) * s;
        double p2y = a3y;
        points.add(new PatternPoint(p2x, p2y));

        double g1x = -(m.getBust() / 4) * s;
        double g1y = a1py - (m.getBust() / 6) * s;
        points.add(new PatternPoint(g1x, g1y));

        double t1x = -(m.getWaist() / 4) * s;
        double t1y = -m.getDtc() * s;
        points.add(new PatternPoint(t1x, t1y));

        double t2x = t1x + 1.0 * s;
        double t2y = t1y;
        points.add(new PatternPoint(t2x, t2y));

        return points;
    }

    // ==================== РУКАВ ====================
    private List<PatternPoint> buildSleeve(MeasurementProfile m) {
        List<PatternPoint> points = new ArrayList<>();
        double s = CM;

        double sleeveLen = m.getSleeveLength() * s;
        double bicepW = (m.getUpperArmCirc() / 2) * STRETCH_FACTOR * s;
        double wristW = (m.getWristCirc() / 2) * STRETCH_FACTOR * s;

        double startX = 0, startY = 0;

        points.add(new PatternPoint(startX - bicepW / 2, startY));
        points.add(new PatternPoint(startX, startY - sleeveLen * 0.3));
        points.add(new PatternPoint(startX + bicepW / 2, startY));
        points.add(new PatternPoint(startX + wristW / 2, startY + sleeveLen));
        points.add(new PatternPoint(startX - wristW / 2, startY + sleeveLen));
        points.add(new PatternPoint(startX - bicepW / 2, startY));

        return points;
    }

    // ==================== ЮБКА ====================
    private List<PatternPoint> buildSkirt(MeasurementProfile m, boolean isFront) {
        List<PatternPoint> points = new ArrayList<>();
        double s = CM;

        double length = isFront ? m.getFrontSkirtLength() : m.getBackSkirtLength();
        double waist = m.getWaist() * STRETCH_FACTOR;

        double rWaist = (waist / (2 * Math.PI)) * s;
        double rBottom = rWaist + (length * s);

        points.add(new PatternPoint(0, 0));
        points.add(new PatternPoint(rWaist, 0));
        points.add(new PatternPoint(rBottom, 0));
        points.add(new PatternPoint(0, rBottom));
        points.add(new PatternPoint(0, rWaist));
        points.add(new PatternPoint(0, 0));

        return points;
    }
}