package com.vikroy.dto;

import com.vikroy.model.PatternPath;

public record PatternResponse(
        PatternPath frontPath,
        PatternPath backPath,
        PatternPath sleevePath
) {}