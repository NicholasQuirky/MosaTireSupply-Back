package com.example.mosawebapp.account.service;

import com.example.mosawebapp.account.domain.RoleRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.account.dto.AccountDto;
import com.example.mosawebapp.account.dto.AccountUpdateForm;
import com.example.mosawebapp.account.dto.ChangePasswordForm;
import com.example.mosawebapp.account.registration.domain.AccountRegistration;
import com.example.mosawebapp.account.registration.domain.AccountRegistrationRepository;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.account.dto.LoginForm;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.api_response.AuthResponseDto;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import com.example.mosawebapp.validate.Validate;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;

@Service
public class AccountServiceImpl implements AccountService{
  private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
  @Value("${reset.password.page}")
  private String resetPasswordPage;
  private static final String ACCOUNT_NOT_EXIST = "Account does not exists";
  private static final String REGISTER = "register";
  private final AccountRepository accountRepository;
  private final RoleRepository roleRepository;
  private final AccountRegistrationRepository registrationRepository;
  private final ActivityLogsService activityLogsService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;
  private final MailService mailService;
  private final Random rnd = new Random();

  @Autowired
  public AccountServiceImpl(AccountRepository accountRepository, RoleRepository roleRepository,
      AccountRegistrationRepository registrationRepository, ActivityLogsService activityLogsService,
      AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
      TokenBlacklistingService tokenBlacklistingService, JwtGenerator jwtGenerator, MailService mailService) {
    this.accountRepository = accountRepository;
    this.roleRepository = roleRepository;
    this.registrationRepository = registrationRepository;
    this.activityLogsService = activityLogsService;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
    this.mailService = mailService;
  }

  @Override
  public List<Account> findAllAccounts(String token) {
    validateIfAccountIsAdmin(token);

    return accountRepository.findAll();
  }

  @Override
  public List<Account> findAllStaffAccounts(String token){
    validateIfAccountIsAdmin(token);

    return accountRepository.findStaffAccounts();
  }

  @Override
  public List<Account> findAllCustomerAccounts(String token){
    validateIfAccountIsAdmin(token);

    return accountRepository.findCustomerAccounts();
  }

