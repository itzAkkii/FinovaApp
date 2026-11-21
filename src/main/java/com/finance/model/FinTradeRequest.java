package com.finance.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FinTradeRequest {
	
	private String market;
	private String symbol;
	private String productType;
	private String instrumentType;
	private String direction;
	private String entryDate;
	private String entryTime;
	private BigDecimal entryPrice;
	private BigDecimal exitPrice;
	private BigDecimal quantity;
	private BigDecimal stopLoss;
	private BigDecimal targetPrice;
	private String broker;
	private String brokerage;
	private String currency;
	private String description;
	private String netProfitLoss;
	private String status;
	private String createdAt;
}
