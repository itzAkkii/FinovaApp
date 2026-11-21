package com.finance.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finance.entity.FinTransactionDetails;

@Repository
public interface FinTransactionRepository extends JpaRepository<FinTransactionDetails, String> {

	@Query(value = "SELECT COALESCE(SUM(CAST(AMOUNT AS NUMERIC)), 0) FROM FIN_TRANSACTION_DETAILS WHERE TRANSACTION_TYPE = 'INCOME' AND USER_ID = :userId AND EXTRACT(YEAR FROM TO_DATE(DATE, 'DD-MM-YYYY')) = :year AND EXTRACT(MONTH FROM TO_DATE(DATE, 'DD-MM-YYYY')) = :month", nativeQuery = true)
	BigDecimal getTotalIncome(@Param("year") int year, @Param("month") int month, @Param("userId") String userId);

	@Query(value = "SELECT COALESCE(SUM(CAST(AMOUNT AS NUMERIC)), 0) FROM FIN_TRANSACTION_DETAILS WHERE TRANSACTION_TYPE = 'EXPENSE' AND USER_ID = :userId AND EXTRACT(YEAR FROM TO_DATE(DATE, 'DD-MM-YYYY')) = :year AND EXTRACT(MONTH FROM TO_DATE(DATE, 'DD-MM-YYYY')) = :month", nativeQuery = true)
	BigDecimal getTotalExpense(@Param("year") int year, @Param("month") int month, @Param("userId") String userId);

//	@Query(value = "SELECT * FROM FIN_TRANSACTION_DETAILS ORDER BY TO_DATE(DATE, 'DD-MM-YYYY') DESC, TO_TIMESTAMP(DATETIME, 'DD-MM-YYYY HH24:MI:SS') DESC LIMIT 3", nativeQuery = true)
//	List<FinTransactionDetails> findRecentTransactions();

	@Query(value = "SELECT * FROM FIN_TRANSACTION_DETAILS WHERE TO_DATE(DATE, 'DD-MM-YYYY') BETWEEN TO_DATE(:fromDate, 'DD-MM-YYYY') AND TO_DATE(:toDate, 'DD-MM-YYYY') ORDER BY TO_DATE(DATE, 'DD-MM-YYYY') DESC, TO_TIMESTAMP(DATETIME, 'DD-MM-YYYY HH24:MI:SS') DESC", nativeQuery = true)
	List<FinTransactionDetails> findTransactionReportByDate(@Param("fromDate") String fromDate,
			@Param("toDate") String toDate);

	@Query(value = "SELECT * FROM FIN_TRANSACTION_DETAILS WHERE EXTRACT(YEAR FROM TO_DATE(DATE, 'DD-MM-YYYY')) = :year AND EXTRACT(MONTH FROM TO_DATE(DATE, 'DD-MM-YYYY')) = :month ORDER BY TO_DATE(DATE, 'DD-MM-YYYY') DESC, TO_TIMESTAMP(DATETIME, 'DD-MM-YYYY HH24:MI:SS') DESC", nativeQuery = true)
	List<FinTransactionDetails> getMonthlyReport(@Param("year") int year, @Param("month") int month);

	@Query(value = "SELECT * FROM FIN_TRANSACTION_DETAILS WHERE EXPENSE_TITLE = :expenseTitle AND DATE BETWEEN :fromDate AND :toDate ORDER BY TO_TIMESTAMP(DATETIME, 'DD-MM-YYYY HH24:MI:SS') DESC", nativeQuery = true)
	List<FinTransactionDetails> findExpenseReportByCategory(@Param("expenseTitle") String expenseTitle,
			@Param("fromDate") String fromDate, @Param("toDate") String toDate);

	@Query(value = """
			SELECT COALESCE(
			    SUM(
			        CASE
			            WHEN TRANSACTION_TITLE = 'ADD Money'
			                THEN CAST(AMOUNT AS NUMERIC)
			            WHEN TRANSACTION_TITLE = 'Send Money'
			                THEN -CAST(AMOUNT AS NUMERIC)
			            ELSE 0
			        END
			    ), 0
			)
			FROM FIN_TRANSACTION_DETAILS
			WHERE TRANSACTION_CATEGORY = 'Trading'
			  AND STATUS = 'S'
			""", nativeQuery = true)
	BigDecimal getTradingCapital();

}
