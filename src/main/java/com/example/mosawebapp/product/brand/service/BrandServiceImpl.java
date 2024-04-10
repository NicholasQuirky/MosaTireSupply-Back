package com.example.mosawebapp.product.brand.service;


import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.file_upload_service.FileUploadService;
import com.example.mosawebapp.logs.service.ActivityLogsService;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.brand.domain.BrandRepository;
import com.example.mosawebapp.product.brand.dto.BrandDto;
import com.example.mosawebapp.product.brand.dto.BrandForm;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDtoV2;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetailsRepository;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
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
public class BrandServiceImpl implements BrandService {
  private static final String BRAND_NOT_EXIST = "Brand does not exists";
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
  private final JwtGenerator jwtGenerator;
  private final AccountRepository accountRepository;
  private final MailService mailService;
  private final ActivityLogsService activityLogsService;

  @Autowired
  public BrandServiceImpl(BrandRepository brandRepository,
      ThreadTypeRepository threadTypeRepository,
      ThreadTypeDetailsRepository threadTypeDetailsRepository, JwtGenerator jwtGenerator,
      AccountRepository accountRepository, MailService mailService,
      ActivityLogsService activityLogsService) {
    this.brandRepository = brandRepository;
    this.threadTypeRepository = threadTypeRepository;
    this.threadTypeDetailsRepository = threadTypeDetailsRepository;
    this.jwtGenerator = jwtGenerator;
    this.accountRepository = accountRepository;
    this.mailService = mailService;
    this.activityLogsService = activityLogsService;
  }


  @Override
  public List<BrandDto> findAllBrands() {
    List<BrandDto> dtos = new ArrayList<>();

    List<Brand> brands = brandRepository.findAll();

    for(Brand brand: brands){
      List<ThreadType> types = threadTypeRepository.findByBrand(brand);
      BrandDto dto = new BrandDto(brand, ThreadTypeDtoV2.buildFromEntities(types));

      dtos.add(dto);
    }

    dtos.sort(Comparator.comparing(BrandDto::getName).reversed());
    return dtos;
  }

  @Override
  public BrandDto findBrand(String id) {
    Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException(BRAND_NOT_EXIST));
    List<ThreadType> types = threadTypeRepository.findByBrand(brand);

    return new BrandDto(brand, ThreadTypeDtoV2.buildFromEntities(types));
  }

  @Override
  public List<Brand> addBrand(String token, List<BrandForm> forms) {
    Account account = getAccountFromToken(token);

    validateIfAccountIsAdmin(account);
    Validate.notNull(forms);

    List<Brand> brands = new ArrayList<>();
    for(BrandForm form: forms){
      if(form.getImageUrl().isEmpty()){
        form.setImageUrl(BLANK_IMAGE);
      }

      brands.add(new Brand(form.getBrandName(), form.getImageUrl()));
    }

    brandRepository.saveAll(brands);
    activityLogsService.brandActivity(account, brands, ADDED);

    return brands;
  }

  @Override
  public int addBrands(String token, MultipartFile file) throws IOException {
    if(!FileUploadService.isFileValid(file)){
      throw new ValidationException("File uploaded not valid. Please upload a CSV or Excel file");
    }

    List<Brand> brands = FileUploadService.getBrandsFromFile(file.getInputStream());
    this.brandRepository.saveAll(brands);

    return brands.size();
  }

  @Override
  public Brand updateBrand(String token, String id, BrandForm form) {
    Account account = getAccountFromToken(token);

    validateIfAccountIsAdmin(account);
    Validate.notNull(form);

    Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException(BRAND_NOT_EXIST));
    brand.setName(form.getBrandName());
    brand.setImageUrl(form.getImageUrl());

    brandRepository.save(brand);
    mailService.sendEmailForBrand(MOSA_TIRE_SUPPLY_EMAIL, brand,UPDATED);
    activityLogsService.brandActivity(account, List.of(brand), UPDATED);
    return brand;
  }

  @Override
  public void deleteBrand(String token, String id) {
    Account account = getAccountFromToken(token);

    validateIfAccountIsAdmin(account);

    Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException(BRAND_NOT_EXIST));
    List<ThreadType> threadTypes = threadTypeRepository.findByBrand(brand);

    threadTypeRepository.deleteByBrand(brand.getId());
    for(ThreadType type: threadTypes){
      threadTypeDetailsRepository.deleteByThreadType(type.getId());
    }

    mailService.sendEmailForBrand(MOSA_TIRE_SUPPLY_EMAIL, brand, DELETED);
    activityLogsService.brandActivity(account, List.of(brand), DELETED);
    brandRepository.delete(brand);
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
