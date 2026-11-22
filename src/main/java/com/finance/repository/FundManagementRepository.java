package com.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinFundTransactionDetails;

@Repository
public interface FundManagementRepository extends JpaRepository<FinFundTransactionDetails, String>{

	@Query(value = "SELECT * FROM FIN_FUND_TRANSFER_DETAILS WHERE USER_ID = :userId ORDER BY FUND_ID DESC", nativeQuery = true)
    List<FinFundTransactionDetails> getAllFunds(@Param("userId") String userId);
}
