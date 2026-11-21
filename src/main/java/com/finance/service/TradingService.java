package com.finance.service;

import java.math.BigDecimal;
import java.util.List;

import com.finance.entity.FinTradingDetails;
import com.finance.entity.TradingFundsDetails;
import com.finance.model.AddMoneyRequest;
import com.finance.model.FinTradeRequest;

public interface TradingService {
	
	public TradingFundsDetails addMoney(AddMoneyRequest addMoneyRequest);
	
	public BigDecimal fetchTradingCapital();
	
	public List<FinTradingDetails> getRecentTrades();

	public FinTradingDetails saveTrade(FinTradeRequest finTradeRequest);

	public FinTradingDetails updateTrade(FinTradingDetails finTradingDetails);

	BigDecimal getTotalCapital();

	public List<FinTradingDetails> getTradeList();

}
