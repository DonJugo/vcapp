package my.vaadin.vcapp;

//Klasse welche einen Teilnehmer einer Aufgabe repraesentiert
public class Participant {

	private String participantName = "";
	private String contact = "";

	// Konstruktor zum erstellen neuer Teilnehmer mit Name und E-Mail Adresse
	public Participant(String participantName, String contact) {
		this.participantName = participantName;
		this.contact = contact;
	}

	// gibt den Namen eines Teilnehmers zurueck
	public String getparticipantName() {
		return participantName;
	}

	// setzt den Namen eines Teilnehmers
	public void setparticipantName(String participantName) {
		this.participantName = participantName;

	}

	// gibt die E-Mail Adresse eines Teilnehmers zurueck
	public String getContact() {
		return contact;
	}

	// Setzt die E-Mail Adresse eines Teilnehmers
	public void setContact(String contact) {

		this.contact = contact;
	}
}
