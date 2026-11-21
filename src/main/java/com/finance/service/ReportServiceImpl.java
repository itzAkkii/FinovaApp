package com.finance.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.finance.entity.FinTransactionDetails;
import com.finance.entity.FinovaUserDetails;
import com.finance.exception.FinovaException;
import com.finance.model.TransactionDetails;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
	
	private final HttpSession httpSession;
	private final TransactionService transactionService;
	private final CustomerService customerService;
	
	public ReportServiceImpl(HttpSession httpSession, TransactionService transactionService, CustomerService customerService) {
		this.httpSession = httpSession;
		this.transactionService = transactionService;
		this.customerService = customerService;
	}

	@Override
	public byte[] downloadTxnReport(TransactionDetails transactionDetails) {
		log.debug("[{}] Download Transaction Report Service :: {}", httpSession.getId(), transactionDetails);
		StringBuilder csvData = new StringBuilder();
		
		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    return new FinovaException("FIN1002", "Please login to access this service");
                });
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);
		
		List<FinTransactionDetails> transactionReport = this.transactionService.generateExpenseReportByDateRange(transactionDetails);
		log.debug("[{}] Report Records Found :: {}", httpSession.getId(), transactionReport.size());

		// CSV Header
				csvData.append("Fin ID,Transaction Title,Category,Amount,Currency,")
						.append("Description,Payment Mode,Transaction ID,Date,Date & Time,")
						.append("Location,Transaction Type,Status").append("\n");

				// CSV Data
				for (FinTransactionDetails transaction : transactionReport) {
					csvData.append(escapeCsv(transaction.getFinTxnId())).append(",")
							.append(escapeCsv(transaction.getTransactionTitle())).append(",")
							.append(escapeCsv(transaction.getTransactionCategory())).append(",")
							.append(escapeCsv(transaction.getAmount())).append(",").append(escapeCsv(transaction.getCurrency()))
							.append(",").append(escapeCsv(transaction.getDescription())).append(",")
							.append(escapeCsv(transaction.getPaymentMode())).append(",")
							.append(escapeCsv(transaction.getTransactionId())).append(",")
							.append(escapeCsv(transaction.getDate())).append(",")
							.append(escapeCsv(transaction.getDateAndTime())).append(",")
							.append(escapeCsv(transaction.getLocation())).append(",")
							.append(escapeCsv(transaction.getTransactionType())).append(",")
							.append(escapeCsv(transaction.getStatus())).append("\n");
				}

				log.info("[{}] Txn Report CSV Generated Successfully", httpSession.getId());
				return csvData.toString().getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public byte[] downloadMonthlyTxnReport(TransactionDetails transactionDetails) {
		log.debug("[{}] Download Monthly Report Service :: {}", httpSession.getId(), transactionDetails);
		StringBuilder csvData = new StringBuilder();
		
		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
                .orElseThrow(() -> {
                    log.debug("[{}] ==> Please login to access this service", httpSession.getId());
                    return new FinovaException("FIN1002", "Please login to access this service");
                });
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		// Fetch monthly transaction data from DB
		List<FinTransactionDetails> monthlyReport = this.transactionService.getMonthlyReport(transactionDetails);
		log.debug("[{}] Report Records Found :: {}", httpSession.getId(), monthlyReport.size());

		// CSV Header
		csvData.append("Fin ID,Transaction Title,Category,Amount,Currency,")
				.append("Description,Payment Mode,Transaction ID,Date,Date & Time,")
				.append("Location,Transaction Type,Status").append("\n");

		// CSV Data
		for (FinTransactionDetails transaction : monthlyReport) {
			csvData.append(escapeCsv(transaction.getFinTxnId())).append(",")
					.append(escapeCsv(transaction.getTransactionTitle())).append(",")
					.append(escapeCsv(transaction.getTransactionCategory())).append(",")
					.append(escapeCsv(transaction.getAmount())).append(",").append(escapeCsv(transaction.getCurrency()))
					.append(",").append(escapeCsv(transaction.getDescription())).append(",")
					.append(escapeCsv(transaction.getPaymentMode())).append(",")
					.append(escapeCsv(transaction.getTransactionId())).append(",")
					.append(escapeCsv(transaction.getDate())).append(",")
					.append(escapeCsv(transaction.getDateAndTime())).append(",")
					.append(escapeCsv(transaction.getLocation())).append(",")
					.append(escapeCsv(transaction.getTransactionType())).append(",")
					.append(escapeCsv(transaction.getStatus())).append("\n");
		}

		log.info("[{}] Monthly Report CSV Generated Successfully", httpSession.getId());
		return csvData.toString().getBytes(StandardCharsets.UTF_8);
	}

	private String escapeCsv(Object value) {

		if (value == null) {
			return "";
		}

		String data = String.valueOf(value);

		// Escape double quotes
		data = data.replace("\"", "\"\"");

		// Wrap value in quotes if required
		if (data.contains(",") || data.contains("\"") || data.contains("\n") || data.contains("\r")) {

			data = "\"" + data + "\"";
		}

		return data;
	}

}
