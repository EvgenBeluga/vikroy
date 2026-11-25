package com.vikroy.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MeasurementRequest(
        @NotNull @Positive Double height,
        @NotNull @Positive Double bust,
        @NotNull @Positive Double waist,
        @NotNull @Positive Double hips,
        @NotNull @Positive Double neckCirc,
        @NotNull @Positive Double dtc,
        @NotNull @Positive Double vsd,
        @NotNull @Positive Double sleeveLength,
        @NotNull @Positive Double upperArmCirc,
        @NotNull @Positive Double wristCirc,
        @NotNull @Positive Double vt,
        @NotNull @Positive Double frontSkirtLength,
        @NotNull @Positive Double backSkirtLength,
        @NotNull @Positive Double dtp,
        @NotNull @Positive Double ankleCirc,
        @NotNull @Positive Double seatHeight
) {}