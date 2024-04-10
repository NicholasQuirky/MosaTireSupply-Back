package com.example.mosawebapp.onsite_order.controller;

import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.cart.dto.CheckoutForm;
import com.example.mosawebapp.cart.dto.OrderForm;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.onsite_order.service.OnsiteOrderService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onsiteOrder")
@CrossOrigin(origins = "*")
public class OnsiteOrderController {
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;
  private final OnsiteOrderService onsiteOrderService;

  @Autowired
  public OnsiteOrderController(TokenBlacklistingService tokenBlacklistingService,
      JwtGenerator jwtGenerator, OnsiteOrderService onsiteOrderService) {
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
    this.onsiteOrderService = onsiteOrderService;
  }

  @GetMapping(value = "/startOrder")
  public ResponseEntity<?> startOrder(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(new ApiResponse("Successfully started an order", HttpStatus.OK));
  }

  @GetMapping(value = "/getAllOrders")
  public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(onsiteOrderService.getAllOrders(token));
  }

  @GetMapping(value = "/getAllCurrentOrders")
  public ResponseEntity<?> getAllCurrentOrders(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(onsiteOrderService.getAllCurrentOrders(token));
  }

  @PostMapping(value = "/addOrder")
  public ResponseEntity<?> addOrder(@RequestHeader("Authorization") String header, @RequestBody
      OrderForm form){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(onsiteOrderService.addOrder(token, form));
  }

  @GetMapping(value = "/addOrderQuantity/{orderId}")
  public ResponseEntity<?> addOrderQuantity(@RequestHeader("Authorization") String header, @PathVariable("orderId") String orderId){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(onsiteOrderService.addOrderQuantity(token, orderId));
  }

  @DeleteMapping(value = "/removeOrder/{orderId}")
  public ResponseEntity<?> removeOrder(@RequestHeader("Authorization") String header, @PathVariable("orderId") String orderId){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    onsiteOrderService.removeOrder(token, orderId);

    return ResponseEntity.ok(new ApiResponse("Successfully removed order", HttpStatus.OK));
  }

  @GetMapping(value = "/subtractOrderQuantity/{orderId}")
  public ResponseEntity<?> subtractOrderQuantity(@RequestHeader("Authorization") String header, @PathVariable("orderId") String orderId){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(onsiteOrderService.subtractOrderQuantity(token, orderId));
  }

  @PostMapping(value = "/checkout")
  public ResponseEntity<?> checkout(@RequestHeader("Authorization") String header, @RequestBody
  CheckoutForm form){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(onsiteOrderService.checkout(token, form));
  }

  @PostMapping(value = "/cancelCheckout")
  public ResponseEntity<?> cancelCheckout(@RequestHeader("Authorization") String header, @RequestBody
  CheckoutForm form){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);
    onsiteOrderService.cancelCheckout(token, form);

    return ResponseEntity.ok(new ApiResponse("Successfully cancelled checkouts", HttpStatus.OK));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
