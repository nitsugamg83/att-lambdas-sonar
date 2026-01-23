package com.mx.att.digital.identity.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ValidateCustomerRequestTest {

    @Test
    void validateCustomerRequest_success() {
        OffsetDateTime now = OffsetDateTime.now();

        ValidateCustomerRequest req = new ValidateCustomerRequest(
            "uuid123",
            now,
            "ID12345",
            "CUST001",
            "OP_VALIDATE",
            "SYSTEM_A"
        );

        assertThat(req.uuid()).isEqualTo("uuid123");
        assertThat(req.timestamp()).isEqualTo(now);
        assertThat(req.idDoc()).isEqualTo("ID12345");
        assertThat(req.customerId()).isEqualTo("CUST001");
        assertThat(req.operation()).isEqualTo("OP_VALIDATE");
        assertThat(req.souceSystem()).isEqualTo("SYSTEM_A");
    }

    // =========================
    // RECORD METHODS (JaCoCo)
    // =========================

    @Test
    void equals_and_hashCode_ok() {
        OffsetDateTime now = OffsetDateTime.now();

        ValidateCustomerRequest r1 =
            new ValidateCustomerRequest(
                "uuid123",
                now,
                "ID12345",
                "CUST001",
                "OP_VALIDATE",
                "SYSTEM_A"
            );

        ValidateCustomerRequest r2 =
            new ValidateCustomerRequest(
                "uuid123",
                now,
                "ID12345",
                "CUST001",
                "OP_VALIDATE",
                "SYSTEM_A"
            );

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void equals_same_instance() {
        ValidateCustomerRequest req =
            new ValidateCustomerRequest(
                "uuid123",
                OffsetDateTime.now(),
                "ID12345",
                "CUST001",
                "OP_VALIDATE",
                "SYSTEM_A"
            );

        assertThat(req).isEqualTo(req);
    }

    @Test
    void equals_null_is_false() {
        ValidateCustomerRequest req =
            new ValidateCustomerRequest(
                "uuid123",
                OffsetDateTime.now(),
                "ID12345",
                "CUST001",
                "OP_VALIDATE",
                "SYSTEM_A"
            );

        assertThat(req.equals(null)).isFalse();
    }

    @Test
    void equals_other_class_is_false() {
        ValidateCustomerRequest req =
            new ValidateCustomerRequest(
                "uuid123",
                OffsetDateTime.now(),
                "ID12345",
                "CUST001",
                "OP_VALIDATE",
                "SYSTEM_A"
            );

        assertThat(req.equals("not-a-request")).isFalse();
    }

    @Test
    void not_equals_when_field_differs() {
        ValidateCustomerRequest r1 =
            new ValidateCustomerRequest(
                "uuid123",
                OffsetDateTime.now(),
                "ID12345",
                "CUST001",
                "OP_VALIDATE",
                "SYSTEM_A"
            );

        ValidateCustomerRequest r2 =
            new ValidateCustomerRequest(
                "uuid999",
                OffsetDateTime.now(),
                "ID12345",
                "CUST001",
                "OP_VALIDATE",
                "SYSTEM_A"
            );

        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    void toString_contains_fields() {
        ValidateCustomerRequest req =
            new ValidateCustomerRequest(
                "uuid123",
                OffsetDateTime.parse("2025-01-01T10:00:00Z"),
                "ID12345",
                "CUST001",
                "OP_VALIDATE",
                "SYSTEM_A"
            );

        assertThat(req.toString())
            .contains("uuid123")
            .contains("ID12345")
            .contains("CUST001")
            .contains("OP_VALIDATE")
            .contains("SYSTEM_A");
    }
}
