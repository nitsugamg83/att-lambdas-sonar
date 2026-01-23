package com.mx.att.digital.identity.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidateCustomerDataTest {

    @Test
    void validateCustomerData_records_workCorrectly() {
        // Creamos AssociatedLine
        ValidateCustomerData.AssociatedLine line1 = new ValidateCustomerData.AssociatedLine(
                "line1", "5551234567", "folio123", "brandA", "typeX"
        );
        ValidateCustomerData.AssociatedLine line2 = new ValidateCustomerData.AssociatedLine(
                "line2", "5559876543", "folio456", "brandB", "typeY"
        );

        // Creamos CustomerInfo
        ValidateCustomerData.CustomerInfo customerInfo =
                new ValidateCustomerData.CustomerInfo(List.of(line1, line2));

        // Creamos Data
        ValidateCustomerData.Data data = new ValidateCustomerData.Data(
                "uuid123", "00", "Success", customerInfo
        );

        // Creamos ValidateCustomerData principal
        OffsetDateTime now = OffsetDateTime.now();
        ValidateCustomerData validateData = new ValidateCustomerData(
                "OK", "Customer validated", data, now
        );

        // Validamos valores
        assertThat(validateData.status()).isEqualTo("OK");
        assertThat(validateData.message()).isEqualTo("Customer validated");
        assertThat(validateData.data()).isSameAs(data);
        assertThat(validateData.timestamp()).isSameAs(now);

        // Validamos nested objects
        assertThat(validateData.data().uuid()).isEqualTo("uuid123");
        assertThat(validateData.data().resultCode()).isEqualTo("00");
        assertThat(validateData.data().resultDesc()).isEqualTo("Success");
        assertThat(validateData.data().customerInfo().associatedLines()).containsExactly(line1, line2);

        assertThat(line1.lineId()).isEqualTo("line1");
        assertThat(line1.phoneNumber()).isEqualTo("5551234567");
        assertThat(line1.folio()).isEqualTo("folio123");
        assertThat(line1.brandId()).isEqualTo("brandA");
        assertThat(line1.typeId()).isEqualTo("typeX");
    }

    @Test
    void validateCustomerData_apiResponse_wrapper_works() {
        // Creamos datos
        ValidateCustomerData.AssociatedLine line = new ValidateCustomerData.AssociatedLine(
                "line1", "5551234567", "folio123", "brandA", "typeX"
        );
        ValidateCustomerData.CustomerInfo customerInfo =
                new ValidateCustomerData.CustomerInfo(List.of(line));
        ValidateCustomerData.Data data = new ValidateCustomerData.Data(
                "uuid123", "00", "Success", customerInfo
        );
        OffsetDateTime now = OffsetDateTime.now();
        ValidateCustomerData validateData = new ValidateCustomerData(
                "OK", "Customer validated", data, now
        );

        // Envolvemos en ApiResponse
        ApiResponse<ValidateCustomerData> apiResponse = new ApiResponse<>(
                "OK", "Success", validateData, OffsetDateTime.now()
        );

        // Validamos el wrapper
        assertThat(apiResponse.status()).isEqualTo("OK");
        assertThat(apiResponse.message()).isEqualTo("Success");
        assertThat(apiResponse.data()).isSameAs(validateData);
        assertThat(apiResponse.timestamp()).isNotNull();
    }
}
