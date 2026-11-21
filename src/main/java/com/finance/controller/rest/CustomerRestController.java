package com.finance.controller.rest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.finance.controller.RestApiController;
import com.finance.entity.FinovaUserDetails;
import com.finance.exception.FinovaException;
import com.finance.model.CustomerDetails;
import com.finance.service.CustomerService;
import com.finance.service.TransactionService;
import com.finance.utils.FinovaUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/customer")
public class CustomerRestController {

	private final HttpSession httpSession;
	private final CustomerService customerService;
	private final TransactionService transactionService;

	public CustomerRestController(HttpSession httpSession, CustomerService customerService,
			TransactionService transactionService) {
		this.httpSession = httpSession;
		this.customerService = customerService;
		this.transactionService = transactionService;
	}

	@GetMapping("/session-expired")
	public String sessionExpired() {

	    return "session-expired";
	}

	@PostMapping("/validation")
	public ResponseEntity<Map<String, Object>> validateUserName(@RequestBody FinovaUserDetails finovaUserDetails) {
		log.debug("[{}] ==> Service Called :: User Registration", httpSession.getId());

		FinovaUserDetails uservalidated = this.customerService.validateUser(finovaUserDetails);
		Map<String, Object> responseData = new HashMap<>();
		if (uservalidated != null) {
			responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
			responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
			responseData.put("INFORMATION", "User Validated successfully");
			responseData.put("DATA", uservalidated);
		} else {
			responseData.put("RESP_STATUS", FinovaUtil.FAILED_RESP_MESSAGE);
			responseData.put("RESP_CODE", "FIN1004");
			responseData.put("INFORMATION", "User Not Found");
		}

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}
	
	@GetMapping("/register")
	public String registerPage() {
		return "register";
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> userRegistration(@RequestBody FinovaUserDetails finovaUserDetails) {
		log.debug("[{}] ==> Service Called :: User Registration", httpSession.getId());

		FinovaUserDetails registeredUser = this.customerService.addUser(finovaUserDetails);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("INFORMATION", "User Registered successfully");
		responseData.put("USER_ID", registeredUser.getUserId());
		responseData.put("DATA", registeredUser);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@GetMapping("/login")
	public String loginPage(Model model) {
		model.addAttribute("customerDetails", new CustomerDetails());
		return "login";
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> customerLogin(@RequestBody CustomerDetails customerDetails) {
		log.debug("[{}] ==> Service Called :: Customer Login", httpSession.getId());

		FinovaUserDetails finovaUser = this.customerService.userLogin(customerDetails);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("INFORMATION", "User Log-in successfully");
		responseData.put("USER_ID", finovaUser.getUserId());
		responseData.put("DATA", finovaUser);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@GetMapping("/logout")
	public String userLogOut(HttpSession session, RedirectAttributes redirectAttributes) {
		String sessionId = session.getId();
		log.info("[{}] ==> Service Called :: User Logout", sessionId);
		try {
			customerService.userLogOut();
			log.info("[{}] ==> User Logged Out Successfully", sessionId);

			redirectAttributes.addFlashAttribute("successMessage", "User logged out successfully");
		} catch (FinovaException e) {
			log.error("[{}] ==> Logout Exception: {}", sessionId, e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", "User already logged out/not found");
		} finally {
			try {
				session.invalidate();
			} catch (IllegalStateException e) {
				log.info("[{}] ==> HTTP session already invalidated", sessionId);
			}
		}
		return "redirect:/customer/login";
	}

	@GetMapping("/home")
	public String home(Model model) {
		log.info("[{}] ==> Fetching Recent Transactions", httpSession.getId());

		LocalDate currentDate = LocalDate.now();
		int year = currentDate.getYear();
		int month = currentDate.getMonthValue();

		// List<FinTransactionDetails> recentTransactions =
		// transactionService.getRecentTransactions();
		
		try {
			BigDecimal totalIncome = transactionService.getTotalIncome(year, month);
			BigDecimal totalExpense = transactionService.getTotalExpense(year, month);
			BigDecimal balance = totalIncome.subtract(totalExpense);

			// model.addAttribute("recentTransactions", recentTransactions);
			model.addAttribute("totalIncome", totalIncome);
			model.addAttribute("totalExpense", totalExpense);
			model.addAttribute("balance", balance);

			return "home";
		} catch (Exception e) {
			// TODO: handle exception
			   log.error("[{}] ==> Session expired. Redirecting to login.",
		                httpSession.getId());

		        return "redirect:/customer/session-expired";
		}
	
	}

	@RequestMapping(value = "/info", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> userInfo() {
		log.info("[{}] ==> Service Called :: Customer Information [{}]", httpSession.getId(), httpSession.getId());

		FinovaUserDetails userProfile = this.customerService.getFinovaUser();
		log.info("[{}] ==> Profile Information Fetched Successfully", httpSession.getId());

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("userId", userProfile.getUserId());
		responseData.put("userName", userProfile.getUserName());
		responseData.put("dob", userProfile.getDob());
		responseData.put("gender", userProfile.getGender());
		responseData.put("maritalStatus", userProfile.getMaritalStatus());
		responseData.put("mobile", userProfile.getMobileNo());
		responseData.put("email", userProfile.getEmail());
		responseData.put("address", userProfile.getAddress());

		log.info("[{}] ==> Final Response: {}", httpSession.getId(), responseData);

		return ResponseEntity.ok().headers(RestApiController.getHeaders()).body(responseData);
	}

	@PostMapping("/update/profile")
	public ResponseEntity<?> updateProfileData(@RequestBody FinovaUserDetails FinovaUserDetails) {
		log.trace("[" + httpSession.getId() + "] ==> Service Called :: Update Profile Information ["
				+ httpSession.getId() + "]");
		log.trace("[" + httpSession.getId() + "] ==> Received Request : " + FinovaUserDetails);
		FinovaUserDetails userProfile = null;
		try {
			userProfile = customerService.updateProfile(FinovaUserDetails);
		} catch (Exception var7) {
			var7.printStackTrace();
			log.trace("[" + httpSession.getId() + "] ==> Exception occured while processing request");
		}

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("INFORMATION", "User Log-in successfully");
		responseData.put("USER_ID", userProfile.getUserId());
		responseData.put("DATA", userProfile);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

}
