package com.example.mosawebapp.onsite_order.service;

import com.example.mosawebapp.cart.dto.CheckoutForm;
import com.example.mosawebapp.cart.dto.OrderForm;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderCheckoutDto;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderDto;
import java.util.List;

public interface OnsiteOrderService {
  void startOrder(String token);
  List<OnsiteOrderDto> getAllOrders(String token);
  List<OnsiteOrderDto> getAllCurrentOrders(String token);
  OnsiteOrderDto getOnsiteOrder(String token, String orderId);
  OnsiteOrderDto addOrder(String token, OrderForm form);
  OnsiteOrderDto addOrderQuantity(String token, String orderId);
  void removeOrder(String token, String orderId);
  OnsiteOrderDto subtractOrderQuantity(String token, String orderId);
  OnsiteOrderCheckoutDto checkout(String token, CheckoutForm form);
  //OnsiteOrderCheckoutDto orderNow(String token, OrderForm form);
  void cancelCheckout(String token, CheckoutForm form);
}
