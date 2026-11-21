package com.finance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetails {

	private String userId;
	private String userName;
	private String password;
	private String loggingMessage;
	private String lastLogin;
}
