package com.mx.att.digital.identity.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class OtpValidateDataTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  static void init() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void close() {
    if (factory != null) factory.close();
  }

  @Test
  void accessors_ok() {
    OtpValidateData data =
        new OtpValidateData("uuid-1", "0", "OK", "https://x");

    assertThat(data.uuid()).isEqualTo("uuid-1");
    assertThat(data.resultCode()).isEqualTo("0");
    assertThat(data.resultDesc()).isEqualTo("OK");
    assertThat(data.onboardingUrl()).isEqualTo("https://x");
  }

  @Test
  void validation_ok() {
    assertThat(
            validator.validate(
                new OtpValidateData("uuid-1", "0", "OK", "https://x")
            )
        )
        .isEmpty();
  }

  @Test
  void onboardingUrl_blank_invalid() {
    assertThat(
            validator.validate(
                new OtpValidateData("uuid-1", "0", "OK", " ")
            )
        )
        .extracting(v -> v.getPropertyPath().toString())
        .contains("onboardingUrl");
  }

  // =========================
  // CASOS EXTRA (coverage)
  // =========================

  @Test
  void onboardingUrl_null_invalid() {
    assertThat(
            validator.validate(
                new OtpValidateData("uuid-1", "0", "OK", null)
            )
        )
        .extracting(v -> v.getPropertyPath().toString())
        .contains("onboardingUrl");
  }

  @Test
  void uuid_null_invalid() {
    assertThat(
            validator.validate(
                new OtpValidateData(null, "0", "OK", "https://x")
            )
        )
        .extracting(v -> v.getPropertyPath().toString())
        .contains("uuid");
  }

  @Test
  void resultCode_null_invalid() {
    assertThat(
            validator.validate(
                new OtpValidateData("uuid-1", null, "OK", "https://x")
            )
        )
        .extracting(v -> v.getPropertyPath().toString())
        .contains("resultCode");
  }

  @Test
  void resultDesc_null_invalid() {
    assertThat(
            validator.validate(
                new OtpValidateData("uuid-1", "0", null, "https://x")
            )
        )
        .extracting(v -> v.getPropertyPath().toString())
        .contains("resultDesc");
  }

  @Test
  void equals_and_hashcode_ok() {
    OtpValidateData d1 =
        new OtpValidateData("uuid-1", "0", "OK", "https://x");

    OtpValidateData d2 =
        new OtpValidateData("uuid-1", "0", "OK", "https://x");

    assertThat(d1).isEqualTo(d2);
    assertThat(d1.hashCode()).isEqualTo(d2.hashCode());
  }

  @Test
  void toString_contains_fields() {
    OtpValidateData data =
        new OtpValidateData("uuid-1", "0", "OK", "https://x");

    assertThat(data.toString())
        .contains("uuid-1")
        .contains("0")
        .contains("OK")
        .contains("https://x");
  }
}
