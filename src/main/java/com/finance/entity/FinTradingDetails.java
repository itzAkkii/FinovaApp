package com.finance.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "FIN_TRADING_DETAILS")
public class FinTradingDetails {

	@Id
	@Column(name = "TRADE_ID")
	private String tradeId;

	@Column(name = "MARKET")
	private String market;

	@Column(name = "SYMBOL")
	private String symbol;
	
	@Column(name = "PRODUCT_TYPE")
	private String productType;

	@Column(name = "INSTRUMENT_TYPE")
	private String instrumentType;

	@Column(name = "DIRECTION")
	private String direction;

	@Column(name = "ENTRY_DATE")
	private String entryDate;
	
	@Column(name = "ENTRY_TIME")
	private String entryTime;

	@Column(name = "ENTRY_PRICE")
	private BigDecimal entryPrice;
	
	@Column(name = "EXIT_PRICE")
	private BigDecimal exitPrice;

	@Column(name = "QUANTITY")
	private BigDecimal quantity;

	@Column(name = "STOP_LOSS")
	private BigDecimal stopLoss;

	@Column(name = "TARGET_PRICE")
	private BigDecimal targetPrice;
	
	@Column(name = "BROKERAGE")
	private String brokerage;
	
	@Column(name = "BROKER")
	private String broker;

	@Column(name = "CURRENCY")
	private String currency;

	@Column(name = "DESCRIPTION")
	private String desc;
	
	@Column(name = "NETPL")
	private String netProfitLoss;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_AT")
	private String createdAt;
	
	@Column(name = "USER_ID")
	private String userId;
}
