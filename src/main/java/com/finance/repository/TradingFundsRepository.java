package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance.entity.TradingFundsDetails;

@Repository
public interface TradingFundsRepository extends JpaRepository<TradingFundsDetails, String> {

}
