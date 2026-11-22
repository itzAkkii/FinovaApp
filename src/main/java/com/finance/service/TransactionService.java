package com.finance.service;

import java.math.BigDecimal;
import java.util.List;

import com.finance.entity.FinTransactionDetails;
import com.finance.model.TransactionDetails;

public interface TransactionService {
	
	BigDecimal getTotalIncome(int year, int month);

	BigDecimal getTotalExpense(int year, int month);
	
	BigDecimal getOverallBalance();
	
//	public List<FinTransactionDetails> getRecentTransactions();

	public FinTransactionDetails addExpense(TransactionDetails transactionDetails);

	public List<FinTransactionDetails> generateExpenseReportByDateRange(TransactionDetails transactionDetails);

	public List<FinTransactionDetails> getMonthlyReport(TransactionDetails transactionDetails);
	
	public List<FinTransactionDetails> generateExpenseReportByCategory(TransactionDetails transactionDetails);

}
