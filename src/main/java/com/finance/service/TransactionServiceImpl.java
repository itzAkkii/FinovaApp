package com.finance.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.finance.entity.FinTransactionDetails;
import com.finance.entity.FinovaUserDetails;
import com.finance.exception.FinovaException;
import com.finance.model.TransactionDetails;
import com.finance.repository.FinTransactionRepository;
import com.finance.utils.FinUtils;
import com.finance.utils.RequestValidator;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
	
	private final HttpSession httpSession;
	private final RequestValidator requestValidator;
	private final FinUtils finUtils;
	private final FinTransactionRepository transactionRepository;
	private final CustomerService customerService;

	public TransactionServiceImpl(HttpSession httpSession, RequestValidator requestValidator, FinUtils finUtils, FinTransactionRepository transactionRepository, CustomerService customerService) {
		this.httpSession = httpSession;
		this.requestValidator = requestValidator;
		this.finUtils = finUtils;
		this.transactionRepository = transactionRepository;
		this.customerService = customerService;
	}
	
	@Override
	public BigDecimal getTotalIncome(int year, int month) {
		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    throw new FinovaException("MPAY1019", "Please login to access this service");
                });
		String userId = finovaUserDetails.getUserId();
		if(userId == null) {
			log.debug("[{}] ==> User Id is Null ::", httpSession.getId());
			throw new FinovaException("MPAY1019", "Internal issue! Please try later");
		}
		return transactionRepository.getTotalIncome(year, month, userId);
	}

	@Override
	public BigDecimal getTotalExpense(int year, int month) {
		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    throw new FinovaException("MPAY1019", "Please login to access this service");
                });
		String userId = finovaUserDetails.getUserId();
		if(userId == null) {
			log.debug("[{}] ==> User Id is Null ::", httpSession.getId());
			throw new FinovaException("MPAY1019", "Internal issue! Please try later");
		}
		return transactionRepository.getTotalExpense(year, month, userId);
	}
	
	@Override
	public BigDecimal getOverallBalance() {
	    FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
	            .orElseThrow(() -> {
	                log.debug("[{}] ==> Please login to access this service", httpSession.getId());
	                return new FinovaException("MPAY1019", "Please login to access this service");
	            });
	    String userId = finovaUserDetails.getUserId();
	    if (userId == null) {
	        log.debug("[{}] ==> User Id is Null ::", httpSession.getId());
	        throw new FinovaException("MPAY1019", "Internal issue! Please try later");
	    }
	    return transactionRepository.getOverallBalance(userId);
	}

	/*
	 * @Override public List<FinTransactionDetails> getRecentTransactions() {
	 * log.debug("[{}] ==> Fetching Recent Transaction Service",
	 * httpSession.getId()); return
	 * transactionDetailsRepository.findRecentTransactions(); }
	 */

	@Override
	public FinTransactionDetails addExpense(TransactionDetails transactionDetails) {
		log.debug("[{}] ==> Save Transaction Service :: {}", httpSession.getId(), transactionDetails);
		
		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    return new FinovaException("MPAY1019", "Please login to access this service");
                });

		FinTransactionDetails finTransactionDetails = new FinTransactionDetails();
		finTransactionDetails.setFinTxnId("FIN" + this.finUtils.dateFormat() + this.finUtils.generateRandomString(2));

		String txnId = "CUST" + this.finUtils.getDateyyyyMMddHHmmss() + this.finUtils.generateRandomString(4);

		finTransactionDetails.setTransactionId(txnId);
		finTransactionDetails.setTransactionTitle(transactionDetails.getTransactionTitle());
		finTransactionDetails.setAmount(transactionDetails.getAmount());
		finTransactionDetails.setPaymentMode(transactionDetails.getPaymentMode());
		finTransactionDetails.setTransactionType(transactionDetails.getTransactionType());
		
		Optional.ofNullable(transactionDetails.getTransactionCategory()).ifPresent(finTransactionDetails::setTransactionCategory);
		Optional.ofNullable(transactionDetails.getDescription()).ifPresent(finTransactionDetails::setDescription);
		Optional.ofNullable(transactionDetails.getLocation()).ifPresent(finTransactionDetails::setLocation);

		finTransactionDetails.setCurrency("INR");
		finTransactionDetails.setStatus("S");
		finTransactionDetails.setDate(this.finUtils.getDate());
		finTransactionDetails.setDateAndTime(this.finUtils.getDateAndTime());
		finTransactionDetails.setUserId(finovaUserDetails.getUserId());

		log.debug("[{}] ==> Adding Transaction into Database ==> {}", httpSession.getId(), finTransactionDetails);
		FinTransactionDetails savedTransaction = Optional.of(this.transactionRepository.save(finTransactionDetails))
				.orElseThrow(() -> {
					log.error("[{}] ==> Failed to save Transaction to Database", httpSession.getId());
					return new FinovaException("FIN1002", "Internal issue! Please try later");
				});

		log.debug("[{}] ==> Transaction Saved into Database Successfully : {}", httpSession.getId(), savedTransaction);
		return savedTransaction;
	}

	@Override
	public List<FinTransactionDetails> generateExpenseReportByDateRange(TransactionDetails expenseRequest) {
		log.debug("[{}] Search Expense Service :: {}", httpSession.getId(), expenseRequest);
		
		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    return new FinovaException("MPAY1019", "Please login to access this service");
                });
		log.debug("[{}] ==> finovaUserDetails ==> {}", httpSession.getId(), finovaUserDetails);

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter databaseFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		LocalDate fromDate = LocalDate.parse(expenseRequest.getFromDate(), inputFormatter);
		LocalDate toDate = LocalDate.parse(expenseRequest.getToDate(), inputFormatter);
		String formattedFromDate = fromDate.format(databaseFormatter);
		String formattedToDate = toDate.format(databaseFormatter);

		log.debug("[{}] ==> Database From Date ==> {}", httpSession.getId(), formattedFromDate);
		log.debug("[{}] ==> Database To Date ==> {}", httpSession.getId(), formattedToDate);

		List<FinTransactionDetails> expenseReportByDateList = Optional
				.ofNullable(transactionRepository.findTransactionReportByDate(formattedFromDate, formattedToDate))
				.filter(list -> !list.isEmpty()).orElseThrow(() -> {
					log.error("[{}] ==> Report Not Found", httpSession.getId());
					return new FinovaException("FIN1002", "Report Not Found");
				});

		log.debug("[{}] ==> Expense Fetched From Database Successfully : {}", httpSession.getId(), expenseReportByDateList);

		return expenseReportByDateList;
	}

	@Override
	public List<FinTransactionDetails> getMonthlyReport(TransactionDetails transactionDetails) {
		log.debug("[{}] Monthly Report Service :: {}", httpSession.getId(), transactionDetails);
		
		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    return new FinovaException("MPAY1019", "Please login to access this service");
                });
		log.debug("[{}] ==> finovaUserDetails ==> {}", httpSession.getId(), finovaUserDetails);
		
