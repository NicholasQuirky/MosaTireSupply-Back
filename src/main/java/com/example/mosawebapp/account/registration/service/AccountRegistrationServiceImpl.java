package com.example.mosawebapp.account.registration.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.AccountStatus;
import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.account.registration.domain.AccountRegistration;
import com.example.mosawebapp.account.registration.domain.AccountRegistrationRepository;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.utils.DateTimeFormatter;
import com.example.mosawebapp.validate.Validate;
import java.util.Date;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;

@Service
public class AccountRegistrationServiceImpl implements AccountRegistrationService{
  private static final Logger logger = LoggerFactory.getLogger(AccountRegistrationServiceImpl.class);
  private final AccountRepository accountRepository;
  private final AccountRegistrationRepository registrationRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtGenerator jwtGenerator;
  private final MailService mailService;
  private final Random rnd = new Random();

  public AccountRegistrationServiceImpl(AccountRepository accountRepository,
      AccountRegistrationRepository registrationRepository, PasswordEncoder passwordEncoder,
      JwtGenerator jwtGenerator, MailService mailService) {
    this.accountRepository = accountRepository;
    this.registrationRepository = registrationRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtGenerator = jwtGenerator;
    this.mailService = mailService;
  }

  @Override
  public AccountRegistration register(AccountForm form) {
    Validate.notNull(form);
    validateForm(form);
    validateIfAccountAlreadyExists(form.getEmail());

    AccountRegistration registration = new AccountRegistration(form.getFullName(), form.getEmail(),
        form.getContactNumber(), form.getAddress(), passwordEncoder.encode(form.getConfirmPassword()), form.getUserRole());

    long otp = 100000 + rnd.nextInt(800000);
    registration.setRegisterOtp(otp);
    registration.setStatus(AccountStatus.FOR_REGISTRATION);
    mailService.sendEmailForRegistration(registration.getEmail(), otp);

    logger.info("otp sent to {}", registration.getEmail());

    if(registrationRepository.findByEmailIgnoreCase(form.getEmail()) != null){
      throw new ValidationException("Email is already for account registration");
    }

    registrationRepository.save(registration);
    return registration;
  }

  @Override
  public Account isRegisterOtpValid(String id, String otp) {
    logger.info("validating otp for registration account {}", id);
    AccountRegistration registration = registrationRepository.findById(id).orElseThrow(() -> new NotAcceptableStatusException("Account does not exists"));

    boolean isValid = registration.getRegisterOtp() == Long.parseLong(otp);

    if(isValid){
      registration.setRegisterOtp(0);
      registration.setStatus(AccountStatus.ACTIVE);
      registrationRepository.save(registration);

      Account newAccount = new Account(registration.getFullName(), registration.getEmail(), registration.getContactNumber(),
          registration.getAddress(), registration.getPassword(), registration.getUserRole());

      mailService.sendEmailForAccountRegistration(newAccount);

      if(accountRepository.findByEmailIgnoreCase(registration.getEmail()) != null){
        throw new ValidationException("An account associated with the email already exists");
      }

      accountRepository.save(newAccount);

      return newAccount;
    }

    return null;
  }

  private void validateIfAccountAlreadyExists(String email){
    logger.info("validating if account already exists");

    boolean existsByEmail = registrationRepository.existsByEmail(email);

    if(existsByEmail){
      throw new ValidationException("Account associated with the email already exists");
    }
  }
  private void validateForm(AccountForm form){
    logger.info("validating form");

    if(Validate.hasIntegersAndSpecialCharacters(form.getFullName())){
      throw new ValidationException("Name should only consists of letters");
    }

    if(!Validate.hasCorrectEmailFormat(form.getEmail())){
      throw new ValidationException("Email format not valid");
    }

    if(Validate.hasLettersInNumberInput(form.getContactNumber())){
      throw new ValidationException("Contact number must not have letters");
    }

    if(form.getPassword().length() < 8 || form.getConfirmPassword().length() < 8){
      throw new ValidationException("Password must have at least 8 characters");
    }

    if(!form.getPassword().equals(form.getConfirmPassword())){
      throw new ValidationException("Passwords are not equal");
    }
  }
}
