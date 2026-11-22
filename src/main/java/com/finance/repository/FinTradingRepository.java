package com.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinTradingDetails;

@Repository
public interface FinTradingRepository extends JpaRepository<FinTradingDetails, String> {

	@Query(value = "SELECT * FROM FIN_TRADING_DETAILS WHERE USER_ID = :userId ORDER BY ENTRY_DATE DESC, ENTRY_TIME DESC LIMIT 10", nativeQuery = true)
	List<FinTradingDetails> findTop10RecentTrades(@Param("userId") String userId);

	@Query(value = "SELECT * FROM FIN_TRADING_DETAILS WHERE USER_ID = :userId ORDER BY ENTRY_DATE DESC, ENTRY_TIME DESC", nativeQuery = true)
	List<FinTradingDetails> getTradeList(@Param("userId") String userId);

}
