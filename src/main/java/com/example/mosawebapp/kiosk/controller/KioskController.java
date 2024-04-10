package com.example.mosawebapp.kiosk.controller;

import com.example.mosawebapp.api_response.ApiResponse;
import com.example.mosawebapp.cart.dto.CheckoutForm;
import com.example.mosawebapp.cart.dto.OrderForm;
import com.example.mosawebapp.exceptions.TokenException;
import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import com.example.mosawebapp.kiosk.dto.KioskQuantityForm;
import com.example.mosawebapp.kiosk.service.KioskService;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.security.domain.TokenBlacklistingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kiosk")
@CrossOrigin(origins = "*")
public class KioskController {
  private static final String BEARER = "Bearer ";
  private static final String TOKEN_INVALID = "Token Invalid/Expired";
  private final TokenBlacklistingService tokenBlacklistingService;
  private final JwtGenerator jwtGenerator;
  private final KioskService kioskService;

  public KioskController(TokenBlacklistingService tokenBlacklistingService,
      JwtGenerator jwtGenerator, KioskService kioskService) {
    this.tokenBlacklistingService = tokenBlacklistingService;
    this.jwtGenerator = jwtGenerator;
    this.kioskService = kioskService;
  }

  @GetMapping(value = "/startOrder")
  public ResponseEntity<?> startOrder(){
    return kioskService.startKioskOrder();
  }

  @GetMapping(value = "/getAllOrders")
  public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(kioskService.getAllKioskOrders(token));
  }

  @GetMapping(value = "/getCompletedOrders")
  public ResponseEntity<?> getCompletedOrders(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(kioskService.getCompletedOrders(token));
  }

  @GetMapping(value = "/getProcessingOrders")
  public ResponseEntity<?> getProcessingOrders(@RequestHeader("Authorization") String header){
    String token = header.replace(BEARER, "");

    validateTokenValidity(token);

    return ResponseEntity.ok(kioskService.getProcessingOrders(token));
  }

  @GetMapping(value = "/setAsCompleted/{kioskToken}")
  public ResponseEntity<?> setOrderAsCompleted(@PathVariable("kioskToken") String kioskToken){
    kioskService.setAsComplete(kioskToken);
    
    return ResponseEntity.ok(new ApiResponse("Order set as completed", HttpStatus.OK));
  }

  @GetMapping(value = "/getAllCurrentOrders/{kioskToken}")
  public ResponseEntity<?> getAllCurrentOrders(@PathVariable("kioskToken") String kioskToken){
    return ResponseEntity.ok(kioskService.getAllCurrentKioskOrders(kioskToken));
  }

  @PostMapping(value = "/addOrder/{kioskToken}")
  public ResponseEntity<?> addKioskOrder(@PathVariable("kioskToken") String kioskToken, @RequestBody
      OrderForm form){
    return ResponseEntity.ok(new KioskDto(kioskService.addKioskOrder(kioskToken, form, "add_order")));
  }

  @GetMapping(value = "/addOrderQuantity/")
  public ResponseEntity<?> addKioskOrderQuantity(@ModelAttribute KioskQuantityForm form){
    return ResponseEntity.ok(kioskService.addKioskOrderQuantity(form.getKioskToken(), form.getOrderId()));
  }

  @DeleteMapping(value = "/removeOrder/")
  public ResponseEntity<?> removeKioskOrder(@ModelAttribute KioskQuantityForm form){
    kioskService.removeKioskOrder(form.getKioskToken(), form.getOrderId());

    return ResponseEntity.ok(new ApiResponse("Order successfully removed", HttpStatus.OK));
  }

  @GetMapping(value = "/subtractOrderQuantity/")
  public ResponseEntity<?> subtractKioskOrderQuantity(@ModelAttribute KioskQuantityForm form){
    return ResponseEntity.ok(kioskService.subtractCartOrderQuantity(form.getKioskToken(), form.getOrderId()));
  }

  @PostMapping(value = "/checkout/{kioskToken}")
  public ResponseEntity<?> checkout(@PathVariable("kioskToken") String kioskToken, @RequestBody CheckoutForm form){
    return ResponseEntity.ok(kioskService.checkout(kioskToken, form, "checkout"));
  }

  @PostMapping(value = "/orderNow/{kioskToken}")
  public ResponseEntity<?> orderNow(@PathVariable("kioskToken") String kioskToken, @RequestBody OrderForm form){
    return ResponseEntity.ok(kioskService.orderNow(kioskToken, form));
  }

  private void validateTokenValidity(String token){
    if(!jwtGenerator.isTokenValid(token) || token.isEmpty() || tokenBlacklistingService.isTokenBlacklisted(token)){
      throw new TokenException(TOKEN_INVALID);
    }
  }
}
