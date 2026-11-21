package com.finance.model;

import lombok.Data;

@Data
public class FundTransactionRequest {

	private String fundId;
    private String personName;
    private String fundType; // RECEIVED / GIVEN
    private String totalAmount;
    private String repaidAmount;
    private String paidAmount;
    private String dueDate;
    private String paymentMode; // CASH / UPI / BANK
    private String description;
    private String txnDate;
    private String status;
    private String location;
    
}
