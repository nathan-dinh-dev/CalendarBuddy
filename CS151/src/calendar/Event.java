package calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * The Event class represents an one-time and recurring events in the calendar.
 * 
 * 
 * Programmed by: Nathan Dinh
 * 
 * Date: 09/13/2024
 * 
 */

public class Event {

	// Declare variables
	private String name;
	private TimeInterval timeInterval;
	private boolean isRecurring;
	private DayOfWeek[] recurringDays; // For recurring events (e.g., MW, TR)
	private LocalDate startDate; // For recurring events
	private LocalDate endDate; // For recurring events

	/**
	 * Constructor for one-time events.
	 * 
	 * @param name:         name of the event
	 * @param timeInterval: the time interval the event occurs
	 */
	public Event(String name, TimeInterval timeInterval) {
		this.name = name;
		this.timeInterval = timeInterval;
		this.isRecurring = false;
	}

	/**
	 * Constructor for recurring events.
	 * 
	 * @param name:          name of the event
	 * @param timeInterval:  time interval the event occurs
	 * @param recurringDays: array of days when the event occurs
	 * @param startDate:     start date of the recurring event
	 * @param endDate:       end date of the recurring event
	 */
	public Event(String name, TimeInterval timeInterval, DayOfWeek[] recurringDays, LocalDate startDate,
			LocalDate endDate) {
		this.name = name;
		this.timeInterval = timeInterval;
		this.isRecurring = true;
		this.recurringDays = recurringDays;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * Gets the name of the event.
	 * 
	 * @return the name of the event
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the event.
	 * 
	 * @param name the new name of the event
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the time interval the event occurs.
	 * 
	 * @return the time interval of the event
	 */
	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	/**
	 * Sets occurs time for the event.
	 * 
	 * @param timeInterval: the new time interval for the event
	 */
	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}

	/**
	 * Checks if the event is recurring.
	 * 
	 * @return true if the event is recurring, false otherwise
	 */
	public boolean isRecurring() {
		return isRecurring;
	}

	/**
	 * Sets whether the event is recurring.
	 * 
	 * @param isRecurring: true if the event is recurring, false otherwise
	 */
	public void setRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	/**
	 * Gets the days on which the recurring event occurs.
	 * 
	 * @return an array of DayOfWeek objects of the recurring days
	 */
	public DayOfWeek[] getRecurringDays() {
		return recurringDays;
	}

	/**
	 * Sets the days on which the recurring event occurs.
	 * 
	 * @param recurringDays: an array of DayOfWeek objects of the new recurring days
	 */
	public void setRecurringDays(DayOfWeek[] recurringDays) {
		this.recurringDays = recurringDays;
	}

	/**
	 * Gets the start date of the recurring event.
	 * 
	 * @return the start date of the recurring event
	 */
	public LocalDate getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date of the recurring event.
	 * 
	 * @param startDate: the new start date of the recurring event
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date of the recurring event.
	 * 
	 * @return the end date of the recurring event
	 */
	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date of the recurring event.
	 * 
	 * @param endDate: the new end date of the recurring event
	 */
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
