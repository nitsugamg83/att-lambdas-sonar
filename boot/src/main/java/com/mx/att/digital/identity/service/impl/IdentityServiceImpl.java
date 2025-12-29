package com.mx.att.digital.identity.service.impl;

import com.mx.att.digital.identity.client.OrchestratorClient;
import com.mx.att.digital.identity.client.OrchestratorClientException;
import com.mx.att.digital.identity.model.*;
import com.mx.att.digital.identity.service.IdentityService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class IdentityServiceImpl implements IdentityService {

    private static final Logger log = LoggerFactory.getLogger(IdentityServiceImpl.class);

    private final OrchestratorClient client;

    public IdentityServiceImpl(OrchestratorClient client) {
        this.client = client;
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    public ApiResponse<SessionInitData> sessionInit(SessionInitRequest req) {
        if (log.isInfoEnabled()) {
            log.info("[IdentityService] sessionInit uuid={}", safe(req == null ? null : req.uuid()));
        }
        ApiResponse<SessionInitData> result = client.sessionInit(req);

        throwIfError(result, "sessionInit returned ERROR", "SESSION_INIT_ERROR");
        
        return result;
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    public ApiResponse<MdnValidateData> mdnValidate(MdnValidateRequest req) {
        if (log.isInfoEnabled()) {
            log.info("[IdentityService] mdnValidate uuid={} msisdn={}",
                safe(req == null ? null : req.uuid()),
                safe(req == null ? null : req.msisdn())
            );
        }
        ApiResponse<MdnValidateData> result = client.mdnValidate(req);

        log.info("result=\"{}\"", result);

        throwIfError(result, "mdnValidate returned ERROR", "MDN_VALIDATE_ERROR");
        
        return result;
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    public ApiResponse<OtpRequestData> otpRequest(OtpRequest req) {
        if (log.isInfoEnabled()) {
            log.info("[IdentityService] otpRequest uuid={}", safe(req == null ? null : req.uuid()));
        }
        ApiResponse<OtpRequestData> result = client.otpRequest(req);

        throwIfError(result, "otpRequest returned ERROR", "OTP_REQUEST_ERROR");
        
        return result;
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    public ApiResponse<OtpValidateData> otpValidate(OtpValidateRequest req) {
        if (log.isInfoEnabled()) {
            log.info("[IdentityService] otpValidate uuid={}", safe(req == null ? null : req.uuid()));
        }
        ApiResponse<OtpValidateData> result = client.otpValidate(req);

        throwIfError(result, "otpValidate returned ERROR", "OTP_VALIDATE_ERROR");
        
        return result;
    }


    /**
     * Lanza OrchestratorClientException si result.status == ERROR.
     * Los campos del error se toman del ApiResponse result.
     */
    private void throwIfError(ApiResponse<?> result, String defaultMsg, String errorCode) {
        String status = (result == null) ? null : result.status();

        if ("ERROR".equalsIgnoreCase(status)) {
            String msg = (result != null && result.message() != null && !result.message().isBlank())
                    ? result.message()
                    : defaultMsg;

            String code = (errorCode != null && !errorCode.isBlank())
                    ? errorCode
                    : "ORCHESTRATOR_CLIENT_ERROR";

            // timestamp nuevo (si no viene en result)
            OffsetDateTime ts = OffsetDateTime.now();

            throw new OrchestratorClientException(msg, status, code, ts);
        }
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    public ApiResponse<OtpForwardData> otpForward(OtpForwardRequest req) {
        if (log.isInfoEnabled()) {
            log.info("[IdentityService] otpForward uuid={}", safe(req == null ? null : req.uuid()));
        }
        ApiResponse<OtpForwardData> result = client.otpForward(req);

        throwIfError(result, "otpForward returned ERROR", "OTP_FORWARD_ERROR");
        
        return result;
    }

    private String safe(String v) {
        return Objects.toString(v, "-");
    }
}
