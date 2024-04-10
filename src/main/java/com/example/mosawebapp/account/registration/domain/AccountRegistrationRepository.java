package com.example.mosawebapp.account.registration.domain;

import com.example.mosawebapp.account.dto.AccountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRegistrationRepository extends JpaRepository<AccountRegistration, String>,
    JpaSpecificationExecutor<AccountRegistration> {

    AccountRegistration findByEmail(String email);
    AccountRegistration findByEmailIgnoreCase(String email);
    @Query("SELECT CASE WHEN COUNT(acc) > 0 THEN true ELSE false END FROM AccountRegistration acc WHERE acc.email = :email")
    boolean existsByEmail(@Param("email") String email);
}
