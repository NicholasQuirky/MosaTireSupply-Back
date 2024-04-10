package com.example.mosawebapp.cart.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.all_orders.domain.OrderStatus;
import com.example.mosawebapp.all_orders.domain.OrderType;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.all_orders.domain.OrdersRepository;
import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.cart.domain.CartRepository;
import com.example.mosawebapp.cart.dto.CartCheckoutDto;
import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.cart.dto.CartDtoV2;
import com.example.mosawebapp.cart.dto.OrderForm;
import com.example.mosawebapp.cart.dto.CheckoutForm;
import com.example.mosawebapp.cart.dto.ReferenceNumberForm;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.mail.MailService;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetailsRepository;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.utils.DateTimeFormatter;
import com.example.mosawebapp.validate.Validate;

import java.util.*;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService{
  private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
  private static final String CART_ORDER_NOT_EXISTS = "Cart Order id does not exists or belong to the user";
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  private final ThreadTypeRepository threadTypeRepository;
  private final ThreadTypeDetailsRepository threadTypeDetailsRepository;
  private final CartRepository cartRepository;
  private final OrdersRepository ordersRepository;
  private final MailService mailService;

  @Autowired
  public CartServiceImpl(AccountRepository accountRepository, JwtGenerator jwtGenerator,
      ThreadTypeRepository threadTypeRepository,
      ThreadTypeDetailsRepository threadTypeDetailsRepository, CartRepository cartRepository,
      OrdersRepository ordersRepository, MailService mailService) {
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
    this.threadTypeRepository = threadTypeRepository;
    this.threadTypeDetailsRepository = threadTypeDetailsRepository;
    this.cartRepository = cartRepository;
    this.ordersRepository = ordersRepository;
    this.mailService = mailService;
  }

  @Override
  public List<CartDto> getAllCartOrders(String token) {
    logger.info("getting all cart orders");
    validateIfAccountIsNotCustomerOrContentManager(token);

    List<Cart> carts = cartRepository.findAll();
    List<CartDto> dto = new ArrayList<>();

    for(Cart cart: carts){
      OrderStatus status = determineOrderStatus(cart.getId());
      dto.add(new CartDto(cart, status));
    }

    dto.sort(Comparator.comparing(CartDto::getDateCreated).reversed());
    return dto;
  }

  @Override
  public CartDtoV2 getAllCurrentUserOrders(String token){
    logger.info("getting current user's orders");

    Account account = getAccountFromToken(token);

    List<Cart> carts = cartRepository.findByAccountAndIsPaid(account.getId());

    if(carts.isEmpty()){
      return null;
    }

    List<CartDto> dto = new ArrayList<>();

    for(Cart cart: carts){
      OrderStatus status = determineOrderStatus(cart.getId());
      dto.add(new CartDto(cart, status));
    }

    dto.sort(Comparator.comparing(CartDto::getDateCreated).reversed());
    return new CartDtoV2(dto);
  }

  @Override
  public List<CartDto> getAllUserCurrentOrders(String token){
    logger.info("getting user's current orders");

    Account account = getAccountFromToken(token);

    List<Cart> carts = cartRepository.findByAccountAndIsNotPaid(account.getId());

    if(carts.isEmpty()){
      return Collections.emptyList();
    }

    List<CartDto> dto = new ArrayList<>();
    for(Cart cart: carts){
      OrderStatus status = determineOrderStatus(cart.getId());
      dto.add(new CartDto(cart, status));
    }

    dto.sort(Comparator.comparing(CartDto::getDateCreated).reversed());
    return dto;
  }

  @Override
  public CartDto getCartOrder(String token, String cartId) {
    logger.info("getting order {}", cartId);
    validateIfAccountIsNotCustomerOrContentManager(token);

    Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new NotFoundException("Cart Order does not exists"));
    OrderStatus status = determineOrderStatus(cart.getId());

    return new CartDto(cart, status);
  }

  private OrderStatus determineOrderStatus(String cartId){
    String orderStatus = ordersRepository.findOrderByCartId(cartId);

    if(orderStatus == null){
      return OrderStatus.NOT_YET_ORDERED;
    }

    return OrderStatus.valueOf(orderStatus);
  }

  @Override
  public CartCheckoutDto orderNow(String token, OrderForm form) {
    Validate.notNull(form);

    CartDto cart = addCartOrder(token, form, "order_now");

    return new CartCheckoutDto(Collections.singletonList(cart));
  }

  @Override
  public CartDto addCartOrder(String token, OrderForm form, String action) {
    Validate.notNull(form);

    Account account = getAccountFromToken(token);
    ThreadType type = validateThreadType(form.getThreadType());
    ThreadTypeDetails details = validateThreadTypeDetails(form, type);

    validateQuantityAndStocks(form, details);

    logger.info("saving order by {}", account.getFullName());

    Cart existingCart = cartRepository.findByAccountAndTypeAndDetailsAndNotCheckedOut(account, type, details);
    if(existingCart != null){
      existingCart.setQuantity(existingCart.getQuantity() + form.getQuantity());
      cartRepository.save(existingCart);

      return new CartDto(existingCart, OrderStatus.NOT_YET_ORDERED);
    }

    Cart cart = new Cart(account, type, details, form.getQuantity(), (form.getQuantity() * details.getPrice()), false, false);

    if(action.equalsIgnoreCase("order_now")){
      cart.setOrderNow(true);
    }

    cartRepository.save(cart);

    return new CartDto(cart, OrderStatus.NOT_YET_ORDERED);
  }

  private ThreadType validateThreadType(String threadType){
    ThreadType type = threadTypeRepository.findByTypeIgnoreCase(threadType);

    if(type == null){
      type = threadTypeRepository.findByTypeId(threadType);

      if(type == null){
        throw new ValidationException("Thread type does not exists");
      }
    }

    return type;
  }

  private ThreadTypeDetails validateThreadTypeDetails(OrderForm form, ThreadType type){
    logger.info("validating thread type details");
    ThreadTypeDetails details = threadTypeDetailsRepository.findByDetails(type.getId(),
        form.getWidth(), form.getAspectRatio(), form.getDiameter(), form.getSidewall());

    if(details == null){
      throw new NotFoundException("Thread Type with these details does not exists");
    }

    if(!(form.getThreadType().equals(details.getThreadType().getId())
        || form.getThreadType().equalsIgnoreCase(details.getThreadType().getType()))){
      throw new NotFoundException("Details under this Thread Type does not exists");
    }

    return details;
  }

  private void validateQuantityAndStocks(OrderForm form, ThreadTypeDetails details){
    if(details.getStocks() == 0){
      throw new ValidationException("Entered thread type and its variant is out of stock");
    }

    if(form.getQuantity() < 1){
      throw new ValidationException("Quantity must be greater than or equal to one");
    }

    if(form.getQuantity() > details.getStocks()){
      throw new ValidationException("Current stock is lower than the entered quantity by " + (form.getQuantity() - details.getStocks()));
    }
  }

  @Override
  public CartDto addCartOrderQuantity(String token, String cartId) {
    Account account = getAccountFromToken(token);
    Cart cart = cartRepository.findByIdAndAccount(cartId, account);

    if(cart == null){
      throw new NotFoundException("Cart Order does not exists for this user");
    }

    if(cart.getDetails().getStocks() == 0){
      throw new ValidationException("Cannot add anymore as the variant is already out of stock");
    }

    logger.info("adding order quantity from cart {} of {}", cartId, account.getFullName());

    cart.setQuantity(cart.getQuantity() + 1);
    cartRepository.save(cart);

    return new CartDto(cart, OrderStatus.NOT_YET_ORDERED);
  }

  @Override
  public void removeCartOrder(String token, String cartId) {
    Account account = getAccountFromToken(token);
    Cart cart = cartRepository.findByIdAndAccount(cartId, account);

    if(cart == null){
      throw new NotFoundException("Cart Order was already removed or does not exists");
    }

    logger.info("deleting cart order {} of {}", cartId, account.getFullName());

    cartRepository.delete(cart);
  }

  @Override
  public CartDto subtractCartOrderQuantity(String token, String cartId) {
    Account account = getAccountFromToken(token);
    Cart cart = cartRepository.findByIdAndAccount(cartId, account);

    if(cart == null){
      throw new NotFoundException("Cart Order does not exists for this user");
    }

    logger.info("subtracting order quantity from cart {} of {}", cartId, account.getFullName());

    cart.setQuantity(cart.getQuantity() - 1);
    cartRepository.save(cart);

    return new CartDto(cart, OrderStatus.NOT_YET_ORDERED);
  }

  @Override
  public CartCheckoutDto checkout(String token, CheckoutForm form) {
    Validate.notNull(form);

    Account account = getAccountFromToken(token);
    List<String> cartIds = new ArrayList<>(form.getIds());

    cartIds.removeIf(id -> {
      Cart cart = cartRepository.findByIdAndAccount(id, account);
      return cart != null && cart.isCheckedOut();
    });

    logger.info("checking out selected cart orders of {}", account.getFullName());

    List<Cart> checkedOutCarts = new ArrayList<>();

    for(String id: cartIds){
      Cart cart = cartRepository.findByIdAndAccount(id, account);

      if(cart == null){
        throw new NotFoundException(CART_ORDER_NOT_EXISTS);
      }

      if(cart.isCheckedOut()){
        throw new ValidationException("One of the cart orders is already checked out");
      }

      cart.setCheckedOut(true);
      checkedOutCarts.add(cart);
    }

    List<CartDto> carts = CartDto.buildFromEntitiesForCheckout(cartRepository.saveAll(checkedOutCarts));
    //mailService(account, orders)
    return new CartCheckoutDto(carts);
  }

  @Override
  public void cancelCheckout(String token, CheckoutForm form){
    Validate.notNull(form);

    Account account = getAccountFromToken(token);
    List<String> cartIds = new ArrayList<>(form.getIds());
    List<Cart> carts = new ArrayList<>();
    List<Cart> orderedNowCarts = new ArrayList<>();

    logger.info("cancelling check out of selected cart orders of {}", account.getFullName());

    for(String id: cartIds){
      Cart cart = cartRepository.findByIdAndAccount(id, account);

      if(cart == null){
        throw new NotFoundException(CART_ORDER_NOT_EXISTS);
      }

      if(cart.isPaid()){
        continue;
      }

      if(cart.isOrderNow()){
        orderedNowCarts.add(cart);
      } else {
        cart.setCheckedOut(false);
        carts.add(cart);
      }
    }

    if(!orderedNowCarts.isEmpty()){
      cartRepository.deleteAll(orderedNowCarts);
    }

    cartRepository.saveAll(carts);
    logger.info("Successfully cancelled the checkouts");
  }
  @Override
  public void pay(String token, ReferenceNumberForm form){
    Validate.notNull(form);
    Account account = getAccountFromToken(token);

    if(form.getPaymentMethod() == null || form.getPaymentMethod().isEmpty()){
      throw new ValidationException("Payment Method is required");
    }

    List<String> cartIds = new ArrayList<>(form.getIds());

    cartIds.removeIf(id -> {
      Cart cart = cartRepository.findByIdAndAccount(id, account);
      return cart != null && cart.isPaid();
    });

    logger.info("processing payment of selected cart orders of {}", account.getFullName());

    String orderId = UUID.randomUUID().toString();

    List<Cart> carts = new ArrayList<>();
    List<Orders> ordersList = new ArrayList<>();
    List<ThreadTypeDetails> detailsList = new ArrayList<>();
    for(String id: cartIds){
      Cart cart = cartRepository.findByIdAndAccount(id, account);

      if(!cart.isOrderNow()){
        validateCartDetails(cart);
      }

      cart.setPaid(true);
      carts.add(cart);

      Orders orders = new Orders(OrderType.ONLINE, OrderStatus.FOR_VERIFICATION, form.getRefNo(), form.getPaymentMethod(), cart, null, null, orderId);
      ordersList.add(orders);

      ThreadTypeDetails details = cart.getDetails();
      details.setStocks(details.getStocks() - cart.getQuantity());
      detailsList.add(details);
    }

    if(!carts.isEmpty()){
      cartRepository.saveAll(carts);
      ordersRepository.saveAll(ordersList);
      threadTypeDetailsRepository.saveAll(detailsList);

      String latestDateOrdered = DateTimeFormatter.get_MMDDYYY_Format(new Date());
      mailService.sendEmailOnPayment(account, latestDateOrdered, form.getRefNo(), carts);

      logger.info("Successfully paid the checkouts");
    } else{
      throw new NotFoundException("There are no payable orders");
    }
  }

  private void validateCartDetails(Cart cart){
    if(cart == null){
      throw new NotFoundException(CART_ORDER_NOT_EXISTS);
    }

    if(!cart.isCheckedOut()){
      throw new ValidationException("Cart Order is not one of the selected orders to be checked out");
    }

    if(cart.isPaid()){
      throw new ValidationException("One of the cart orders is already paid");
    }
  }

  private Account getAccountFromToken(String token){
    String id = jwtGenerator.getUserFromJWT(token);

    return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account does not exists"));
  }

  private void validateIfAccountIsNotCustomerOrContentManager(String token){
    String accId = jwtGenerator.getUserFromJWT(token);
    Account adminAccount = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException("Account does not exists"));

    if(!adminAccount.getUserRole().equals(UserRole.ADMINISTRATOR)){
      throw new ValidationException("Only Administrators, Product, and Order managers have access to this feature");
    }
  }
}
