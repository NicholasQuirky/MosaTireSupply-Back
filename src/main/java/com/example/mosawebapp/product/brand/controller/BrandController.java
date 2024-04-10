package com.example.mosawebapp.product.brand.controller;

import com.example.mosawebapp.api_response.ApiObjectResponse;
import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.product.brand.dto.BrandDto;
import com.example.mosawebapp.product.brand.dto.BrandForm;
import com.example.mosawebapp.product.brand.service.BrandService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/brand")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class BrandController {
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private static final String VALID = "valid";
  private static final String NOT_VALID = "not valid";
  private static final String NUMERIC_INPUTS_ONLY = "Numeric Inputs Only";
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;
  private final BrandService brandService;

  @Autowired
  public BrandController(TokenBlacklistingService tokenBlacklistingService,
      JwtGenerator jwtGenerator, BrandService brandService) {
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
    this.brandService = brandService;
  }

  @GetMapping(value = "/getAllBrands")
  public ResponseEntity<?> getAllBrands(){
      return ResponseEntity.ok(brandService.findAllBrands());
  }

@GetMapping(value = "/getBrand/{id}")
  public ResponseEntity<?> getBrand(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(brandService.findBrand(id));
}

  @PostMapping(value = "/addBrand")
  public ResponseEntity<?> addBrand(@RequestHeader("Authorization") String header, @RequestBody
    List<BrandForm> form){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    List<BrandDto> dtos = BrandDto.buildFromEntities(brandService.addBrand(token,form));

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, form.size() + " Brand/s added" , dtos));
  }

  @PostMapping(value = "/addBrands")
  public ResponseEntity<?> addBrands(@RequestHeader("Authorization") String header, @RequestParam("file") MultipartFile file)
      throws IOException {
    String token = header.replace(BEARER, "");
    validateTokenValidity(token);

    int addCount = brandService.addBrands(token, file);

    return ResponseEntity.ok(new ApiResponse("Added " + addCount + " brands successfully", HttpStatus.CREATED));
  }

  @CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
  @PutMapping(value = "/updateBrand/{id}")
  public ResponseEntity<?> updateBrand(@RequestHeader("Authorization") String header, @RequestBody
  BrandForm form, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    BrandDto dto = BrandDto.buildFromEntity(brandService.updateBrand(token,id,form));

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Brand " + dto.getName() + " updated" , dto));
  }

  @DeleteMapping(value = "/deleteBrand/{id}")
  public ResponseEntity<?> updateBrand(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    brandService.deleteBrand(token,id);

    return ResponseEntity.ok(new ApiResponse("Brand Deleted", HttpStatus.OK));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
