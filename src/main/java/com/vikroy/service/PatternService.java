package com.vikroy.service;

import com.vikroy.dto.MeasurementRequest;
import com.vikroy.dto.PatternResponse;
import com.vikroy.entity.MeasurementProfile;
import com.vikroy.entity.User;
import com.vikroy.repository.MeasurementProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatternService {

    private final MeasurementProfileRepository measurementRepository;
    private final PatternCalculator patternCalculator;

    public PatternService(MeasurementProfileRepository measurementRepository,
                          PatternCalculator patternCalculator) {
        this.measurementRepository = measurementRepository;
        this.patternCalculator = patternCalculator;
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
        return patternCalculator.build(m);
    }
}