package my.vaadin.vcapp;

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TaskForm2 extends FormLayout {

	protected Button delete = new Button("Löschen");
	protected Button change = new Button("Bearbeiten");
	protected Button participate = new Button("Teilnehmen");
	protected Button setDone = new Button("Aufgabe erledigt");
	protected Button setDate = new Button("Umfrage beenden");
	protected Button notify = new Button("Teilnehmer benachrichtigen");
	protected VerticalLayout buttons = new VerticalLayout();

	// Konstruktor zum hinzufuegen aller Componenten/Buttons
	public TaskForm2(MyUI myUI) {
		delete.setStyleName("danger");
		change.setStyleName("primary");
		participate.setStyleName("friendly");
		buttons.addComponents(participate, change, setDone, setDate, delete, notify);
		setSizeUndefined();
		addComponents(buttons);
		
	
		
	}
}
