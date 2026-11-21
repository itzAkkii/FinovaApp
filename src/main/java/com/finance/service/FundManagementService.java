package com.finance.service;

import java.util.List;

import com.finance.entity.FinFundTransactionDetails;
import com.finance.model.FundTransactionRequest;

public interface FundManagementService {

	public FinFundTransactionDetails fundTransfer(FundTransactionRequest fundTransactionRequest);

	public List<FinFundTransactionDetails> findFunds();

	public FinFundTransactionDetails updateEvent(FundTransactionRequest fundTransactionRequest);

	public String deleteFund(String id);

}
