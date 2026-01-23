package com.mx.att.digital.identity.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AprovalResponseTest {

    @Test
    void aprovalResponse_record_fields_workCorrectly() {
        OffsetDateTime timestamp = OffsetDateTime.parse("2025-01-01T10:00:00Z");
       // Validación Tests Aproval
        AprovalResponse.Data data = new AprovalResponse.Data(
                "uuid123",
                "200",
                "Approved"
        );

        AprovalResponse response = new AprovalResponse(
                "OK",
                "Success",
                data,
                timestamp
        );

        assertThat(response.status()).isEqualTo("OK");
        assertThat(response.message()).isEqualTo("Success");
        assertThat(response.data()).isEqualTo(data);
        assertThat(response.timestamp()).isEqualTo(timestamp);

        // Validar Data
        assertThat(response.data().uuid()).isEqualTo("uuid123");
        assertThat(response.data().resultCode()).isEqualTo("200");
        assertThat(response.data().resultDesc()).isEqualTo("Approved");
    }

    @Test
    void equals_and_hashCode_ok() {
        OffsetDateTime timestamp = OffsetDateTime.parse("2025-01-01T10:00:00Z");

        AprovalResponse.Data data1 =
                new AprovalResponse.Data("uuid123", "200", "Approved");
        AprovalResponse.Data data2 =
                new AprovalResponse.Data("uuid123", "200", "Approved");

        AprovalResponse resp1 =
                new AprovalResponse("OK", "Success", data1, timestamp);
        AprovalResponse resp2 =
                new AprovalResponse("OK", "Success", data2, timestamp);

        assertThat(resp1).isEqualTo(resp2);
        assertThat(resp1.hashCode()).isEqualTo(resp2.hashCode());
    }

    @Test
    void not_equals_when_field_differs() {
        OffsetDateTime timestamp = OffsetDateTime.parse("2025-01-01T10:00:00Z");

        AprovalResponse resp1 = new AprovalResponse(
                "OK",
                "Success",
                new AprovalResponse.Data("uuid123", "200", "Approved"),
                timestamp
        );

        AprovalResponse resp2 = new AprovalResponse(
                "ERROR", // diferencia clave
                "Success",
                new AprovalResponse.Data("uuid123", "200", "Approved"),
                timestamp
        );

        assertThat(resp1).isNotEqualTo(resp2);
    }

    @Test
    void toString_contains_all_fields() {
        AprovalResponse response = new AprovalResponse(
                "OK",
                "Success",
                new AprovalResponse.Data("uuid123", "200", "Approved"),
                OffsetDateTime.parse("2025-01-01T10:00:00Z")
        );

        assertThat(response.toString())
                .contains("OK")
                .contains("Success")
                .contains("uuid123")
                .contains("200")
                .contains("Approved");
    }
}
