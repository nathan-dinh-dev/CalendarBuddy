package calendar;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

/**
 * MyCalendar class load, save, store, display, and manage events. It supports
 * one-time and recurring events, allowing users to add, delete, view, and save
 * events.
 * 
 * Programmed by: Nathan Dinh
 * 
 * Date: 09/13/2024
 */

public class MyCalendar {

	// Declare events list variable
	private List<Event> events;

	/**
	 * Default constructor with empty Array List
	 */
	public MyCalendar() {
		this.events = new ArrayList<>();
	}

	/**
	 * Loads events from a file and save data to events array.
	 * 
	 * @param filename: the file path type String
	 */
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

	/**
	 * Parses a string of characters representing days of the week and converts them
	 * into DayOfWeek.
	 * 
	 * @param days: String showing the days (example: "MW" for Monday and Wednesday)
	 * @return an array of DayOfWeek corresponding to the days
	 */
	private DayOfWeek[] parseDays(String days) {
		DayOfWeek[] dayArray = new DayOfWeek[days.length()];

		for (int i = 0; i < days.length(); i++) {
			char c = days.charAt(i);
			switch (c) {
			case 'M':
				dayArray[i] = DayOfWeek.MONDAY;
				break;
			case 'T':
				dayArray[i] = DayOfWeek.TUESDAY;
				break;
			case 'W':
				dayArray[i] = DayOfWeek.WEDNESDAY;
				break;
			case 'R':
				dayArray[i] = DayOfWeek.THURSDAY;
				break;
			case 'F':
				dayArray[i] = DayOfWeek.FRIDAY;
				break;
			case 'S':
				dayArray[i] = DayOfWeek.SATURDAY;
				break;
			case 'U':
				dayArray[i] = DayOfWeek.SUNDAY;
				break;
			default:
				throw new IllegalArgumentException("Invalid day character: " + c);
			}
		}

		return dayArray;
	}

	/**
	 * Checks if the event is recurring based on the presence of day abbreviations
	 * (S, M, T, W, R, F, A) at the beginning of the event details.
	 * 
	 * @param details: the event details string
	 * @return true if the event is recurring, false otherwise
	 */
	private boolean isRecurringEvent(String details) {
		// Check if the first character(s) are valid day abbreviations
		if (details == null || details.isEmpty()) {
			return false;
		}

		char firstLetter = details.charAt(0);
		return (firstLetter == 'S' || firstLetter == 'M' || firstLetter == 'T' || firstLetter == 'W'
				|| firstLetter == 'R' || firstLetter == 'F' || firstLetter == 'A');
	}

	/**
	 * Adds a new event to the calendar.
	 * 
	 * @param event: the event to be added
	 */
	public void addEvent(Event event) {
		events.add(event);
	}

	/**
	 * Deletes an event from the calendar based on user input.
	 * 
	 * @param scanner: Scanner object to receive user input
	 */
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

	/**
	 * Deletes a specific one-time event based on date and name.
	 * 
	 * @param scanner: Scanner object to receive user input
	 */
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

	/**
	 * Deletes all one-time events on a specific date.
	 * 
	 * @param scanner: Scanner object to receive user input
	 */
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

	/**
	 * Deletes a recurring event based on its name.
	 * 
	 * @param scanner: Scanner object to receive user input
	 */
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

	/**
	 * Get all events on a specific date and return a list of all events that occur
	 * on the specified date.
	 *
	 * @param date: date for which events should be retrieved
	 * @return a list of events that occur on the specified date
	 */
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

	/**
	 * Displays the month view for the given date, showing all days of the month and
	 * highlighting the current day and any days that have events.
	 *
	 * @param date: date representing the month to display
	 */
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

	/**
	 * Checks if there are any events scheduled on the given date.
	 * 
	 * @param date: date to check for events
	 * @return true if there are events scheduled on the date, false otherwise
	 */
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

	/**
	 * Checks if the given recurring event occurs on the specified date.
	 * 
	 * @param event: recurring event to check
	 * @param date:  date to check for the event
	 * @return true if the event occurs on the date, false otherwise
	 */
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

	/**
	 * Displays all events in the calendar, both one-time and recurring. One-time
	 * events are displayed first, followed by recurring events.
	 */
	public void showAllEvents() {
		System.out.println("\nALL EVENTS:");

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
		System.out.println();
	}

	/**
	 * Saves all events to output file.
	 * 
	 * @param filename: the file path to save the events to
	 */
	public void saveEventsToFile(String filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (Event event : events) {
				writer.write(formatEventForFile(event));
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("An error occurred while saving events to file.");
			e.printStackTrace();
		}
	}

	/**
	 * Formats an event for writing to a file. Recurring events are formatted with
	 * day abbreviations, while one-time events are written with their date and
	 * time.
	 * 
	 * @param event: event to format
	 * @return the String of formatted event
	 */
	private String formatEventForFile(Event event) {
		StringBuilder sb = new StringBuilder();

		sb.append(event.getName()).append("\n");

		if (event.isRecurring()) {
			// Format recurring event
			StringBuilder recurringDaysString = new StringBuilder();
			for (DayOfWeek day : event.getRecurringDays()) {
				recurringDaysString.append(day.getDisplayName(TextStyle.NARROW, Locale.US));
			}
			sb.append(recurringDaysString.toString()).append(" ").append(event.getTimeInterval().getStartTime())
					.append(" ").append(event.getTimeInterval().getEndTime()).append(" ")
					.append(event.getTimeInterval().getStartDate()).append(" ")
					.append(event.getTimeInterval().getEndDate());
		} else {
			// Format one-time event
			sb.append(event.getTimeInterval().getStartDate()).append(" ").append(event.getTimeInterval().getStartTime())
					.append(" ").append(event.getTimeInterval().getEndTime());
		}

		return sb.toString();
	}
}
