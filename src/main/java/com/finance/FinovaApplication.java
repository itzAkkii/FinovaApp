/**
 * @author Kiran Shinde
 *
 */

package com.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class FinovaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinovaApplication.class, args);
		
		log.info("<----------- Innovative financial management system ------------->");
		log.debug("********* Finova App Started **********");
	}

}
