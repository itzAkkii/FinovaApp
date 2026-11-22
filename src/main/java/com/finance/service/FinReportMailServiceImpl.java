package com.finance.service;

import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.finance.entity.FinTransactionDetails;
import com.finance.entity.FinovaUserDetails;
import com.finance.repository.FinTransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FinReportMailServiceImpl implements FinReportMailService {

	private final FinTransactionRepository transactionRepository;
	private final FinMailService finMailService;

	public FinReportMailServiceImpl(FinTransactionRepository transactionRepository, FinMailService finMailService) {
		this.transactionRepository = transactionRepository;
		this.finMailService = finMailService;
	}

	public byte[] generateMonthlyReport(String customerId, int year, int month) {
		log.debug("Generating Monthly Report for Customer :: {}", customerId);

		String datePattern = "%-" + String.format("%02d", month) + "-" + year;

		log.info("Customer ID :: {}", customerId);
		log.info("Date Pattern :: {}", datePattern);
		
		List<FinTransactionDetails> monthlyReport = transactionRepository.getMonthlyReportByCustomer(customerId, year, month);
		log.info("Monthly Report Record Count :: {}", monthlyReport.size());
		if (monthlyReport == null || monthlyReport.isEmpty()) {
			log.info("No Transactions Found for Customer :: {}", customerId);
			return new byte[0];
		}

		StringBuilder csvData = new StringBuilder();

		// CSV Header
		csvData.append("Fin ID,Transaction Title,Category,Amount,Currency,")
				.append("Description,Payment Mode,Transaction ID,Date,Date & Time,")
				.append("Location,Transaction Type,Status\n");

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

		log.info("Monthly CSV Generated :: Customer :: {} Records :: {}", customerId, monthlyReport.size());
		return csvData.toString().getBytes(StandardCharsets.UTF_8);
	}

	public void sendMonthlyReport(FinovaUserDetails finovaUserDetails, int year, int month) {

		byte[] report = generateMonthlyReport(finovaUserDetails.getUserId(), year, month);

		if (report.length == 0) {
			log.info("Monthly Report Skipped :: {}", finovaUserDetails.getUserId());
			return;
		}

		String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		String fileName = String.format("FINOVA_%s_%d.csv", monthName, year);
		String subject = String.format("FINOVA Monthly Transaction Report - %s %d", monthName, year);
		String message = String.format("""
				Dear %s,

				Please find attached your FINOVA monthly transaction report
				for %s %d.

				Thank you for using FINOVA.

				Regards,
				FINOVA Support Team
				""", finovaUserDetails.getUserName(), monthName, year);

		finMailService.sendMailWithAttachment(finovaUserDetails.getEmail(), subject, message, fileName, report);

		log.info("Monthly Report Email Sent Successfully :: {}", finovaUserDetails.getEmail());
	}

	private String escapeCsv(Object value) {
		if (value == null) {
			return "";
		}
		String data = value.toString();
		return "\"" + data.replace("\"", "\"\"") + "\"";
	}
}
