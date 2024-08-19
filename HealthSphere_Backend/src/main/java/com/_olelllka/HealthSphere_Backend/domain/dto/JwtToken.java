package com._olelllka.HealthSphere_Backend.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtToken {
    private String accessToken;
}
