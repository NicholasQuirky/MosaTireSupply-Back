package com.example.mosawebapp.product.threadtypedetails.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.file_upload_service.FileUploadService;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetailsRepository;
import com.example.mosawebapp.product.threadtypedetails.dto.AddStockForm;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsForm;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ThreadTypeDetailsServiceImpl implements ThreadTypeDetailsService{
  private static final String TYPE_DETAILS_NOT_EXIST = "Thread Type Detai;s does not exists";
  private static final String ADDED = "Added";
  private static final String UPDATED = "Updated";
  private static final String DELETED = "Deleted";
  @Value("${mosatiresupply.official.email}")
  private String MOSA_TIRE_SUPPLY_EMAIL;
  @Value("${default.blank.image.cdn}")
  private String BLANK_IMAGE;
  private final ThreadTypeRepository threadTypeRepository;
  private final ThreadTypeDetailsRepository threadTypeDetailsRepository;
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  private final MailService mailService;
  private final ActivityLogsService activityLogsService;
  private final FileUploadService fileUploadService;
  @Autowired
  public ThreadTypeDetailsServiceImpl(ThreadTypeRepository threadTypeRepository,
      ThreadTypeDetailsRepository threadTypeDetailsRepository, AccountRepository accountRepository,
      JwtGenerator jwtGenerator, MailService mailService, ActivityLogsService activityLogsService,
      FileUploadService fileUploadService) {
    this.threadTypeRepository = threadTypeRepository;
    this.threadTypeDetailsRepository = threadTypeDetailsRepository;
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
    this.mailService = mailService;
    this.activityLogsService = activityLogsService;
    this.fileUploadService = fileUploadService;
  }

  @Override
  public List<ThreadTypeDetailsDto> findAllThreadTypesDetails() {
    List<ThreadTypeDetails> details = threadTypeDetailsRepository.findAll();

    List<ThreadTypeDetailsDto> dto = new ArrayList<>();

    for(ThreadTypeDetails detail: details){
      ThreadType threadType = threadTypeRepository.findByTypeId(detail.getThreadType().getId());

      if(threadType == null){
        dto.add(ThreadTypeDetailsDto.buildFromEntity(detail));
      } else{
        dto.add(new ThreadTypeDetailsDto(threadType, detail));
      }
    }

    dto.sort(Comparator.comparing(ThreadTypeDetailsDto::getWidth).reversed());
    return dto;
  }

  @Override
  public List<ThreadTypeDetailsDto> findAllInCriticalStocks(String token) {
    validateIfAccountIsAdmin(getAccountFromToken(token));

    List<ThreadTypeDetails> details = threadTypeDetailsRepository.findAllDetailsInCriticalStocks();

    List<ThreadTypeDetailsDto> dto = new ArrayList<>();

    for(ThreadTypeDetails detail: details){
      ThreadType threadType = threadTypeRepository.findByTypeId(detail.getThreadType().getId());

      if(threadType == null){
        dto.add(ThreadTypeDetailsDto.buildFromEntity(detail));
      } else{
        dto.add(new ThreadTypeDetailsDto(threadType, detail));
      }
    }

    dto.sort(Comparator.comparing(ThreadTypeDetailsDto::getWidth).reversed());
    return dto;
  }

  @Override
  public void addStock(String token, AddStockForm form) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);

    ThreadTypeDetails details = threadTypeDetailsRepository.findById(form.getId()).orElseThrow(() -> new NotFoundException(TYPE_DETAILS_NOT_EXIST));
    details.setStocks(details.getStocks() + form.getStocks());

    threadTypeDetailsRepository.save(details);
  }

  @Override
  public ThreadTypeDetailsDto findThreadTypeDetails(String id) {
    ThreadTypeDetails details = threadTypeDetailsRepository.findById(id).orElseThrow(() -> new NotFoundException(TYPE_DETAILS_NOT_EXIST));
    ThreadType threadType = threadTypeRepository.findByTypeId(details.getThreadType().getId());

    if(threadType == null){
      return ThreadTypeDetailsDto.buildFromEntity(details);
    }

    return new ThreadTypeDetailsDto(threadType, details);
  }

  @Override
  public ThreadTypeDetailsDto addThreadTypeDetails(String token, ThreadTypeDetailsForm form) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);
    Validate.notNull(form);
    validateForm(form);

    ThreadType threadType = threadTypeRepository.findByTypeIgnoreCase(form.getThreadType());

    if(threadType == null){
      threadType = threadTypeRepository.findById(form.getThreadType()).orElseThrow(() -> new NotFoundException("Thread Type does not exists"));
    }

    ThreadTypeDetails details = new ThreadTypeDetails(form.getWidth(), form.getAspectRatio(), form.getDiameter(), form.getSidewall(), form.getPlyRating(),
        form.getStocks(), form.getPrice(), threadType);

    //mailService.sendEmailForThreadTypeDetails(MOSA_TIRE_SUPPLY_EMAIL, details, ADDED);
    threadTypeDetailsRepository.save(details);
    activityLogsService.threadTypeDetailsActivity(account, details, ADDED);

    return new ThreadTypeDetailsDto(threadType, details);
  }

  @Override
  public int addThreadTypeDetailsFromFile(String token, MultipartFile file) throws IOException {
    if(!FileUploadService.isFileValid(file)){
      throw new ValidationException("File uploaded not valid. Please upload a CSV or Excel file");
    }

    List<ThreadTypeDetails> details = fileUploadService.getThreadTypeDetailsFromFile(file.getInputStream());

    for (ThreadTypeDetails detail : details) {
      if (threadTypeDetailsRepository.existsByThreadType(detail.getThreadType())) {
        throw new ValidationException("There are already existing variants with the same Thread Type. " +
                "Please double check the contents.");
      }
    }

    this.threadTypeDetailsRepository.saveAll(details);

    return details.size();
  }

  @Override
  public ThreadTypeDetailsDto updateThreadTypeDetails(String token, String id,
      ThreadTypeDetailsForm form) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);
    Validate.notNull(form);
    validateForm(form);

    ThreadTypeDetails details = threadTypeDetailsRepository.findById(id).orElseThrow(() -> new NotFoundException("Thread Type Details does not exists"));
    ThreadType threadType = details.getThreadType();

    details.setWidth(form.getWidth());
    details.setAspectRatio(form.getAspectRatio());
    details.setDiameter(form.getDiameter());
    details.setSidewall(form.getSidewall());
    details.setPlyRating(form.getPlyRating());
    details.setPrice(form.getPrice());
    details.setThreadType(threadType);

    if(form.getStocks() != null){
      details.setStocks(form.getStocks());
    }

    //mailService.sendEmailForThreadTypeDetails(MOSA_TIRE_SUPPLY_EMAIL, details, UPDATED);
    threadTypeDetailsRepository.save(details);
    activityLogsService.threadTypeDetailsActivity(account, details, UPDATED);

    return new ThreadTypeDetailsDto(threadType, details);
  }

  @Override
  public void deleteThreadTypeDetails(String token, String id) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);

    ThreadTypeDetails details = threadTypeDetailsRepository.findById(id).orElseThrow(() ->
            new NotFoundException("Thread Type Details does not exists"));

    //mailService.sendEmailForThreadTypeDetails(MOSA_TIRE_SUPPLY_EMAIL, details, DELETED);
    threadTypeDetailsRepository.delete(details);
    activityLogsService.threadTypeDetailsActivity(account, details, DELETED);
  }

  private void validateForm(ThreadTypeDetailsForm form){
    if(form.getPrice() < 0){
      throw new ValidationException("Price must be greater than 0");
    }

    if(form.getStocks() != null && form.getStocks() < 0){
      throw new ValidationException("Stock quantity must be greater than or equal to 0");
    }
  }

  private Account getAccountFromToken(String token){
    String id = jwtGenerator.getUserFromJWT(token);

    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account does not exists"));
  }

  private void validateIfAccountIsAdmin(Account account){
    if(account.getUserRole() == UserRole.CUSTOMER){
      throw new ValidationException("Only Administrators and staff can use this feature");

    }
  }
}
