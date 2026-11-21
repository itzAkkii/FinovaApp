package com.finance.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finance.entity.FinovaUserDetails;
import com.finance.model.AddMoneyRequest;
import com.finance.model.CustomerDetails;
import com.finance.model.FinTradeRequest;
import com.finance.model.TransactionDetails;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestValidator {

	@Autowired
	private HttpSession httpSession;
	
	public boolean preValidateDetailsforUser(FinovaUserDetails userRegister) {
		
		if (userRegister.getMobileNo() != null && userRegister.getMobileNo().trim().isEmpty()) {
			log.debug("[{}] Mobile Number is invalid", httpSession.getId());
			httpSession.setAttribute("respMsg", "Mobile Number is empty, please fill mandatory fields!");
			return false;
		}

		log.debug("[{}] required fields validated successfully", httpSession.getId());
		return true;
	}

	public boolean validateDetailsforUserRegistration(FinovaUserDetails userRegister) {
		log.debug("[{}] Validating Required Fields for User Registration", httpSession.getId());

		// First Name
		if (userRegister.getFirstName() == null || userRegister.getFirstName().trim().isEmpty()) {
			httpSession.setAttribute("respMsg", "First Name is Empty, Please Fill mandatory Fields !");
			return false;
		}

		// Last Name
		if (userRegister.getLastName() == null || userRegister.getLastName().trim().isEmpty()) {
			httpSession.setAttribute("respMsg", "Last Name is Empty, Please Fill mandatory Fields !");
			return false;
		}

		// Email
		String email = userRegister.getEmail();
		if (email == null || email.trim().isEmpty()) {
			httpSession.setAttribute("respMsg", "Email is Empty, Please Fill mandatory Fields !");
			return false;
		}

		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		if (!email.matches(emailRegex)) {
			httpSession.setAttribute("respMsg", "Please Enter a Valid Email Address !");
			return false;
		}

		// Mobile Number
		String mobileNo = userRegister.getMobileNo();
		if (mobileNo == null || mobileNo.trim().isEmpty()) {
			httpSession.setAttribute("respMsg", "Mobile Number is Empty, Please Fill mandatory Fields !");
			return false;
		}

		if (!mobileNo.matches("^[6-9]\\d{9}$")) {
			httpSession.setAttribute("respMsg", "Please Enter a Valid 10 Digit Indian Mobile Number !");
			return false;
		}

		// Date Of Birth
		if (userRegister.getDob() == null || userRegister.getDob().toString().trim().isEmpty()) {
			httpSession.setAttribute("respMsg", "Date Of Birth is Empty, Please Fill mandatory Fields !");
			return false;
		}

		// Marital Status
		if (userRegister.getMaritalStatus() == null || userRegister.getMaritalStatus().trim().isEmpty()) {
			httpSession.setAttribute("respMsg", "Marital Status is Empty, Please Fill mandatory Fields !");
			return false;
		}

		// User Name
		if (userRegister.getUserName() == null || userRegister.getUserName().trim().isEmpty()) {
			httpSession.setAttribute("respMsg", "User Name is Empty, Please Fill mandatory Fields !");
			return false;
		}

		// Password
		String password = userRegister.getPassword();
		if (password == null || password.trim().isEmpty()) {
			httpSession.setAttribute("respMsg", "Password is Empty, Please Fill mandatory Fields !");
			return false;
		}

		String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+=-])[A-Za-z\\d@$!%*?&#^()_+=-]{8,20}$";

		if (!password.matches(passwordRegex)) {
			httpSession.setAttribute("respMsg",
					"Password must contain 8-20 characters, at least one uppercase letter, one lowercase letter, one number and one special character !");
			return false;
		}

		log.debug("[{}] All Required Fields Validated Successfully for Customer Registration", httpSession.getId());
		return true;
	}

	public boolean validateDetailsforExpenseReportByCategory(TransactionDetails expenseRequest) {
		log.debug("[{}] Validing Required Field for Expense Report By category", httpSession.getId());

		if (expenseRequest.getFromDate() == null || expenseRequest.getFromDate().equals("")
				|| expenseRequest.getFromDate().isEmpty()) {
			log.debug("[" + httpSession.getId() + "] ==> From Date is Null");
			this.httpSession.setAttribute("respMsg", "From Date is Empty, Please Fill mandatory Fields !");
			return false;
		}

		if (expenseRequest.getToDate() == null || expenseRequest.getToDate().equals("")
				|| expenseRequest.getToDate().isEmpty()) {
			log.debug("[" + httpSession.getId() + "] ==> To Date is Null");
			this.httpSession.setAttribute("respMsg", "To Date is Empty, Please Fill mandatory Fields !");
			return false;
		}

		if (expenseRequest.getTransactionTitle() == null || expenseRequest.getTransactionTitle().equals("")
				|| expenseRequest.getTransactionTitle().isEmpty()) {
			log.debug("[" + httpSession.getId() + "] ==> Title is Null");
			this.httpSession.setAttribute("respMsg", "Title is Empty, Please Fill mandatory Fields !");
			return false;
		}

		log.debug("[{}] All Required Field Validated Successfully for Expense Category Report", httpSession.getId());
		return true;
	}

	public boolean validateDetailsforUserLogin(CustomerDetails customerDetails) {
		log.debug("[{}] Validing Required Field for Customer Login", httpSession.getId());

		if (customerDetails.getUserName() == null || customerDetails.getUserName().equals("")
				|| customerDetails.getUserName().isEmpty()) {
			log.debug("[" + httpSession.getId() + "] ==> User Name is Null");
			this.httpSession.setAttribute("respMsg", "User Name is Empty, Please Fill mandatory Fields !");
			return false;
		} 

		if (customerDetails.getPassword() == null || customerDetails.getPassword().equals("")
				|| customerDetails.getPassword().isEmpty()) {
			log.debug("[" + httpSession.getId() + "] ==> Password is Null");
			this.httpSession.setAttribute("respMsg", "Password is Empty, Please Fill mandatory Fields !");
			return false;
		}

		log.debug("[{}] All Required Field Validated Successfully for Customer Login", httpSession.getId());
		return true;
	}

	public boolean validateDetailsforSaveTrade(FinTradeRequest finTradeRequest) {
		log.debug("[{}] Validating required fields for Save Trade", httpSession.getId());

		if (finTradeRequest.getMarket() == null || finTradeRequest.getMarket().trim().isEmpty()) {
			log.debug("[{}] Market field is empty", httpSession.getId());
			httpSession.setAttribute("respMsg", "Market is empty, please fill mandatory fields!");
			return false;
		}

		if (finTradeRequest.getSymbol() == null || finTradeRequest.getSymbol().trim().isEmpty()) {
			log.debug("[{}] Symbol field is empty", httpSession.getId());
			httpSession.setAttribute("respMsg", "Symbol is empty, please fill mandatory fields!");
			return false;
		}

		if (finTradeRequest.getProductType() == null || finTradeRequest.getProductType().trim().isEmpty()) {
			log.debug("[{}] Product Type field is empty", httpSession.getId());
			httpSession.setAttribute("respMsg", "Product Type is empty, please fill mandatory fields!");
			return false;
		}

		if (finTradeRequest.getInstrumentType() == null || finTradeRequest.getInstrumentType().trim().isEmpty()) {
			log.debug("[{}] Instrument Type field is empty", httpSession.getId());
			httpSession.setAttribute("respMsg", "Instrument Type is empty, please fill mandatory fields!");
			return false;
		}

		if (finTradeRequest.getDirection() == null || finTradeRequest.getDirection().trim().isEmpty()) {
			log.debug("[{}] Direction field is empty", httpSession.getId());
			httpSession.setAttribute("respMsg", "Direction is empty, please fill mandatory fields!");
			return false;
		}

		if (finTradeRequest.getEntryDate() != null && finTradeRequest.getEntryDate().trim().isEmpty()) {
			log.debug("[{}] Entry Date is invalid", httpSession.getId());
			httpSession.setAttribute("respMsg", "Entry Date is empty, please fill mandatory fields!");
			return false;
		}

		if (finTradeRequest.getEntryPrice() == null
				|| finTradeRequest.getEntryPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
			log.debug("[{}] Entry Price is invalid", httpSession.getId());
			httpSession.setAttribute("respMsg", "Entry Price must be greater than zero!");
			return false;
		}

		if (finTradeRequest.getExitPrice() == null
				|| finTradeRequest.getExitPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
			log.debug("[{}] Exit Price is invalid", httpSession.getId());
			httpSession.setAttribute("respMsg", "Exit Price must be greater than zero!");
			return false;
		}

		if (finTradeRequest.getQuantity() == null
				|| finTradeRequest.getQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
			log.debug("[{}] Quantity is invalid", httpSession.getId());
			httpSession.setAttribute("respMsg", "Quantity must be greater than zero!");
			return false;
		}

		if (finTradeRequest.getNetProfitLoss() == null || finTradeRequest.getNetProfitLoss().trim().isEmpty()) {
			log.debug("[{}] NetProfitLoss field is empty", httpSession.getId());
			httpSession.setAttribute("respMsg", "NetProfitLoss is empty, please fill mandatory fields!");
			return false;
		}

		if (finTradeRequest.getBrokerage() != null && finTradeRequest.getBrokerage().trim().isEmpty()) {
			log.debug("[{}] Brokerage is invalid", httpSession.getId());
			httpSession.setAttribute("respMsg", "Brokerage is empty, please fill mandatory fields!");
			return false;
		}

		log.debug("[{}] All required fields validated successfully for Save Trade", httpSession.getId());
		return true;
	}

	public boolean validateDetailsforAddMoney(AddMoneyRequest addMoneyRequest) {
		log.debug("[{}] Validating required fields for Trading Funds", httpSession.getId());

		if (addMoneyRequest.getTxnType() != null && addMoneyRequest.getTxnType().trim().isEmpty()) {
			log.debug("[{}] TXN Type is invalid", httpSession.getId());
			httpSession.setAttribute("respMsg", "Transaction type is empty, please fill mandatory fields!");
			return false;
		}

		if (addMoneyRequest.getAmount() != null && addMoneyRequest.getAmount().trim().isEmpty()) {
			log.debug("[{}] Amount is invalid", httpSession.getId());
			httpSession.setAttribute("respMsg", "Amount is empty, please fill mandatory fields!");
			return false;
		}

		if (addMoneyRequest.getBrokerName() != null && addMoneyRequest.getBrokerName().trim().isEmpty()) {
			log.debug("[{}] Boker Name is invalid", httpSession.getId());
			httpSession.setAttribute("respMsg", "Boker Name is empty, please fill mandatory fields!");
			return false;
		}

		if (addMoneyRequest.getPaymentApp() != null && addMoneyRequest.getPaymentApp().trim().isEmpty()) {
			log.debug("[{}] Payment App is invalid", httpSession.getId());
			httpSession.setAttribute("respMsg", "Amount is empty, please fill mandatory fields!");
			return false;
		}

		log.debug("[{}] All required fields validated successfully for Trading Funds", httpSession.getId());
		return true;
	}

	

}
