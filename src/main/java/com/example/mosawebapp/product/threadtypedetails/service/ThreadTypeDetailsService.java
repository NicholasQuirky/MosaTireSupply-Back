package com.example.mosawebapp.product.threadtypedetails.service;

import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.dto.AddStockForm;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsForm;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ThreadTypeDetailsService {
  List<ThreadTypeDetailsDto> findAllThreadTypesDetails();
  List<ThreadTypeDetailsDto> findAllInCriticalStocks(String token);
  void addStock(String token, AddStockForm form);

  ThreadTypeDetailsDto findThreadTypeDetails(String id);
  ThreadTypeDetailsDto addThreadTypeDetails(String token, ThreadTypeDetailsForm form);

  int addThreadTypeDetailsFromFile(String token, MultipartFile file) throws IOException;

  ThreadTypeDetailsDto updateThreadTypeDetails(String token, String id, ThreadTypeDetailsForm form);
  void deleteThreadTypeDetails(String token, String id);
}
