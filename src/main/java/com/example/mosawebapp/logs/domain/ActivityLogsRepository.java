package com.example.mosawebapp.logs.domain;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, String>,
    JpaSpecificationExecutor {

  @Override
  Page<ActivityLogs> findAll(Pageable pageable);

  @Query(value = "SELECT * FROM activity_logs WHERE date_created > :minDate AND date_created < :maxDate", nativeQuery = true)
  List<ActivityLogs> findByDateRange(@Param("minDate") Date minDate, @Param("maxDate") Date maxDate);

  List<ActivityLogs> findByActor(String actor);

}
