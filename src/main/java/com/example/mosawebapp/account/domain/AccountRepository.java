package com.example.mosawebapp.account.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>,
    JpaSpecificationExecutor<Account> {
  Account findByEmail(String email);
  Account findByEmailIgnoreCase(String email);
  Account findByChangePasswordToken(String token);
  @Query("SELECT CASE WHEN COUNT(acc) > 0 THEN true ELSE false END FROM Account acc WHERE lower(acc.email) = lower(:email)")
  boolean existsByEmail(@Param("email") String email);

  @Query("SELECT acc FROM Account acc WHERE acc.userRole <> 'CUSTOMER'")
  List<Account> findStaffAccounts();

  @Query("SELECT acc FROM Account acc WHERE acc.userRole = 'CUSTOMER'")
  List<Account> findCustomerAccounts();
}
