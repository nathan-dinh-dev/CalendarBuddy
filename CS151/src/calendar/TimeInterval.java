package calendar;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeInterval {
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalTime startTime;
	private LocalTime endTime;

	// Default constructors
	public TimeInterval(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
	}

	// Method to check if two time intervals overlap
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

	// Getters for start and end dates and times
	public LocalDate getStartDate() {
		return this.startDate;
	}

	public LocalDate getEndDate() {
		return this.endDate;
	}

	public LocalTime getStartTime() {
		return this.startTime;
	}

	public LocalTime getEndTime() {
		return this.endTime;
	}
}
