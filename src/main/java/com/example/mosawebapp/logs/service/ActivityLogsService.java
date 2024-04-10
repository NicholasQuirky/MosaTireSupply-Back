package com.example.mosawebapp.logs.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.all_orders.domain.OrderStatus;
import com.example.mosawebapp.all_orders.domain.Orders;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.logs.domain.ActivityLogs;
import com.example.mosawebapp.logs.domain.ActivityLogsRepository;
import com.example.mosawebapp.logs.dto.ActivityLogsDto;
import com.example.mosawebapp.onsite_order.domain.OnsiteOrder;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderDto;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.scheduling.domain.Schedule;
import com.example.mosawebapp.security.JwtGenerator;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogsService {
  private static final Logger logger = LoggerFactory.getLogger(ActivityLogsService.class);
  private final ActivityLogsRepository activityLogsRepository;
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  public ActivityLogsService(ActivityLogsRepository activityLogsRepository,
      AccountRepository accountRepository, JwtGenerator jwtGenerator) {
    this.activityLogsRepository = activityLogsRepository;
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
  }

  public Page<ActivityLogsDto> getAllLogs(String token, Pageable pageable){
    validateIfAccountIsAdmin(token);
    Page<ActivityLogs> activityLogs = activityLogsRepository.findAll(pageable);
    List<ActivityLogs> allActivityLogs = activityLogs.getContent();
    List<ActivityLogsDto> activityLogsDto = ActivityLogsDto.buildFromEntities(allActivityLogs);

    activityLogsDto.sort(Comparator.comparing(ActivityLogsDto::getDateCreated).reversed());
    return new PageImpl<>(activityLogsDto, pageable, activityLogs.getTotalElements());
  }

  public void loginActivity(Account account){
    String actor = account.getFullName();
    String message = actor + " just logged into the system";
    boolean isStaff = account.getUserRole() != UserRole.CUSTOMER;

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, isStaff);
    activityLogsRepository.save(log);
  }

  public void logoutActivity(Account account){
    String actor = account.getFullName();
    String message = actor + " just logged out of the system";
    boolean isStaff = account.getUserRole() != UserRole.CUSTOMER;

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, isStaff);
    activityLogsRepository.save(log);
  }

  public void adminCreateAccountActivity(Account user, Account createdAccount){
    String actor = user.getFullName();
    String message = user.getFullName() + " just created an account for " + createdAccount.getFullName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void adminUpdateAccountActivity(Account user, Account updatedAccount) {
    String actor = user.getFullName();
    String message =
        user.getFullName() + " just updated the account of " + updatedAccount.getFullName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void adminDeleteAccountActivity(Account user, Account deletedAccount) {
    String actor = user.getFullName();
    String message =
        user.getFullName() + " just deleted the account of " + deletedAccount.getFullName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void userUpdateAccountActivity(Account account){
    String actor = account.getFullName();
    String message =
        account.getFullName() + " just updated their account";

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, false);
    activityLogsRepository.save(log);
  }

  public void userDeleteAccountActivity(Account account){
    String actor = account.getFullName();
    String message =
        account.getFullName() + " just deleted their account";

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, false);
    activityLogsRepository.save(log);
  }

  public void changePasswordActivity(Account account){
    String actor = account.getFullName();
    String message = "Account Password of " + actor + " has been changed";
    boolean isStaff = account.getUserRole() != UserRole.CUSTOMER;

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, isStaff);
    activityLogsRepository.save(log);
  }

  public void adminChangePasswordActivity(Account user, Account targetUser){
    String actor = user.getFullName();
    String message = actor + " changed the password of " + targetUser.getFullName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void makeSchedule(Account account, Schedule schedule){
    String actor = account.getFullName();
    String message = actor + " just submitted a schedule for service on " + schedule.getDateScheduled();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, false);
    activityLogsRepository.save(log);
  }

  public void approveSchedule(Account account, Schedule schedule){
    String actor = account.getFullName();
    String message = actor + " just approved the schedule for service on " + schedule.getDateScheduled() +
        " submitted by " + schedule.getOrderedBy().getFullName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void validateIfAccountIsAdmin(String token){
    String accId = jwtGenerator.getUserFromJWT(token);
    Account adminAccount = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException("Account does not exists"));

    if(!adminAccount.getUserRole().equals(UserRole.ADMINISTRATOR)){
      throw new ValidationException("Only Administrators have access to this feature");
    }
  }

  public void brandActivity(Account account, List<Brand> brands, String action){
    String actor = account.getFullName();

    for(Brand brand: brands){
      String message = actor + " just " + action + " the brand " + brand.getName() +
          " in Mosa Tire Supply";

      ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
      activityLogsRepository.save(log);
    }
  }

  public void threadTypeActivity(Account account, List<ThreadType> types, String action){
    String actor = account.getFullName();

    for(ThreadType type: types){
      String message = actor + " just " + action + " the Thread Type " + type.getType() +
          " with brand " + type.getBrand().getName() + " in Mosa Tire Supply";

      ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
      activityLogsRepository.save(log);
    }
  }

  public void threadTypeDetailsActivity(Account account, ThreadTypeDetails details, String action) {
    String actor = account.getFullName();
    String message =
        actor + " just " + action + " a Thread Type details for " + details.getThreadType()
            .getType() + " in Mosa Tire Supply";

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void onlineOrderActivity(Account account, String refNo, OrderStatus orderStatus){
    String actor = account.getFullName();
    String message = actor + " just changed the status of order with reference number '" + refNo
        + "' to " + orderStatus.toString();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void onsiteOrderActivity(Account account, String action, OnsiteOrder order){
    String actor = account.getFullName();
    String message = actor + " just " + action + " an order of " + order.getType().getType() + " on " + order.getDateCreated();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void onsiteOrderCheckout(Account account, List<OnsiteOrderDto> orders){
    List<String> orderedItems = new ArrayList<>();
    Date currentDate = new Date();

    for(OnsiteOrderDto dto: orders){
      orderedItems.add(dto.getThreadType());
    }

    String actor = account.getFullName();
    String message = actor + " just completed an order of " + orderedItems + " on " + DateTimeFormatter.get_MMDDYYY_Format(currentDate);

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }
}
