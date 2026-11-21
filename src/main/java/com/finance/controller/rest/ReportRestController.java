package com.finance.controller.rest;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.finance.controller.RestApiController;
import com.finance.model.TransactionDetails;
import com.finance.service.ReportService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/finova/api/v1")
public class ReportRestController {

	private final HttpSession httpSession;
	private final ReportService reportService;

	public ReportRestController(HttpSession httpSession, ReportService reportService) {
	    this.httpSession = httpSession;
	    this.reportService = reportService;
	}

	@PostMapping("/download/txn/report")
	public ResponseEntity<byte[]> downloadTXNReport(@RequestBody TransactionDetails transactionDetails) {
		log.debug("[{}] ==> Service Called :: Download TXN Report", httpSession.getId());

		log.debug("[{}] ==> Download TXN Report Request ==> {}", httpSession.getId(), transactionDetails);
		byte[] csvFile = this.reportService.downloadTxnReport(transactionDetails);
		log.info("[{}] ==> TXN Report Downloaded Successfully", httpSession.getId());

		String fileName = "Finova_Txn_Report.csv";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("text/csv"));
		headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
		headers.setContentLength(csvFile.length);
		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(csvFile);
	}

	@PostMapping("/download/monthly/report")
	public ResponseEntity<byte[]> downloadMonthlyReport(@RequestBody TransactionDetails transactionDetails) {
		log.debug("[{}] ==> Service Called :: Download Monthly Report", httpSession.getId());

		log.debug("[{}] ==> Download Monthly Report Request ==> {}", httpSession.getId(), transactionDetails);
		byte[] csvFile = this.reportService.downloadMonthlyTxnReport(transactionDetails);
		log.info("[{}] ==> Monthly Report Downloaded Successfully", httpSession.getId());

		String fileName = "Finova_Monthly_Report.csv";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("text/csv"));
		headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
		headers.setContentLength(csvFile.length);
		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(csvFile);
	}

}
