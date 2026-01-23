package com.mx.att.digital.identity.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InitAuthRequest(
    @NotBlank String uuid,
    @NotBlank String timestamp,
    @NotBlank String customerId,
    @NotBlank String initFlowType,
    @NotBlank String customerEmail,
    @NotNull @Valid InitAuthMsisdn msisdn,
    @NotBlank String operation
) {}
