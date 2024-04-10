package com.example.mosawebapp.product.brand.service;

import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.brand.domain.BrandRepository;
import com.example.mosawebapp.product.brand.dto.BrandDto;
import com.example.mosawebapp.product.brand.dto.BrandForm;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface BrandService {
  List<BrandDto> findAllBrands();
  BrandDto findBrand(String id);
  List<Brand> addBrand(String token, List<BrandForm> forms);

  int addBrands(String token, MultipartFile file) throws IOException;

  Brand updateBrand(String token, String id, BrandForm form);
  void deleteBrand(String token, String id);
}
