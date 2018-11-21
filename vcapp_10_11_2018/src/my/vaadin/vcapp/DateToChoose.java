package my.vaadin.vcapp;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class DateToChoose implements Serializable, Cloneable {
	private List<Participant> participantList = new LinkedList<Participant>();
	private LocalDate date;
	private Long id;

	public DateToChoose(LocalDate date) {
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 43 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public DateToChoose clone() throws CloneNotSupportedException {
		return (DateToChoose) super.clone();
	}

	public void addParticipantToDate(Participant participant) {
		participantList.add(participant);
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<Participant> getParticipateList() {
		return participantList;
	}

	public String getParticipateListAsString() { // Änderung auf nur Teilnehmeranzahl in Umfrage wird angezeigt
		StringBuilder sb = new StringBuilder();
		sb.append(participantList.size());
		/*for (Participant s : this.participantList) {
			sb.append(s.getparticipantName() + s.getContact() + participantList.size());
			sb.append("\n");

		}
		return sb.toString();*/
		return sb.toString();
	}

	public int numberOfParticipants() {
		return participantList.size();
	}

	public void setParticipateList(List<Participant> participateList) {
		this.participantList = participateList;
	}

}
