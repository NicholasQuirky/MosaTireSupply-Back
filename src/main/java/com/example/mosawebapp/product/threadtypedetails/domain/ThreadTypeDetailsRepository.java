package com.example.mosawebapp.product.threadtypedetails.domain;

import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

public interface ThreadTypeDetailsRepository extends JpaRepository<ThreadTypeDetails, String>,
    JpaSpecificationExecutor {
  boolean existsByThreadType(ThreadType threadType);
  List<ThreadTypeDetails> findByThreadType(ThreadType type);

  @Query(value = "SELECT * FROM thread_type_details WHERE "
      + "thread_type_id = :typeId "
      + "AND width = :width "
      + "AND aspect_ratio = :ratio "
      + "AND diameter = :diameter "
      + "AND sidewall = :sidewall ", nativeQuery = true)
  ThreadTypeDetails findByDetails(@Param("typeId") String typeId, @Param("width") String width, @Param("ratio") String ratio, @Param("diameter") String diameter,
      @Param("sidewall") String sidewall);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM thread_type_details WHERE thread_type_id = :id", nativeQuery = true)
  void deleteByThreadType(@Param("id") String id);

  @Query(value = "SELECT FROM thread_type_details WHERE stocks <= 50", nativeQuery = true)
  List<ThreadTypeDetails> findAllDetailsInCriticalStocks();
}
