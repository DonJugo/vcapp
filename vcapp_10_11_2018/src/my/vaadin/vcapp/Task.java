package my.vaadin.vcapp;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

//Klasse welche Aufgaben repraesentiert
@SuppressWarnings("serial")
public class Task implements Serializable, Cloneable {
	
	private User creator;

	private boolean done = false;

	private Long id;

	private String name = "";

	private TaskDateSpecification datespecification;

	private String description = "";

	private String password = "";

	private Integer peopleNeeded;

	private TaskGroup group;

	protected List<Participant> participateList = new LinkedList<Participant>();

	private LocalDate date;

	public Survey survey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isPersisted() {
		return id != null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof Task && obj.getClass().equals(getClass())) {
			return this.id.equals(((Task) obj).getId());
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 43 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public Task clone() throws CloneNotSupportedException {
		return (Task) super.clone();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void addParticipant(Participant participant) {
		participateList.add(participant);
	}

	// gibt einen String aus allen Namen der Teilnehmer der Aufgabe zurueck
	public String getParticipateList() {
		StringBuilder sb = new StringBuilder();
		for (Participant s : this.participateList) {
			sb.append(s.getparticipantName());
			sb.append("\n");
		}
		return sb.toString();
	}

	public void setParticipateList(List<Participant> participateList) {
		this.participateList = participateList;
	}

	public TaskDateSpecification getDateSpecification() {
		return datespecification;
	}

	public void setDateSpecification(TaskDateSpecification datespecification) {
		this.datespecification = datespecification;
	}

	public Integer getPeopleNeeded() {
		return peopleNeeded;
	}

	public void setPeopleNeeded(Integer peopleNeeded) {
		this.peopleNeeded = peopleNeeded;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getDate() {
		return date;
	}

	// gibt das Datum oder "-" wenn kein Datum vorhanden ist
	// inklusive Datumsbezeichnung zurueck
	public String getDateString() {
		if (this.getDate() == null&&this.getDateSpecification().toString() != "AuswahlDatum") {
			return "-";
		}
		if (this.getDateSpecification().toString() == "Fixdatum") {
			return date.toString() + " (fixes Datum)";
		} else if (this.getDateSpecification().toString() == "Deadline") {
			return date.toString() + " (Deadline)";
		} 
		else if(this.getDateSpecification().toString() == "AuswahlDatum") {
			return "Umfrage läuft";
		}
		else {
			return date.toString();
		}
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public TaskGroup getGroup() {
		return group;
	}

	public void setGroup(TaskGroup group) {
		this.group = group;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone() {
		this.done = true;
	}

	// fuer Datumsumfrage
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public Survey getSurvey() {
		return this.survey;
	}

	// gibt einen String bestehend aus allen E-Mail Adressen, getrennt durch
	// Komma
	// der Teilnehmer zurueck
	public String getContactString() {
		String contactString = "";
		for (Participant participant : participateList) {
			contactString += participant.getContact() + ("; ");
		}
		return contactString;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

}