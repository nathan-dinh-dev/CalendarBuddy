package calendar;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeInterval {
	private LocalTime startTime;
	private LocalTime endTime;

	// default constructors
	public TimeInterval(LocalTime startTime, LocalTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public boolean overlaps(TimeInterval otherEventTimeInterval) {
		return !(this.endTime.isBefore(otherEventTimeInterval.startTime)
				|| this.startTime.isAfter(otherEventTimeInterval.endTime));
	}

	public LocalDate getStartDate() {
		// TODO Auto-generated method stub
		return LocalDate.parse("2024-12-12");
	}

	public LocalTime getStartTime() {
		return this.startTime;
	}

	public LocalTime getEndTime() {
		return this.endTime;
	}
}
