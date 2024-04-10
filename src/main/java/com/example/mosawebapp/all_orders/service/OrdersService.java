package com.example.mosawebapp.all_orders.service;

import com.example.mosawebapp.all_orders.dto.OrdersDto;
import java.util.List;

public interface OrdersService {
  List<OrdersDto> getAllOrders(String token);

  String verifyOrder(String token, String orderId);
  String forPickup(String token, String orderId);

  String completeOrder(String token, String orderId);

  String orderNotVerified(String token, String orderId);
}
