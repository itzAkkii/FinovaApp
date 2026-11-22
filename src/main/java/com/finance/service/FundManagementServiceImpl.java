package com.finance.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.finance.entity.FinFundTransactionDetails;
import com.finance.entity.FinTransactionDetails;
import com.finance.entity.FinovaUserDetails;
import com.finance.exception.FinovaException;
import com.finance.model.FundTransactionRequest;
import com.finance.repository.FinTransactionRepository;
import com.finance.repository.FundManagementRepository;
import com.finance.utils.FinUtils;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FundManagementServiceImpl implements FundManagementService {

	private final HttpSession httpSession;
	private final FinUtils finUtils;
	private final FundManagementRepository fundManagementRepository;
	private final FinTransactionRepository transactionRepository;
	private final CustomerService customerService;

	public FundManagementServiceImpl(HttpSession httpSession, FinUtils finUtils,
			FundManagementRepository fundManagementRepository, FinTransactionRepository transactionRepository,
			CustomerService customerService) {
		this.httpSession = httpSession;
		this.finUtils = finUtils;
		this.fundManagementRepository = fundManagementRepository;
		this.transactionRepository = transactionRepository;
		this.customerService = customerService;
	}

	@Override
	public FinFundTransactionDetails fundTransfer(FundTransactionRequest fundTransactionRequest) {
		log.debug("[{}] Fund Transfer Service :: {}", httpSession.getId(), fundTransactionRequest);

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});

		FinFundTransactionDetails finFundTransactionDetails = new FinFundTransactionDetails();
		finFundTransactionDetails
				.setFundId("FUND" + this.finUtils.dateFormat() + this.finUtils.generateRandomString(2));

		String txnId = "CUST" + this.finUtils.getDateyyyyMMddHHmmss() + this.finUtils.generateRandomString(4);
		finFundTransactionDetails.setTransactionId(txnId);

		finFundTransactionDetails.setPersonName(fundTransactionRequest.getPersonName());
		finFundTransactionDetails.setTotalAmount(fundTransactionRequest.getTotalAmount());
		finFundTransactionDetails.setTransactionType(fundTransactionRequest.getPaymentMode());
		finFundTransactionDetails.setFundType(fundTransactionRequest.getFundType());

		Optional.ofNullable(fundTransactionRequest.getTxnDate()).ifPresent(finFundTransactionDetails::setTxnDate);
		Optional.ofNullable(fundTransactionRequest.getDueDate()).ifPresent(finFundTransactionDetails::setDueDate);
		Optional.ofNullable(fundTransactionRequest.getDescription()).ifPresent(finFundTransactionDetails::setRemarks);

		finFundTransactionDetails.setFundStatus("ACTIVE");
		finFundTransactionDetails.setStatus("S");
		finFundTransactionDetails.setCreatedDate(this.finUtils.getDate());
		finFundTransactionDetails.setDate(this.finUtils.getDate());
		finFundTransactionDetails.setUserId(finovaUserDetails.getUserId());

		log.debug("[{}] ==> Adding Fund to Database ==> {}", httpSession.getId(), finFundTransactionDetails);
		FinFundTransactionDetails savedFinTxnDetails = Optional
				.of(this.fundManagementRepository.save(finFundTransactionDetails)).orElseThrow(() -> {
					log.error("[{}] ==> Failed Add Fund into DB", httpSession.getId());
					return new FinovaException("FIN1002", "Internal issue! Please try later");
				});
		log.debug("[{}] ==> Fund Saved into Database : {}", httpSession.getId(), savedFinTxnDetails);

		FinTransactionDetails finTransactionDetails = new FinTransactionDetails();
		finTransactionDetails.setFinTxnId("FIN" + this.finUtils.dateFormat() + this.finUtils.generateRandomString(2));

		finTransactionDetails.setTransactionId(txnId);
		finTransactionDetails.setTransactionReferenceNumber(finFundTransactionDetails.getFundId());
		finTransactionDetails.setTransactionTitle("Fund-Transfer");
		finTransactionDetails.setAmount(fundTransactionRequest.getTotalAmount());
		finTransactionDetails.setPaymentMode(fundTransactionRequest.getPaymentMode());
		finTransactionDetails.setCurrency("INR");
		finTransactionDetails.setTransactionCategory("FUND");

		String fundType = fundTransactionRequest.getFundType();
		if ("GIVEN".equalsIgnoreCase(fundType)) {
			finTransactionDetails.setTransactionType("EXPENSE");
			finTransactionDetails.setTransactionTitle("Send-Money");
		} else if ("RECEIVED".equalsIgnoreCase(fundType)) {
			finTransactionDetails.setTransactionType("INCOME");
			finTransactionDetails.setTransactionTitle("Recieved-Money");
		} else {
			finTransactionDetails.setTransactionType("UNKNOWN");
		}

		Optional.ofNullable(fundTransactionRequest.getDescription()).ifPresent(finTransactionDetails::setDescription);
		Optional.ofNullable(fundTransactionRequest.getLocation()).ifPresent(finTransactionDetails::setLocation);

		finTransactionDetails.setDate(this.finUtils.getDate());
		finTransactionDetails.setDateAndTime(this.finUtils.getDateAndTime());
		finTransactionDetails.setStatus("S");
		finTransactionDetails.setUserId(finovaUserDetails.getUserId());

		log.debug("[{}] ==> Adding Transaction into Database ==> {}", httpSession.getId(), finTransactionDetails);
		FinTransactionDetails savedTransaction = Optional.of(this.transactionRepository.save(finTransactionDetails))
				.orElseThrow(() -> {
					log.error("[{}] ==> Failed to save Transaction to Database", httpSession.getId());
					return new FinovaException("FIN1002", "Internal issue! Please try later");
				});

		log.debug("[{}] ==> Transaction Saved into Database Successfully : {}", httpSession.getId(), savedTransaction);
		log.debug("[{}] ==> Fund Transfer done Successfully : {}", httpSession.getId(), savedFinTxnDetails);
		return savedFinTxnDetails;
	}

	@Override
	public List<FinFundTransactionDetails> findFunds() {
		log.debug("[{}] Find Funds Service ::", httpSession.getId());

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});

		return Optional.ofNullable(this.fundManagementRepository.getAllFunds(finovaUserDetails.getUserId()))
				.orElse(Collections.emptyList());
	}

	@Override
	public FinFundTransactionDetails updateEvent(FundTransactionRequest request) {
		log.debug("[{}] Fund Updation Service {}", httpSession.getId(), request);

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});

		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		String fundId = request.getFundId();

		if (fundId == null || fundId.isBlank()) {
			throw new FinovaException("FIN1002", "Fund ID not found");
		}

		FinFundTransactionDetails existingFundDetails = fundManagementRepository.findById(fundId)
				.orElseThrow(() -> new FinovaException("FIN1002", "Fund Not Found"));

		// Validate fund ownership
		if (!existingFundDetails.getUserId().equals(finovaUserDetails.getUserId())) {
			throw new FinovaException("FIN1002", "Invalid Fund Details");
		}

		BigDecimal totalAmount = new BigDecimal(existingFundDetails.getTotalAmount());

		BigDecimal alreadyPaid = existingFundDetails.getRepaidAmount() == null
				|| existingFundDetails.getRepaidAmount().isBlank() ? BigDecimal.ZERO
						: new BigDecimal(existingFundDetails.getRepaidAmount());

		if (request.getPaidAmount() == null || request.getPaidAmount().isBlank()) {
			throw new FinovaException("FIN1002", "Paid Amount is required");
		}

		BigDecimal currentPayment = new BigDecimal(request.getPaidAmount());

		if (currentPayment.compareTo(BigDecimal.ZERO) <= 0) {
			throw new FinovaException("FIN1002", "Payment must be greater than zero");
		}

		BigDecimal updatedPaidAmount = alreadyPaid.add(currentPayment);

		if (updatedPaidAmount.compareTo(totalAmount) > 0) {
			throw new FinovaException("FIN1002", "Payment exceeds remaining amount");
		}

		BigDecimal remainingAmount = totalAmount.subtract(updatedPaidAmount);

		// Update fund payment details
		existingFundDetails.setRepaidAmount(updatedPaidAmount.toString());
		existingFundDetails.setRemainingAmount(remainingAmount.toString());

		Optional.ofNullable(request.getPersonName()).ifPresent(existingFundDetails::setPersonName);
		Optional.ofNullable(request.getPaymentMode()).ifPresent(existingFundDetails::setTransactionType);
		Optional.ofNullable(request.getFundType()).ifPresent(existingFundDetails::setFundType);
		Optional.ofNullable(request.getDueDate()).ifPresent(existingFundDetails::setDueDate);
		Optional.ofNullable(request.getDescription()).ifPresent(existingFundDetails::setRemarks);

		existingFundDetails.setTxnDate(finUtils.getDate());

		// Update fund status based on remaining amount
		existingFundDetails.setFundStatus(remainingAmount.compareTo(BigDecimal.ZERO) == 0 ? "COMPLETED" : "ACTIVE");
		existingFundDetails.setStatus("S");

		log.debug("[{}] ==> Updating Fund Details ==> {}", httpSession.getId(), existingFundDetails);
		FinFundTransactionDetails savedFundDetails = fundManagementRepository.save(existingFundDetails);
		log.debug("[{}] ==> Fund Updated Successfully ==> {}", httpSession.getId(), savedFundDetails);

		// Create individual repayment transaction
		FinTransactionDetails finTransactionDetails = new FinTransactionDetails();

		String txnId = "CUST" + this.finUtils.getDateyyyyMMddHHmmss() + this.finUtils.generateRandomString(4);

		finTransactionDetails.setFinTxnId("FIN" + this.finUtils.dateFormat() + this.finUtils.generateRandomString(2));

		finTransactionDetails.setTransactionId(txnId);
		finTransactionDetails.setTransactionReferenceNumber(fundId);
		finTransactionDetails.setTransactionTitle("Fund-Repayment");
		finTransactionDetails.setAmount(currentPayment.toString());
		finTransactionDetails.setPaymentMode(request.getPaymentMode());
		finTransactionDetails.setCurrency("INR");
		finTransactionDetails.setTransactionCategory("FUND");

		String fundType = existingFundDetails.getFundType();

		if ("GIVEN".equalsIgnoreCase(fundType)) {
			finTransactionDetails.setTransactionType("INCOME");
			finTransactionDetails.setTransactionTitle("Received-Money");
		} else if ("RECEIVED".equalsIgnoreCase(fundType)) {
			finTransactionDetails.setTransactionType("EXPENSE");
			finTransactionDetails.setTransactionTitle("Repaid-Money");
		} else {
			throw new FinovaException("FIN1002", "Invalid Fund Type");
		}

		Optional.ofNullable(request.getDescription()).ifPresent(finTransactionDetails::setDescription);

		finTransactionDetails.setDate(finUtils.getDate());
		finTransactionDetails.setDateAndTime(finUtils.getDateAndTime());
		finTransactionDetails.setStatus("S");
		finTransactionDetails.setUserId(finovaUserDetails.getUserId());

		log.debug("[{}] ==> Adding Repayment Transaction ==> {}", httpSession.getId(), finTransactionDetails);

		FinTransactionDetails savedTransaction = transactionRepository.save(finTransactionDetails);

		log.debug("[{}] ==> Repayment Transaction Saved Successfully ==> {}", httpSession.getId(), savedTransaction);

		return savedFundDetails;
	}

	@Override
	public String deleteFund(String id) {
		log.debug("[{}] Fund Deleted Service {}:: ", httpSession.getId(), id);

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		if (id == null) {
			log.debug("[{}] Fund Id Not Found In Request:: ", httpSession.getId());
			throw new FinovaException("FIN1002", "Internal issue! Please try later");
		}

		FinFundTransactionDetails existingFundDetails = this.fundManagementRepository.findById(id).orElseThrow(() -> {
			log.debug("Financial event not found with ID: {}", id);
			return new FinovaException("FIN1002", "Internal issue! Please try later");
		});

		log.debug("[{}] Existing Fund Details Found {}:: ", httpSession.getId(), existingFundDetails);
		existingFundDetails.setFundStatus("IN-ACTIVE");
		existingFundDetails.setDate(this.finUtils.getDate());
		fundManagementRepository.save(existingFundDetails);

		log.debug("[{}] Fund Deleted Successfully :: ", httpSession.getId());
		return "Fund Deleted Successfully";
	}

}
