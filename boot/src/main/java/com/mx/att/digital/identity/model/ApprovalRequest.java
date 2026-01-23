package com.mx.att.digital.identity.model;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;

public record ApprovalRequest(
    @NotNull String uuid,
    @NotNull OffsetDateTime timestamp,
    @NotNull String phaseId,
    @NotNull String customerId,
    @NotNull String operation,
    @NotNull String ip,
    @NotNull String userAgent
) {
}
