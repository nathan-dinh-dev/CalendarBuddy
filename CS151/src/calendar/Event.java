package calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Event {
	private String name;
	private TimeInterval timeInterval;
	private boolean isRecurring;
	private DayOfWeek[] recurringDays; // For recurring events (e.g., MW, TR)
	private LocalDate startDate; // For recurring events
	private LocalDate endDate; // For recurring events

	// Default constructor - for one-time events
	public Event(String name, TimeInterval timeInterval) {
		this.name = name;
		this.timeInterval = timeInterval;
		this.isRecurring = false;
	}

	// Constructor for recurring events
	public Event(String name, TimeInterval timeInterval, DayOfWeek[] recurringDays, LocalDate startDate,
			LocalDate endDate) {
		this.name = name;
		this.timeInterval = timeInterval;
		this.isRecurring = true;
		this.recurringDays = recurringDays;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}

	public boolean isRecurring() {
		return isRecurring;
	}

	public void setRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public DayOfWeek[] getRecurringDays() {
		return recurringDays;
	}

	public void setRecurringDays(DayOfWeek[] recurringDays) {
		this.recurringDays = recurringDays;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	// Additional methods, like checking if an event falls on a given date for
	// recurring events
	public boolean occursOn(LocalDate date) {
		if (isRecurring) {
			// Check if the event is within the start and end date and falls on the same day
			// of the week
			if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
				for (DayOfWeek day : recurringDays) {
					if (date.getDayOfWeek() == day) {
						return true;
					}
				}
			}
			return false;
		} else {
			// For one-time events, just check if the date matches the event's date
			return timeInterval.getStartDate().equals(date);
		}
	}
}
