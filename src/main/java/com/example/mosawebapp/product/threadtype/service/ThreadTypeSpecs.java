package com.example.mosawebapp.product.threadtype.service;

import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import javax.persistence.criteria.Join;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

public class ThreadTypeSpecs {
  private static final Logger logger = LoggerFactory.getLogger(ThreadTypeSpecs.class);
  public ThreadTypeSpecs(){
    // needed by sonarlint
  }
  public static Specification<ThreadType> searchThreadType(String search){
    logger.info("searching thread type by {}", search);

    return((root, query, criteriaBuilder) -> {
      Join<ThreadType, Brand> brandJoin = root.join("brand");

      return criteriaBuilder.or(
          criteriaBuilder.like(criteriaBuilder.lower(brandJoin.get("name")), "%" + search.toLowerCase() + "%"),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("type")),"%" + search.toLowerCase() + "%"),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),"%" + search.toLowerCase() + "%")
      );
    });
  }
}
