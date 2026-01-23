package com.mx.att.digital.identity.service;

import com.mx.att.digital.identity.model.*;

public interface IdentityService {

    ApiResponse<SessionInitData> sessionInit(SessionInitRequest req);

    ApiResponse<MdnValidateData> mdnValidate(MdnValidateRequest req);

    ApiResponse<OtpRequestData> otpRequest(OtpRequest req);

    ApiResponse<OtpValidateData> otpValidate(OtpValidateRequest req);

    ApiResponse<OtpForwardData> otpForward(OtpForwardRequest req);
    
    ApiResponse<ValidateCustomerData> validateCustomer(ValidateCustomerRequest req);

    ApiResponse<AprovalResponse> approvalRequest(ApprovalRequest req);

    
}
