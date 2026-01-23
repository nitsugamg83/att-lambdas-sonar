package com.mx.att.digital.identity.model;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SessionInitLinesRequest(
    @NotBlank String uuid,
    @NotNull OffsetDateTime timestamp,
    String msisdn,
    @NotBlank String operation
) { }
