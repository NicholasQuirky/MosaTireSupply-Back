package com.example.mosawebapp.account.config;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.RoleRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AccountConfig {
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

  @Autowired
  public AccountConfig(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
      RoleRepository roleRepository) {
    this.accountRepository = accountRepository;
    this.passwordEncoder = passwordEncoder;
    this.roleRepository = roleRepository;
  }

  @Bean
  public CommandLineRunner createAdminAccount() {
    return args -> {
      if(accountRepository.findByEmail("noreplymosatiresupply@gmail.com") == null){
        Account account = new Account();
        account.setEmail("noreplymosatiresupply@gmail.com");
        account.setPassword(passwordEncoder.encode("admin123"));
        account.setFullName("John Doe");
        account.setUserRole(UserRole.ADMINISTRATOR);
        account.setRoles(Collections.singletonList(roleRepository.findByName(UserRole.ADMINISTRATOR.name())));
        accountRepository.save(account);
      }
    };
  }
}
