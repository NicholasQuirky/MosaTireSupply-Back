package com.example.mosawebapp.product.threadtypedetails.controller;

import com.example.mosawebapp.api_response.ApiObjectResponse;
import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.product.threadtypedetails.dto.AddStockForm;
import com.example.mosawebapp.product.threadtypedetails.dto.DetailsBulkDeleteForm;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsForm;
import com.example.mosawebapp.product.threadtypedetails.service.ThreadTypeDetailsService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import java.io.IOException;
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
@RequestMapping("/api/threadTypeDetails")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class ThreadTypeDetailsController {
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;
  private final ThreadTypeDetailsService threadTypeDetailsService;

  public ThreadTypeDetailsController(TokenBlacklistingService tokenBlacklistingService,
      JwtGenerator jwtGenerator, ThreadTypeDetailsService threadTypeDetailsService) {
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
    this.threadTypeDetailsService = threadTypeDetailsService;
  }

  @GetMapping(value = "/getAllDetails")
  public ResponseEntity<?> getAllThreadTypesDetails(){
    return ResponseEntity.ok(threadTypeDetailsService.findAllThreadTypesDetails());
  }

  @GetMapping(value = "/getDetails/{id}")
  public ResponseEntity<?> getThreadTypeDetails(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(threadTypeDetailsService.findThreadTypeDetails(id));
  }

  @GetMapping(value = "/getCriticalStocks")
  public ResponseEntity<?> getDetailsInCriticalStocks(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(threadTypeDetailsService.findAllInCriticalStocks(token));
  }

  @PostMapping(value = "/addDetails")
  public ResponseEntity<?> addThreadTypeDetails(@RequestHeader("Authorization") String header, @RequestBody
  ThreadTypeDetailsForm form){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    ThreadTypeDetailsDto dto = threadTypeDetailsService.addThreadTypeDetails(token, form);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, "Thread Type Details for " + dto.getThreadType() + " created" , dto));
  }

  @PostMapping(value = "/addFileDetails", consumes = {"application/json", "multipart/form-data"})
  public ResponseEntity<?> addThreadTypeDetailsFromFile(@RequestHeader("Authorization") String header, @RequestParam("file") MultipartFile file)
      throws IOException {
    String token = header.replace(BEARER, "");
    validateTokenValidity(token);

    int addCount = threadTypeDetailsService.addThreadTypeDetailsFromFile(token, file);

    return ResponseEntity.ok(new ApiResponse("Added " + addCount + " Thread Type Details successfully", HttpStatus.CREATED));
  }

  @PostMapping(value = "/bulkDelete")
  public ResponseEntity<?> bulkDelete(@RequestHeader("Authorization") String header, @RequestBody
      DetailsBulkDeleteForm form){
    String token = header.replace(BEARER, "");
    validateTokenValidity(token);

    for(String id: form.getIds()){
      threadTypeDetailsService.deleteThreadTypeDetails(token, id);
    }

    return ResponseEntity.ok(new ApiResponse("Details deleted successfully", HttpStatus.OK));
  }

  @PutMapping(value = "/addStock")
  public ResponseEntity<?> addStock(@RequestHeader("Authorization") String header, @RequestBody
      AddStockForm form){
    String token = header.replace(BEARER, "");
    validateTokenValidity(token);

    threadTypeDetailsService.addStock(token,form);

    return ResponseEntity.ok(new ApiResponse("Stock added to the item", HttpStatus.OK));
  }
  @PutMapping(value = "/updateDetails/{id}")
  public ResponseEntity<?> updateBrand(@RequestHeader("Authorization") String header, @RequestBody
    ThreadTypeDetailsForm form, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    ThreadTypeDetailsDto dto = threadTypeDetailsService.updateThreadTypeDetails(token, id, form);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Thread Type Details " + dto.getThreadType() + " updated" , dto));
  }

  @DeleteMapping(value = "/deleteDetails/{id}")
  public ResponseEntity<?> updateBrand(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    threadTypeDetailsService.deleteThreadTypeDetails(token,id);

    return ResponseEntity.ok(new ApiResponse("Thread Type Details Deleted", HttpStatus.OK));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
