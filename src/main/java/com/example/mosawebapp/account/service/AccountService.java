package com.example.mosawebapp.account.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.account.dto.AccountUpdateForm;
import com.example.mosawebapp.account.dto.ChangePasswordForm;
import com.example.mosawebapp.account.dto.LoginForm;
import com.example.mosawebapp.api_response.AuthResponseDto;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface AccountService {
  List<Account> findAllAccounts(String token);

  List<Account> findAllStaffAccounts(String token);

  List<Account> findAllCustomerAccounts(String token);

  Account findOne(String id, String token);

  Account findAccountByChangePasswordToken(String token);

  Account findAccount(String id);

  Account createAccount(AccountForm form, String token);
  Account updateAccount(String id, String token, String action, AccountUpdateForm form);
  void deleteAccount(String id, String token, String action);
  ResponseEntity<AuthResponseDto> login(LoginForm form);
  boolean isOtpCorrect(String accId, String otp, String action);
  Account validateEmailForChangePassword(String email);
  void changePassword(String accId, ChangePasswordForm form, boolean isReset);
  String resetOtp(String id, boolean isRegister);
  void validateIfAccountIsAdmin(String token);
}
