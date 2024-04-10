package com.example.mosawebapp.kiosk.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.all_orders.domain.OrderStatus;
import com.example.mosawebapp.all_orders.domain.OrderType;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.all_orders.domain.OrdersRepository;
import com.example.mosawebapp.api_response.ApiKioskResponse;
import com.example.mosawebapp.cart.dto.CheckoutForm;
import com.example.mosawebapp.cart.dto.OrderForm;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.kiosk.domain.KioskRepository;
import com.example.mosawebapp.kiosk.dto.KioskCheckoutDto;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetailsRepository;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.validate.Validate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class KioskServiceImpl implements KioskService{
  private static final Logger logger = LoggerFactory.getLogger(KioskServiceImpl.class);
  private static final String ORDER_NOW = "order_now";
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  private final ThreadTypeRepository threadTypeRepository;
  private final ThreadTypeDetailsRepository threadTypeDetailsRepository;
  private final KioskRepository kioskRepository;
  private final OrdersRepository ordersRepository;

  @Autowired
  public KioskServiceImpl(AccountRepository accountRepository, JwtGenerator jwtGenerator,
      ThreadTypeRepository threadTypeRepository,
      ThreadTypeDetailsRepository threadTypeDetailsRepository, KioskRepository kioskRepository,
      OrdersRepository ordersRepository) {
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
    this.threadTypeRepository = threadTypeRepository;
    this.threadTypeDetailsRepository = threadTypeDetailsRepository;
    this.kioskRepository = kioskRepository;
    this.ordersRepository = ordersRepository;
  }

  @Override
  public ResponseEntity<?> startKioskOrder() {
    logger.info("generating token for kiosk ordering");

    String token = RandomStringUtils.randomAlphanumeric(10);

    return ResponseEntity.ok(new ApiKioskResponse(HttpStatus.OK, token));
  }

  @Override
  public List<KioskDto> getAllKioskOrders(String adminToken) {
    logger.info("getting all kiosk orders");
    validateIfAccountIsNotCustomerOrContentManager(adminToken);

    List<Kiosk> kiosks = kioskRepository.findAll();
    List<KioskDto> dto = new ArrayList<>();

    for(Kiosk kiosk: kiosks){
      dto.add(new KioskDto(kiosk));
    }

    dto.sort(Comparator.comparing(KioskDto::getDateCreated).reversed());
    return dto;
  }

  @Override
  public List<KioskDto> getCompletedOrders(String adminToken) {
    logger.info("getting all completed kiosk orders");
    validateIfAccountIsNotCustomerOrContentManager(adminToken);

    List<Kiosk> kiosks = kioskRepository.findCompletedOrders();
    List<KioskDto> dto = new ArrayList<>();

    for(Kiosk kiosk: kiosks){
      dto.add(new KioskDto(kiosk));
    }

    dto.sort(Comparator.comparing(KioskDto::getDateCreated).reversed());
    return dto;
  }

  @Override
  public List<KioskDto> getProcessingOrders(String adminToken) {
    logger.info("getting all processing kiosk orders");
    validateIfAccountIsNotCustomerOrContentManager(adminToken);

    List<Kiosk> kiosks = kioskRepository.findProcessingOrders();
    List<KioskDto> dto = new ArrayList<>();

    for(Kiosk kiosk: kiosks){
      dto.add(new KioskDto(kiosk));
    }

    dto.sort(Comparator.comparing(KioskDto::getDateCreated).reversed());
    return dto;
  }

  @Override
  public List<KioskDto> getAllCurrentKioskOrders(String kioskToken) {
    logger.info("getting current kiosk orders");

    List<Kiosk> kiosks = kioskRepository.findNotCheckedOutKioskByToken(kioskToken);

    if(kiosks.isEmpty()){
      return Collections.emptyList();
    }

    List<KioskDto> dto = new ArrayList<>();

    for(Kiosk kiosk: kiosks){
      dto.add(new KioskDto(kiosk));
    }

    dto.sort(Comparator.comparing(KioskDto::getDateCreated).reversed());
    return dto;
  }

  @Override
  public Kiosk addKioskOrder(String kioskToken, OrderForm form, String action) {
    Validate.notNull(form);

    ThreadType type = validateThreadType(form.getThreadType());
    ThreadTypeDetails details = validateThreadTypeDetails(form, type);

    validateQuantityAndStocks(form, details);

    logger.info("saving kiosk order by {}", kioskToken);

    Kiosk kiosk = kioskRepository.findByTokenAndTypeAndDetailsAndNotCheckedOut(kioskToken, type, details);
      if(kiosk != null){
        kiosk.setQuantity(kiosk.getQuantity() + form.getQuantity());
        kioskRepository.save(kiosk);

        return kiosk;
      }

    logger.info("validating existing kiosk");
    Kiosk existingKiosk = kioskRepository.findKioskByToken(kioskToken);

    if(existingKiosk != null){
      Kiosk newKiosk = new Kiosk(kioskToken, type, details, form.getQuantity(), (form.getQuantity() * details.getPrice()), false);
      newKiosk.setQueueingNumber(existingKiosk.getQueueingNumber());

      return kioskRepository.save(newKiosk);
    }

    boolean isOrderNow = action.equalsIgnoreCase(ORDER_NOW);

    Kiosk newKiosk = new Kiosk(kioskToken, type, details, form.getQuantity(), (form.getQuantity() * details.getPrice()), isOrderNow);

    logger.info("finding latest queueing number");
    Kiosk latestKiosk = kioskRepository.findLatestQueueingNumber();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String currentDate = dateFormat.format(new Date());

    logger.info("setting queueing number");
    newKiosk.setQueueingNumber(latestKiosk != null && currentDate.equals(dateFormat.format(latestKiosk.getDateCreated()))
        ? latestKiosk.getQueueingNumber() + 1 : 1L);

    logger.info("saving kiosk order");
    return kioskRepository.save(newKiosk);
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
  public KioskDto addKioskOrderQuantity(String kioskToken, String kioskId) {
    Kiosk kiosk = kioskRepository.findByIdAndToken(kioskId, kioskToken);

    if(kiosk == null){
      throw new NotFoundException("Kiosk Order does not exists");
    }

    if(kiosk.getDetails().getStocks() == 0){
      throw new ValidationException("Cannot add anymore as the variant is already out of stock");
    }

    logger.info("adding order quantity from kiosk {} of {}", kioskId, kioskToken);

    kiosk.setQuantity(kiosk.getQuantity() + 1);
    kioskRepository.save(kiosk);

    return new KioskDto(kiosk);
  }

  @Override
  public void removeKioskOrder(String kioskToken, String kioskId) {
    Kiosk kiosk = kioskRepository.findByIdAndToken(kioskId, kioskToken);

    if(kiosk == null){
      throw new NotFoundException("Kiosk Order was already removed or does not exists");
    }

    logger.info("deleting kiosk order {} of {}", kioskId, kioskToken);

    kioskRepository.delete(kiosk);
  }

  @Override
  public KioskDto subtractCartOrderQuantity(String kioskToken, String kioskId) {
    Kiosk kiosk = kioskRepository.findByIdAndToken(kioskId, kioskToken);

    if(kiosk == null){
      throw new NotFoundException("Kiosk Order does not exists");
    }

    logger.info("subtracting order quantity from kiosk {} of {}", kioskId, kioskToken);

    kiosk.setQuantity(kiosk.getQuantity() - 1);
    kioskRepository.save(kiosk);

    return new KioskDto(kiosk);
  }

  @Override
  public KioskCheckoutDto checkout(String kioskToken, CheckoutForm form, String action) {
    Validate.notNull(form);

    List<String> kioskIds = new ArrayList<>(form.getIds());

    if(!action.equalsIgnoreCase(ORDER_NOW)){
      kioskIds.removeIf(id -> {
        Kiosk kiosk = kioskRepository.findByIdAndToken(id, kioskToken);
        return kiosk != null && kiosk.isCheckedOut();
      });
    }

    logger.info("checking out selected kiosk orders of {}", kioskToken);

    List<Kiosk> checkedOutKiosks = new ArrayList<>();
    List<Orders> ordersList = new ArrayList<>();
    List<ThreadTypeDetails> detailsList = new ArrayList<>();

    String orderId = UUID.randomUUID().toString();
    long queueingNumber = 0;
    for(String id: kioskIds){
      Kiosk kiosk = kioskRepository.findByIdAndToken(id, kioskToken);

      if(kiosk == null){
        throw new NotFoundException("Kiosk Order id does not exists or belong to the current order");
      }

      if(!action.equalsIgnoreCase(ORDER_NOW) && kiosk.isCheckedOut()){
        throw new ValidationException("One of the kiosk orders is already checked out");
      }

      kiosk.setCheckedOut(true);
      checkedOutKiosks.add(kiosk);

      Orders orders = new Orders(OrderType.KIOSK, OrderStatus.PROCESSING, null, null, null, kiosk, null, orderId);
      ordersList.add(orders);

      ThreadTypeDetails details = kiosk.getDetails();
      details.setStocks(details.getStocks() - kiosk.getQuantity());
      detailsList.add(details);

      queueingNumber = kiosk.getQueueingNumber();
    }

    List<KioskDto> kiosks = KioskDto.buildFromEntities(kioskRepository.saveAll(checkedOutKiosks));

    ordersRepository.saveAll(ordersList);
    threadTypeDetailsRepository.saveAll(detailsList);

    KioskCheckoutDto dto = new KioskCheckoutDto(kiosks);
    dto.setQueueingNumber(String.valueOf(queueingNumber));

    return dto;
  }

  @Override
  public KioskCheckoutDto orderNow(String kioskToken, OrderForm form) {
    Validate.notNull(form);

    Kiosk kiosk = addKioskOrder(kioskToken, form, ORDER_NOW);

    CheckoutForm checkoutForm = new CheckoutForm(Collections.singletonList(kiosk.getId()));
    return checkout(kioskToken, checkoutForm, ORDER_NOW);
  }

  @Override
  public void cancelCheckout(String kioskToken, CheckoutForm form) {
    Validate.notNull(form);

    List<String> kioskIds = new ArrayList<>(form.getIds());
    List<Kiosk> cancelledKiosks = new ArrayList<>();

    logger.info("cancelling check out of selected kiosk orders of {}", kioskToken);

    for(String id: kioskIds){
      Kiosk kiosk = kioskRepository.findByIdAndToken(id, kioskToken);

      if(kiosk == null){
        throw new NotFoundException("Kiosk Order id does not exists or belong to the user");
      }

      kiosk.setCheckedOut(false);
      cancelledKiosks.add(kiosk);
    }

    kioskRepository.saveAll(cancelledKiosks);
    logger.info("Successfully cancelled the checkouts");
  }

  @Override
  public void setAsComplete(String kioskToken) {
    logger.info("setting kiosk orders as complete");

    List<Orders> orders = ordersRepository.findOrdersByKioskToken(kioskToken);
    List<Orders> completedOrders = new ArrayList<>();

    for (Orders order : orders) {
      if (!order.getKiosk().isCheckedOut()) {
        throw new ValidationException("Cannot set as completed if it is not yet checked out");
      }

      if(order.getOrderStatus().equals(OrderStatus.ORDER_COMPLETED)){
        throw new ValidationException("One of the orders is already completed");
      }

      order.setOrderStatus(OrderStatus.ORDER_COMPLETED);
      completedOrders.add(order);
    }

    ordersRepository.saveAll(completedOrders);
    logger.info("done changing status to completed");
  }

  private void validateIfAccountIsNotCustomerOrContentManager(String token){
    String accId = jwtGenerator.getUserFromJWT(token);
    Account adminAccount = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException("Account does not exists"));

    if(!adminAccount.getUserRole().equals(UserRole.ADMINISTRATOR)){
      throw new ValidationException("Only Administrators, Product, and Order managers have access to this feature");
    }
  }
}
