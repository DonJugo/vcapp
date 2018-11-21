package my.vaadin.vcapp;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SurveyWindow extends Window {
	//private DateField date = new DateField("Datum");
	private Survey survey = new Survey();
    private VerticalLayout subContent = new VerticalLayout();
    private VerticalLayout subContent2 = new VerticalLayout(); // DateField 
	private Grid<DateToChoose> grid;
	
    private Set<DateToChoose> selectedDates= null; // für Abstimmung von Teilnehmern 
    private DateToChoose date; // das händisch gewählte Datum des Erstellers nach der Abstimmung

	
	public SurveyWindow() { // wenn die Umfrage erstellt wird -> bei Value Change auf Auswahl Datum im DropDown Menü in Project Form
		 super("Termine zur Abstimmung wählen"); // Set window caption
		 center();
	        setClosable(true);
	        subContent.addComponent(new Button("Datum hinzufügen",event ->{
	        DateField field = new DateField();
	        subContent2.addComponent(field);
	        	
	        field.addValueChangeListener(e->{
	        addDate(field.getValue()); // wenn sich der Wert 2 mal ändert werden leider beide Datum gespeichert
	        field.setEnabled(false);
	        });
	        
	        }));
	        subContent.addComponents(new Button ("Umfrage erstellen", click -> close()),subContent2);
	        setContent(subContent);
	      
	    }
	
	public SurveyWindow(Survey survey,Participant participant) { // Wenn die Umfrage von einem Teilnehmer geöffnet wird
		 super("An welchen Terminen haben Sie Zeit?");
	    
	     center();
	     setClosable(true);
	     grid = new Grid<>(DateToChoose.class);
	     
	     grid.setColumns(/*"name", "description", "peopleNeeded"*/);
	     grid.addColumn(DateToChoose::getDate).setCaption("Termin");
	     grid.addColumn(DateToChoose::getParticipateListAsString).setCaption("Teilnehmer");
	     grid .setSelectionMode(SelectionMode.MULTI);
	     grid.addSelectionListener(event -> {
	    	   // Set<DateToChoose> selected = event.getAllSelectedItems();
	    	    this.selectedDates= event.getAllSelectedItems();

	    	    Notification.show(selectedDates.size() + " Termine gewählt");
	    	        	
	    	    });
	     
	     
	     subContent.addComponent(grid);
	     subContent.addComponents(new Button ("Auswahl speichern", click -> {
	    	 if(this.selectedDates!=null) {

	    	 survey.addParticipantToSurvey(selectedDates,participant);	
	    	 }
	    	 close();
	    	 
	    	 
	     }));
	     setContent(subContent);
	     
	     
	     List<DateToChoose> dates = survey.findAll();
	     grid.setItems(dates);
	    
	}
	
	public SurveyWindow(Survey survey) { // wenn vom Ersteller aus der Umfrage ein Termin fixiert wird
		 super("Welcher Termin soll fixiert werden?");
		    
	     center();
	     setClosable(true);
	     grid = new Grid<>(DateToChoose.class);
	     grid.setColumns();
	     grid.addColumn(DateToChoose::getDate).setCaption("Termin");
	     grid.addColumn(DateToChoose::numberOfParticipants).setCaption("Teilnehmer");
	     grid.asSingleSelect().addValueChangeListener(event -> {
	    	 if (event.getValue() != null) {
	    	 this.date = event.getValue();
	    	 }
			});	
	     subContent.addComponent(grid);
	     subContent.addComponents(new Button ("Auswahl speichern", click -> {
	    	 close();
	     }));
	     
	     setContent(subContent);
	     
	     
	     List<DateToChoose> dates = survey.findAll();
	     grid.setItems(dates);
	}


	private void addDate(LocalDate date) { // 
		this.survey.addDate(new DateToChoose(date));
	}
	
	public Survey getSurvey() { // gibt die Umfrage als ganzes Zurück. Darin enthalten sind alle möglichen Datum und deren Teilnehmer
		return this.survey;
	}
	
	public DateToChoose getDate() { // gibt das gewählte Fixdatum zurück
		return this.date;
	}
	
	public void reset() {
    	this.survey= new Survey();
    	subContent2.removeAllComponents();
   
    	
    }
	}

	


