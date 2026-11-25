package com.vikroy.model;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PatternPath {
    private final List<PathCommand> commands = new ArrayList<>();

    public void moveTo(double x, double y) { commands.add(PathCommand.moveTo(x, y)); }
    public void lineTo(double x, double y) { commands.add(PathCommand.lineTo(x, y)); }
    public void curveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
        commands.add(PathCommand.curveTo(cp1x, cp1y, cp2x, cp2y, x, y));
    }
}