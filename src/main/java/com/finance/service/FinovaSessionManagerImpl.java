package com.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finance.model.FinovaSessionUser;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FinovaSessionManagerImpl implements FinovaSessionManager {

	@Autowired
	private HttpSession httpSession;

	private HashOperations hashOperations;

	public FinovaSessionManagerImpl(RedisTemplate redisTemplate) {
		log.info("Redis Hash operations initialized successfully");
		hashOperations = redisTemplate.opsForHash();
	}

	@Override
	public FinovaSessionUser getFinovaUser(String sessionId) {
		log.info("[" + httpSession.getId() + "] ==> Getting Login User Information Using Session Id");
		FinovaSessionUser finovaSessionUser = null;
		try {
			finovaSessionUser = (FinovaSessionUser) hashOperations.get("FINOVAUSER", sessionId);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("[" + httpSession.getId() + "] ==> failed to find user information from session");
			throw e;
		}
		return finovaSessionUser;
	}

	@Override
	public String addFinovaUser(FinovaSessionUser finovaSessionUser) {
		log.debug("[" + httpSession.getId() + "] ==> Adding User Information in Session");
		try {
			boolean status = hashOperations.putIfAbsent("FINOVAUSER", httpSession.getId(), finovaSessionUser);
			log.debug("[" + httpSession.getId() + "] ==> Adding Session user " + status);
			if (status == true)
				return "SUCCESS";
			else
				return "FAIL";
		} catch (Exception e) {
			System.out.println("Exception while accessing redis");
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public String deleteFinovaUser(String sessionId) {
		log.debug("[" + httpSession.getId() + "] ==> Delate Mpay session user service");
		FinovaSessionUser finovaUser = null;
		try {
			finovaUser = getFinovaUser(sessionId);
			if (finovaUser != null) {
				hashOperations.delete("FINOVAUSER", sessionId);
				log.debug("[" + httpSession.getId() + "] ==> session user deletion success ");
				return "SUCCESS";
			} else
				return "FAIL";
		} catch (Exception e) {
			log.debug("[" + httpSession.getId() + "] ==> Exception while accessing redis");
			e.printStackTrace();
			throw e;
		}
	}
}
