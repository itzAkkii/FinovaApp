package com.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinTradingDetails;

@Repository
public interface FinTradingRepository extends JpaRepository<FinTradingDetails, String> {

	@Query(value = "SELECT * FROM FIN_TRADING_DETAILS ORDER BY ENTRY_DATE DESC LIMIT 10", nativeQuery = true)
	List<FinTradingDetails> findTop10RecentTrades();

}
