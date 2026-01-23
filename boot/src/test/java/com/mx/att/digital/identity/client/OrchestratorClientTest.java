package com.mx.att.digital.identity.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.att.digital.identity.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrchestratorClientTest {

    @Mock
    private LambdaClient lambdaClient;

    @Mock
    private ObjectMapper objectMapper;

    private OrchestratorClient client;

    private static final String FUNCTION_ARN =
            "arn:aws:lambda:us-east-1:123:function:test";

    @BeforeEach
    void setup() {
        client = new OrchestratorClient(
                lambdaClient,
                objectMapper,
                FUNCTION_ARN,
                "RequestResponse",
                "Tail"
        );
    }

    // =========================
    // mdnValidate
    // =========================
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

        when(objectMapper.readValue(
                anyString(),
                ArgumentMatchers.<TypeReference<ApiResponse<MdnValidateData>>>any()
        )).thenReturn(expected);

        ApiResponse<MdnValidateData> out =
                client.mdnValidate(new MdnValidateRequest(null, null, null, null, null, null, null));

        assertThat(out).isSameAs(expected);
    }

    // =========================
    // validateCustomer
    // =========================
    @Test
    void validateCustomer_success_returns_parsed_response() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        InvokeResponse response = InvokeResponse.builder()
                .statusCode(200)
                .payload(SdkBytes.fromString("{\"status\":\"OK\"}", StandardCharsets.UTF_8))
                .build();

        when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

        ApiResponse<ValidateCustomerData> expected =
                new ApiResponse<>("OK", "Success", null, OffsetDateTime.now());

        when(objectMapper.readValue(
                anyString(),
                ArgumentMatchers.<TypeReference<ApiResponse<ValidateCustomerData>>>any()
        )).thenReturn(expected);

        ValidateCustomerRequest req =
                new ValidateCustomerRequest(
                        "uuid",
                        OffsetDateTime.now(),
                        "doc",
                        "cust",
                        "op",
                        "sys"
                );

        ApiResponse<ValidateCustomerData> out = client.validateCustomer(req);

        assertThat(out).isSameAs(expected);
    }

    // =========================
    // approval (REAL)
    // =========================
    @Test
    void approval_success_returns_parsed_response() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        InvokeResponse response = InvokeResponse.builder()
                .statusCode(200)
                .payload(SdkBytes.fromString("{\"status\":\"OK\"}", StandardCharsets.UTF_8))
                .build();

        when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

        ApprovalRequest req = new ApprovalRequest(
                "123",
                OffsetDateTime.now(),
                "PHASE_1",
                "CUST_456",
                "APPROVAL",
                "192.168.1.1",
                "Mozilla/5.0"
        );

        ApiResponse<ApprovalRequest> expected =
                new ApiResponse<>("OK", "Success", req, OffsetDateTime.now());

        when(objectMapper.readValue(
                anyString(),
                ArgumentMatchers.<TypeReference<ApiResponse<ApprovalRequest>>>any()
        )).thenReturn(expected);

        ApiResponse<ApprovalRequest> out = client.approval(req);

        assertThat(out).isSameAs(expected);
    }

    // =========================
    // errores
    // =========================
    @Test
    void lambdaException_propagated() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        when(lambdaClient.invoke(any(InvokeRequest.class)))
                .thenThrow(LambdaException.builder().message("AWS error").build());

        assertThrows(LambdaException.class,
                () -> client.mdnValidate(new MdnValidateRequest(null, null, null, null, null, null, null)));
    }

    @Test
    void functionError_throwsRuntimeException() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        InvokeResponse response = InvokeResponse.builder()
                .statusCode(200)
                .functionError("Unhandled")
                .payload(SdkBytes.fromString("{}", StandardCharsets.UTF_8))
                .build();

        when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

        assertThrows(RuntimeException.class,
                () -> client.otpRequest(new OtpRequest(null, null, null, null, null, null)));
    }

    @Test
    void emptyPayload_throwsRuntimeException() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        InvokeResponse response = InvokeResponse.builder()
                .statusCode(200)
                .payload(SdkBytes.fromString("", StandardCharsets.UTF_8))
                .build();

        when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

        assertThrows(RuntimeException.class,
                () -> client.otpValidate(new OtpValidateRequest(null, null, null, null, null, null, null)));
    }

    @Test
    void parseError_throwsRuntimeException() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        InvokeResponse response = InvokeResponse.builder()
                .statusCode(200)
                .payload(SdkBytes.fromString("{\"bad\":true}", StandardCharsets.UTF_8))
                .build();

        when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

        when(objectMapper.readValue(
                anyString(),
                ArgumentMatchers.<TypeReference<ApiResponse<OtpRequestData>>>any()
        )).thenThrow(new IllegalArgumentException("boom"));

        assertThrows(RuntimeException.class,
                () -> client.otpRequest(new OtpRequest(null, null, null, null, null, null)));
    }
}
