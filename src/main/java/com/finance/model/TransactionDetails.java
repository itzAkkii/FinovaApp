package com.finance.model;

import lombok.Data;

@Data
public class TransactionDetails {

    private String transactionTitle;
    private String amount;
    private String currency;
    private String transactionCategory;
    private String description;
    private String paymentMode;
    private String location;
    private String transactionType;  // INCOME or EXPENSE
    
    private String fromDate;
    private String toDate;
    
    private String totalExpense;
    private String totalIncome;
    
    private String month;
    private String year;
    
    private String txnType;
    
}
