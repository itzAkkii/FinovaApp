package com.finance.service;

import com.finance.model.FinovaSessionUser;

public interface FinovaSessionManager {

	FinovaSessionUser getFinovaUser(String sessionId);

	String addFinovaUser(FinovaSessionUser finovaSessionUser);

	String deleteFinovaUser(String lastSession);
}
