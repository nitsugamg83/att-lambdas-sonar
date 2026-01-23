package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;

public record InitAuthMsisdn(
    @NotBlank String lineId,
    @NotBlank String phoneNumber,
    @NotBlank String folio,
    @NotBlank String brandId,
    @NotBlank String typeId
) {}
