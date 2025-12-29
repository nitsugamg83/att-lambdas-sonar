package com.mx.att.digital.identity.client;

import java.time.OffsetDateTime;

public class OrchestratorClientException extends RuntimeException {

  public final String status;
  public final OffsetDateTime timestamp;
  public final String errCode;


  public OrchestratorClientException(String message) {
    super(message);
    this.status=null;
    this.timestamp=null;
    this.errCode = null;
  }

  public OrchestratorClientException(String message, String status,String errCode, OffsetDateTime timestamp) {
    super(message);
    this.status=status;
    this.timestamp= timestamp;
    this.errCode=errCode;
  }

  public OrchestratorClientException(String message, Throwable cause) {
    super(message, cause);
    this.status=null;
    this.timestamp=null;
    this.errCode = null;
  }
}
