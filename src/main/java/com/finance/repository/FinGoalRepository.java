package com.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinGoalDetails;

@Repository
public interface FinGoalRepository extends JpaRepository<FinGoalDetails, String> {

	@Query(value = "SELECT * FROM FIN_GOAL_DETAILS WHERE USER_ID = :userId ORDER BY TO_DATE(DATE, 'DD-MM-YYYY') DESC", nativeQuery = true)
	List<FinGoalDetails> findGoalList(@Param("userId") String userId);
}
