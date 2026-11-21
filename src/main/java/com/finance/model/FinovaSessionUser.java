package com.finance.model;

import java.io.Serializable;

import org.json.JSONObject;

import lombok.Data;

@Data
public class FinovaSessionUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userId;
	private String loginTime;
	private JSONObject sessionData;

	public FinovaSessionUser(String userId, String loginTime) {
        this.userId = userId;
        this.loginTime = loginTime;
    }

}
