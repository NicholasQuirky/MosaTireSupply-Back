package com.example.mosawebapp.scheduling.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.service.AccountService;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.scheduling.domain.Schedule;
import com.example.mosawebapp.scheduling.domain.ScheduleRepository;
import com.example.mosawebapp.scheduling.dto.ScheduleForm;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService{
  private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
  private final ScheduleRepository scheduleRepository;
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  private final ActivityLogsService activityLogsService;
  private final AccountService accountService;

  public ScheduleServiceImpl(ScheduleRepository scheduleRepository,
      AccountRepository accountRepository, JwtGenerator jwtGenerator,
      ActivityLogsService activityLogsService, AccountService accountService) {
    this.scheduleRepository = scheduleRepository;
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
    this.activityLogsService = activityLogsService;
    this.accountService = accountService;
  }

  @Override
  public Schedule makeSchedule(ScheduleForm form){
    Validate.notNull(form);
    validateForm(form);

    logger.info("saving customer scheduled service appointment");

    Account account = accountRepository.findByEmail(form.getEmail());

    if(account.getAddress().isEmpty()){
      account.setAddress(form.getAddress());
      accountRepository.save(account);
    }

    /* If only registered users can make a schedule
    if(account == null){
      throw new ValidationException("Account with the email is not yet registered");
    }*/

    Schedule schedule = new Schedule(account, form.getDate(), form.getComments(), false);

    activityLogsService.makeSchedule(account, schedule);
    scheduleRepository.save(schedule);

    return schedule;
  }

  @Override
  public Schedule approveSchedule(String id, String token){
    accountService.validateIfAccountIsAdmin(token);
    Account admin = accountRepository.findById(jwtGenerator.getUserFromJWT(token)).orElseThrow(() -> new NotFoundException("Account does not exists"));

    Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new NotFoundException("Schedule not found"));

    schedule.setApproved(true);

    activityLogsService.approveSchedule(admin, schedule);
    scheduleRepository.save(schedule);

    logger.info("schedule {} approved", schedule.getId());

    return schedule;
  }
  private void validateForm(ScheduleForm form){
    logger.info("validating schedule form");
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    LocalDate date = LocalDate.parse(form.getDate(), dateFormat);

    if(date.isBefore(LocalDate.now())){
      throw new ValidationException("Date scheduled must not be before the current date");
    }

    if(!Validate.hasCorrectEmailFormat(form.getEmail())){
      throw new ValidationException("Email format not valid");
    }

    if(Validate.hasIntegersAndSpecialCharacters(form.getFirstName()) || Validate.hasIntegersAndSpecialCharacters(form.getLastName())){
      throw new ValidationException("Name must have of letters only");
    }

    if(Validate.hasLettersInNumberInput(form.getContactNumber())){
      throw new ValidationException("Contact number must have numbers only");
    }
  }
}
