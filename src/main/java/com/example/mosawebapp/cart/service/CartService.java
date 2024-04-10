package com.example.mosawebapp.cart.service;

import com.example.mosawebapp.cart.dto.CartCheckoutDto;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartDtoV2;
import com.example.mosawebapp.cart.dto.OrderForm;
import com.example.mosawebapp.cart.dto.CheckoutForm;
import com.example.mosawebapp.cart.dto.ReferenceNumberForm;
import java.util.List;

public interface CartService {
  List<CartDto> getAllCartOrders(String token);
  CartDtoV2 getAllCurrentUserOrders(String token);

  List<CartDto> getAllUserCurrentOrders(String token);

  CartDto getCartOrder(String token, String cartId);
  CartDto addCartOrder(String token, OrderForm form, String action);
  CartDto addCartOrderQuantity(String token, String cartId);
  void removeCartOrder(String token, String cartId);
  CartDto subtractCartOrderQuantity(String token, String cartId);
  CartCheckoutDto checkout(String token, CheckoutForm form);

  CartCheckoutDto orderNow(String token, OrderForm form);

  void cancelCheckout(String token, CheckoutForm form);
  void pay(String token, ReferenceNumberForm form);
}
