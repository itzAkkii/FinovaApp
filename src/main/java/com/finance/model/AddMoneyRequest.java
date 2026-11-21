package com.finance.model;

import lombok.Data;

@Data
public class AddMoneyRequest {

	private String brokerName;
	private String amount;
	private String currency;
	private String paymentMode;
	private String paymentApp;
	private String remarks;
	private String txnType;
}
