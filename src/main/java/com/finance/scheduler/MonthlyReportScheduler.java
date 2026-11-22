package com.finance.scheduler;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.finance.entity.FinovaUserDetails;
import com.finance.repository.UserRegisterRepository;
import com.finance.service.FinReportMailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MonthlyReportScheduler {
	
	private final UserRegisterRepository userRegisterRepository;
	private final FinReportMailService finReportMailService;
	
	public MonthlyReportScheduler(UserRegisterRepository userRegisterRepository, FinReportMailService finReportMailService) {
		this.userRegisterRepository = userRegisterRepository;
		this.finReportMailService = finReportMailService;
	}

	private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

	/**
	 * Executes on 1st date of every month at 09:00 AM.
	 */
	@Scheduled(cron = "0 0 9 1 * *", zone = "Asia/Kolkata")
	public void sendMonthlyTransactionReports() {
		log.info("========== FINOVA MONTHLY REPORT SCHEDULER STARTED ==========");

		// Previous month report
		YearMonth previousMonth = YearMonth.now(ZONE).minusMonths(1);

		int year = previousMonth.getYear();
		int month = previousMonth.getMonthValue();

		log.info("Generating Reports for :: {}", previousMonth);
		List<FinovaUserDetails> customers = this.userRegisterRepository.findAll();
		log.info("Total Customers Found :: {}", customers.size());

		int successCount = 0;
		int failureCount = 0;
		for (FinovaUserDetails user : customers) {
			try {
				if (user.getEmail() == null || user.getEmail().isBlank()) {
					log.warn("Customer Email Not Available :: {}", user.getUserId());
					continue;
				}

				finReportMailService.sendMonthlyReport(user, year, month);

				successCount++;

			} catch (Exception e) {
				failureCount++;
				log.error("Monthly Report Failed for Customer :: {}", user.getUserId(), e);
			}
		}

		log.info("""
				========== FINOVA MONTHLY REPORT COMPLETED ==========
				Report Month : {}
				Total Customers : {}
				Successful : {}
				Failed : {}
				=====================================================
				""", previousMonth, customers.size(), successCount, failureCount);
	}
}
