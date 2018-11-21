package my.vaadin.vcapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Survey {
	private final HashMap<Long, DateToChoose> dateList = new HashMap<>();
	private long nextId = 0;
	private boolean found = false;

	public Survey() {
	}

	public void addDate(DateToChoose date) {
		if (date == null)
			return;

		if (dateList.containsValue(date))
			return;

		if (date.getId() == null) {
			date.setId(nextId++);
		}
		try {
			date = (DateToChoose) date.clone();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		dateList.put(date.getId(), date);
	}

	public synchronized void addParticipantToSurvey(Set<DateToChoose> selected, Participant participant) {
		for (DateToChoose date : dateList.values()) {
			for (DateToChoose date2 : selected) {

				if (date2.getDate().equals(date.getDate())) {
					date.addParticipantToDate(participant);
				}
			}
		}
	}

	public synchronized List<DateToChoose> findAll() {
		ArrayList<DateToChoose> arrayList = new ArrayList<>();
		for (DateToChoose date : dateList.values()) {
			try {
				arrayList.add(date.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		Collections.sort(arrayList, new Comparator<DateToChoose>() {

			@Override
			public int compare(DateToChoose d1, DateToChoose d2) {
				return (int) (d2.getId() - d1.getId());
			}
		});
		return arrayList;
	}
	
	public synchronized boolean containsParticipant(User user){
		found = false;
		dateList.forEach((id,date)->{
			date.getParticipateList().forEach(e->{
				if(e.getparticipantName().equals(user.getName())&&e.getContact().equals(user.getEmail())) {
					found = true;
					return;
				}
			});
			
			
		});
		return found;
	}

}
