package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record AprovalResponse(

    @NotBlank  String status,
    @NotBlank  String message,
    @NotNull   Data data,
    @NotNull  OffsetDateTime timestamp
) {

public record Data(@NotBlank  String uuid,
        @NotBlank  String resultCode,
        @NotBlank  String resultDesc
    ) {}
}