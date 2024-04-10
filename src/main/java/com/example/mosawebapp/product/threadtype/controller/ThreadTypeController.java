package com.example.mosawebapp.product.threadtype.controller;

import com.example.mosawebapp.api_response.ApiObjectResponse;
import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDto;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeForm;
import com.example.mosawebapp.product.threadtype.service.ThreadTypeService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/threadType")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class ThreadTypeController {
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private static final String VALID = "valid";
  private static final String NOT_VALID = "not valid";
  private static final String NUMERIC_INPUTS_ONLY = "Numeric Inputs Only";
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;
  private final ThreadTypeService threadTypeService;

  @Autowired
  public ThreadTypeController(TokenBlacklistingService tokenBlacklistingService,
      JwtGenerator jwtGenerator, ThreadTypeService threadTypeService) {
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
    this.threadTypeService = threadTypeService;
  }

  @GetMapping(value = "/getAllThreadTypes/{brand}")
  public ResponseEntity<?> getAllThreadTypes(@PathVariable("brand") String brand){
    return ResponseEntity.ok(threadTypeService.findAllThreadTypes(brand));
  }

  @GetMapping(value = "/getThreadType/{id}")
  public ResponseEntity<?> getBrand(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(threadTypeService.findThreadType(id));
  }

  @GetMapping(value = "/searchThreadType/{search}")
  public ResponseEntity<?> searchThreadType(@RequestHeader("Authorization") String header, @PathVariable("search") String search){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(threadTypeService.searchThreadType(search));
  }
  @PostMapping(value = "/addThreadType")
  public ResponseEntity<?> addBrand(@RequestHeader("Authorization") String header, @RequestBody
  List<ThreadTypeForm> forms){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    List<ThreadTypeDto> dtos = threadTypeService.addThreadType(token,forms);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.CREATED, forms.size()+ " Thread Type/s added" , dtos));
  }

  @PutMapping(value = "/updateThreadType/{id}")
  public ResponseEntity<?> updateBrand(@RequestHeader("Authorization") String header, @RequestBody
  ThreadTypeForm form, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    ThreadTypeDto dto = threadTypeService.updateThreadType(token, id, form);

    return ResponseEntity.ok(new ApiObjectResponse(HttpStatus.OK, "Thread Type " + dto.getType() + " updated" , dto));
  }

  @DeleteMapping(value = "/deleteThreadType/{id}")
  public ResponseEntity<?> updateBrand(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    threadTypeService.deleteThreadType(token,id);

    return ResponseEntity.ok(new ApiResponse("Thread Type Deleted", HttpStatus.OK));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
