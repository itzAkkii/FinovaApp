package com.finance.model;

import lombok.Data;

@Data
public class JournalEntryDetails {
	
	private String entryType;      // DIARY / FINANCIAL_GOAL
	private String title;
	private String subject;
	private String category;
	private String date;
	private String priority;
	
	private String amount;
	private String currentAmount;
	private String targetAmount;
	private String targetDate;
	
    private String userId;

	
}
