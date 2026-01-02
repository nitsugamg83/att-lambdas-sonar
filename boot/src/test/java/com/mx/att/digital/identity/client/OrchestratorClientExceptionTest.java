package com.mx.att.digital.identity.client;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.OffsetDateTime;

class OrchestratorClientExceptionTest {

  @Test
  void ctor_message_setsMessage_andNoCause() {
    OrchestratorClientException ex = new OrchestratorClientException("boom");

    assertThat(ex.getMessage()).isEqualTo("boom");
    assertThat(ex.getCause()).isNull();
  }

  @Test
  void ctor_messageAndCause_setsBoth() {
    Throwable cause = new IllegalStateException("root");

    OrchestratorClientException ex = new OrchestratorClientException("boom", cause);

    assertThat(ex.getMessage()).isEqualTo("boom");
    assertThat(ex.getCause()).isSameAs(cause);
  }

  @Test
  void ctor_messageStatusErrCodeTimestamp_setsFields() {
    OffsetDateTime ts = OffsetDateTime.parse("2025-01-01T10:00:00Z");

    OrchestratorClientException ex =
        new OrchestratorClientException("bad request", "ERROR", "X1", ts);

    assertThat(ex.getMessage()).isEqualTo("bad request");
    assertThat(ex.status).isEqualTo("ERROR");
    assertThat(ex.errCode).isEqualTo("X1");
    assertThat(ex.timestamp).isEqualTo(ts);
  }
}
