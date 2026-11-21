package com.finance.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.finance.controller.RestApiController;
import com.finance.entity.FinDiaryDetails;
import com.finance.entity.FinEventDetails;
import com.finance.entity.FinGoalDetails;
import com.finance.model.JournalEntryDetails;
import com.finance.service.JournalService;
import com.finance.utils.FinovaUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/finova/api/v1")
public class JournalController {

	private final HttpSession httpSession;
	private final JournalService journalService;

	public JournalController(HttpSession httpSession, JournalService journalService) {

		this.httpSession = httpSession;
		this.journalService = journalService;
	}

	@GetMapping("/journal")
	public String dailyNotes() {
		return "journal";
	}

	@PostMapping("/add/journal")
	public ResponseEntity<Map<String, Object>> journal(@RequestBody JournalEntryDetails JournalEntryDetails) {
		log.debug("[{}] ==> Service Called :: Add journal", httpSession.getId());

		log.debug("[{}] ==> Add Notes Request ==> {}", httpSession.getId(), JournalEntryDetails);
		String noteResponseMessage = this.journalService.addJournal(JournalEntryDetails);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", noteResponseMessage);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@GetMapping("/view/diary")
	public String viewDiary() {
		return "diary";
	}

	@PostMapping("/diary/list")
	public ResponseEntity<Map<String, Object>> diaryList() {
		log.debug("[{}] ==> Service Called :: Fetch Diary List", httpSession.getId());

		List<FinDiaryDetails> finDiaryDetailsList = this.journalService.getDiaryList();

		log.debug("[{}] ==> Diary List Fetched Successfully ==> {}", httpSession.getId(), finDiaryDetailsList);
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", finDiaryDetailsList);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}
	
	@PostMapping("/diary/delete")
	public ResponseEntity<Map<String, Object>> deleteDiary(@RequestBody Map<String, Object> diaryEntryDetails) {
		log.debug("[{}] ==> Service Called :: Delete Diary", httpSession.getId());
		String diaryId = (String) diaryEntryDetails.get("noteId");
		FinDiaryDetails responseMessge = this.journalService.deleteDiary(diaryId);

		log.debug("[{}] ==> Diary Deleted Successfully ==> {}", httpSession.getId(), diaryId);
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", responseMessge);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@GetMapping("/view/goal")
	public String viewGoals() {
		return "goal";
	}

	@PostMapping("/goal/list")
	public ResponseEntity<Map<String, Object>> goalList() {
		log.debug("[{}] ==> Service Called :: Fetch Goal List", httpSession.getId());

		List<FinGoalDetails> finGoalDetailsList = this.journalService.getGoalList();

		log.debug("[{}] ==> Goal List Fetched Successfully ==> {}", httpSession.getId(), finGoalDetailsList);
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", finGoalDetailsList);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PostMapping("/goal/update")
	public ResponseEntity<Map<String, Object>> modifyGoal(@RequestBody FinGoalDetails finGoalDetails) {
		log.debug("[{}] ==> Service Called :: Update Goal", httpSession.getId());

		FinGoalDetails updatedGoalDetails = this.journalService.updateGoal(finGoalDetails);

		log.debug("[{}] ==> Goal Updated Successfully ==> {}", httpSession.getId(), updatedGoalDetails);
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", updatedGoalDetails);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PostMapping("/goal/delete")
	public ResponseEntity<Map<String, Object>> removeGoal(@RequestBody FinGoalDetails finGoalDetails) {
		log.debug("[{}] ==> Service Called :: Delete Goal", httpSession.getId());

		FinGoalDetails deletedGoalDetails = this.journalService.deleteGoal(finGoalDetails);

		log.debug("[{}] ==> Goal Deleted Successfully ==> {}", httpSession.getId(), deletedGoalDetails);
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", deletedGoalDetails);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}
	
	@GetMapping("/calendar")
	public String financialCalendar() {
		return "calendar";
	}

	@GetMapping("/events")
	public ResponseEntity<List<FinEventDetails>> getAllEvents() {
		List<FinEventDetails> events = journalService.getAllEvents();
		return ResponseEntity.ok(events);
	}

	@PostMapping("/add/event")
	public ResponseEntity<Map<String, Object>> saveEvent(@RequestBody FinEventDetails finEventDetails) {
		log.debug("[{}] ==> Service Called :: Add Event in Calendar", httpSession.getId());
		log.debug("[{}] ==> Add Event in Calendar {}", httpSession.getId(), finEventDetails);

		finEventDetails = this.journalService.addEvent(finEventDetails);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", finEventDetails);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PutMapping("/update/event")
	public ResponseEntity<Map<String, Object>> updateEvent(@RequestBody FinEventDetails event) {
		log.debug("[{}] ==> Service Called :: Update Event in Calendar", httpSession.getId());
		log.debug("[{}] ==> Update Event in Calendar {}", httpSession.getId(), event);
		
		FinEventDetails updatedEvent = journalService.updateEvent(event);
		
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", updatedEvent);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

	@PutMapping("/delete/event/{id}")
	public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable String id) {
		log.debug("[{}] ==> Service Called :: Delete Event in Calendar", httpSession.getId());
		log.debug("[{}] ==> Delete Event in Calendar {}", httpSession.getId(), id);
		
		String eventMessage = journalService.deleteEvent(id);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("RESP_STATUS", FinovaUtil.SUCCESS_RESP_MESSAGE);
		responseData.put("RESP_CODE", FinovaUtil.SUCCESS_RESP_CODE);
		responseData.put("respMessage", eventMessage);

		return ResponseEntity.status(HttpStatus.OK).headers(RestApiController.getHeaders()).body(responseData);
	}

}