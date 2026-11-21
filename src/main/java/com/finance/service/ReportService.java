package com.finance.service;

import com.finance.model.TransactionDetails;

public interface ReportService {

	public byte[] downloadTxnReport(TransactionDetails transactionDetails);

	public byte[] downloadMonthlyTxnReport(TransactionDetails transactionDetails);

}