//		if(transactionDetails.getTxnType() == null || transactionDetails.getTxnType().equals("")) {
//			log.debug("[{}] TXN Type is Null", httpSession.getId());
//			return null;
//		}

		int year = Integer.parseInt(transactionDetails.getYear());
		int month = Integer.parseInt(transactionDetails.getMonth());

		System.out.println("year >> " + year + " month >> " + month);

		List<FinTransactionDetails> monthlyReport = Optional
				.ofNullable(transactionRepository.getMonthlyReport(year, month)).filter(list -> !list.isEmpty())
				.orElseThrow(() -> {
					log.error("[{}] ==> No Report Found", httpSession.getId());
					return new FinovaException("FIN1002", "Report Not Found");
				});

		return monthlyReport;
	}

	@Override
	public List<FinTransactionDetails> generateExpenseReportByCategory(TransactionDetails expenseRequest) {
		log.debug("[{}] Search Expense By Category Service :: {}", httpSession.getId(), expenseRequest);
		
		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    return new FinovaException("MPAY1019", "Please login to access this service");
                });
		log.debug("[{}] ==> finovaUserDetails ==> {}", httpSession.getId(), finovaUserDetails);

		if (!requestValidator.validateDetailsforExpenseReportByCategory(expenseRequest)) {
			log.info("[{}] ==> Validation failed", httpSession.getId());
			String respMessage = Optional.ofNullable((String) httpSession.getAttribute("respMsg"))
					.orElse("Unknown error");
			throw new FinovaException("FIN1002", respMessage);
		}

		List<FinTransactionDetails> expenseReportByDateList = Optional
				.ofNullable(transactionRepository.findExpenseReportByCategory(
						expenseRequest.getTransactionTitle(), expenseRequest.getFromDate(), expenseRequest.getToDate()))
				.filter(list -> !list.isEmpty()).orElseThrow(() -> {
					log.error("[{}] ==> No Expense Report Found", httpSession.getId());
					return new FinovaException("FIN1002", "No expense report found");
				});

		log.debug("[{}] ==> Expense Fetched From Database Successfully : {}", httpSession.getId(),
				expenseReportByDateList);
		return expenseReportByDateList;
	}

}
