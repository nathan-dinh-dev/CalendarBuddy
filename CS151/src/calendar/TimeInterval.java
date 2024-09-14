package calendar;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * TimeInterval class to store and get dates and times of events.
 * 
 * Programmed by: Nathan Dinh
 * 
 * Date: 09/13/2024
 */

public class TimeInterval {

	// Private variables
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalTime startTime;
	private LocalTime endTime;

	/**
	 * Default constructor with initial parameters of dates and time
	 *
	 * @param startDate: the start date
	 * @param startTime: the start time
	 * @param endDate:   the end date
	 * @param endTime:   the end time
	 */
	public TimeInterval(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
	}

	/**
	 * Checks if current event's time overlaps with another event's time.
	 * 
	 * First, it checks whether the dates overlap, and then it checks if the times
	 * overlap.
	 *
	 * @param otherEventTimeInterval: the other time interval to check with current
	 *                                time interval
	 * @return true if the time overlap, false otherwise
	 */
	public boolean overlaps(TimeInterval otherEventTimeInterval) {
		// First check if the dates overlap
		if (this.endDate.isBefore(otherEventTimeInterval.startDate)
				|| this.startDate.isAfter(otherEventTimeInterval.endDate)) {
			return false;
		}

		// If dates overlap, check if the times overlap on the overlapping dates
		return !(this.endTime.isBefore(otherEventTimeInterval.startTime)
				|| this.startTime.isAfter(otherEventTimeInterval.endTime));
	}

	/**
	 * Gets the start date of event.
	 *
	 * @return the start date of the event
	 */
	public LocalDate getStartDate() {
		return this.startDate;
	}

	/**
	 * Gets the end date of event.
	 *
	 * @return the end date of the event
	 */
	public LocalDate getEndDate() {
		return this.endDate;
	}

	/**
	 * Gets the start time of event.
	 *
	 * @return the start time of the event
	 */
	public LocalTime getStartTime() {
		return this.startTime;
	}

	/**
	 * Gets the end time of event.
	 *
	 * @return the end time of the event
	 */
	public LocalTime getEndTime() {
		return this.endTime;
	}
}
