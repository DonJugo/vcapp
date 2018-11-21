package my.vaadin.vcapp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Klasse welche alle Services/Handling fuer Aufgaben ermoeglicht
public class TaskHandling {

	private static TaskHandling instance;
	private static final Logger LOGGER = Logger.getLogger(TaskHandling.class.getName());
	LocalDate today = LocalDate.now();
	private final HashMap<Long, Task> tasks = new HashMap<>();
	private long nextId = 0;

	private TaskHandling() {
	}

	// gibt Instanz eines ProjectHandlings zurueck
	public static TaskHandling getInstance() {
		if (instance == null) {
			instance = new TaskHandling();
		}
		return instance;
	}

	// gibt alle vorhandenen Aufgaben zurueck
	public synchronized List<Task> findAll() {
		return findAll(null);
	}
	public synchronized boolean isPersistend()
	{
		tasks.containsKey(nextId);
		return false;
		
	}

	public synchronized List<Task> findAll(String stringFilter) {
		ArrayList<Task> arrayList = new ArrayList<>();
		for (Task task : tasks.values()) {
			try {
				// Aufgabennamenfilter
				boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
						|| task.toString().toLowerCase().contains(stringFilter.toLowerCase());
				// Filter auf Aufgabe erledigt
				boolean passesFilter2 = (!task.isDone());
				// Filter auf Datum vor dem heutigen Tag
				boolean passesFilter3 = (task.getDate() == null || !task.getDate().isBefore(today));
				if (passesFilter && passesFilter2 && passesFilter3) {
					arrayList.add(task.clone());
				}
			} catch (CloneNotSupportedException ex) {
				Logger.getLogger(TaskHandling.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		Collections.sort(arrayList, new Comparator<Task>() {

			@Override
			public int compare(Task p1, Task p2) {
				return (int) (p2.getId() - p1.getId());
			}
		});
		return arrayList;
	}

	// gibt alle Aufgabe zurueck die durch den Filter gehen und limitiert so das
	// Resultset
	public synchronized List<Task> findAll(String stringFilter, int start, int maxresults) {
		ArrayList<Task> arrayList = new ArrayList<>();
		for (Task task : tasks.values()) {
			try {
				// Aufgabenamenfilter
				boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
						|| task.toString().toLowerCase().contains(stringFilter.toLowerCase());
				// Filter auf Aufgabe erledigt
				boolean passesFilter2 = (!task.isDone());
				// Filter auf Datum vor dem heutigen Tag
				boolean passesFilter3 = (task.getDate() == null || !task.getDate().isBefore(today));

				if (passesFilter && passesFilter2 && passesFilter3) {
					arrayList.add(task.clone());
				}
			} catch (CloneNotSupportedException ex) {
				Logger.getLogger(TaskHandling.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		Collections.sort(arrayList, new Comparator<Task>() {

			@Override
			public int compare(Task p1, Task p2) {
				return (int) (p2.getId() - p1.getId());
			}
		});
		int end = start + maxresults;
		if (end > arrayList.size()) {
			end = arrayList.size();
		}
		return arrayList.subList(start, end);
	}

	// gibt die Anzahl der vorhandenen Aufgaben zurueck
	public synchronized long count() {
		return tasks.size();
	}

	// loescht eine Aufgabe vom System
	public synchronized void delete(Task value) {
		tasks.remove(value.getId());
	}

	// persistiert Aufgabe oder aktualisiert
	public synchronized void save(Task entry) {
		if (entry == null) {
			LOGGER.log(Level.SEVERE,
					"Project is null. Are you sure you have connected your form to the application as described in tutorial chapter 7?");
			return;
		}
		if (entry.getId() == null) {
			
			while (tasks.containsKey(nextId))
			{nextId++;}
			
			entry.setId(nextId++);
		}
		try {
			entry = (Task) entry.clone();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		tasks.put(entry.getId(), entry);
	}

	// setzt Aufgabe auf erledigt
	public synchronized void setDone(Task task) {
		tasks.get(task.getId()).setDone();
	}
}
