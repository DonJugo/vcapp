package my.vaadin.vcapp;

import java.time.LocalDate;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
// Fenster zur Auswahl eines Datums bei Fixdatum und Deadline
public class DateSubWindow extends Window {
	// vaadin Datumsfeld
	private DateField date = new DateField("Datum");

	// Konstruktor
	public DateSubWindow() {
		super("Termin wählen");
		center();
		VerticalLayout subContent = new VerticalLayout();
		setClosable(true);
		subContent.addComponents(date, new Button("OK", event -> close()));
		setContent(subContent);
	}

	public LocalDate getDate() {
		return this.date.getValue();
	}

	public void reset() {
		this.date.clear();
	}
}
