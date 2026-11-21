package com.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "FIN_TRADING_TXN_DETAILS")
public class TradingFundsDetails {

	@Id
	@Column(name = "TRADE_TXN_ID")
	private String tradeTxnId;

	@Column(name = "TXN_ID")
	private String transactionId;

	@Column(name = "BROKER_NAME")
	private String brokerName;

	@Column(name = "AMOUNT")
	private String amount;

	@Column(name = "CCY")
	private String currency;

	@Column(name = "PAYMENT_MODE")
	private String paymentMode;

	@Column(name = "PAYMENTAPP")
	private String paymentApp;

	@Column(name = "UPI_TXN_ID")
	private String upiTransactionId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "TXN_DATE")
	private String transactionDate;

	@Column(name = "USER_ID")
	private String userId;
}
