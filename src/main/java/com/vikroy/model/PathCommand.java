package com.vikroy.model;

import lombok.Getter;

@Getter
public class PathCommand {
    public enum Type { MOVE_TO, LINE_TO, CURVE_TO }

    private final Type type;
    // Для MOVE_TO и LINE_TO используем x1, y1 (остальные null)
    // Для CURVE_TO используем все (x1,y1 - контрольная 1, x2,y2 - контрольная 2, x3,y3 - конечная)
    private final double x1, y1;
    private final double x2, y2;
    private final double x3, y3;

    private PathCommand(Type type, double x1, double y1, double x2, double y2, double x3, double y3) {
        this.type = type;
        this.x1 = x1; this.y1 = y1;
        this.x2 = x2; this.y2 = y2;
        this.x3 = x3; this.y3 = y3;
    }

    public static PathCommand moveTo(double x, double y) {
        return new PathCommand(Type.MOVE_TO, x, y, 0, 0, 0, 0);
    }

    public static PathCommand lineTo(double x, double y) {
        return new PathCommand(Type.LINE_TO, x, y, 0, 0, 0, 0);
    }

    public static PathCommand curveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
        return new PathCommand(Type.CURVE_TO, cp1x, cp1y, cp2x, cp2y, x, y);
    }
}