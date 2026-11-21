package com.finance.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finance.entity.FinovaUserDetails;
import com.finance.exception.FinovaException;
import com.finance.model.CustomerDetails;
import com.finance.model.FinovaSessionUser;
import com.finance.repository.UserRegisterRepository;
import com.finance.utils.FinUtils;
import com.finance.utils.RequestValidator;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

	@Value("${login.username}")
	private String username;

	@Value("${login.password}")
	private String password;

	private final PasswordEncoder passwordEncoder;
	private final UserRegisterRepository userRegisterRepository;
	private final HttpSession httpSession;
	private final RequestValidator requestValidator;
	private final FinUtils finUtils;
	private final FinovaSessionManager finovaSessionManager;

	public CustomerServiceImpl(HttpSession httpSession, RequestValidator requestValidator,
			UserRegisterRepository userRegisterRepository, PasswordEncoder passwordEncoder, FinUtils finUtils,
			FinovaSessionManager finovaSessionManager) {
		this.httpSession = httpSession;
		this.requestValidator = requestValidator;
		this.userRegisterRepository = userRegisterRepository;
		this.passwordEncoder = passwordEncoder;
		this.finUtils = finUtils;
		this.finovaSessionManager = finovaSessionManager;
	}

	public FinovaUserDetails getFinovaUser() {
		log.trace("[{}] ==> User information service", httpSession.getId());
		FinovaUserDetails finovaUserDetails = null;

		FinovaSessionUser finovaSessionUser = finovaSessionManager.getFinovaUser(httpSession.getId());
		if (finovaSessionUser == null) {
			log.trace("[{}] ==> Login session not found for user information! Login again", httpSession.getId());
			httpSession.setAttribute("respMsg", "Oops! Your session has expired. Please log in again to continue.");
			String respMsg = Optional.ofNullable((String) httpSession.getAttribute("respMsg")).orElse("Unknown error");
			throw new FinovaException("MPAY1019", respMsg);
		}
		log.trace("[{}] ==> Finding User information from Database : {}", httpSession.getId(),
				finovaSessionUser.getUserId());

		finovaUserDetails = this.userRegisterRepository.findById(finovaSessionUser.getUserId()).orElseThrow(() -> {
			log.trace("[{}] ==> Unable to get User information", httpSession.getId());
			httpSession.setAttribute("respMsg", "User Information not found");
			String respMsg = Optional.ofNullable((String) httpSession.getAttribute("respMsg")).orElse("Unknown error");
			return new FinovaException("MPAY0005", respMsg);
		});

		log.trace("[{}] ==> User information fetched successfully", httpSession.getId());
		return finovaUserDetails;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FinovaUserDetails validateUser(FinovaUserDetails finovaUserDetails) {

		if (!requestValidator.preValidateDetailsforUser(finovaUserDetails)) {
			log.info("[{}] ==> Validation failed", httpSession.getId());
			String respMessage = Optional.ofNullable((String) httpSession.getAttribute("respMsg"))
					.orElse("Unknown error");
			throw new FinovaException("FIN1001", respMessage);
		}

		FinovaUserDetails registeredUser = null;
		try {
			registeredUser = (FinovaUserDetails) userRegisterRepository
					.findByMobileNo(finovaUserDetails.getMobileNo().trim()).orElseThrow(() -> {
						log.warn("[{}] ==> Invalid login credentials", httpSession.getId());
						return new FinovaException("FIN1004", "User Not Found!");
					});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return registeredUser;
	}

	@Override
	public FinovaUserDetails addUser(FinovaUserDetails userRegister) {

		if (!requestValidator.validateDetailsforUserRegistration(userRegister)) {
			log.info("[{}] ==> Validation failed", httpSession.getId());
			String respMessage = Optional.ofNullable((String) httpSession.getAttribute("respMsg"))
					.orElse("Unknown error");
			throw new FinovaException("FIN1001", respMessage);
		}

		String userId = "FIN" + this.finUtils.generateRandomString(5);
		userRegister.setUserId(userId);

		String rawPassword = userRegister.getPassword();
		String encryptedPassword = passwordEncoder.encode(rawPassword);
		userRegister.setPassword(encryptedPassword);

		FinovaUserDetails userRegistered = Optional.of(this.userRegisterRepository.save(userRegister))
				.orElseThrow(() -> {
					log.error("[{}] ==> Failed to save User Details to Database", httpSession.getId());
					return new FinovaException("FIN1002", "Internal issue! Please try later");
				});

		log.debug("[{}] ==> User Registration Successful", httpSession.getId());
		return userRegistered;
	}

	@Override
	public FinovaUserDetails userLogin(CustomerDetails customerDetails) {
		log.debug("[{}] ==> User Login Service :: {}", httpSession.getId(), customerDetails);
		FinovaSessionUser finovaSessionUser = null;

		if (!requestValidator.validateDetailsforUserLogin(customerDetails)) {
			log.info("[{}] ==> Login validation failed", httpSession.getId());
			String respMessage = Optional.ofNullable((String) httpSession.getAttribute("respMsg"))
					.orElse("Unknown error");
			throw new FinovaException("FIN1001", respMessage);
		}

		String userName = customerDetails.getUserName();
		log.debug("[{}] ==> Checking User in DB", httpSession.getId());
		FinovaUserDetails finovaUser = userRegisterRepository.findByUserName(userName).orElseThrow(() -> {
			log.warn("[{}] ==> Invalid login credentials", httpSession.getId());
			return new FinovaException("FIN1005", "Invalid username or password");
		});

		String enteredPassword = customerDetails.getPassword();
		String storedPassword = finovaUser.getPassword();
		if (!passwordEncoder.matches(enteredPassword, storedPassword)) {
			log.warn("[{}] ==> Invalid login credentials", httpSession.getId());
			throw new FinovaException("FIN1005", "Invalid username or password");
		}
		log.debug("[{}] ==> Password verified successfully", httpSession.getId());

		try {
			log.debug("[{}] ==> Checking User Session in REDIS", httpSession.getId());
			finovaSessionUser = finovaSessionManager.getFinovaUser(httpSession.getId());
		} catch (Exception e) {
			log.error("[{}] ==> Unable to retrieve session from REDIS", httpSession.getId(), e);
			throw new FinovaException("FIN1001", "Internal issue! Please try later");
		}

		if (finovaSessionUser == null) {
			log.debug("[{}] ==> Session not available in REDIS", httpSession.getId());
			finovaSessionUser = new FinovaSessionUser(finovaUser.getUserId(), customerDetails.getLastLogin());
			String status = null;
			try {
				status = finovaSessionManager.addFinovaUser(finovaSessionUser);
			} catch (Exception e) {
				log.error("[{}] ==> Unable to create REDIS session", httpSession.getId(), e);
				throw new FinovaException("FIN1001", "Internal issue! Please try later");
			}

			if ("FAIL".equalsIgnoreCase(status) || status == null) {
				log.error("[{}] ==> REDIS session creation failed", httpSession.getId());
				throw new FinovaException("FIN1001", "Internal issue! Please try later");
			}
			log.info("[{}] ==> User session created successfully in REDIS", httpSession.getId());
		} else {
			log.debug("[{}] ==> Existing REDIS session found", httpSession.getId());
		}

		log.info("[{}] ==> User Login Successful :: USER_ID: {}", httpSession.getId(), finovaUser.getUserId());
		return finovaUser;
	}

	@Override
	public String userLogOut() {
		String sessionId = httpSession.getId();
		log.debug("[{}] ==> User LogOut Service ::", sessionId);
		FinovaSessionUser finovaSessionUser = null;
		try {
			finovaSessionUser = finovaSessionManager.getFinovaUser(sessionId);
		} catch (Exception e) {
			log.error("[{}] ==> Unable to get session information from REDIS", sessionId, e);
			throw new FinovaException("MPAY0005", "Unable to get session information from REDIS");
		}
		if (finovaSessionUser != null) {
			log.debug("[{}] ==> Deleting user information from REDIS", sessionId);
			try {
				finovaSessionManager.deleteFinovaUser(sessionId);
				log.debug("[{}] ==> User Logout details updated successfully", sessionId);
			} catch (Exception e) {
				log.error("[{}] ==> Unable to delete user session information from REDIS", sessionId, e);
				throw new FinovaException("MPAY0005", "Unable to process your request! Please try later");
			}

			log.debug("[{}] ==> User removed from Redis successfully", sessionId);
			return "SUCCESS";
		} else {
			log.debug("[{}] ==> User already logged out/not found", sessionId);
			throw new FinovaException("MPAY0007", "User already logged out/not found");
		}
	}

	@Override
	public FinovaUserDetails updateProfile(FinovaUserDetails finovaUserProfile) {
		FinovaUserDetails finovaUserDetails = null;
		FinovaUserDetails userProfileUpdated = null;
		finovaUserDetails = this.getFinovaUser();
		if (finovaUserDetails == null) {
			log.debug("[] ==> User Not Found");
			throw new FinovaException("MPAY0005", "Unable to process your request! Please try later");
		} else {
			log.trace("[" + this.httpSession.getId() + "] ==> User information fetched successfully");

			Optional.ofNullable(finovaUserProfile.getAddress()).ifPresent(finovaUserDetails::setAddress);
			Optional.ofNullable(finovaUserProfile.getEmail()).ifPresent(finovaUserDetails::setEmail);
			Optional.ofNullable(finovaUserProfile.getMaritalStatus()).ifPresent(finovaUserDetails::setMaritalStatus);
			Optional.ofNullable(finovaUserProfile.getMobileNo()).ifPresent(finovaUserDetails::setMobileNo);
			Optional.ofNullable(finovaUserProfile.getUserName()).ifPresent(finovaUserDetails::setUserName);
			
			userProfileUpdated = Optional.of(this.userRegisterRepository.save(finovaUserDetails)).orElseThrow(() -> {
				log.error("[{}] ==> Failed to save User Details to Database", httpSession.getId());
				return new FinovaException("FIN1002", "Internal issue! Please try later");
			});
		}
		log.debug("[{}] ==> User Profile updated Successfully", userProfileUpdated);
		return userProfileUpdated;
	}

}
