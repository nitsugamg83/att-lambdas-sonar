package com.mx.att.digital.identity.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.att.digital.identity.model.ApiResponse;
import com.mx.att.digital.identity.model.MdnValidateData;
import com.mx.att.digital.identity.model.MdnValidateRequest;
import com.mx.att.digital.identity.model.OtpRequest;
import com.mx.att.digital.identity.model.OtpRequestData;
import com.mx.att.digital.identity.model.OtpValidateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.services.lambda.model.LogType;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrchestratorClientTest {

  private static final String FUNCTION_ARN = "arn:aws:lambda:mx-central-1:000000000000:function:test";

  @Mock private LambdaClient lambdaClient;
  @Mock private ObjectMapper objectMapper;

  private OrchestratorClient client;

  @BeforeEach
  void setUp() {
    client = new OrchestratorClient(
        lambdaClient,
        objectMapper,
        FUNCTION_ARN,
        "RequestResponse",
        "Tail"
    );
  }

  @Test
  void mdnValidate_success_returns_parsed_response() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("{\"status\":\"OK\"}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    ApiResponse<MdnValidateData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    // OJO: no uses eq("{...}") porque el body puede
    // variar (espacios/orden/campos)
    when(objectMapper.readValue(
        anyString(),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<MdnValidateData>>>any()
    )).thenReturn(expected);

    MdnValidateRequest req = new MdnValidateRequest(null, null, null, null, null, null, null);

    ApiResponse<MdnValidateData> out = client.mdnValidate(req);

    assertThat(out).isSameAs(expected);

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verify(objectMapper).readValue(
        anyString(),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<MdnValidateData>>>any()
    );
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  @Test
  void invoke_lambdaException_throwsRuntimeException() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");
    when(lambdaClient.invoke(any(InvokeRequest.class))).thenThrow(LambdaException.builder().message("fail").build());

    OtpRequest req = new OtpRequest(null, null, null, null, null, null);

    assertThrows(RuntimeException.class, () -> client.otpRequest(req));

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  @Test
  void invoke_emptyPayload_throwsRuntimeException() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    OtpValidateRequest req = new OtpValidateRequest(null, null, null, null, null, null, null);

    assertThrows(RuntimeException.class, () -> client.otpValidate(req));

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  @Test
  void invoke_parseFails_throwsRuntimeException() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("{\"status\":\"OK\"}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    when(objectMapper.readValue(
        anyString(),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<OtpRequestData>>>any()
    )).thenThrow(new RuntimeException("bad json"));

    OtpRequest req = new OtpRequest(null, null, null, null, null, null);

    assertThrows(RuntimeException.class, () -> client.otpRequest(req));

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verify(objectMapper).readValue(
        anyString(),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<OtpRequestData>>>any()
    );
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  @Test
  void invoke_withLogTail_present_doesNotThrow_andReturnsParsedResponse() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    String logText = "hello from lambda";
    String logBase64 = java.util.Base64.getEncoder().encodeToString(logText.getBytes(StandardCharsets.UTF_8));

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .logResult(logBase64)
        .payload(SdkBytes.fromString("{\"status\":\"OK\"}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    ApiResponse<MdnValidateData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(objectMapper.readValue(
        anyString(),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<MdnValidateData>>>any()
    )).thenReturn(expected);

    MdnValidateRequest req = new MdnValidateRequest(null, null, null, null, null, null, null);

    ApiResponse<MdnValidateData> out = client.mdnValidate(req);

    assertThat(out).isSameAs(expected);
    verify(lambdaClient).invoke(any(InvokeRequest.class));
  }

  @Test
  void invoke_whenWritePayloadFails_throwsRuntimeException_andDoesNotCallLambda() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("boom"));

    OtpRequest req = new OtpRequest(null, null, null, null, null, null);

    assertThrows(RuntimeException.class, () -> client.otpRequest(req));

    verify(objectMapper).writeValueAsString(any());
    verifyNoInteractions(lambdaClient);
  }

  @Test
  void constructor_withLogTypeNone_setsInvokeRequestLogTypeNone() throws Exception {
    OrchestratorClient clientNone = new OrchestratorClient(
        lambdaClient,
        objectMapper,
        FUNCTION_ARN,
        "RequestResponse",
        "None"
    );

    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("{\"status\":\"OK\"}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    ApiResponse<OtpRequestData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(objectMapper.readValue(
        anyString(),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<OtpRequestData>>>any()
    )).thenReturn(expected);

    ArgumentCaptor<InvokeRequest> captor = ArgumentCaptor.forClass(InvokeRequest.class);

    OtpRequest req = new OtpRequest(null, null, null, null, null, null);

    ApiResponse<OtpRequestData> out = clientNone.otpRequest(req);

    assertThat(out).isSameAs(expected);
    verify(lambdaClient).invoke(captor.capture());
    assertThat(captor.getValue().logType()).isEqualTo(LogType.NONE);
  }
}
