package my.vaadin.vcapp;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class LogInWindow extends Window {
	private TextField userName = new TextField("Geben Sie Ihren Namen ein *");
	private TextField userMail = new TextField("Geben Sie Ihre E-mail Adresse ein *");
	private TextField adminField = new TextField("AdminCode");
	private Button btn = new Button("OK", event -> {
		
		if (!userName.getValue().isEmpty() && !userMail.getValue().isEmpty()) {
			Notification.show("Anmeldung erfolgreich!");
			
			
			
			close();
		} else
			Notification.show("Anmeldung nur mit Namen und Email m�glich!");

	});



	public LogInWindow() {
		super("Anmeldedaten eingeben. Anmeldung nur mit Namen und Email möglich!");
		center();
		VerticalLayout subContent = new VerticalLayout();
		setClosable(true);
		subContent.addComponents(userName, userMail,adminField, btn);
		setContent(subContent);
	}

	public String getUserMail() {
		return this.userMail.getValue();
	}

	public String getUserName() {
		return this.userName.getValue();
	}
	
	public String getAdminField() {
		return this.adminField.getValue();
	}
	

	public void reset() {
		this.userName.clear();
		this.userMail.clear();
	}
	public Button getBtn() {
		return this.btn;
	}

}
