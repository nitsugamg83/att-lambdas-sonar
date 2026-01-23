package com.mx.att.digital.identity.model;

import java.time.OffsetDateTime;

import software.amazon.awssdk.annotations.NotNull;

public record ValidateCustomerRequest(
    @NotNull String uuid,
    @NotNull OffsetDateTime timestamp,
    @NotNull String idDoc,
    @NotNull String customerId,
    @NotNull String operation,
    @NotNull String souceSystem
) {

}