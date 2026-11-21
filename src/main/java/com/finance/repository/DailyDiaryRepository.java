package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinDiaryDetails;

@Repository
public interface DailyDiaryRepository extends JpaRepository<FinDiaryDetails, String> {

}
