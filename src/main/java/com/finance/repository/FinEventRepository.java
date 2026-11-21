package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinEventDetails;

@Repository
public interface FinEventRepository extends JpaRepository<FinEventDetails, String>{

}
