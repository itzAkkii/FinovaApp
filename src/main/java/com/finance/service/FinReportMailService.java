package com.finance.service;

import com.finance.entity.FinovaUserDetails;

public interface FinReportMailService {

	void sendMonthlyReport(FinovaUserDetails user, int year, int month);

}
