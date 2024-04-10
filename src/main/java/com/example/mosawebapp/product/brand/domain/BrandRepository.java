package com.example.mosawebapp.product.brand.domain;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String>, JpaSpecificationExecutor {
  Brand findByNameIgnoreCase(String name);
  Brand findByName(String name);

  @Query(value = "SELECT * FROM brand WHERE id = :id", nativeQuery = true)
  Brand findByBrandId(@Param("id") String id);
}
