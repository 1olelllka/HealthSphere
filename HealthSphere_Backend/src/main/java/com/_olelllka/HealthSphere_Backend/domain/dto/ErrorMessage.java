package com._olelllka.HealthSphere_Backend.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorMessage {
    private String message;
}
