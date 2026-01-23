package com.mx.att.digital.identity.exception;

import com.mx.att.digital.identity.model.ErrorResponse;
import com.mx.att.digital.identity.client.OrchestratorClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    /* ===================== NoResourceFoundException ===================== */

    @Test
    void handleNoResource_favicon() {
        NoResourceFoundException ex = Mockito.mock(NoResourceFoundException.class);
        Mockito.when(ex.getMessage()).thenReturn("favicon.ico");

        ResponseEntity<ErrorResponse> response = handler.handleNoResource(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleNoResource_other() {
        NoResourceFoundException ex = Mockito.mock(NoResourceFoundException.class);
        Mockito.when(ex.getMessage()).thenReturn("/api/test");

        ResponseEntity<ErrorResponse> response = handler.handleNoResource(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /* ===================== Validation ===================== */

    @Test
    void handleValidation_withErrors() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "field", "must not be blank"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleValidation_noErrors() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /* ===================== Malformed JSON ===================== */

    @Test
    void handleNotReadable() {
        org.springframework.http.HttpInputMessage inputMessage =
                org.mockito.Mockito.mock(org.springframework.http.HttpInputMessage.class);

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("Malformed JSON", inputMessage);

        ResponseEntity<ErrorResponse> response = handler.handleNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    /* ===================== Missing Parameter ===================== */

    @Test
    void handleMissingParam() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("param", "String");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParam(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /* ===================== HttpStatusCodeException ===================== */

    @Test
    void handleHttpClient_4xx() {
        HttpClientErrorException ex =
                HttpClientErrorException.create(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        HttpHeaders.EMPTY,
                        "client error".getBytes(),
                        null
                );

        ResponseEntity<ErrorResponse> response = handler.handleHttpClient(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleHttpClient_5xx() {
        HttpServerErrorException ex =
                HttpServerErrorException.create(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Server Error",
                        HttpHeaders.EMPTY,
                        "server error".getBytes(),
                        null
                );

        ResponseEntity<ErrorResponse> response = handler.handleHttpClient(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /* ===================== Generic ===================== */

    @Test
    void handleGeneric() {
        Exception ex = new RuntimeException("unexpected");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /* ===================== OrchestratorClientException (AGREGADOS) ===================== */

    @Test
    void handleOrchestratorClient_usesDefaults_whenFieldsMissingOrBlank() {
        OrchestratorClientException ex = new OrchestratorClientException("   ", " ", " ", null);

        ResponseEntity<ErrorResponse> response = handler.handleOrchestratorClient(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ERROR", response.getBody().status());
        assertEquals("Bad request", response.getBody().message());
        assertEquals("ORCHESTRATOR_CLIENT_ERROR", response.getBody().errorCode());
        assertNotNull(response.getBody().timestamp());
        assertFalse(response.getBody().retryable());
    }

    @Test
    void handleOrchestratorClient_usesProvidedFields() {
        OffsetDateTime ts = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OrchestratorClientException ex = new OrchestratorClientException("boom", "ERROR", "X1", ts);

        ResponseEntity<ErrorResponse> response = handler.handleOrchestratorClient(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ERROR", response.getBody().status());
        assertEquals("boom", response.getBody().message());
        assertEquals("X1", response.getBody().errorCode());
        assertEquals(ts, response.getBody().timestamp());
        assertFalse(response.getBody().retryable());
    }
}