  @Override
  public Account findOne(String id, String token) {
    validateIfAccountIsAdmin(token);

    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));
  }

  @Override
  public Account findAccountByChangePasswordToken(String token){
    Account account = accountRepository.findByChangePasswordToken(token);

    if(account == null){
      throw new NotFoundException(ACCOUNT_NOT_EXIST);
    }

    return account;
  }

  @Override
  public Account findAccount(String id){
    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));
  }

  @Override
  @Transactional
  public Account createAccount(AccountForm form, String token) {
    validateIfAccountIsAdmin(token);
    Validate.notNull(form);
    validateForm(form);
    validateIfAccountAlreadyExists(form.getEmail());

    if(form.getUserRole() == null){
      form.setUserRole(UserRole.CUSTOMER);
    }

    String id = jwtGenerator.getUserFromJWT(token);
    Account staff = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    Account account = new Account( form.getFullName(), form.getEmail(), form.getContactNumber(), form.getAddress(),
        passwordEncoder.encode(form.getPassword()), form.getUserRole());
    account.setRoles(Collections.singletonList(roleRepository.findByName(form.getUserRole().name())));

    accountRepository.save(account);
    mailService.sendEmailForAccountCreation(form);
    activityLogsService.adminCreateAccountActivity(staff, account);

    return account;
  }

  @Override
  @Transactional
  public Account updateAccount(String id, String token, String action, AccountUpdateForm form){
    if(action.equalsIgnoreCase("admin_update")){
      validateIfAccountIsAdmin(token);
    }

    Validate.notNull(form);
    validateUpdateForm(form);

    Account updatedAccount = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    updatedAccount.setFullName(form.getFullName());
    updatedAccount.setEmail(form.getEmail());
    updatedAccount.setContactNumber(form.getContactNumber());

    if(form.getAddress() != null)
      updatedAccount.setAddress(form.getAddress());

    if(form.getUserRole() != null)
      updatedAccount.setUserRole(form.getUserRole());
    //updatedAccount.setPassword(passwordEncoder.encode(form.getPassword())); Can be changed in Change Password Feature

    updatedAccount = accountRepository.save(updatedAccount);

    if(action.equalsIgnoreCase("admin_update")){
      String adminId = jwtGenerator.getUserFromJWT(token);
      Account staff = accountRepository.findById(adminId).orElseThrow(() -> new NotFoundException("Account does not exists"));

      activityLogsService.adminUpdateAccountActivity(staff, updatedAccount);
    } else {
      activityLogsService.userUpdateAccountActivity(updatedAccount);

    }

    return updatedAccount;
  }

  @Override
  @Transactional
  public void deleteAccount(String id, String token, String action){
    if(action.equalsIgnoreCase("admin_delete")){
      validateIfAccountIsAdmin(token);
    }

    Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));
    AccountRegistration registration = registrationRepository.findByEmail(account.getEmail());

    logger.info("deleting account of {}", account.getEmail());

    if(action.equalsIgnoreCase("admin_delete")){
      String adminId = jwtGenerator.getUserFromJWT(token);
      Account staff = accountRepository.findById(adminId).orElseThrow(() -> new NotFoundException("Account does not exists"));

      activityLogsService.adminDeleteAccountActivity(staff, account);
    } else {
      activityLogsService.userDeleteAccountActivity(account);
    }

    tokenBlacklistingService.addTokenToBlacklist(token);
    accountRepository.delete(account);
    if(registration != null){
      registrationRepository.delete(registration);
    }
  }

  @Override
  public ResponseEntity<AuthResponseDto> login(LoginForm form){
    Validate.notNull(form);
    logger.info("Logging user {} into the system", form.getEmail());

    Account account = accountRepository.findByEmail(form.getEmail());

    if(account == null){
      throw new ValidationException(ACCOUNT_NOT_EXIST);
    }

    boolean isPasswordSame = passwordEncoder.matches(form.getPassword(), account.getPassword());
    if(!isPasswordSame){
      throw new ValidationException("Password is incorrect");
    }

    /*Set login otp for the account and save to database
    long otp = rnd.nextInt(800000);
    account.setLoginOtp(otp);
    accountRepository.save(account);*/

    Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(account.getId(), form.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(auth);
    String token = jwtGenerator.generateAccessToken(auth);
    AccountDto dto = new AccountDto(account);
    /* Send otp to the account's email
    mailService.sendEmailForLogin(account.getEmail(), otp);*/

    logger.info("Account for {} found. Redirecting to otp form", form.getEmail());
    activityLogsService.loginActivity(account);
    return new ResponseEntity<>(new AuthResponseDto(HttpStatus.ACCEPTED, dto, token, "Successfully logged in"), HttpStatus.ACCEPTED);
  }

  @Override
  @Transactional
  public boolean isOtpCorrect(String accId, String otp, String action) {
    Account account = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    boolean isValid = determineActionAndOtpValidation(account, otp, action);

    if(isValid){ // For Account Creation and Password Change/Reset
      String changePasswordToken = RandomStringUtils.randomAlphanumeric(30);

      account.setLoginOtp(0);
      account.setChangePasswordOtp(0);
      account.setChangePasswordToken(changePasswordToken);

      mailService.sendEmailForResetPasswordLink(account.getEmail(), resetPasswordPage, changePasswordToken);
      accountRepository.save(account);
    }

    return isValid;
  }

  private boolean determineActionAndOtpValidation(Account account, String otp, String action){
    boolean isValid = false;

    if(action.equalsIgnoreCase("login")){
      isValid = account.getLoginOtp() == Long.parseLong(otp);
    } else if(action.equalsIgnoreCase("password change")){
      isValid = account.getChangePasswordOtp() == Long.parseLong(otp);
    }

    return isValid;
  }

  @Override
  @Transactional
  public Account validateEmailForChangePassword(String email){
    Account account = accountRepository.findByEmail(email);

    if(account == null){
      throw new ValidationException("Account with email " + email + " does not exist");
    }

    long otp = 100000 + rnd.nextInt(800000);
    account.setChangePasswordOtp(otp);
    accountRepository.save(account);

    mailService.sendEmailForChangePassword(email, otp);
    logger.info("otp sent to {} for password reset", email);

    return account;
  }

  @Override
  @Transactional
  public void changePassword(String accId, ChangePasswordForm form, boolean isReset) {
    Account account = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

    if(account.getChangePasswordOtp() != 0){
      throw new ValidationException("Enter OTP for password reset before proceeding");
    }

    validatePasswordChanges(account, form, isReset);

    account.setPassword(passwordEncoder.encode(form.getConfirmPassword()));
    account.setChangePasswordToken("");
    accountRepository.save(account);
  }

  @Override
  @Transactional
  public String resetOtp(String id, boolean isRegister){
    long otp = 100000 + rnd.nextInt(800000);
    String email;

    if(isRegister){
      logger.info("Id {} | isRegister {}", id, isRegister);
      AccountRegistration registration = registrationRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));
      registration.setRegisterOtp(otp);

      mailService.sendEmailForRegistration(registration.getEmail(), otp);
      registrationRepository.save(registration);

      email = registration.getEmail();
    } else{
      logger.info("Id {} | isRegister {}", id, isRegister);
      Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));
      account.setChangePasswordOtp(otp);

      mailService.sendEmailForChangePassword(account.getEmail(), otp);
      accountRepository.save(account);

      email = account.getEmail();
    }

    logger.info("reset otp sent to {}", email);

    return email;
  }
  private void validatePasswordChanges(Account account, ChangePasswordForm form, boolean isReset){
    if(form.getNewPassword().length() < 8 || form.getConfirmPassword().length() < 8){
      throw new ValidationException("Password must have at least 8 characters");
    }

    if(isReset){
      validateWithoutOldPassword(account, form);
    } else {
      validateWithOldPassword(account, form);
    }
  }

  private void validateWithoutOldPassword(Account account, ChangePasswordForm form){
    boolean isCurrentAndNewPasswordSame = passwordEncoder.matches(form.getNewPassword(), account.getPassword());
    boolean isCurrentAndFinalPasswordSame = passwordEncoder.matches(form.getConfirmPassword(), account.getPassword());

    if(isCurrentAndNewPasswordSame){
      throw new ValidationException("New Password is same with the Old Password");
    }

    if(isCurrentAndFinalPasswordSame){
      throw new ValidationException("Confirmed Password is same with the Old Password");
    }

    if(!form.getNewPassword().equals(form.getConfirmPassword())){
      throw new ValidationException("Passwords must be equal");
    }
  }

  private void validateWithOldPassword(Account account, ChangePasswordForm form){
    boolean isCurrentAndOldPasswordSame = passwordEncoder.matches(form.getOldPassword(), account.getPassword());

    if(!isCurrentAndOldPasswordSame){
      throw new ValidationException("Old Password is not the same with the Current Password");
    }

    if(form.getOldPassword().equals(form.getNewPassword())){
      throw new ValidationException("New Password is same with the Old Password");
    }

    if(form.getOldPassword().equals(form.getConfirmPassword())){
      throw new ValidationException("Confirmed Password is same with the Old Password");
    }

    if(!form.getNewPassword().equals(form.getConfirmPassword())){
      throw new ValidationException("Passwords must be equal");
    }
  }

  private void validateIfAccountAlreadyExists(String email){
    logger.info("validating if account already exists");

    boolean existsByEmail = accountRepository.existsByEmail(email);

    if(existsByEmail){
      throw new ValidationException("Account associated with the email already exists");
    }
  }

  private void validateUpdateForm(AccountUpdateForm form){
    logger.info("validating update form");

    if(Validate.hasIntegersAndSpecialCharacters(form.getFullName())){
      throw new ValidationException("Name should only consists of letters");
    }

    if(!Validate.hasCorrectEmailFormat(form.getEmail())){
      throw new ValidationException("Email format not valid");
    }

    if(Validate.hasLettersInNumberInput(form.getContactNumber())){
      throw new ValidationException("Contact number must not have letters");
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

    if(!UserRole.getRolesList().contains(form.getUserRole())){
      throw new ValidationException("Role is not valid or does not exists");
    }
  }

  @Override
  public void validateIfAccountIsAdmin(String token){
      String accId = jwtGenerator.getUserFromJWT(token);
      Account adminAccount = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException(ACCOUNT_NOT_EXIST));

      if(!adminAccount.getUserRole().equals(UserRole.ADMINISTRATOR)){
        throw new ValidationException("Only Administrators have access to this feature");
      }
  }
}
