package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinFundTransactionDetails;

@Repository
public interface FundManagementRepository extends JpaRepository<FinFundTransactionDetails, String>{

}
