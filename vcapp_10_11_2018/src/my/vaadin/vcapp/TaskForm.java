package my.vaadin.vcapp;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class TaskForm extends FormLayout {

	protected TextField name = new TextField("Aufgabenname");
	protected TextField description = new TextField("Beschreibung");
	protected TextField peopleNeeded1 = new TextField("Anzahl an Personen");
	protected TextField password = new TextField("Passwort");
	protected NativeSelect<my.vaadin.vcapp.TaskGroup> group = new NativeSelect<>("Art der Aufgabe");
	protected NativeSelect<my.vaadin.vcapp.TaskDateSpecification> datespecification = new NativeSelect<>(
			"Datumsart");
	private DateField date = new DateField("Termin");
	protected Button participate = new Button("Teilnehmen");
	protected Button create = new Button("Erstellen / Speichern");

	protected TextField participateName = new TextField("Geben Sie Ihren Namen ein");
	protected TextField participateMail = new TextField("Geben Sie Ihre E-mail Adresse ein");

	private TaskHandling service = TaskHandling.getInstance();
	private User user= User.getInstance();
	
	private Task task;
	private MyUI myUI;
	private Binder<Task> binder1 = new Binder<>(Task.class);
	private TaskForm2 form2 = new TaskForm2(myUI);
	private DateSubWindow subWindowDATE = new DateSubWindow();
	private SurveyWindow surveyWindow = new SurveyWindow();

	// ---------------------------------------------------------------------------------------------
	public TaskForm(MyUI myUI) {

		
		
		
		
		
		
		
		
		this.myUI = myUI;
		setSizeUndefined();
		date.setVisible(false);

		// um nur das Pop Up Window bearbeiten zu koennen
		// Hintergrund kann nicht bearbeitet werden
		subWindowDATE.setModal(true);

		// um nur das Pop Up Window bearbeiten zu koennen
		// Hintergrund kann nicht bearbeitet werden
		surveyWindow.setModal(true);

		// Verbinden der NativeSelect Felder mit den jeweiligen Enumklassen
		datespecification.setItems(TaskDateSpecification.values());
		group.setItems(TaskGroup.values());

		create.setStyleName(ValoTheme.BUTTON_PRIMARY);
		create.setClickShortcut(KeyCode.ENTER);

		// manuelles Binden des Feldes fuer die Anzahl der benoetigten
		// Teilnehmer
		// nur Integerwerte koennen eingegeben werden
		binder1.forField(peopleNeeded1).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(Integer.valueOf(0), "integers only"))
				.bind(Task::getPeopleNeeded, Task::setPeopleNeeded);

		// automatisches Binden aller Felder
		binder1.bindInstanceFields(this);

		// oeffnen des Datumsauswahl Fensters wenn Fixdatum oder Deadline
		// ausgewahlt wurde
		datespecification.addValueChangeListener(e -> {
			if ((datespecification.getValue() == TaskDateSpecification.Fixdatum
					|| datespecification.getValue() == TaskDateSpecification.Deadline)
					&& datespecification.isVisible()) {
				myUI.addWindow(subWindowDATE);
			}
			// oeffnen des Umfragenfensters wenn Auswahldatum ausgewaehlt wird
			if (datespecification.getValue() == TaskDateSpecification.AuswahlDatum
					&& datespecification.isVisible()) {
				myUI.addWindow(surveyWindow);
			}
		});

		// Ueberpruefung ob Passwort und Bezeichung der Aufgabe eingegeben
		// wurden, sonst Fehlermeldungen
		create.addClickListener(e -> {

			if (password.getValue().isEmpty() && name.getValue().isEmpty()) {
				Notification.show("Passwort und Aufgabe muessen eingegeben werden");
			} else if (password.getValue().isEmpty()) {
				Notification.show("Passwort muss eingegeben werden");
			} else if (name.getValue().isEmpty()) {
				Notification.show("Aufgabe muss eingegeben werden");
			} else {
				if (task.isPersisted() == false && (datespecification.getValue() == TaskDateSpecification.Fixdatum
						|| datespecification.getValue() == TaskDateSpecification.Deadline)) {
					// um beim Erstellen der Aufgabe das
					// gewaehlte Datum aus dem SubWindow zu erhalten
					date.setValue(subWindowDATE.getDate());
				}

				if (task.isPersisted() == false
						&& datespecification.getValue() == TaskDateSpecification.AuswahlDatum) {
					task.setSurvey(surveyWindow.getSurvey());
				}

				this.save();
				surveyWindow.reset();
				subWindowDATE.reset();

			}
		});

		// -Loeschen
		// wenn Passwort im SubWindow richtig eingegeben ist und das Fenster mit
		// OK geschlossen wird, wird ueber den Close Listener die Methode delete
		// fuer das gewaehlte  ausgeloest
		form2.delete.addClickListener(e -> {
			PasswordSubWindow subWindowPW = new PasswordSubWindow(task);
			subWindowPW.setModal(true);
			myUI.addWindow(subWindowPW);

			subWindowPW.addCloseListener(close -> {
				if (subWindowPW.getCorrect()) {
					Notification.show("Aufgabe " + task.getName() + " wurde erfolgreich gelöscht!");
					delete();
				}
			});

		});

		// -Aendern---------------------------------------------------------------------------------------------------
		// wenn Passwort im SubWindow richtig eingegeben ist und das Fenster mit
		// OK geschlossen wird wird ueber den Close Listener die Methode
		// setProject fuer das gewaehlte  ausgeloest
		form2.change.addClickListener(e -> {
			PasswordSubWindow subWindowPW = new PasswordSubWindow(task);
			subWindowPW.setModal(true);
			myUI.addWindow(subWindowPW);

			subWindowPW.addCloseListener(close -> {
				if (subWindowPW.getCorrect()) {
					// subWindowPW.reset();
					this.setProject(task);
				}
			});
		});

		// -Teilnehmer-hinzufuegen
		form2.participate.addClickListener(e -> this.addParticipant(task));

		// Teilnehmer des s durch E-Mail benachrichtigen durch
		// mailto-Link
		form2.notify.addClickListener(e -> {
			Resource res = new ExternalResource(
					"mailto:" + task.getContactString() + "?subject=" + task.getName() + "%20&amp");
			ResourceReference rr = ResourceReference.create(res, myUI, "email");
			Page.getCurrent().open(rr.getURL(), null);
		});

		// an  Teilnehmen mit Name und E-Mail Adresse (obligatorisch)
		participate.setClickShortcut(KeyCode.ENTER);
		participate.addClickListener(event -> {

			if (participateName.getValue().isEmpty() && participateMail.getValue().isEmpty()) {
				Notification.show("Name und Email müssen eingegeben werden");
			} else if (participateName.getValue().isEmpty()) {
				Notification.show("Name muss eingegeben werden");
			} else if (participateMail.getValue().isEmpty()) {
				Notification.show("Email muss eingegeben werden");

			} else {

				if (task.getDateSpecification() == TaskDateSpecification.AuswahlDatum) {
					binder1.removeBean();
					SurveyWindow surWindow = new SurveyWindow(task.getSurvey(),
							new Participant(participateName.getValue(), participateMail.getValue()));
					// um nur das Pop-Up Window bearbeiten zu koennen
					surWindow.setModal(true);
					myUI.addWindow(surWindow);
					surWindow.addCloseListener(close -> task.setSurvey(surWindow.getSurvey()));

				} else {
					task.addParticipant(new Participant(participateName.getValue(), participateMail.getValue()));
				}
				this.save();
				participateName.clear();
				participateMail.clear();
				setVisible(false);
			}

		});

		//  auf Erledigt setzen wenn Passwort richtig eingegeben wurde
		form2.setDone.addClickListener(e -> {

			PasswordSubWindow subWindowPW = new PasswordSubWindow(task);
			subWindowPW.setModal(true);
			myUI.addWindow(subWindowPW);

			subWindowPW.addCloseListener(close -> {
				if (subWindowPW.getCorrect()) {
					setProjectDone();
				}
			});

		});

		// endgueltiges Datum bei Auswahldatum festlegen bei Eingabe des
		// richtigen Passwortes
		form2.setDate.addClickListener(click -> {

			PasswordSubWindow subWindowPW = new PasswordSubWindow(task);
			subWindowPW.setModal(true);
			myUI.addWindow(subWindowPW);

			subWindowPW.addCloseListener(close -> {
				if (subWindowPW.getCorrect()) {
					SurveyWindow surveyWindow = new SurveyWindow(task.getSurvey());
					// um nur das Pop Up Window bearbeiten zu koennen
					surveyWindow.setModal(true);
					myUI.addWindow(surveyWindow);
					surveyWindow.addCloseListener(closeSurvey -> {
						task.setDate(surveyWindow.getDate().getDate());
						task.setParticipateList(surveyWindow.getDate().getParticipateList());
						task.setDateSpecification(TaskDateSpecification.Fixdatum);

						// Benachrichtigen der relevanten Teilnehmer, dass Datum
						// ausgewaehlt wurde
						Resource res = new ExternalResource(
								"mailto:" + task.getContactString() + "?subject=" + task.getName()
										+ "&amp;body=F�r%20die%20Aufgabe%20wurde%20folgender%20Termin%20ausgewahlt:%20"
										+ task.getDate().toString());
						ResourceReference rr = ResourceReference.create(res, myUI, "email2");
						Page.getCurrent().open(rr.getURL(), null);
						save();
					});
				}
			});

		});
	}

	// Setzt  auf erledigt
	public void setProjectDone() {
		service.setDone(task);
		myUI.updateList();
		setVisible(false);
	}

	public void optionsProject(Task task) {
		// um Abfage zu ermoeglichen -> ansonsten ValueChangeListener aktiv bei
		// Datumform
		datespecification.setVisible(false);
		removeAllComponents();
		this.task = task;
		binder1.setBean(task);
		form2.change.setVisible(user.isAdmin());
		form2.delete.setVisible(user.isAdmin());
		form2.setDone.setVisible(user.isAdmin());
		form2.setDate.setVisible(user.isAdmin());
		form2.notify.setVisible(user.isAdmin());
		form2.participate.setVisible(true);
		addComponent(form2);
		if (datespecification.getValue() == TaskDateSpecification.AuswahlDatum) {
			form2.setDate.setVisible(user.isAdmin());
		} else
			form2.setDate.setVisible(false);
			setVisible(true);
		
	}
	

	// fuegt neuen Teilnehmer hinzu
	public void addParticipant(Task task) {
		if(user.isLoggedIn()) {
			participateName.setValue(user.getName());
			participateMail.setValue(user.getEmail());
		}
		this.task = task;
		binder1.setBean(task);

		removeAllComponents();
		addComponents(participateName, participateMail, participate);
	}

	// fuegt neues  hinzu
	public void setProject(Task task) {
		removeAllComponents();
		addComponents(name, description, peopleNeeded1, datespecification, group, date, password, create);
		// um Abfage zu ermoeglichen -> ansonsten ValueChangeListener aktiv bei
		// Datumform
		datespecification.setVisible(true);

		// datespecification kann nur einmal beim Erstellen bestimmt werden
		if (task.isPersisted())
			datespecification.setEnabled(false);
		else
			datespecification.setEnabled(true);
		this.task = task;
		binder1.setBean(task);
		setVisible(true);
		name.selectAll();
	}

	// loescht 
	protected void delete() {
		service.delete(task);
		myUI.updateList();
		setVisible(false);
	}

	// speichert
	private void save() {
		service.save(task);
		myUI.updateList();
		setVisible(false);
	}
}
