package com.example.mosawebapp.account.registration.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.account.registration.domain.AccountRegistration;
import org.springframework.http.ResponseEntity;

public interface AccountRegistrationService {
  AccountRegistration register(AccountForm form);
  Account isRegisterOtpValid(String id, String otp);
}
