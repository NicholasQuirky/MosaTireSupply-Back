package com.example.mosawebapp.kiosk.service;

import com.example.mosawebapp.cart.dto.CheckoutForm;
import com.example.mosawebapp.cart.dto.OrderForm;
import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.kiosk.dto.KioskCheckoutDto;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface KioskService {
  ResponseEntity<?> startKioskOrder();
  List<KioskDto> getAllKioskOrders(String adminToken);
  List<KioskDto> getAllCurrentKioskOrders(String kioskToken);
  Kiosk addKioskOrder(String kioskToken, OrderForm form, String action);
  KioskDto addKioskOrderQuantity(String kioskToken, String kioskId);
  void removeKioskOrder(String kioskToken, String kioskId);
  KioskDto subtractCartOrderQuantity(String kioskToken, String kioskId);
  KioskCheckoutDto checkout(String kioskToken, CheckoutForm form, String action);
  KioskCheckoutDto orderNow(String kioskToken, OrderForm form);
  void cancelCheckout(String kioskToken, CheckoutForm form);
  void setAsComplete(String kioskToken);
  List<KioskDto> getCompletedOrders(String adminToken);
  List<KioskDto> getProcessingOrders(String adminToken);
}
