package com.vikroy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Точка выкройки в координатах PDF (1 см = 28.35 pt).
 * Используется для построения контуров деталей (перед, спинка, рукав, юбка).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatternPoint {

    private double x;
    private double y;

    public PatternPoint copy() {
        return new PatternPoint(x, y);
    }

    public PatternPoint translate(double dx, double dy) {
        return new PatternPoint(x + dx, y + dy);
    }

    @Override
    public String toString() {
        return String.format("(%.2f; %.2f)", x, y);
    }
}