package com.mx.att.digital.identity.controller;

import jakarta.validation.Valid;
import com.mx.att.digital.identity.model.ApiResponse;
import com.mx.att.digital.identity.model.ApprovalRequest;
import com.mx.att.digital.identity.model.AprovalResponse;
import com.mx.att.digital.identity.model.InitAuthData;
import com.mx.att.digital.identity.model.InitAuthRequest;
import com.mx.att.digital.identity.model.MdnValidateData;
import com.mx.att.digital.identity.model.MdnValidateRequest;
import com.mx.att.digital.identity.model.OtpForwardData;
import com.mx.att.digital.identity.model.OtpForwardRequest;
import com.mx.att.digital.identity.model.OtpRequest;
import com.mx.att.digital.identity.model.OtpRequestData;
import com.mx.att.digital.identity.model.OtpValidateData;
import com.mx.att.digital.identity.model.OtpValidateRequest;
import com.mx.att.digital.identity.model.SessionInitData;
import com.mx.att.digital.identity.model.SessionInitLinesData;
import com.mx.att.digital.identity.model.SessionInitLinesRequest;
import com.mx.att.digital.identity.model.SessionInitRequest;
import com.mx.att.digital.identity.model.ValidateCustomerData;
import com.mx.att.digital.identity.model.ValidateCustomerRequest;
import com.mx.att.digital.identity.model.ValidateCustomerRequest;
import com.mx.att.digital.identity.service.IdentityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Identity Orchestration", description = "Endpoints para orquestación de identidad (AT&T MX)")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityController {

  private static final Logger log = LoggerFactory.getLogger(IdentityController.class);


  private final IdentityService service;
  public IdentityController(IdentityService service) { this.service = service; }

  @Operation(
      summary = "Inicializa sesión",
      description = "Crea una sesión de orquestación (por ejemplo, para flujos de verificación).",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Sesión creada",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = SessionInitRequest.class))
  )
  @PostMapping(path = "/session/init", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<SessionInitData>> sessionInit(@Valid @RequestBody SessionInitRequest req) {
    log.info("Llamada a /session/init req{}",req);
    ApiResponse<SessionInitData> response = service.sessionInit(req);
    log.info("response en /session/init res{}",response);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Valida MDN",
      description = "Valida MSISDN/MDN dentro del flujo de identidad.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Validación procesada",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = MdnValidateRequest.class))
  )
  @PostMapping(path = "/mdn/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<MdnValidateData>> mdnValidate(@Valid @RequestBody MdnValidateRequest req) {
    return ResponseEntity.ok(service.mdnValidate(req));
  }


  

  @Operation(
      summary = "Solicita OTP",
      description = "Genera y envía un OTP al canal configurado.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "OTP solicitado",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = OtpRequest.class))
  )
  @PostMapping(path = "/otp/request", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OtpRequestData>> otpRequest(@Valid @RequestBody OtpRequest req) {
    return ResponseEntity.ok(service.otpRequest(req));
  }

  @Operation(
      summary = "Valida OTP",
      description = "Valida el código OTP recibido por el usuario.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "OTP validado",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = OtpValidateRequest.class))
  )
  @PostMapping(path = "/otp/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OtpValidateData>> otpValidate(@Valid @RequestBody OtpValidateRequest req) {
    return ResponseEntity.ok(service.otpValidate(req));
  }

  @Operation(
      summary = "Reenvía OTP",
      description = "Reenvía el OTP al usuario por el canal configurado.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "OTP reenviado",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = OtpForwardRequest.class))
  )
  @PostMapping(path = "/otp/forward", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OtpForwardData>> otpForward(@Valid @RequestBody OtpForwardRequest req) {
    return ResponseEntity.ok(service.otpForward(req));
  }

  @Operation(
      summary = "Session Init Lines ",
      description = "Inicializa una nueva sesion biometrica cuaneo el usuario alcanza el portal web de identidad",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Sesion inicializada",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = SessionInitLinesRequest.class))
  )
  @PostMapping(path = "/session/initLines", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<SessionInitLinesData>> sessionInitLines(@Valid @RequestBody SessionInitLinesRequest req) {
    return ResponseEntity.ok(service.sessionInitLines(req));
  }

   @Operation(
    summary = "Init Auth",
    description = "Inicializa el Incode webflow para el proceso de autenticación.",
    responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flujo inicializado",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(schema = @Schema(implementation = InitAuthRequest.class))
    )
    @PostMapping(path = "/initAuth", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InitAuthData>> initAuth(@Valid @RequestBody InitAuthRequest req) {
        return ResponseEntity.ok(service.initAuth(req));
    }

    @Operation(
        summary = "Validate Customer",
        description = "Valida información del cliente.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Cliente validado",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(schema = @Schema(implementation = ValidateCustomerRequest.class))
    )
    @PostMapping(path = "/validateCustomer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ValidateCustomerData>> validateCustomer(
        @Valid @RequestBody ValidateCustomerRequest req) {
    return ResponseEntity.ok(service.validateCustomer(req));
    }

  @Operation(
        summary = "Approval",
        description = "Servicio de aprobación.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Aprobación procesada",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(schema = @Schema(implementation = ApprovalRequest.class))
    )
    @PostMapping(path = "/approval", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AprovalResponse>> approval(
        @Valid @RequestBody ApprovalRequest req) {
        return ResponseEntity.ok(service.approvalRequest(req));
}



    
    

}
