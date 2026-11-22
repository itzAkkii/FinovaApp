package com.finance.model;

import lombok.Data;

@Data
public class FinMailRequest {

	private String to;
	private String subject;
	private String message;
}
