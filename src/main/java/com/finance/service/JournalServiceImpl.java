package com.finance.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.entity.FinDiaryDetails;
import com.finance.entity.FinEventDetails;
import com.finance.entity.FinGoalDetails;
import com.finance.entity.FinovaUserDetails;
import com.finance.exception.FinovaException;
import com.finance.model.JournalEntryDetails;
import com.finance.repository.DailyDiaryRepository;
import com.finance.repository.FinEventRepository;
import com.finance.repository.FinGoalRepository;
import com.finance.utils.FinUtils;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JournalServiceImpl implements JournalService {

	private final HttpSession httpSession;
	private final FinUtils finUtils;
	private final DailyDiaryRepository finDiaryRepository;
	private final FinGoalRepository finGoalRepository;
	private final FinEventRepository finEventRepository;
	private final CustomerService customerService;

	public JournalServiceImpl(HttpSession httpSession, FinUtils finUtils, DailyDiaryRepository finDiaryRepository,
			FinGoalRepository finGoalRepository, FinEventRepository finEventRepository,
			CustomerService customerService) {
		this.httpSession = httpSession;
		this.finUtils = finUtils;
		this.finDiaryRepository = finDiaryRepository;
		this.finGoalRepository = finGoalRepository;
		this.finEventRepository = finEventRepository;
		this.customerService = customerService;
	}

	@Override
	public String addJournal(JournalEntryDetails journalEntryDetails) {
		log.debug("[{}] Add Journal Service :: {}", httpSession.getId(), journalEntryDetails);
		String entryType = journalEntryDetails.getEntryType();
		FinDiaryDetails finDiaryDetails = null;
		FinGoalDetails finGoalDetails = null;

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});

		log.debug("[{}] Journal Entry Type :: {}", httpSession.getId(), journalEntryDetails.getEntryType());
		if (entryType.equalsIgnoreCase("DIARY")) {
			finDiaryDetails = new FinDiaryDetails();
			finDiaryDetails.setNoteId("FIN" + this.finUtils.dateFormat() + this.finUtils.generateRandomString(2));
			finDiaryDetails.setNoteTitle(journalEntryDetails.getTitle());
			finDiaryDetails.setDiary(journalEntryDetails.getSubject());
			finDiaryDetails.setDate(this.finUtils.getDate());
			finDiaryDetails.setDateAndTime(this.finUtils.getDateAndTime());
			finDiaryDetails.setStatus("S");
			finDiaryDetails.setFlag("Y");
			finDiaryDetails.setUserId(finovaUserDetails.getUserId());

			finDiaryDetails.setUserId(finovaUserDetails.getUserId());

			log.debug("[{}] ==> Adding Diary to Database ==> {}", httpSession.getId(), finDiaryDetails);
			Optional.of(this.finDiaryRepository.save(finDiaryDetails)).orElseThrow(() -> {
				log.error("[{}] ==> Failed to save Dairy to Database", httpSession.getId());
				return new FinovaException("FIN1002", "Internal issue! Please try later");
			});
		} else {
			finGoalDetails = new FinGoalDetails();
			finGoalDetails.setGoalId("FIN" + this.finUtils.dateFormat() + this.finUtils.generateRandomString(2));
			finGoalDetails.setGoalTitle(journalEntryDetails.getTitle());
			finGoalDetails.setGoal(journalEntryDetails.getSubject());
			finGoalDetails.setAmount(journalEntryDetails.getAmount());
			finGoalDetails.setTargetAmount(journalEntryDetails.getTargetAmount());
			finGoalDetails.setPriority(journalEntryDetails.getPriority());
			finGoalDetails.setCurrency("INR");
			finGoalDetails.setDtCreated(this.finUtils.getDate());
			finGoalDetails.setTargetDate(journalEntryDetails.getTargetDate());
			finGoalDetails.setDateAndTime(this.finUtils.getDateAndTime());
			finGoalDetails.setStatus("S");
			finGoalDetails.setFlag("Y");
			finGoalDetails.setUserId(finovaUserDetails.getUserId());

			log.debug("[{}] ==> Adding Financial Goal to Database ==> {}", httpSession.getId(), finGoalDetails);
			Optional.of(this.finGoalRepository.save(finGoalDetails)).orElseThrow(() -> {
				log.error("[{}] ==> Failed to save Goal to Database", httpSession.getId());
				return new FinovaException("FIN1002", "Internal issue! Please try later");
			});
		}

		return "Journal Successfull";
	}

	@Override
	public List<FinDiaryDetails> getDiaryList() {
		log.debug("[{}] ==> Fetch Diary Service :: ", httpSession.getId());

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		return Optional.ofNullable(this.finDiaryRepository.findAll()).filter(list -> !list.isEmpty())
				.orElseThrow(() -> {
					log.error("[{}] ==> Diary Not Found", httpSession.getId());
					return new FinovaException("FIN1002", "No Diary Entries Found");
				});
	}

	@Override
	public List<FinGoalDetails> getGoalList() {
		log.debug("[{}] ==> Fetch Goal Service :: ", httpSession.getId());

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		return Optional.ofNullable(this.finGoalRepository.findAll()).filter(list -> !list.isEmpty()).orElseThrow(() -> {
			log.error("[{}] ==> Goals Not Found", httpSession.getId());
			return new FinovaException("FIN1002", "No Financial Goals Found");
		});
	}

	@Override
	public FinDiaryDetails deleteDiary(String diaryId) {
		log.debug("[{}] Delete Diary Service :: {}", httpSession.getId(), diaryId);

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		if (diaryId == null || diaryId.trim().isEmpty()) {
			log.error("[{}] ==> Diary ID is null or empty", httpSession.getId());
			throw new FinovaException("FIN1001", "Diary ID is required");
		}
		Optional<FinDiaryDetails> diaryOptional = this.finDiaryRepository.findById(diaryId);
		if (diaryOptional.isEmpty()) {
			log.error("[{}] ==> Diary Not Found :: {}", httpSession.getId(), diaryId);
			throw new FinovaException("FIN1002", "Diary Entry Not Found");
		}

		FinDiaryDetails existingDiaryDetails = diaryOptional.get();
		existingDiaryDetails.setFlag("N");

		Optional.of(this.finDiaryRepository.save(existingDiaryDetails)).orElseThrow(() -> {
			log.error("[{}] ==> Failed to save Dairy to Database", httpSession.getId());
			return new FinovaException("FIN1002", "Internal issue! Please try later");
		});

		return existingDiaryDetails;
	}

	@Override
	public FinEventDetails addEvent(FinEventDetails finEventDetails) {
		log.debug("[{}] Add Event Service :: {}", httpSession.getId(), finEventDetails);

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});

		finEventDetails.set__FVE1("FIN" + this.finUtils.dateFormat() + this.finUtils.generateRandomString(2));
		finEventDetails.set__created_at(this.finUtils.getDate());
		finEventDetails.set__created_at(this.finUtils.getDate());
		finEventDetails.setUserId(finovaUserDetails.getUserId());

		log.debug("[{}] ==> Adding Event to Database", httpSession.getId());
		Optional.of(this.finEventRepository.save(finEventDetails)).orElseThrow(() -> {
			log.error("[{}] ==> Failed to save Dairy to Database", httpSession.getId());
			throw new FinovaException("FIN1002", "Internal issue! Please try later");
		});

		log.debug("[{}] ==> Event Added Successfully ==> {}", httpSession.getId(), finEventDetails);
		return finEventDetails;
	}

	public List<FinEventDetails> getAllEvents() {

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		return finEventRepository.findAll();
	}

	@Transactional
	@Override
	public FinEventDetails updateEvent(FinEventDetails event) {
		log.debug("[{}] Event Updation Service {}:: ", httpSession.getId(), event);

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		if (event.get__FVE1() == null) {
			log.debug("[{}] Event Id Not Found In Request:: ", httpSession.getId());
			throw new FinovaException("FIN1002", "Internal issue! Please try later");
		}

		FinEventDetails existingEvent = finEventRepository.findById(event.get__FVE1()).orElseThrow(() -> {
			log.debug("Financial event not found with ID: {}", event.get__FVE1());
			return new FinovaException("FIN1002", "Event Not Found");
		});

		existingEvent.set__FVE2(event.get__FVE2());
		existingEvent.set__FVE3(event.get__FVE3());
		existingEvent.set__FVE4(event.get__FVE4());
		existingEvent.set__FVE5(event.get__FVE5());
		existingEvent.set__FVE6(event.get__FVE6());
		existingEvent.setReminder(event.getReminder());
		existingEvent.setStatus(event.getStatus());
		existingEvent.setDesc(event.getDesc());

		log.debug("[{}] Event Updated Successfully {}", httpSession.getId(), existingEvent);
		return finEventRepository.save(existingEvent);
	}

	@Override
	public String deleteEvent(String id) {
		log.debug("[{}] Event Deleted Service {}:: ", httpSession.getId(), id);

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		FinEventDetails existingEvent = finEventRepository.findById(id).orElseThrow(() -> {
			log.debug("Financial event not found with ID: {}", id);
			return new FinovaException("FIN1002", "Internal issue! Please try later");
		});

		log.debug("[{}] Existing Event Found {}:: ", httpSession.getId(), existingEvent);
		existingEvent.setStatus("In-Active");
		existingEvent.set__event_end_at(this.finUtils.getDate());
		finEventRepository.save(existingEvent);

		log.debug("[{}] Event Deleted Service :: ", httpSession.getId());
		return "Event Deleted Successfully";
	}

	@Override
	public FinGoalDetails updateGoal(FinGoalDetails finGoalDetails) {
		log.debug("[{}] Update Goal Service :: {}", httpSession.getId(), finGoalDetails);

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		String goalId = finGoalDetails.getGoalId();
		if (goalId == null) {
			log.debug("[{}] Goal Id Not Found In Request:: ", httpSession.getId());
			throw new FinovaException("FIN1002", "Internal issue! Please try later");
		}

		FinGoalDetails existingGoalDetails = this.finGoalRepository.findById(goalId).orElseThrow(() -> {
			log.debug("Goal Not found with ID: {}", goalId);
			return new FinovaException("FIN1002", "Event Not Found");
		});

		Optional.ofNullable(finGoalDetails.getCurrentAmount()).ifPresent(existingGoalDetails::setCurrentAmount);
		Optional.ofNullable(finGoalDetails.getTargetAmount()).ifPresent(existingGoalDetails::setTargetAmount);
		Optional.ofNullable(finGoalDetails.getTargetDate()).ifPresent(existingGoalDetails::setTargetDate);
		Optional.ofNullable(finGoalDetails.getGoal()).ifPresent(existingGoalDetails::setGoal);
		Optional.ofNullable(finGoalDetails.getPriority()).ifPresent(existingGoalDetails::setPriority);
		existingGoalDetails.setDate(this.finUtils.getDate());

		log.debug("[{}] Event Updated Successfully {}", httpSession.getId(), existingGoalDetails);
		return finGoalRepository.save(existingGoalDetails);
	}

	@Override
	public FinGoalDetails deleteGoal(FinGoalDetails finGoalDetails) {
		log.debug("[{}] Delete Goal Service :: {}", httpSession.getId(), finGoalDetails);

		FinovaUserDetails finovaUserDetails = Optional.ofNullable(this.customerService.getFinovaUser())
				.orElseThrow(() -> {
					log.debug("[{}] ==> Please login to access this service", httpSession.getId());
					return new FinovaException("MPAY1019", "Please login to access this service");
				});
		log.debug("[{}] finovaUserDetails ==> :: {}", httpSession.getId(), finovaUserDetails);

		String goalId = finGoalDetails.getGoalId();
		FinGoalDetails existingGoalDetails = this.finGoalRepository.findById(goalId).orElseThrow(() -> {
			log.debug("Goal not found with ID: {}", goalId);
			return new FinovaException("FIN1002", "Internal issue! Please try later");
		});

		log.debug("[{}] Existing Goal Found {}:: ", httpSession.getId(), existingGoalDetails);
		existingGoalDetails.setFlag("N");
		existingGoalDetails.setDate(this.finUtils.getDate());
		
		FinGoalDetails updatedFinGoalDetails = Optional.of(this.finGoalRepository.save(existingGoalDetails)).orElseThrow(() -> {
			log.error("[{}] ==> Failed to Update Goal to Database", httpSession.getId());
			throw new FinovaException("FIN1002", "Internal issue! Please try later");
		});

		log.debug("[{}] Goal Deleted Service :: ", httpSession.getId());
		return updatedFinGoalDetails;
	}

}
