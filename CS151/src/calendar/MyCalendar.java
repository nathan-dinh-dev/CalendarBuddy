package calendar;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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
					TimeInterval timeInterval = new TimeInterval(startDate, startTime, endDate, endTime);

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
					TimeInterval timeInterval = new TimeInterval(date, startTime, date, endTime);

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

	public void deleteEvent(Scanner scanner) {
		System.out.println("[S]elected  [A]ll  [Dr]ecurring");
		String option = scanner.nextLine().trim().toUpperCase();

		switch (option) {
		case "S":
			deleteSelectedEvent(scanner);
			break;

		case "A":
			deleteAllEventsOnDate(scanner);
			break;
		case "DR":
			deleteRecurringEvent(scanner);
			break;
		default:
			System.out.println("Invalid option.");
			break;
		}
	}

	// 1. Delete a specific one-time event by date and name
	private void deleteSelectedEvent(Scanner scanner) {
		System.out.println("Enter the date [MM/DD/YYYY]:");
		String dateStr = scanner.nextLine();
		LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("M/d/yyyy"));

		System.out.println("Here are the events scheduled on " + date + ":");
		List<Event> eventsOnDate = getEventsOnDate(date);
		if (eventsOnDate.isEmpty()) {
			System.out.println("No events found on this date.");
			return;
		}

		for (Event event : eventsOnDate) {
			System.out.println(event.getTimeInterval().getStartTime() + " - " + event.getTimeInterval().getEndTime()
					+ " " + event.getName());
		}

		System.out.println("Enter the name of the event to delete:");
		String eventName = scanner.nextLine().trim();

		boolean eventDeleted = false;
		Iterator<Event> iterator = events.iterator();
		while (iterator.hasNext()) {
			Event event = iterator.next();
			if (!event.isRecurring() && event.getName().equalsIgnoreCase(eventName)
					&& event.getTimeInterval().getStartDate().equals(date)) {
				iterator.remove();
				eventDeleted = true;
				System.out.println("Event '" + eventName + "' deleted.");
				break;
			}
		}

		if (!eventDeleted) {
			System.out.println("Event not found.");
		}
	}

	// 2. Delete all one-time events on a specific date
	private void deleteAllEventsOnDate(Scanner scanner) {
		System.out.println("Enter the date [MM/DD/YYYY]:");
		String dateStr = scanner.nextLine();
		LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("M/d/yyyy"));

		boolean eventsDeleted = false;
		Iterator<Event> iterator = events.iterator();
		while (iterator.hasNext()) {
			Event event = iterator.next();
			if (!event.isRecurring() && event.getTimeInterval().getStartDate().equals(date)) {
				iterator.remove();
				eventsDeleted = true;
			}
		}

		if (eventsDeleted) {
			System.out.println("All one-time events on " + date + " deleted.");
		} else {
			System.out.println("No one-time events found on this date.");
		}
	}

	// 3. Delete a recurring event by name
	private void deleteRecurringEvent(Scanner scanner) {
		System.out.println("Enter the name of the recurring event to delete:");
		String eventName = scanner.nextLine().trim();

		boolean eventDeleted = false;
		Iterator<Event> iterator = events.iterator();
		while (iterator.hasNext()) {
			Event event = iterator.next();
			if (event.isRecurring() && event.getName().equalsIgnoreCase(eventName)) {
				iterator.remove();
				eventDeleted = true;
				System.out.println("Recurring event '" + eventName + "' deleted.");
				break;
			}
		}

		if (!eventDeleted) {
			System.out.println("Recurring event not found.");
		}
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

	public void showMonth(LocalDate date) {

		// Get today's date
		LocalDate today = LocalDate.now();

		// Get the first day of the month and the total number of days in the month
		LocalDate firstOfMonth = date.withDayOfMonth(1);
		int monthLength = date.lengthOfMonth();

		// Get the day of the week the month starts on (1 = Monday, 7 = Sunday)
		int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue();

		System.out.println("      " + date.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + date.getYear());
		System.out.println("Su Mo Tu We Th Fr Sa");

		// Adjust startDayOfWeek to work with a Sunday-starting week (Sunday = 0)
		int adjustedStartDayOfWeek = (startDayOfWeek == 7) ? 0 : startDayOfWeek;

		// Print initial spaces for the first week (if the month doesn't start on
		// Sunday)
		for (int i = 0; i < adjustedStartDayOfWeek; i++) {
			System.out.print("   ");
		}

		// Iterate over the days of the month
		for (int day = 1; day <= monthLength; day++) {
			LocalDate currentDate = firstOfMonth.withDayOfMonth(day);

			// Check if there are events on this day and highlight if needed
			if (today.getDayOfMonth() == day && today.getMonth() == date.getMonth() && hasEventsOnDate(currentDate)) {
				System.out.printf("[{%2d}] ", day); // Highlight the day with curly braces
			} else if (today.getDayOfMonth() == day && today.getMonth() == date.getMonth()) {
				System.out.printf("[%2d] ", day);

			} else if (hasEventsOnDate(currentDate)) {
				System.out.printf("{%2d} ", day); // Highlight the day with curly braces
			} else {
				System.out.printf("%2d ", day); // Print the day normally
			}

			// Newline after Saturday (if day + adjustedStartDayOfWeek is divisible by 7)
			if ((day + adjustedStartDayOfWeek) % 7 == 0) {
				System.out.println();
			}
		}
		System.out.println(); // Final newline after the month
	}

	private boolean hasEventsOnDate(LocalDate date) {
		// Check if there are any events scheduled on the given date
		for (Event event : events) {
			if (!event.isRecurring() && event.getTimeInterval().getStartDate().equals(date)) {
				return true; // One-time event matches
			}
			if (event.isRecurring()) {
				// Check if the recurring event occurs on the given date
				if (isEventOnDate(event, date)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isEventOnDate(Event event, LocalDate date) {
		// Check if the given date falls within the recurring event's range
		if (!date.isBefore(event.getStartDate()) && !date.isAfter(event.getEndDate())) {
			// Check if the event occurs on the same day of the week
			DayOfWeek dayOfWeek = date.getDayOfWeek();
			for (DayOfWeek recurringDay : event.getRecurringDays()) {
				if (dayOfWeek.equals(recurringDay)) {
					return true;
				}
			}
		}
		return false;
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
