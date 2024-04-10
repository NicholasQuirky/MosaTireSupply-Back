package com.example.mosawebapp.account.controller;

import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.account.dto.*;
import com.example.mosawebapp.account.registration.dto.AccountRegistrationDto;
import com.example.mosawebapp.account.registration.service.AccountRegistrationService;
import com.example.mosawebapp.api_response.ApiObjectResponse;
import com.example.mosawebapp.exceptions.SecurityException;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.service.AccountService;
import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class AccountController {
  private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
  @Value("${reset.password.page}")
  private String resetPasswordPage;
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private static final String VALID = "valid";
  private static final String NOT_VALID = "not valid";
  private static final String NUMERIC_INPUTS_ONLY = "Numeric Inputs Only";
  private final AccountService accountService;
  private final AccountRegistrationService registrationService;
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;


  public AccountController(AccountService accountService,
      AccountRegistrationService registrationService, TokenBlacklistingService tokenBlacklistingService, JwtGenerator jwtGenerator) {
    this.accountService = accountService;
    this.registrationService = registrationService;
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
  }

  @GetMapping(value="/getAccounts")
  public ResponseEntity<?> getAccounts(@RequestHeader("Authorization") String header){
    logger.info("getting all accounts");
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    List<AccountDto> accountDto = AccountDto.buildFromEntities(accountService.findAllAccounts(token));

    accountDto.sort(Comparator.comparing(AccountDto::getFullName).reversed());

    return ResponseEntity.ok(AccountDto.buildFromEntities(accountService.findAllAccounts(token)));
  }

  @GetMapping(value="/getStaffAccounts")
  public ResponseEntity<?> getStaffAccounts(@RequestHeader("Authorization") String header){
    logger.info("getting all staff accounts");
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    List<AccountDto> accountDto = AccountDto.buildFromEntities(accountService.findAllStaffAccounts(token));

    accountDto.sort(Comparator.comparing(AccountDto::getFullName).reversed());

    return ResponseEntity.ok(AccountDto.buildFromEntities(accountService.findAllStaffAccounts(token)));
  }

  @GetMapping(value="/getCustomerAccounts")
  public ResponseEntity<?> getCustomerAccounts(@RequestHeader("Authorization") String header){
    logger.info("getting all customer accounts");
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    List<AccountDto> accountDto = AccountDto.buildFromEntities(accountService.findAllCustomerAccounts(token));

    accountDto.sort(Comparator.comparing(AccountDto::getFullName).reversed());

    return ResponseEntity.ok(AccountDto.buildFromEntities(accountService.findAllCustomerAccounts(token)));
  }

  @GetMapping(value="/getAccount/{accId}")
  public ResponseEntity<?> getAccount(@PathVariable("accId") String id, @RequestHeader("Authorization") String header){
    logger.info("getting account with id {}", id);
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    AccountDto dto = AccountDto.buildFromEntity(accountService.findOne(id, token));

    logger.info("done fetching account for {}", dto.getEmail());
    return ResponseEntity.ok(dto);
  }

  @GetMapping(value="/logout")
  public ResponseEntity<?> logout(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    if(!jwtGenerator.isTokenValid(token)){
      throw new TokenException(TOKEN_INVALID);
    }

    if(tokenBlacklistingService.isTokenBlacklisted(token)) {
      throw new SecurityException("Token can no longer be used");
    }

    tokenBlacklistingService.addTokenToBlacklist(token);

    logger.info("user logged out");
    return new ResponseEntity<>(new ApiResponse("Logged out successfully", HttpStatus.OK), HttpStatus.OK);
  }

  @GetMapping(value="/currentUser")
  public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    String accId = jwtGenerator.getUserFromJWT(token);
    Account account= accountService.findOne(accId, token);

    return ResponseEntity.ok(AccountDto.buildFromEntity(account));
  }

  @PostMapping(value="/addAccount")
  public ResponseEntity<?> createAccount(@RequestBody AccountForm form, @RequestHeader("Authorization") String header){
    logger.info("creating account with form {}", form);
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    AccountDto dto = AccountDto.buildFromEntity(accountService.createAccount(form, token));

    logger.info("account created for {}", dto.getFullName());
    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Account created for " + dto.getEmail(), dto));
  }

  @PostMapping(value="/register")
  public ResponseEntity<?> register(@RequestBody AccountForm form){
    logger.info("creating account for customer with form {}", form);

    form.setUserRole(UserRole.CUSTOMER);

    AccountRegistrationDto dto= AccountRegistrationDto.buildFromEntity(registrationService.register(form));

    logger.info("account created for {}", dto.getFullName());
    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Account registered for " + dto.getEmail(), dto));
  }

  @PostMapping(value="/registerOtp/{accId}")
  public ResponseEntity<?> registerOtp(@PathVariable("accId") String id, @RequestBody OtpForm form){
    Account account = registrationService.isRegisterOtpValid(id, form.getOtp());

    if(account == null){
      throw new ValidationException("OTP Incorrect. Please Try Again");
    }

    AccountDto dto = AccountDto.buildFromEntity(account);
    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Account created for " + dto.getEmail(), dto));
  }

  @PostMapping(value="/login")
  public ResponseEntity<?> login(@RequestBody LoginForm loginForm){
    logger.info("user {} attempting to login", loginForm.getEmail());

    return accountService.login(loginForm);
  }

  @PostMapping(value="/loginOtp/{accId}")
  public ResponseEntity<?> loginOTP(@PathVariable("accId") String id, @RequestBody OtpForm form){
    logger.info("validating login otp for account {}", id);

    boolean isValid = accountService.isOtpCorrect(id, form.getOtp(), "login");

    logger.info("Otp is {} for logging in", isValid ? VALID : NOT_VALID);
    return ResponseEntity.ok(new ApiResponse(String.valueOf(isValid), HttpStatus.OK));
  }

  @PostMapping(value="/forgotPassword")
  public ResponseEntity<?> validateEmailForChangePassword(@RequestBody EmailForm form){
    logger.info("validating email {} for password reset", form.getEmail());
    Account account = accountService.validateEmailForChangePassword(form.getEmail());

    return ResponseEntity.ok(AccountDto.buildFromEntity(account));
  }

  @GetMapping(value="/resetOtp")
  public ResponseEntity<?> resetOtp(@RequestBody ResetOtpForm form){
    String action = form.isRegister() ? " for registration" : " for change password";
    logger.info("resetting otp {}", action);

    String email = accountService.resetOtp(form.getId(), form.isRegister());

    return ResponseEntity.ok(new ApiResponse("New OTP sent to " + email + action, HttpStatus.OK));
  }

  @PostMapping(value="/changePassword")
  public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String header, @RequestBody ChangePasswordForm form){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    String id = jwtGenerator.getUserFromJWT(token);

    Account account = accountService.findAccount(id);

    logger.info("changing password for {}", account.getEmail());

    accountService.changePassword(account.getId(), form, false);

    return ResponseEntity.ok(new ApiResponse("Password successfully changed", HttpStatus.OK));
  }

  @PostMapping(value="/resetPasswordOtp/{accId}")
  public ResponseEntity<?> resetPasswordOtp(@PathVariable("accId") String id, @RequestBody OtpForm form){
    logger.info("validating login otp for account {}", id);

    boolean isValid = accountService.isOtpCorrect(id, form.getOtp(), "password change");

    if(!isValid){
      throw new ValidationException("OTP Incorrect. Try Again");
    }

    Account account = accountService.findAccount(id);

    logger.info("Otp is {} for password change", VALID);
    return ResponseEntity.ok(new ApiResponse("Link for reset password is sent to " + account.getEmail(), HttpStatus.OK));
  }


  @PostMapping(value= "/resetPassword/{resetToken}")
  public ResponseEntity<?> resetPassword(@PathVariable("resetToken") String resetToken, @RequestBody ChangePasswordForm form){
    Account account = accountService.findAccountByChangePasswordToken(resetToken);

    logger.info("resetting password for {}", account.getEmail());

    accountService.changePassword(account.getId(), form, true);

    return ResponseEntity.ok(new ApiResponse("Password successfully changed", HttpStatus.OK));
  }

  @PutMapping(value="/updateAccount/{accId}")
  public ResponseEntity<?> updateAccount(@PathVariable("accId") String id, @RequestBody AccountUpdateForm form, @RequestHeader("Authorization") String header){
    logger.info("updating account with form {}", form);
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    AccountDto dto = AccountDto.buildFromEntity(accountService.updateAccount(id, token, "admin_update", form));

    logger.info("account updated for {}", dto.getFullName());
    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Account updated for " + dto.getEmail(), dto));
  }

  @PutMapping(value="/updateMyAccount")
  public ResponseEntity<?> updateMyAccount(@RequestBody AccountUpdateForm form, @RequestHeader("Authorization") String header){
    logger.info("updating account with form {}", form);
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    String id = jwtGenerator.getUserFromJWT(token);

    AccountDto dto = AccountDto.buildFromEntity(accountService.updateAccount(id, token, "", form));

    logger.info("account updated for {}", dto.getFullName());
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping(value="/deleteAccount/{accId}")
  public ResponseEntity<?> deleteAccount(@PathVariable("accId") String id, @RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    accountService.deleteAccount(id, token, "admin_delete");

    logger.info("done deleting account");
    return new ResponseEntity<>(new ApiResponse("Account Deleted Successfully", HttpStatus.OK), HttpStatus.OK);
  }

  @DeleteMapping(value="/deleteMyAccount")
  public ResponseEntity<?> deleteMyAccount(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    String id = jwtGenerator.getUserFromJWT(token);

    accountService.deleteAccount(id,  token, "");

    logger.info("done deleting account");
    return new ResponseEntity<>(new ApiResponse("Account Deleted Successfully", HttpStatus.OK), HttpStatus.OK);
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
