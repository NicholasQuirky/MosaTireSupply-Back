package com.example.mosawebapp.all_orders.controller;

import com.example.mosawebapp.all_orders.service.OrdersService;
import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class OrdersController {
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final OrdersService ordersService;
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;
  public OrdersController(OrdersService ordersService,
      TokenBlacklistingService tokenBlacklistingService, JwtGenerator jwtGenerator) {
    this.ordersService = ordersService;
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
  }

  @GetMapping(value = "/getAllOrders")
  public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(ordersService.getAllOrders(token));
  }

  @GetMapping(value = "/verify/{id}")
  public ResponseEntity<?> setAsVerified(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    String refNo = ordersService.verifyOrder(token,id);
    return ResponseEntity.ok(new ApiResponse("Order with reference number " + refNo + " was verified", HttpStatus.OK));
  }

  @GetMapping(value = "/forPickup/{id}")
  public ResponseEntity<?> setAsForPickup(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    String refNo = ordersService.forPickup(token,id);
    return ResponseEntity.ok(new ApiResponse("Order with reference number " + refNo + " is ready for pick up", HttpStatus.OK));
  }

  @GetMapping(value = "/completeOrder/{id}")
  public ResponseEntity<?> setAsOrderCompleted(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    String refNo = ordersService.completeOrder(token,id);
    return ResponseEntity.ok(new ApiResponse("Order with reference number " + refNo + " was delivered", HttpStatus.OK));
  }

  @GetMapping(value = "/invalidOrder/{id}")
  public ResponseEntity<?> setAsInvalid(@RequestHeader("Authorization") String header, @PathVariable("id") String id){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    String refNo = ordersService.orderNotVerified(token,id);
    return ResponseEntity.ok(new ApiResponse("Order with reference number " + refNo + " was not verified", HttpStatus.OK));
  }
  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
