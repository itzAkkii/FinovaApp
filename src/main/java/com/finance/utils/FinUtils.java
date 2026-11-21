package com.finance.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class FinUtils {

	public String dateFormat() {
		LocalDate today = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMM");

        String result = today.format(formatter);
        System.out.println(result);
        
		return result;
	}
	
	  public String getDateAndTime() {
	        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	        return simpleFormat.format(new Date());
	    }
	  
	  public String getDateTime() {
	        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        return simpleFormat.format(new Date());
	    }
	  
	  public String getDate() {
	        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy");
	        return simpleFormat.format(new Date());
	    }
	  
	  public String getDateyyyyMMddHHmmss() {
		  SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	        return simpleFormat.format(new Date());
	    }
	  
	  public String generateRandomString(int charLength) {
	        return String.valueOf(charLength < 1 ? 0
	                : new Random().nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
	                + (int) Math.pow(10, charLength - 1));
	    }

	public static void main(String[] args) {
		FinUtils finUtils = new FinUtils();
		finUtils.dateFormat();
		finUtils.generateRandomString(2);
	}

	

	 
}
