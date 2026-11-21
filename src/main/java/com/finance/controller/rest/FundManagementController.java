package com.finance.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.finance.controller.RestApiController;
import com.finance.entity.FinFundTransactionDetails;
import com.finance.model.FundTransactionRequest;
import com.finance.service.FundManagementService;
import com.finance.utils.FinovaUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/finova/api/v1")
public class FundManagementController {

	private final HttpSession httpSession;
	private final FundManagementService fundManagementService;

	public FundManagementController(HttpSession httpSession, FundManagementService fundManagementService) {
		this.httpSession = httpSession;
		this.fundManagementService = fundManagementService;
	}

	@GetMapping("/fund-management")
	public String fundManagementPage() {
		return "fund-management";
	}

	@PostMapping("/fund/transfers")
	public ResponseEntity<Map<String, Object>> fundTransfer(
			@RequestBody FundTransactionRequest fundTransactionRequest) {
		log.debug("[{}] ==> Service Called :: Fund Transfer", httpSession.getId());

		FinFundTransactionDetails finFundTransactionDetails = this.fundManagementService
				.fundTransfer(fundTransactionRequest);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("INFORMATION", "Fund Transfer done successfully");
		responseData.put("STATUS", "OPEN");
		responseData.put("FIN_ID", finFundTransactionDetails.getFundId());
		responseData.put("DATA", finFundTransactionDetails);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PostMapping("/get/funds")
	public ResponseEntity<Map<String, Object>> getAllFund() {
		log.debug("[{}] ==> Service Called :: Get All Funds", httpSession.getId());

		List<FinFundTransactionDetails> fetchedFundTransactionDetails = this.fundManagementService.findFunds();

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("INFORMATION", "Fund Fetched successfully");
		responseData.put("DATA", fetchedFundTransactionDetails);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PutMapping("/update/fund")
	public ResponseEntity<Map<String, Object>> updateFund(@RequestBody FundTransactionRequest fundTransactionRequest) {
		log.debug("[{}] ==> Service Called :: Update Fund", httpSession.getId());

		FinFundTransactionDetails updatedFund = this.fundManagementService.updateEvent(fundTransactionRequest);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", updatedFund);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PutMapping("/delete/fund/{id}")
	public ResponseEntity<Map<String, Object>> deleteFund(@PathVariable String id) {
		log.debug("[{}] ==> Service Called :: Delete Event in Calendar {}", httpSession.getId(), id);

		String responseMessage = this.fundManagementService.deleteFund(id);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", responseMessage);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

}
