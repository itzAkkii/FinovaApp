package com.finance.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.finance.controller.RestApiController;
import com.finance.utils.FinovaUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@Autowired
	private HttpSession httpSession;

	@ExceptionHandler(FinovaException.class)
	public ResponseEntity<Map<String, Object>> handleMpayUserException(FinovaException ex) {
		log.error("Exception Occurred : {} ", ex.getErrorMsg());

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.FAILED_RESP_MESSAGE);

		String reason = Optional.ofNullable((String) httpSession.getAttribute("respMsg"))
				.orElse(Optional.ofNullable(ex.getErrorMsg()).orElse("Internal issue! Please try later."));

		String respCode = Optional.ofNullable((String) httpSession.getAttribute("respCode"))
				.orElse(Optional.ofNullable(ex.getErrorCode()).orElse("MPAY0001"));

		responseData.put("INFORMATION", "Sorry for the inconvenience! Please try later");
		responseData.put("REASON", reason);
		responseData.put("RESP_CODE", respCode);

		log.trace("[{}] ==> responseData --> {}", httpSession.getId(), responseData);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(RestApiController.getHeaders()).body(responseData);
	}

}
