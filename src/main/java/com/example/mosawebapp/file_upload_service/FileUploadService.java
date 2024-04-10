package com.example.mosawebapp.file_upload_service;

import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.brand.domain.BrandRepository;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {
  private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

  @Value("${default.blank.image.cdn}")
  private String BLANK_IMAGE;
  @Autowired
  private final ThreadTypeRepository threadTypeRepository;
  @Autowired
  private final BrandRepository brandRepository;

  public FileUploadService(ThreadTypeRepository threadTypeRepository,
      BrandRepository brandRepository) {
    this.threadTypeRepository = threadTypeRepository;
    this.brandRepository = brandRepository;
  }

  public static boolean isFileValid(MultipartFile file){
    return Objects.equals(file.getContentType(),"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
        Objects.equals(file.getContentType(), "text/csv");
  }

  public static List<Brand> getBrandsFromFile(InputStream inputStream){
    List<Brand> brands = new ArrayList<>();

    XSSFWorkbook workbook = null;

    try{
      workbook = new XSSFWorkbook(inputStream);
      XSSFSheet sheet = workbook.getSheet("Brands");

      int rowIndex = 0;
      for(Row row: sheet){
        if(rowIndex == 0){
          rowIndex++;
          continue;
        }

        Iterator<Cell> cellIterator = row.iterator();
        int cellIndex = 0;
        Brand brand = new Brand();

        while(cellIterator.hasNext()){
          Cell cell = cellIterator.next();

          if (cellIndex == 0) {
            brand.setName(cell.getStringCellValue());
          }
          cellIndex++;
        }
        brands.add(brand);
      }
    } catch (NullPointerException ne){
      throw new NullPointerException(ne.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
    return brands;
  }

  public List<ThreadTypeDetails> getThreadTypeDetailsFromFile(InputStream inputStream){
    List<ThreadTypeDetails> detailsList = new ArrayList<>();

    try{
      XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
      XSSFSheet sheet = workbook.getSheet("Details");

      int rowIndex = 0;
      int totalRows = sheet.getLastRowNum();
      for(Row row: sheet){
        if(rowIndex == 0){
          rowIndex++;
          continue;
        }

        ThreadTypeDetails details = new ThreadTypeDetails();
        String brand = null;

        detailsList.add(setThreadTypeDetails(row, brand, details));
        rowIndex++;

        if(rowIndex >= totalRows){
          break;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
    return detailsList;
  }

  public ThreadTypeDetails setThreadTypeDetails(Row row, String brand, ThreadTypeDetails details){
    for(int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++){
      Cell cell = row.getCell(cellIndex, MissingCellPolicy.CREATE_NULL_AS_BLANK);

      switch(cellIndex){
        case 0 -> brand = validateBrand(cell.getStringCellValue());
        case 1 -> details.setThreadType(validateThreadType(brand, cell.getStringCellValue()));
        case 2 -> details.setWidth(isCellNumeric(cell) ? String.format("%.0f",cell.getNumericCellValue()) : cell.getStringCellValue());
        case 3 -> details.setAspectRatio(isCellNumeric(cell) ? String.format("%.0f",cell.getNumericCellValue()) : cell.getStringCellValue());
        case 4 -> details.setDiameter(isCellNumeric(cell) ? String.format("%.0f",cell.getNumericCellValue()) : cell.getStringCellValue());
        case 5 -> details.setSidewall(cell.getStringCellValue());
        case 6 -> details.setPlyRating(cell.getStringCellValue());
        case 7 -> details.setPrice(validatePrice(cell));
        case 8 -> details.setStocks(validateStock(cell));
        default -> {}
      }
    }

    return details;
  }
  public float validatePrice(Cell cell){
    if(cell.getCellTypeEnum() == CellType.STRING){
      String value = cell.getStringCellValue().replace(",", "");

      return Float.parseFloat(value);
    }

    return (float) cell.getNumericCellValue();
  }

  public Long validateStock(Cell cell){
    if(cell.getCellTypeEnum() == CellType.STRING){
      String value = cell.getStringCellValue().replace(",", "");

      logger.info("VALUE {}", value);
      return Long.parseLong(value);
    }

    logger.info("CELL {}", cell.getNumericCellValue());
    return (long) cell.getNumericCellValue();
  }

  public ThreadType validateThreadType(String brand, String type) {
    ThreadType threadType = threadTypeRepository.findByTypeIgnoreCase(type);

    if (threadType == null) {
      Brand checkBrand = brandRepository.findByNameIgnoreCase(brand);
      ThreadType newThreadType = new ThreadType(type, BLANK_IMAGE, "", checkBrand);
      newThreadType.setRating(5);

      return threadTypeRepository.save(newThreadType);
    }

    return threadType;
  }

  public String validateBrand(String brand){
    Brand checkBrand = brandRepository.findByNameIgnoreCase(brand);

    if(checkBrand == null){
      Brand newBrand = new Brand(brand, BLANK_IMAGE);
      brandRepository.save(newBrand);

      return newBrand.getName();
    }

    return brand;
  }
  public boolean isCellNumeric(Cell cell){
    return cell.getCellTypeEnum() == CellType.NUMERIC;
  }
}
