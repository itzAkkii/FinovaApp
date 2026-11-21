package com.finance.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.finance.entity.FinTradingDetails;
import com.finance.entity.FinTransactionDetails;
import com.finance.entity.FinovaUserDetails;
import com.finance.entity.TradingFundsDetails;
import com.finance.exception.FinovaException;
import com.finance.model.AddMoneyRequest;
import com.finance.model.FinTradeRequest;
import com.finance.repository.FinTradingRepository;
import com.finance.repository.FinTransactionRepository;
import com.finance.repository.TradingFundsRepository;
import com.finance.utils.FinUtils;
import com.finance.utils.RequestValidator;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TradingServiceImpl implements TradingService {

	private final HttpSession httpSession;
	private final FinUtils finUtils;
	private final RequestValidator requestValidator;
	private final FinTradingRepository finTradingRepository;
	private final TradingFundsRepository tradingFundsRepository;
	private final FinTransactionRepository transactionRepository;
	private final CustomerService customerService;

	public TradingServiceImpl(HttpSession httpSession, FinUtils finUtils, RequestValidator requestValidator,
			FinTradingRepository finTradingRepository, TradingFundsRepository tradingFundsRepository,
			FinTransactionRepository transactionRepository, CustomerService customerService) {
		this.httpSession = httpSession;
		this.finUtils = finUtils;
		this.requestValidator = requestValidator;
		this.finTradingRepository = finTradingRepository;
		this.tradingFundsRepository = tradingFundsRepository;
		this.transactionRepository = transactionRepository;
		this.customerService = customerService;
	}

	@Override
	public TradingFundsDetails addMoney(AddMoneyRequest addMoneyRequest) {
		log.debug("[{}] Trading Fund Service :: {}", httpSession.getId(), addMoneyRequest);
		
		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    return new FinovaException("MPAY1019", "Please login to access this service");
                });


		if (!requestValidator.validateDetailsforAddMoney(addMoneyRequest)) {
			log.info("[{}] ==> Validation failed", httpSession.getId());
			String respMessage = Optional.ofNullable((String) httpSession.getAttribute("respMsg"))
					.orElse("Unknown error");
			throw new FinovaException("FIN1002", respMessage);
		}

		TradingFundsDetails addMoneyTXNDetails = new TradingFundsDetails();
		addMoneyTXNDetails.setTradeTxnId("AM" + this.finUtils.dateFormat() + this.finUtils.generateRandomString(2));

		String txnId = "CUST" + this.finUtils.getDateyyyyMMddHHmmss() + this.finUtils.generateRandomString(4);

		addMoneyTXNDetails.setUpiTransactionId(txnId);
		addMoneyTXNDetails.setBrokerName(addMoneyRequest.getBrokerName());
		addMoneyTXNDetails.setAmount(addMoneyRequest.getAmount());
		addMoneyTXNDetails.setTransactionDate(this.finUtils.getDate());
		addMoneyTXNDetails.setPaymentApp(addMoneyRequest.getPaymentApp());

		Optional.ofNullable(addMoneyRequest.getRemarks()).ifPresent(addMoneyTXNDetails::setRemarks);

		addMoneyTXNDetails.setUpiTransactionId("UPI" + this.finUtils.generateRandomString(7));
		addMoneyTXNDetails.setPaymentMode("UPI");
		addMoneyTXNDetails.setCurrency("INR");
		addMoneyTXNDetails.setStatus("S");
		addMoneyTXNDetails.setUserId(finovaUserDetails.getUserId());

		TradingFundsDetails moneyAdded = Optional.of(this.tradingFundsRepository.save(addMoneyTXNDetails))
				.orElseThrow(() -> {
					log.error("[{}] ==> Unable to Save Add Money Details", httpSession.getId());
					throw new FinovaException("FIN1002", "Internal issue! Please try later");
				});

		FinTransactionDetails expenseDetails = new FinTransactionDetails();
		expenseDetails.setFinTxnId("FIN" + this.finUtils.dateFormat() + this.finUtils.generateRandomString(2));

		expenseDetails.setTransactionId(txnId);
		expenseDetails.setTransactionReferenceNumber(addMoneyTXNDetails.getTradeTxnId());
		expenseDetails.setAmount(addMoneyRequest.getAmount());
		expenseDetails.setPaymentMode(addMoneyRequest.getPaymentMode());

		Optional.ofNullable(addMoneyRequest.getRemarks()).ifPresent(expenseDetails::setDescription);

		String txnType = addMoneyRequest.getTxnType();
		if ("DEPOSITE".equalsIgnoreCase(txnType)) {
			expenseDetails.setTransactionType("EXPENSE");
			expenseDetails.setTransactionTitle("DEPOSITE");
		} else if ("WITHDRWAL".equalsIgnoreCase(txnType)) {
			expenseDetails.setTransactionType("INCOME");
			expenseDetails.setTransactionTitle("WITHDRWAL");
		} else {
			expenseDetails.setTransactionType("UNKNOWN");
			expenseDetails.setTransactionTitle("UNKNOWN");
		}

		expenseDetails.setCurrency("INR");
		expenseDetails.setPaymentMode("UPI");
		expenseDetails.setTransactionCategory("Trading");
		expenseDetails.setDate(this.finUtils.getDate());
		expenseDetails.setDateAndTime(this.finUtils.getDateAndTime());
		expenseDetails.setStatus("S");
		expenseDetails.setUserId(finovaUserDetails.getUserId());

		log.debug("[{}] ==> Trading TXNs Saving into Database ==> {}", httpSession.getId(), expenseDetails);
		FinTransactionDetails savedTXNDetails = Optional.of(this.transactionRepository.save(expenseDetails))
				.orElseThrow(() -> {
					log.error("[{}] ==> Failed to save Transaction to Database", httpSession.getId());
					return new FinovaException("FIN1002", "Internal issue! Please try later");
				});

		log.debug("[{}] TXN Saved Successfully :: {}", httpSession.getId(), savedTXNDetails);
		log.debug("[{}] Trading Fund Saved Successfully :: {}", httpSession.getId(), moneyAdded);
		return moneyAdded;
	}

	public BigDecimal fetchTradingCapital() {
		log.debug("[{}] Add Money Service :: ", httpSession.getId());
		List<TradingFundsDetails> depositList = this.tradingFundsRepository.findAll();

		if (depositList == null || depositList.isEmpty()) {
			log.error("[{}] ==> Unable to fetch Add Money Details", httpSession.getId());
			throw new FinovaException("FIN1002", "No Add Money details found");
		}

		BigDecimal totalCapital = depositList.stream().map(TradingFundsDetails::getAmount).filter(Objects::nonNull)
				.map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

		log.debug("[{}] Total Capital Fetched Sucessfully :: {}", httpSession.getId(), totalCapital);
		return totalCapital;
	}

	@Override
	public List<FinTradingDetails> getRecentTrades() {
		log.debug("[{}] GET Recent Trades Service", httpSession.getId());
		return finTradingRepository.findTop10RecentTrades();
	}

	@Override
	public FinTradingDetails saveTrade(FinTradeRequest finTradeRequest) {
		log.debug("[{}] Add Trade Service :: {}", httpSession.getId(), finTradeRequest);
		
		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    return new FinovaException("MPAY1019", "Please login to access this service");
                });

		if (!requestValidator.validateDetailsforSaveTrade(finTradeRequest)) {
			log.info("[{}] ==> Validation failed", httpSession.getId());
			String respMessage = Optional.ofNullable((String) httpSession.getAttribute("respMsg"))
					.orElse("Unknown error");
			throw new FinovaException("FIN1002", respMessage);
		}

		FinTradingDetails finTradingDetails = new FinTradingDetails();
		finTradingDetails.setTradeId("TRD" + this.finUtils.dateFormat() + this.finUtils.generateRandomString(2));

		finTradingDetails.setMarket(finTradeRequest.getMarket());
		finTradingDetails.setSymbol(finTradeRequest.getSymbol());
		finTradingDetails.setProductType(finTradeRequest.getProductType());
		finTradingDetails.setInstrumentType(finTradeRequest.getInstrumentType());
		finTradingDetails.setDirection(finTradeRequest.getDirection());
		finTradingDetails.setEntryDate(finTradeRequest.getEntryDate());
		finTradingDetails.setEntryTime(finTradeRequest.getEntryTime());
		finTradingDetails.setEntryPrice(finTradeRequest.getEntryPrice());
		finTradingDetails.setExitPrice(finTradeRequest.getExitPrice());
		finTradingDetails.setQuantity(finTradeRequest.getQuantity());
		finTradingDetails.setBrokerage(finTradeRequest.getBrokerage());

		finTradingDetails.setNetProfitLoss(calculateNetProfitLoss(finTradeRequest));

		Optional.ofNullable(finTradeRequest.getBroker()).ifPresent(finTradingDetails::setBroker);
		Optional.ofNullable(finTradeRequest.getStopLoss()).ifPresent(finTradingDetails::setStopLoss);
		Optional.ofNullable(finTradeRequest.getTargetPrice()).ifPresent(finTradingDetails::setTargetPrice);
		Optional.ofNullable(finTradeRequest.getDescription()).ifPresent(finTradingDetails::setDesc);

		finTradingDetails.setCurrency("INR");
		finTradingDetails.setStatus("S");
		finTradingDetails.setCreatedAt(this.finUtils.getDate());
		finTradingDetails.setUserId(finovaUserDetails.getUserId());

		FinTradingDetails savedTradingDetails = Optional.of(this.finTradingRepository.save(finTradingDetails))
				.orElseThrow(() -> {
					log.error("[{}] ==> Unable to Save Trading Details", httpSession.getId());
					throw new FinovaException("FIN1002", "Internal issue! Please try later");
				});

		log.debug("[{}] Trade Saved Successfully :: {}", httpSession.getId(), savedTradingDetails);
		return savedTradingDetails;
	}

	@Override
	public FinTradingDetails updateTrade(FinTradingDetails finTradingDetails) {
		log.debug("[{}] Update Trade Service :: {}", httpSession.getId(), finTradingDetails);

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    return new FinovaException("MPAY1019", "Please login to access this service");
                });

		if (finTradingDetails.getTradeId() == null) {
			log.debug("[{}] Txn Id Not Found In Request:: ", httpSession.getId());
			throw new FinovaException("FIN1002", "Internal issue! Please try later");
		}

		FinTradingDetails tradeHistory = this.finTradingRepository.findById(finTradingDetails.getTradeId())
				.orElseThrow(() -> {
					log.debug("Financial event not found with ID: {}", finTradingDetails.getTradeId());
					return new FinovaException("FIN1002", "Event Not Found");
				});

		tradeHistory.setMarket(finTradingDetails.getMarket());
		tradeHistory.setSymbol(finTradingDetails.getSymbol());
		tradeHistory.setProductType(finTradingDetails.getProductType());
		tradeHistory.setDirection(finTradingDetails.getDirection());
		tradeHistory.setEntryDate(finTradingDetails.getEntryDate());
		tradeHistory.setEntryTime(finTradingDetails.getEntryTime());
		tradeHistory.setEntryPrice(finTradingDetails.getEntryPrice());
		tradeHistory.setExitPrice(finTradingDetails.getExitPrice());
		tradeHistory.setQuantity(finTradingDetails.getQuantity());
		tradeHistory.setBrokerage(finTradingDetails.getBrokerage());
		tradeHistory.setUserId(finovaUserDetails.getUserId());

		// Create request object for P/L calculation
		FinTradeRequest finTradeRequest = new FinTradeRequest();

		finTradeRequest.setDirection(finTradingDetails.getDirection());
		finTradeRequest.setEntryPrice(finTradingDetails.getEntryPrice());
		finTradeRequest.setExitPrice(finTradingDetails.getExitPrice());
		finTradeRequest.setQuantity(finTradingDetails.getQuantity());
		finTradeRequest.setBrokerage(finTradingDetails.getBrokerage());

		// Calculate and set Net Profit/Loss
		tradeHistory.setNetProfitLoss(calculateNetProfitLoss(finTradeRequest));

		log.debug("[{}] Event Updated Successfully {}", httpSession.getId(), tradeHistory);
		return finTradingRepository.save(tradeHistory);
	}

	private String calculateNetProfitLoss(FinTradeRequest finTradeRequest) {
		// If trade is still open, return null
		if (finTradeRequest.getExitPrice() == null) {
			return null;
		}

		BigDecimal entryPrice = finTradeRequest.getEntryPrice();
		BigDecimal exitPrice = finTradeRequest.getExitPrice();
		BigDecimal quantity = finTradeRequest.getQuantity();

		BigDecimal brokerage = finTradeRequest.getBrokerage() != null && !finTradeRequest.getBrokerage().isBlank()
				? new BigDecimal(finTradeRequest.getBrokerage())
				: BigDecimal.ZERO;

		BigDecimal priceDifference;

		// Calculate price difference based on direction
		if ("LONG".equalsIgnoreCase(finTradeRequest.getDirection())
				|| "BUY".equalsIgnoreCase(finTradeRequest.getDirection())) {

			priceDifference = exitPrice.subtract(entryPrice);

		} else if ("SHORT".equalsIgnoreCase(finTradeRequest.getDirection())
				|| "SELL".equalsIgnoreCase(finTradeRequest.getDirection())) {

			priceDifference = entryPrice.subtract(exitPrice);

		} else {
			throw new IllegalArgumentException("Invalid trade direction");
		}

		// Gross P/L = Price Difference * Quantity * Contract Multiplier
		BigDecimal grossProfitLoss = priceDifference.multiply(quantity);

		// Net P/L = Gross P/L - Brokerage
		BigDecimal netProfitLoss = grossProfitLoss.subtract(brokerage).setScale(2, RoundingMode.HALF_UP);

		return netProfitLoss.toPlainString();
	}

	@Override
	public BigDecimal getTotalCapital() {
		log.debug("[{}] Get Total Capital Service", httpSession.getId());
		BigDecimal totalCapital = transactionRepository.getTradingCapital();
		log.debug("[{}] Total Capital Fetched Successfully :: {}", httpSession.getId(), totalCapital);
		return totalCapital;
	}

	@Override
	public List<FinTradingDetails> getTradeList() {
		log.debug("[{}] GET List Of Trades", httpSession.getId());
		return finTradingRepository.findAll();
	}
}
