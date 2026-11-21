package com.finance.controller.rest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.finance.controller.RestApiController;
import com.finance.entity.FinTradingDetails;
import com.finance.entity.TradingFundsDetails;
import com.finance.model.AddMoneyRequest;
import com.finance.model.FinTradeRequest;
import com.finance.service.TradingService;
import com.finance.utils.FinovaUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/finova/api/v1")
public class TradingRestController {

	private final HttpSession httpSession;
	private final TradingService tradingService;

	public TradingRestController(HttpSession httpSession, TradingService tradingService) {
		this.httpSession = httpSession;
		this.tradingService = tradingService;
	}

	@GetMapping("/recent-trades")
	public String showRecentTrades() {
		return "recent-trades";
	}

	@GetMapping("/recent/trades")
	public ResponseEntity<List<FinTradingDetails>> getRecentTrades() {
		List<FinTradingDetails> trades = tradingService.getRecentTrades();
		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(trades);
	}

	@GetMapping("/trading")
	public String tradingPage() {
		return "add-trade";
	}

	@PostMapping("/trading/fund")
	public ResponseEntity<Map<String, Object>> addMoney(@RequestBody AddMoneyRequest addMoneyRequest) {
		log.debug("[{}] ==> Service Called :: Save Trade", httpSession.getId());

		TradingFundsDetails addMoneyTXNDetails = this.tradingService.addMoney(addMoneyRequest);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("INFORMATION", "Money Added in Broker successfully");
		responseData.put("STATUS", "OPEN");
		responseData.put("TXN_ID", addMoneyTXNDetails.getTradeTxnId());
		responseData.put("DATA", addMoneyTXNDetails);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PostMapping("/trading/capital")
	public ResponseEntity<Map<String, Object>> getTradingCapital() {
		log.debug("[{}] ==> Service Called :: Get Total Capital", httpSession.getId());

		// BigDecimal totalDepositedAmount = this.tradingService.fetchTradingCapital();
		BigDecimal totalCapital = tradingService.getTotalCapital();

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("INFORMATION", "Money Fetched successfully");
		responseData.put("STATUS", "OPEN");
		responseData.put("totalCapital", totalCapital);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PostMapping("/trades")
	public ResponseEntity<Map<String, Object>> addTrade(@RequestBody FinTradeRequest finTradeRequest) {
		log.debug("[{}] ==> Service Called :: Save Trade", httpSession.getId());
		try {
			FinTradingDetails finTradingDetails = this.tradingService.saveTrade(finTradeRequest);

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
			responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
			responseData.put("INFORMATION", "Trade saved successfully");
			responseData.put("STATUS", "OPEN");
			responseData.put("TRADE_ID", finTradingDetails.getTradeId());
			responseData.put("DATA", finTradingDetails);

			return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);

		} catch (Exception e) {
			log.error("[{}] ==> Session expired. Redirecting to login.", httpSession.getId(), e);

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("RESP_STATUS", FinovaUtil.FAILED_RESP_MESSAGE);
			responseData.put("RESP_CODE", "MPAY1019");
			responseData.put("INFORMATION", "Sorry for the inconvenience! Please try later");
			responseData.put("REASON", "Oops! Your session has expired. Please log in again to continue.");
			responseData.put("REDIRECT_URL", "/customer/session-expired");

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(RestApiController.getHeaders())
					.body(responseData);
		}
	}

	@PutMapping("/update/trades")
	public ResponseEntity<Map<String, Object>> updateTrade(@RequestBody FinTradingDetails finTradingDetails) {
		log.debug("[{}] ==> Service Called :: Update Trade", httpSession.getId());

		FinTradingDetails updatedTradeDetails = this.tradingService.updateTrade(finTradingDetails);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("INFORMATION", "Trade Updated successfully");
		responseData.put("DATA", updatedTradeDetails);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}
	
	@GetMapping("/all/trades")
	public ResponseEntity<List<FinTradingDetails>> getTrades() {
		log.debug("[{}] ==> Service Called :: Trade List", httpSession.getId());
		List<FinTradingDetails> trades = tradingService.getTradeList();
		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(trades);
	}
}
