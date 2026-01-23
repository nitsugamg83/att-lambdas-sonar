package com.mx.att.digital.identity.model;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SessionInitLinesData(
    @NotBlank String status,
    @NotBlank String message,
    @NotBlank String uuid,
    @NotBlank String resultCode,
    @NotBlank String resultDesc,
    @NotNull OffsetDateTime timestamp
) {}
