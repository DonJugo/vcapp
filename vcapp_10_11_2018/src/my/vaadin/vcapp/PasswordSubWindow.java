package my.vaadin.vcapp;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

//Fenster fuer Aktionen fuer die man ein Passwort benoetigt
@SuppressWarnings("serial")
public class PasswordSubWindow extends Window {
	private TextField password = new TextField("Passwort eingeben");
	boolean correct = false;

	public PasswordSubWindow(Task task) {
		super("Geben Sie das Passwort ein:");
		center();
		VerticalLayout subContent = new VerticalLayout();
		setClosable(true);

		subContent.addComponents(password, new Button("OK", e -> {
			// eingegebenes Passwort mit dem bei der Aufgabe hinterlegtem
			// vergleichen
			if (this.password.getValue().equals(task.getPassword())) {
				correct = true;
				close();
			} else {
				// bei falscher Passworteingabe
				Notification.show("falsches Passwort");
				correct = false;
			}
		}));
		setContent(subContent);

	}

	// gibt zurueck ob Passwort korrekt eingegeben wurde oder nicht
	public boolean getCorrect() {
		return correct;
	}

	public void reset() {
		this.password.clear();
		correct = false;
	}
}
