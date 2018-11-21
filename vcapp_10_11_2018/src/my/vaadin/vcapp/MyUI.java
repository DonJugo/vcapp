package my.vaadin.vcapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.StreamException;

//User Interface zur Interaktion f�r den Nutzer
@SuppressWarnings("serial")
@Theme("mytheme")
public class MyUI extends UI {

	private TaskHandling service = TaskHandling.getInstance();
	private User user = User.getInstance();
	private Grid<Task> grid = new Grid<>(Task.class);
	private TaskForm form = new TaskForm(this);
	private TaskForm2 form2 = new TaskForm2(this);
	private String pfad = "C:\\dev\\projects.xml";
	private static final String Cookiename = "Uname";
	private static final String Cookiemail = "Umail";
	private static final String Cookieadmin = "Uadmin";
	private String adminCode = "admin";

	// Methode welche das User Interface initiiert und Komponenten hinzufuegt
	@Override
	protected void init(VaadinRequest vaadinRequest) {

		/* emins imput */
		XStream xstream = new XStream();
		try {
			ArrayList<Task> proj = (ArrayList<Task>) xstream.fromXML(new File(pfad));
			for (Task t : proj) {
				service.save(t);
			}
			updateList();
		} catch (Exception e) {

		}

		final VerticalLayout layout = new VerticalLayout();
		// Ueberschrift auf der Startseite

		Label header = new Label("Freiwilligen Koordination");
		header.addStyleName("h2");
		header.addStyleName("colored");

		Label loggedtext = new Label("");
		loggedtext.addStyleName("h2");
		loggedtext.addStyleName("colored");

		layout.addComponent(header);
		layout.addComponent(loggedtext);

		// Bennenen der Spalten der Tabelle in welcher Aufgaben angezeigt werden
		grid.setColumns();
		grid.addColumn(Task::getName).setCaption("Name");
		grid.addColumn(Task::getDescription).setCaption("Beschreibung");
		grid.addColumn(Task::getPeopleNeeded).setCaption("Personenanzahl");
		grid.addColumn(Task::getParticipateList).setCaption("Teilnehmer");
		grid.addColumn(Task::getDateString).setCaption("Datum");

		HorizontalLayout main = new HorizontalLayout(grid, form, form2);

		// Button zum hinzufuegen einer neuen Aufgabe
		Button addTaskBtn = new Button("Neue Aufgabe erstellen");
		addTaskBtn.setEnabled(user.isAdmin());
		// Bei klicken auf den Button soll eine neue Aufgabe hinzugefuegt werden
		addTaskBtn.addClickListener(create -> {
			grid.asSingleSelect().clear();
			form.setProject(new Task());
		});

		// Button zum Anmelden eines Users
		Button UserLogInBtn = new Button("Anmelden");
		Button UserLogOutBtn = new Button("Abmelden");
		UserLogOutBtn.setEnabled(user.isLoggedIn());

		if (getCookieByName(Cookiename) != null) {
			user.setName(getCookieByName(Cookiename).getValue());
			user.setEmail(getCookieByName(Cookiemail).getValue());
			user.setAdmin(getCookieByName(Cookieadmin).getValue().matches(adminCode));
			user.setLoggedIn(true);
			addTaskBtn.setEnabled(user.isAdmin());
			UserLogOutBtn.setEnabled(true);
			UserLogInBtn.setEnabled(false);

			loggedtext.setValue("eingeloggt als " + user.getName());

		}

		UserLogOutBtn.addClickListener(logoutclick -> {
			deleteCookies();
			user.logOut();
			UserLogInBtn.setEnabled(true);
			UserLogOutBtn.setEnabled(user.isLoggedIn());
			addTaskBtn.setEnabled(user.isAdmin());
			loggedtext.setValue("");

		});

		UserLogInBtn.addClickListener(loginclick -> {

			if (getCookieByName(Cookiename) == null) {
				LogInWindow logIn = new LogInWindow();

				logIn.setModal(true);
				this.addWindow(logIn);
				logIn.getBtn().addClickListener(close -> {
					user.setEmail(logIn.getUserMail());
					user.setName(logIn.getUserName());
					user.setLoggedIn(true);
					user.setAdmin(logIn.getAdminField().matches(adminCode));
					addTaskBtn.setEnabled(user.isAdmin());
					UserLogInBtn.setEnabled(user.isLoggedIn());
					Cookie ck_name = new Cookie("Uname", logIn.getUserName());
					Cookie ck_mail = new Cookie("Umail", logIn.getUserMail());
					Cookie ck_admin = new Cookie("Uadmin", logIn.getAdminField());

					int cookieAge = 600;

					ck_name.setMaxAge(cookieAge);
					ck_mail.setMaxAge(cookieAge);
					ck_admin.setMaxAge(cookieAge);

					ck_name.setPath(VaadinService.getCurrentRequest().getContextPath());
					ck_mail.setPath(VaadinService.getCurrentRequest().getContextPath());
					ck_admin.setPath(VaadinService.getCurrentRequest().getContextPath());

					VaadinService.getCurrentResponse().addCookie(ck_name);
					VaadinService.getCurrentResponse().addCookie(ck_mail);
					VaadinService.getCurrentResponse().addCookie(ck_admin);

					UserLogOutBtn.setEnabled(user.isLoggedIn());
					addTaskBtn.setEnabled(user.isAdmin());

					loggedtext.setValue("eingeloggt als " + user.getName());

				});

			} else {
				Notification.show("angemeldet als " + getCookieByName(Cookiename).getValue());
				addTaskBtn.setEnabled(user.isAdmin());
				UserLogOutBtn.setEnabled(user.isLoggedIn());
				loggedtext.setValue("eingeloggt als " + user.getName());

			}

		});

		HorizontalLayout buttons = new HorizontalLayout(addTaskBtn, UserLogInBtn, UserLogOutBtn);

		layout.addComponents(buttons, main);

		main.setSizeFull();
		grid.setSizeFull();
		main.setExpandRatio(grid, 1);

		setContent(layout);
		form.setVisible(false);
		form2.setVisible(false);

		grid.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() == null) {
				form.setVisible(false);
			} else {
				form.optionsProject(event.getValue());
			}
		});

	}

	// aktualisiert die Liste welche alle Aufgaben enthaelt
	public void updateList() {
		List<Task> proj = service.findAll();
		/* emin input */
		XStream xstream = new XStream();
		try {
			xstream.toXML(proj, new FileOutputStream(new File(pfad)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		grid.setItems(proj);

	}

	private Cookie getCookieByName(String name) {
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		for (Cookie ck : cookies) {
			if (name.equals(ck.getName())) {
				return ck;
			}
		}
		return null;
	}

	private void deleteCookies() {
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		for (Cookie ck : cookies) {
			ck.setValue("");
			ck.setPath(VaadinService.getCurrentRequest().getContextPath());
			ck.setMaxAge(0);
			VaadinService.getCurrentResponse().addCookie(ck);
		}
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {

	}
}
