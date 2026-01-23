package com.mx.att.digital.identity.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ApprovalRequestValidationTest {

    private static Validator validator;
    private static ValidatorFactory factory;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void valid_request_passes_validation() {
        ApprovalRequest req = new ApprovalRequest(
                "uuid",
                OffsetDateTime.parse("2025-01-01T10:00:00Z"),
                "PHASE",
                "CUSTOMER",
                "APPROVE",
                "127.0.0.1",
                "Mozilla"
        );

        assertThat(validator.validate(req)).isEmpty();
    }

    @Test
    void null_uuid_is_invalid() {
        ApprovalRequest req = new ApprovalRequest(
                null,
                OffsetDateTime.now(),
                "PHASE",
                "CUSTOMER",
                "APPROVE",
                "127.0.0.1",
                "Mozilla"
        );

        assertThat(validator.validate(req))
                .extracting(v -> v.getPropertyPath().toString())
                .contains("uuid");
    }

    @Test
    void null_timestamp_is_invalid() {
        ApprovalRequest req = new ApprovalRequest(
                "uuid",
                null,
                "PHASE",
                "CUSTOMER",
                "APPROVE",
                "127.0.0.1",
                "Mozilla"
        );

        assertThat(validator.validate(req))
                .extracting(v -> v.getPropertyPath().toString())
                .contains("timestamp");
    }
}
