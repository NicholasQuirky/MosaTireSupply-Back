package com.example.mosawebapp.scheduling.domain;

import com.example.mosawebapp.account.domain.Account;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String>,
    JpaSpecificationExecutor {

  Schedule findByOrderedBy(Account account);
}
