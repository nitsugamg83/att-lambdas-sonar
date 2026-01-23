// =======================
// ValidateCustomerData
// =======================
package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

public record ValidateCustomerData(
    @NotBlank  String status,
    @NotBlank  String message,
    @NotNull   Data data,
    @NotNull   OffsetDateTime timestamp
) {

public record Data( @NotBlank String uuid,
        @NotBlank  String resultCode,
        @NotBlank  String resultDesc,
        @NotNull   CustomerInfo customerInfo
    ) {}

public record CustomerInfo( @NotNull  List<AssociatedLine> associatedLines
    ) {}

public record AssociatedLine(@NotBlank  String lineId,
        @NotBlank   String phoneNumber,
        @NotBlank   String folio,
        @NotBlank   String brandId,
        @NotBlank   String typeId

    ) {}
}
