package com.finance.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.finance.controller.RestApiController;
import com.finance.entity.FinTransactionDetails;
import com.finance.model.TransactionDetails;
import com.finance.service.TransactionService;
import com.finance.utils.FinovaUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/finova/api/v1")
public class TransactionController {

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private TransactionService transactionService;

	@GetMapping("/save/transaction")
	public String addExpensePage() {
		return "save-transaction";
	}

	@PostMapping("/save/transaction")
	public ResponseEntity<Map<String, Object>> saveTransaction(@RequestBody TransactionDetails transactionDetails) {
		log.debug("[{}] ==> Service Called :: save Transaction", httpSession.getId());

		log.debug("[{}] ==> save Transaction Request ==> {}", httpSession.getId(), transactionDetails);
		FinTransactionDetails transactionResponse = this.transactionService.addExpense(transactionDetails);
		log.debug("[{}] ==> After Calling Add Transaction Service ==> {}", httpSession.getId(), transactionDetails);

		log.info("[{}] ==> Transaction Saved Successfully", httpSession.getId());

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("transactionResponse", transactionResponse);

		log.debug("[{}] ==> Final Response of Save Transaction ==> {}", httpSession.getId(), responseData);
		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@GetMapping("/search/transactions")
	public String searchExpensePage() {
		return "search-transaction";
	}

	@PostMapping("/search/transactions")
	public ResponseEntity<Map<String, Object>> searchExpenseByDate(@RequestBody TransactionDetails transactionDetails) {
		log.debug("[{}] ==> Service Called :: Transactions Report", httpSession.getId());

		log.debug("[{}] ==> Search Expense Request ==> {}", httpSession.getId(), transactionDetails);
		List<FinTransactionDetails> txnReportByDateList = this.transactionService
				.generateExpenseReportByDateRange(transactionDetails);
		log.debug("[{}] ==> After Calling Search Expense Service ==> {}", httpSession.getId(), txnReportByDateList);

		log.info("[{}] ==> Expense Searched Successfully", httpSession.getId());

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("expenseReportByDateList", txnReportByDateList);

		log.debug("[{}] ==> Final Response of Expense By Date Expense ==> {}", httpSession.getId(), responseData);
		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@GetMapping("/monthly-report")
	public String monthlyReportPage() {
		return "monthly-report";
	}

	@PostMapping("/monthly/report")
	public ResponseEntity<Map<String, Object>> monthlyReport(@RequestBody TransactionDetails transactionDetails) {
		log.debug("[{}] ==> Service Called :: Calculate Monthly Report", httpSession.getId());

		log.debug("[{}] ==> Monthly Report Request ==> {}", httpSession.getId(), transactionDetails);
		List<FinTransactionDetails> monthlyReport = this.transactionService.getMonthlyReport(transactionDetails);
		log.debug("[{}] ==> After Calling Monthly Report Service ==> {}", httpSession.getId(), monthlyReport);

		log.info("[{}] ==> Monthly Report Fetched Successfully", httpSession.getId());

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("monthlyReport", monthlyReport);

		log.debug("[{}] ==> Final Response of Monthly Report ==> {}", httpSession.getId(), responseData);
		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PostMapping("/search/expense/category")
	public ResponseEntity<Map<String, Object>> searchExpenseByCategory(
			@RequestBody TransactionDetails transactionDetails) {
		log.debug("[{}] ==> Service Called :: Search Expense by Category", httpSession.getId());

		log.debug("[{}] ==> Search Expense By Category Request ==> {}", httpSession.getId(), transactionDetails);
		List<FinTransactionDetails> expenseReportByCategoryList = this.transactionService
				.generateExpenseReportByCategory(transactionDetails);
		log.debug("[{}] ==> After Calling Search Expense By Category Service ==> {}", httpSession.getId(),
				expenseReportByCategoryList);

		log.info("[{}] ==> Expense Searched Successfully", httpSession.getId());

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("expenseReportByCategoryList", expenseReportByCategoryList);

		log.debug("[{}] ==> Final Response of Expense By Category ==> {}", httpSession.getId(), responseData);
		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

}
