package com.example.mosawebapp.product.threadtype.domain;

import com.example.mosawebapp.product.brand.domain.Brand;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ThreadTypeRepository extends JpaRepository<ThreadType, String>,
    JpaSpecificationExecutor {

  List<ThreadType> findByBrand(Brand brand);
  List<ThreadType> findByBrand(String brand);
  List<ThreadType> findAll(Specification specification);
  @Query(value = "SELECT * FROM thread_type WHERE id = :id", nativeQuery = true)
  ThreadType findByTypeId(@Param("id") String id);
  ThreadType findByTypeIgnoreCase(String type);
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM thread_type WHERE brand_id = :brandId", nativeQuery = true)
  void deleteByBrand(@Param("brandId") String brandId);
}
