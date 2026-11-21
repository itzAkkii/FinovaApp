package com.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "FIN_TRANSACTION_DETAILS")
public class FinTransactionDetails {

	@Id
	@Column(name = "FIN_ID")
	private String finTxnId;
	
	@Column(name = "TXN_ID")
	private String transactionId;

	@Column(name = "TRANSACTION_TITLE")
	private String transactionTitle;
	
	@Column(name = "TRANSACTION_CATEGORY")
	private String transactionCategory;

	@Column(name = "AMOUNT")
	private String amount;
	
	@Column(name = "CCY")
	private String currency;
	
	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "PAYMENT_MODE")
	private String paymentMode;
	
	@Column(name = "TXN_REF_NO")
	private String transactionReferenceNumber;

	@Column(name = "DATE")
	private String date;

	@Column(name = "DATETIME")
	private String dateAndTime;

	@Column(name = "LOCATION")
	private String location;

	@Column(name = "TRANSACTION_TYPE")
	private String transactionType;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "USER_ID")
	private String userId;
}
