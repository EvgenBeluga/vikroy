package com.vikroy.dto;

import com.vikroy.model.PatternPoint;

import java.util.List;

public record PatternResponse(
        List<PatternPoint> frontPoints,
        List<PatternPoint> backPoints,
        List<PatternPoint> sleevePoints,
        List<PatternPoint> frontSkirtPoints,
        List<PatternPoint> backSkirtPoints
) {}