package com.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "FIN_DIARY_Details")
public class FinDiaryDetails {

	@Id
	@Column(name = "NOTE_ID")
	private String noteId;

	@Column(name = "NOTE_TITLE")
	private String noteTitle;

	@Column(name = "DIARY")
	private String diary;

	@Column(name = "DATE")
	private String Date;

	@Column(name = "DATETIME")
	private String dateAndTime;

	@Column(name = "ACTIVE_FLAG")
	private String flag;
	
	@Column(name = "Status")
	private String status;

	@Column(name = "USER_ID")
	private String userId;
}
