package com.mx.att.digital.identity.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.att.digital.identity.model.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.services.lambda.model.LogType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class OrchestratorClient {

  private static final Logger log = LoggerFactory.getLogger(OrchestratorClient.class);

  
  private final LambdaClient lambda;
  private final ObjectMapper mapper;
  private final String functionArn;
  private final String invocationType; 
  private final String logType;        

  public OrchestratorClient(
      LambdaClient lambda,
      ObjectMapper mapper,
      @Value("${aws.lambda.function-arn}") String functionArn,
      @Value("${aws.lambda.invocation-type:RequestResponse}") String invocationType,
      @Value("${aws.lambda.log-type:Tail}") String logType
  ) {
    this.lambda = lambda;
    this.mapper = mapper;
    this.functionArn = Objects.requireNonNull(functionArn, "aws.lambda.function-arn es requerido");
    this.invocationType = invocationType;
    this.logType = logType;
  }

  

  public ApiResponse<SessionInitData> sessionInit(SessionInitRequest req) {
    return invoke("sessionInit", req, new TypeReference<>() {});
  }

  public ApiResponse<MdnValidateData> mdnValidate(MdnValidateRequest req) {
    return invoke("mdnValidate", req, new TypeReference<>() {});
  }

  public ApiResponse<OtpRequestData> otpRequest(OtpRequest req) {
    return invoke("otpRequest", req, new TypeReference<>() {});
  }

  public ApiResponse<OtpValidateData> otpValidate(OtpValidateRequest req) {
    return invoke("otpValidate", req, new TypeReference<>() {});
  }

  public ApiResponse<OtpForwardData> otpForward(OtpForwardRequest req) {
    return invoke("otpForward", req, new TypeReference<>() {});
  }

// =======================
// NUEVO SERVICE AGREGADO
// =======================
public ApiResponse<ValidateCustomerData> validateCustomer(ValidateCustomerRequest req) {
    return invoke(
        "validateCustomer",
        req,
        new TypeReference<ApiResponse<ValidateCustomerData>>() {}
    );
}

public ApiResponse<ApprovalRequest> approval(
        ApprovalRequest req) {

    return invoke(
        "approval",
        req,
        new TypeReference<ApiResponse<ApprovalRequest>>() {}
    );
}
  public ApiResponse<SessionInitLinesData> sessionInitLines(SessionInitLinesRequest req) {
    return invoke("sessionInitLines", req, new TypeReference<>() {});
  }

  public ApiResponse<InitAuthData> initAuth(InitAuthRequest req) {
    return invoke("initAuth", req, new TypeReference<>() {});
  }


  

  private <Q, R> ApiResponse<R> invoke(
      String operation,
      Q requestBody,
      TypeReference<ApiResponse<R>> typeRef
  ) {
    try {
      String json = buildPayloadJson(operation, requestBody);

      InvokeRequest invokeReq = InvokeRequest.builder()
          .functionName(functionArn)
          .invocationType(invocationType)
          .logType(resolveLogType(logType))
          .payload(SdkBytes.fromString(json, StandardCharsets.UTF_8))
          .build();

      log.debug("[LAMBDA] invoke op={} arn={} itype={} log={}", operation, functionArn, invocationType, logType);

      InvokeResponse resp = lambda.invoke(invokeReq);

      logTailIfPresent(resp);
      throwIfFunctionError(resp, operation);

      String body = extractBodyOrThrow(resp, operation);

      ApiResponse<R> parsed = mapper.readValue(body, typeRef);
      if (parsed == null) {
        throw new OrchestratorClientException("No se pudo parsear la respuesta de Lambda (op=" + operation + ")");
      }
      return parsed;

    } catch (LambdaException awsEx) {
      throw awsEx;

    } catch (OrchestratorClientException ex) {
      throw ex;

    } catch (Exception ex) {
      throw new OrchestratorClientException(
          "Error invocando Lambda (op=" + operation + ", arn=" + functionArn + "): " + ex.getMessage(),
          ex
      );
    }
  }

  private String buildPayloadJson(String operation, Object requestBody) {
    try {
      Map<String, Object> payload = new HashMap<>();
      payload.put("operation", operation);
      payload.put("request", requestBody);
      
      return mapper.writeValueAsString(payload);
    } catch (Exception ex) {

      throw new OrchestratorClientException(
          "No se pudo serializar payload para Lambda (op=" + operation + ", arn=" + functionArn + "): " + ex.getMessage(),
          ex
      );
    }
  }

  private void logTailIfPresent(InvokeResponse resp) {
    String logResult = resp.logResult();
    if (logResult != null && !logResult.isEmpty()) {
      String logsDecoded = new String(Base64.getDecoder().decode(logResult), StandardCharsets.UTF_8);
      log.debug("[LAMBDA][logs]\n{}", logsDecoded);
    }
  }

  private void throwIfFunctionError(InvokeResponse resp, String operation) {
    String functionError = resp.functionError();
    if (functionError == null || functionError.isEmpty()) return;

    String errPayload = resp.payload() != null ? resp.payload().asUtf8String() : "";
    log.error("[LAMBDA] functionError={} statusCode={} op={} payload={}",
        functionError, resp.statusCode(), operation, errPayload);

    throw new OrchestratorClientException("Lambda function error: " + functionError + " (op=" + operation + ")");
  }

  private String extractBodyOrThrow(InvokeResponse resp, String operation) {
    String body = resp.payload() != null ? resp.payload().asUtf8String() : null;
    if (body == null || body.isBlank()) {
      log.error("[LAMBDA] Respuesta vacía op={} statusCode={}", operation, resp.statusCode());
      throw new OrchestratorClientException("Respuesta vacía de Lambda (op=" + operation + ")");
    }
    return body;
  }

  private LogType resolveLogType(String configured) {
    if ("Tail".equalsIgnoreCase(configured)) return LogType.TAIL;
    return LogType.NONE;
  }

  static class OrchestratorClientException extends RuntimeException {
    OrchestratorClientException(String message) { super(message); }
    OrchestratorClientException(String message, Throwable cause) { super(message, cause); }
  }

  public ApiResponse<ValidateCustomerData> validateCustomerRequest(ValidateCustomerRequest req) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'validateCustomerRequest'");
  }

  public ApiResponse<AprovalResponse> approvalRequest(ApprovalRequest req) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'approvalRequest'");
  }
}
