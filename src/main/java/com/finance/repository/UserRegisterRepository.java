package com.finance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.entity.FinovaUserDetails;

public interface UserRegisterRepository extends JpaRepository<FinovaUserDetails, String> {

	@SuppressWarnings("rawtypes")
	Optional findByMobileNo(String mobileNo);

    Optional<FinovaUserDetails> findByUserName(String userName);

}
