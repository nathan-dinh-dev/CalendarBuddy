package calendar;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyCalendar {
	private List<Event> events;

	public MyCalendar() {
		this.events = new ArrayList<>();
	}

	public void loadEvents(String filename) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(filename));
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

			for (int i = 0; i < lines.size(); i += 2) {
				String eventName = lines.get(i).trim();
				String eventInfo = lines.get(i + 1).trim();

				if (isRecurringEvent(eventInfo)) {
					// Parse recurring event
					String[] details = eventInfo.split(" ");
					String days = details[0];
					String startTimeStr = details[1];
					String endTimeStr = details[2];
					String startDateStr = details[3];
					String endDateStr = details[4];

					// Parse the time and date
					LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
					LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);

					LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("M/d/yy"));
					LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("M/d/yy"));

					// Create TimeInterval
					TimeInterval timeInterval = new TimeInterval(startTime, endTime);

					// Parse days (e.g., "TR" -> [TUESDAY, THURSDAY])
					DayOfWeek[] recurringDays = parseDays(days);

					// Create recurring event
					Event newEvent = new Event(eventName, timeInterval, recurringDays, startDate, endDate);
					addEvent(newEvent);
				} else {
					// Parse one-time event
					String[] details = eventInfo.split(" ");
					String dateStr = details[0];
					String startTimeStr = details[1];
					String endTimeStr = details[2];

					// Parse the time and date
					LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
					LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);
					LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("M/d/yy"));

					// Create TimeInterval
					TimeInterval timeInterval = new TimeInterval(startTime, endTime);

					// Create one-time event
					Event newEvent = new Event(eventName, timeInterval);
					addEvent(newEvent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DayOfWeek[] parseDays(String days) {
		return days.chars().mapToObj(c -> {
			switch (c) {
			case 'M':
				return DayOfWeek.MONDAY;
			case 'T':
				return DayOfWeek.TUESDAY;
			case 'W':
				return DayOfWeek.WEDNESDAY;
			case 'R':
				return DayOfWeek.THURSDAY;
			case 'F':
				return DayOfWeek.FRIDAY;
			case 'S':
				return DayOfWeek.SATURDAY;
			case 'U':
				return DayOfWeek.SUNDAY;
			default:
				throw new IllegalArgumentException("Invalid day character: " + (char) c);
			}
		}).toArray(DayOfWeek[]::new);
	}

	private boolean isRecurringEvent(String details) {
		// Check if the second line starts with day abbreviations (SMTWRFA)
		return details.matches("^[SMTWRFA]+.*");
	}

	public void saveEvents(String filename) {

	}

	public void addEvent(Event event) {
		events.add(event);
	}

	public void deleteEvent(String name, LocalDate date) {

	}

	public List<Event> getEventsOnDate(LocalDate date) {
		List<Event> eventsOnDate = new ArrayList<>();

		for (Event event : events) {
			// Check for one-time events
			if (!event.isRecurring() && event.getTimeInterval().getStartDate().equals(date)) {
				eventsOnDate.add(event);
			}

			// Check for recurring events
			if (event.isRecurring()) {
				LocalDate startDate = event.getStartDate();
				LocalDate endDate = event.getEndDate();
				if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
					DayOfWeek dayOfWeek = date.getDayOfWeek();
					for (DayOfWeek recurringDay : event.getRecurringDays()) {
						if (dayOfWeek.equals(recurringDay)) {
							eventsOnDate.add(event);
							break; // Event occurs on this day
						}
					}
				}
			}
		}

		return eventsOnDate;
	}

	public void showAllEvents() {
		System.out.println("ALL EVENTS:");

		System.out.println("\nONE-TIME EVENTS:");
		for (Event event : events) {
			if (!event.isRecurring()) {
				// One-time event
				LocalDate eventDate = event.getTimeInterval().getStartDate();
				LocalTime startTime = event.getTimeInterval().getStartTime();
				LocalTime endTime = event.getTimeInterval().getEndTime();

				System.out.println(eventDate + " " + startTime + " - " + endTime + " " + event.getName());
			}
		}

		System.out.println("\nRECURRING EVENTS:");
		for (Event event : events) {
			if (event.isRecurring()) {
				// Recurring event
				DayOfWeek[] recurringDays = event.getRecurringDays();
				LocalDate startDate = event.getStartDate();
				LocalDate endDate = event.getEndDate();
				LocalTime startTime = event.getTimeInterval().getStartTime();
				LocalTime endTime = event.getTimeInterval().getEndTime();

				// Print recurring days as a string (e.g., "MWF" or "TR")
				String days = "";
				for (DayOfWeek day : recurringDays) {
					days += day.getDisplayName(TextStyle.SHORT, Locale.US).charAt(0);
				}

				System.out.println(event.getName() + ": " + days + " " + startTime + " - " + endTime + " (" + startDate
						+ " to " + endDate + ")");
			}
		}
	}
}
