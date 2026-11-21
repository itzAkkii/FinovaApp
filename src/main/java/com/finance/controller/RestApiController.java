package com.finance.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.utils.FinovaUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/finova")
public class RestApiController {
	
	@Autowired
	private HttpSession httpSession;

	@GetMapping("/message")
	public ResponseEntity<Map<String, Object>> getMessage() {
		log.debug("[{}] ==> Service Called :: Message", httpSession.getId());

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("message", "Finova Application Working");

		log.debug("[{}] ==> Message : {}", httpSession.getId(), responseData);

		return ResponseEntity.ok()	
				.headers(getHeaders())
				.body(responseData);
	}
	
	
	public static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Credentials", "true");
		headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        headers.add("Access-Control-Max-Age", "3600");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
        return headers;
    }
}
