package com.finance.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "FIN_EVENT_DETAILS")
public class FinEventDetails {

	@Id
	@JsonProperty("__FVE1")
	@Column(name = "EVENT_ID")
	private String __FVE1; 
	
	@JsonProperty("__FVE2")
	@Column(name = "EVENT_TITLE")
	private String __FVE2;
	
	@JsonProperty("__FVE3")
	@Column(name = "EVENT_TYPE")
	private String __FVE3;
	
	@JsonProperty("__FVE4")
	@Column(name = "AMOUNT")
	private String __FVE4;
	
	@JsonProperty("__FVE5")
	@Column(name = "EVENT_DATE")
	private String __FVE5;
	
	@JsonProperty("__FVE6")
	@Column(name = "FREQUENCY")
	private String __FVE6;
	
	@Column(name = "REMINDER_DAYS")
	private String reminder;   
	
	@Column(name = "EVENT_STATUS")
	private String status;    // active / inactive
	
	@Column(name = "DESCRIPTION")
	private String desc;
	
	@Column(name = "CREATED_AT")
	private String __created_at;
	
	@Column(name = "END_AT")
	private String __event_end_at;
	
	@Column(name = "USER_ID")
	private String userId;
}
