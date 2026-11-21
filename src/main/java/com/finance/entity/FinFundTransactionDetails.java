package com.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "FIN_FUND_TRANSFER_DETAILS")
public class FinFundTransactionDetails {

	@Id
	@Column(name = "FUND_ID")
	private String fundId;

	@Column(name = "TXN_ID")
	private String transactionId;

	@Column(name = "PERSON_NAME")
	private String personName;

	@Column(name = "TRANSACTION_TYPE")
	private String transactionType;
	
	@Column(name = "FUND_TYPE")
	private String fundType;

	@Column(name = "DUE_DATE")
	private String dueDate;

	@Column(name = "TOTAL_AMOUNT")
	private String totalAmount;
	
	@Column(name = "REPAID_AMOUNT")
	private String repaidAmount;
	
	@Column(name = "REMAINING_AMOUNT")
	private String remainingAmount;
	
	@Column(name = "TXN_DATE")
	private String txnDate;
	
	@Column(name = "CREATED_DATE")
	private String createdDate;
	
	@Column(name = "DATE")
	private String date;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "FUND_STATUS")
	private String fundStatus;

	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "USER_ID")
	private String userId;
}
