package com.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "FIN_GOAL_Details")
public class FinGoalDetails {

	@Id
	@Column(name = "GOAL_ID")
	private String goalId;
	
	@Column(name = "GOAL_TITLE")
	private String goalTitle;
	
	@Column(name = "CURRENT_AMOUNT")
	private String currentAmount;
	
	@Column(name = "INITIAL_AMOUNT")
	private String amount;
	
	@Column(name = "TARGET_AMOUNT")
	private String targetAmount;

	@Column(name = "CCY")
	private String currency;
	
	@Column(name = "TARGET_DATE")
	private String targetDate;
	
	@Column(name = "GOAL")
	private String goal;
	
	@Column(name = "DATE")
	private String Date;
	
	@Column(name = "DTCREATED")
	private String dtCreated;
	
	@Column(name = "DATETIME")
	private String dateAndTime;
	
	@Column(name = "PRIORITY")
	private String priority;
	
	@Column(name = "Status")
	private String status;
	
	@Column(name = "ACTIVE_FLAG")
	private String flag;
	
	@Column(name = "USER_ID")
	private String userId;
}
