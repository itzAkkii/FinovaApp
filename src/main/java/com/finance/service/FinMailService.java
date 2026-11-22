package com.finance.service;

import com.finance.model.FinMailRequest;

public interface FinMailService {

	String sendMail(FinMailRequest finMailRequest);

	void sendMailWithAttachment(String to, String subject, String message, String fileName, byte[] attachment);
}
