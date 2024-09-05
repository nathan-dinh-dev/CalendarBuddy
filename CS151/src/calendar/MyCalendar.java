package calendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MyCalendar {
	private List<Event> events;

	public MyCalendar() {
		this.events = new ArrayList<>();
	}

	public void loadEvents(String filename) {

	}

	public void saveEvents(String filename) {

	}

	public void addEvent(Event event) {

	}

	public void deleteEvent(String name, LocalDate date) {

	}

	public List<Event> getEventsonDate(LocalDate date) {
		return new ArrayList<Event>();
	}
}
