package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;

public record InitAuthData(
    @NotBlank String uuid,
    @NotBlank String resultCode,
    @NotBlank String resultDesc,
    String platform,
    String status,
    String onboardingUrl
) {}
