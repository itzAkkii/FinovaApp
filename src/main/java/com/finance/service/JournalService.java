package com.finance.service;

import java.util.List;

import com.finance.entity.FinDiaryDetails;
import com.finance.entity.FinEventDetails;
import com.finance.entity.FinGoalDetails;
import com.finance.model.JournalEntryDetails;

public interface JournalService {

	public String addJournal(JournalEntryDetails journalEntryDetails);

	public List<FinDiaryDetails> getDiaryList();

	public List<FinGoalDetails> getGoalList();

	public FinDiaryDetails deleteDiary(String diaryId);

	public FinEventDetails addEvent(FinEventDetails finEventDetails);

	public List<FinEventDetails> getAllEvents();

	public FinEventDetails updateEvent(FinEventDetails event);

	public String deleteEvent(String id);

	public FinGoalDetails updateGoal(FinGoalDetails finGoalDetails);

	public FinGoalDetails deleteGoal(FinGoalDetails finGoalDetails);

}
