package com.example.mosawebapp.product.threadtype.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.brand.domain.BrandRepository;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDto;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeForm;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeSearch;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetailsRepository;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ThreadTypeServiceImpl implements ThreadTypeService{
  private static final String TYPE_NOT_EXIST = "Thread Type does not exists";
  private static final String ADDED = "Added";
  private static final String UPDATED = "Updated";
  private static final String DELETED = "Deleted";
  @Value("${mosatiresupply.official.email}")
  private String MOSA_TIRE_SUPPLY_EMAIL;
  @Value("${default.blank.image.cdn}")
  private String BLANK_IMAGE;
  private final BrandRepository brandRepository;
  private final ThreadTypeRepository threadTypeRepository;
  private final ThreadTypeDetailsRepository threadTypeDetailsRepository;
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  private final MailService mailService;
  private final ActivityLogsService activityLogsService;

  @Autowired
  public ThreadTypeServiceImpl(BrandRepository brandRepository,
      ThreadTypeRepository threadTypeRepository,
      ThreadTypeDetailsRepository threadTypeDetailsRepository, AccountRepository accountRepository,
      JwtGenerator jwtGenerator, MailService mailService, ActivityLogsService activityLogsService) {
    this.brandRepository = brandRepository;
    this.threadTypeRepository = threadTypeRepository;
    this.threadTypeDetailsRepository = threadTypeDetailsRepository;
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
    this.mailService = mailService;
    this.activityLogsService = activityLogsService;
  }

  @Override
  public ThreadTypeDto findThreadType(String id) {
    ThreadType threadType = threadTypeRepository.findById(id).orElseThrow(() -> new NotFoundException(TYPE_NOT_EXIST));
    List<ThreadTypeDetails> details = threadTypeDetailsRepository.findByThreadType(threadType);

    if(details.isEmpty()){
      return new ThreadTypeDto(threadType);
    }

    return new ThreadTypeDto(threadType, details);
  }

  @Override
  public List<ThreadTypeDto> findAllThreadTypes(String brand) {
    List<ThreadType> types;

    if(brand.isEmpty() || brand.equalsIgnoreCase("\"\"")){
      types = threadTypeRepository.findAll();
    } else{
      Brand filterBrand = brandRepository.findByNameIgnoreCase(brand);
      types = threadTypeRepository.findByBrand(filterBrand);
    }

    List<ThreadTypeDto> dtos = new ArrayList<>();
    for(ThreadType type: types){
      List<ThreadTypeDetails> details = threadTypeDetailsRepository.findByThreadType(type);

      if(details.isEmpty()){
        dtos.add(new ThreadTypeDto(type));
      } else {
        details.sort(Comparator.comparing(ThreadTypeDetails::getPrice));

        dtos.add(new ThreadTypeDto(type, details));
      }
    }

    return dtos;
  }

  @Override
  public List<ThreadTypeDto> addThreadType(String token, List<ThreadTypeForm> forms) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);
    Validate.notNull(forms);

    List<ThreadType> types = new ArrayList<>();

    for(ThreadTypeForm form: forms){
      if(form.getImageUrl().isEmpty()){
        form.setImageUrl(BLANK_IMAGE);
      }

      if(form.getDescription().isEmpty() || form.getDescription() == null){
        form.setDescription("No description for this item yet");
      }

      Brand brand = brandRepository.findByNameIgnoreCase(form.getBrand());

      if(brand == null){
        brand = brandRepository.findById(form.getBrand()).orElseThrow(() -> new NotFoundException(form.getBrand() + " Brand does not exists"));
      }

      types.add(new ThreadType(form.getType(), form.getImageUrl(), form.getDescription(), brand));
    }

    threadTypeRepository.saveAll(types);
    activityLogsService.threadTypeActivity(account, types, ADDED);
    return ThreadTypeDto.buildFromEntitiesV2(types);
  }

  @Override
  public ThreadTypeDto updateThreadType(String token, String id, ThreadTypeForm form) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);
    Validate.notNull(form);

    if(form.getImageUrl().isEmpty()){
      form.setImageUrl(BLANK_IMAGE);
    }

    if(form.getDescription().isEmpty() || form.getDescription() == null){
      form.setDescription("No description for this item yet");
    }

    ThreadType threadType = threadTypeRepository.findById(id).orElseThrow(() -> new NotFoundException("Thread type does not exists"));
    Brand brand = brandRepository.findByNameIgnoreCase(form.getBrand());

    if(brand == null){
      brand = brandRepository.findById(form.getBrand()).orElseThrow(() -> new NotFoundException("Brand does not exists"));
    }

    threadType.setBrand(brand);
    threadType.setType(form.getType());
    threadType.setImageUrl(form.getImageUrl());
    threadType.setDescription(form.getDescription());

    mailService.sendEmailForThreadType(MOSA_TIRE_SUPPLY_EMAIL, brand, threadType, UPDATED);
    threadTypeRepository.save(threadType);
    activityLogsService.threadTypeActivity(account, List.of(threadType), UPDATED);

    List<ThreadTypeDetails> details = threadTypeDetailsRepository.findByThreadType(threadType);

    return new ThreadTypeDto(threadType, details);
  }

  @Override
  public List<ThreadTypeDto> searchThreadType(String search) {
    Specification<ThreadType> specs = ThreadTypeSpecs.searchThreadType(search);
    List<ThreadType> threadTypes = threadTypeRepository.findAll(specs);
    List<ThreadTypeDto> dtos = new ArrayList<>();

    for(ThreadType type: threadTypes){
      List<ThreadTypeDetails> details = threadTypeDetailsRepository.findByThreadType(type);

      if(details.isEmpty()){
        dtos.add(new ThreadTypeDto(type));
      } else {
        dtos.add(new ThreadTypeDto(type, details));
      }
    }

    dtos.sort(Comparator.comparing(ThreadTypeDto::getDateCreated).reversed());

    return dtos;
  }

  @Override
  public void deleteThreadType(String token, String id) {
    Account account = getAccountFromToken(token);
    validateIfAccountIsAdmin(account);

    ThreadType threadType = threadTypeRepository.findById(id).orElseThrow(() -> new NotFoundException("Thread type does not exists"));

    threadTypeDetailsRepository.deleteByThreadType(threadType.getId());
    mailService.sendEmailForThreadType(MOSA_TIRE_SUPPLY_EMAIL, threadType.getBrand(),threadType, DELETED);
    activityLogsService.threadTypeActivity(account, List.of(threadType), DELETED);
    threadTypeRepository.delete(threadType);
  }

  private Account getAccountFromToken(String token){
    String id = jwtGenerator.getUserFromJWT(token);

    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account does not exists"));
  }

  private void validateIfAccountIsAdmin(Account account){
    if(account.getUserRole() != UserRole.ADMINISTRATOR){
      throw new ValidationException("Only Administrators can use this feature");

    }
  }
}
