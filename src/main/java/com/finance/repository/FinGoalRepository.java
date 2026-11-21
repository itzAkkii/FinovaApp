package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinGoalDetails;

@Repository
public interface FinGoalRepository extends JpaRepository<FinGoalDetails, String> {

}
