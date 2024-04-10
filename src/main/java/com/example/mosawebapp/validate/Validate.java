package com.example.mosawebapp.validate;
import com.example.mosawebapp.exceptions.NotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
public class Validate {
    private Validate() {}

    public static void notNull(Object obj){
      if(obj == null){
        throw new NotFoundException("Object is null");
      }
    }

  public static boolean IsNull(Object obj){
    return obj == null;
  }

    public static void notEmpty(String... str){
      for(String string: str){
        notEmpty(string);
      }
    }

    public static void notEmpty(String str){
      if(StringUtils.isEmpty(str)){
        throw new NotFoundException("Object is empty");
      }
    }

    public static boolean hasIntegersAndSpecialCharacters(String str){
      String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[-+_!@#$%^&*., ?]).+$";
      Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(str);

      return matcher.find();
    }

    public static boolean hasCorrectEmailFormat(String str){
      String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
          + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

      return Pattern.compile(regex)
          .matcher(str)
          .matches();
    }

    public static boolean hasLettersInNumberInput(String str){
      return str.matches(".*[a-z].*");
    }
}
