package com.finance.controller.rest;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.finance.controller.RestApiController;
import com.finance.entity.FinovaUserDetails; 
import com.finance.model.FinMailRequest;
import com.finance.service.FinMailService;
import com.finance.service.FinReportMailService;
import com.finance.utils.FinovaUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/finova/api/v1")
public class EMailRestController {

	private final HttpSession httpSession;
	private final FinMailService finMailService;
	private final FinReportMailService finReportMailService;

	public EMailRestController(HttpSession httpSession, FinMailService finMailService, FinReportMailService finReportMailService) {
		this.httpSession = httpSession;
		this.finMailService = finMailService;
		this.finReportMailService = finReportMailService;
	}

	@PostMapping("/send/email")
	public ResponseEntity<Map<String, Object>> sendMail(@RequestBody FinMailRequest finMailRequest) {
		log.debug("[{}] ==> Service Called :: Send E-Mail", httpSession.getId());

		String mail = this.finMailService.sendMail(finMailRequest);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("INFORMATION", "EMail Send Successfully");
		responseData.put("DATA", mail);

		log.info("[{}] ==> Final Response: {}", httpSession.getId(), responseData);
		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PostMapping("/send/mail/report")
	public ResponseEntity<String> sendMonthlyReport() {
		log.debug("[{}] ==> Service Called :: Send Report E-Mail", httpSession.getId());

		FinovaUserDetails finovaUserDetails =  new FinovaUserDetails();
		finovaUserDetails.setUserId("FIN58708");
		finovaUserDetails.setUserName("Akkii");
		finovaUserDetails.setEmail("akshayshindevk18@gmail.com");
		YearMonth previousMonth = YearMonth.now(ZoneId.of("Asia/Kolkata")).minusMonths(1);

		finReportMailService.sendMonthlyReport(finovaUserDetails, previousMonth.getYear(), previousMonth.getMonthValue());

		return ResponseEntity.ok("Monthly Transaction Report Email Processed Successfully");
	}
}
