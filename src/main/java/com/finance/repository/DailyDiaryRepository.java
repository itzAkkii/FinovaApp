package com.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinDiaryDetails;

@Repository
public interface DailyDiaryRepository extends JpaRepository<FinDiaryDetails, String> {

	@Query(value = "SELECT * FROM FIN_DIARY_DETAILS WHERE USER_ID = :userId ORDER BY TO_DATE(DATE, 'DD-MM-YYYY') DESC", nativeQuery = true)
	List<FinDiaryDetails> findDiaryList(@Param("userId") String userId);
}
