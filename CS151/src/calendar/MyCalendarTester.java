package calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * MyCalendarTester is the main class for interacting with the calendar
 * application. This is user UI to viewing, creating new events, deleting
 * events, and navigating.
 * 
 * Programmed by: Nathan Dinh
 * 
 * Date: 09/13/2024
 */

public class MyCalendarTester {

	/**
	 * Starting point of the application.
	 * 
	 * Displays the current month, loads events, and processes user input through
	 * console.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {

		// Display today on calendar view
		LocalDate today = LocalDate.now();
		displayMonthCalendar(today);

		// Create calendar instance
		MyCalendar calendar = new MyCalendar();

		// Load calendar
		calendar.loadEvents("C:\\Users\\nghia\\git\\CalendarBuddy\\CS151\\src\\calendar\\events.txt");

		// Print out string indicates loading successfully
		System.out.println("\nLoading is done!\n");

		// Print out all events
		// Optional: Uncomment this to show all events
		// calendar.showAllEvents();

		// Boolean value to run the main menu loop
		boolean isRunning = true;
		Scanner scanner = new Scanner(System.in);

		while (isRunning) {
			showMainMenu();
			String input = scanner.nextLine().trim().toUpperCase();

			switch (input) {
			case "V":
				viewByOption(calendar, scanner);
				break;
			case "C":
				createEvent(calendar, scanner);
				break;
			case "G":
				goToOption(calendar, scanner);
				break;
			case "E":
				calendar.showAllEvents(); // Displays the event list
				break;
			case "D":
				deleteEvent(calendar, scanner);
				break;
			case "Q":
				calendar.saveEventsToFile("C:\\Users\\nghia\\git\\CalendarBuddy\\CS151\\src\\calendar\\output.txt");
				System.out.println("\nGood Bye!");
				isRunning = false;
				break;
			default:
				System.out.println("Invalid option. Please choose again.");
				break;
			}
		}

		scanner.close();

	}

	/**
	 * Displays the main menu options for the user to choose from.
	 */
	private static void showMainMenu() {
		System.out.println("Select one of the following main menu options:");
		System.out.println("[V]iew by  [C]reate  [G]o to  [E]vent list  [D]elete  [Q]uit");
	}

	/**
	 * View By option, allowing the user to select a day or month view.
	 * 
	 * @param calendar: MyCalendar instance
	 * @param scanner:  Scanner for user input
	 */
	private static void viewByOption(MyCalendar calendar, Scanner scanner) {
		System.out.println("[D]ay view or [M]onth view ?");
		String input = scanner.nextLine().trim().toUpperCase();

		if (input.equals("D")) {
			// Show today's events in Day view
			LocalDate today = LocalDate.now();
			showDayView(calendar, today, scanner);
		} else if (input.equals("M")) {
			// Show current month's calendar in Month view
			LocalDate today = LocalDate.now();
			showMonthView(calendar, today, scanner);
		} else {
			System.out.println("Invalid option. Returning to main menu.");
		}
	}

	/**
	 * Day view for a specified date, showing all events scheduled for that day.
	 * 
	 * @param calendar: MyCalendar instance
	 * @param datet:    date to display events
	 * @param scanner:  Scanner for user input
	 */
	private static void showDayView(MyCalendar calendar, LocalDate date, Scanner scanner) {
		System.out.println("Day View for " + date);
		List<Event> events = calendar.getEventsOnDate(date);
		if (events.isEmpty()) {
			System.out.println("No events scheduled for " + date);
		} else {
			for (Event event : events) {
				System.out.println(event.getName() + ": " + event.getTimeInterval().getStartTime() + " - "
						+ event.getTimeInterval().getEndTime());
			}
		}
		handleDayNavigation(calendar, date, scanner);
	}

	/**
	 * Month view for a specified date, highlighting days with events.
	 * 
	 * @param calendar: MyCalendar instance
	 * @param date:     date of the month to display
	 * @param scanner:  Scanner for user input
	 */
	private static void showMonthView(MyCalendar calendar, LocalDate date, Scanner scanner) {
		System.out.println("Month View for " + date.getMonth() + " " + date.getYear());
		calendar.showMonth(date);

		handleMonthNavigation(calendar, date, scanner);
	}

	/**
	 * Handles navigation in the Month view, allowing the user to move to the
	 * previous or next month.
	 * 
	 * @param calendar: MyCalendar instance
	 * @param date:     date currently displayed in the month view
	 * @param scanner:  Scanner for user input
	 */
	private static void handleMonthNavigation(MyCalendar calendar, LocalDate date, Scanner scanner) {
		System.out.println("[P]revious or [N]ext or [G]o back to main menu ?");
		String input = scanner.nextLine().trim().toUpperCase();

		if (input.equals("P")) {
			showMonthView(calendar, date.minusMonths(1), scanner); // Show previous month
		} else if (input.equals("N")) {
			showMonthView(calendar, date.plusMonths(1), scanner); // Show next month
		} else if (input.equals("G")) {
			System.out.println("Returning to main menu.");
		} else {
			System.out.println("Invalid option. Returning to main menu.");
		}
	}

	/**
	 * Navigation in the Day view, allowing the user to move to the previous or next
	 * day.
	 * 
	 * @param calendar: MyCalendar instance
	 * @param date:     date currently displayed in the day view
	 * @param scanner:  Scanner for user input
	 */
	private static void handleDayNavigation(MyCalendar calendar, LocalDate date, Scanner scanner) {
		System.out.println("[P]revious or [N]ext or [G]o back to the main menu ?");
		String input = scanner.nextLine().trim().toUpperCase();

		if (input.equals("P")) {
			showDayView(calendar, date.minusDays(1), scanner); // Show previous day
		} else if (input.equals("N")) {
			showDayView(calendar, date.plusDays(1), scanner); // Show next day
		} else if (input.equals("G")) {
			System.out.println("Returning to main menu.");
		} else {
			System.out.println("Invalid option. Returning to main menu.");
		}
	}

	/**
	 * Prompts the user to create a new event.
	 * 
	 * @param calendar: MyCalendar instance
	 * @param scanner:  Scanner for user input
	 */
	private static void createEvent(MyCalendar calendar, Scanner scanner) {
		try {
			System.out.println("Enter event name: ");
			String name = scanner.nextLine();

			System.out.println("Enter event date (MM/DD/YYYY): ");
			String dateStr = scanner.nextLine();
			LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("M/d/yyyy"));

			System.out.println("Enter start time (HH:mm, 24-hour format): ");
			String startTimeStr = scanner.nextLine();
			LocalTime startTime = LocalTime.parse(startTimeStr);

			System.out.println("Enter end time (HH:mm, 24-hour format): ");
			String endTimeStr = scanner.nextLine();
			LocalTime endTime = LocalTime.parse(endTimeStr);

			TimeInterval timeInterval = new TimeInterval(date, startTime, date, endTime);

			// Check for conflicts with existing events on the same date
			boolean isConflicted = false;
			List<Event> eventsOnDate = calendar.getEventsOnDate(date);
			for (Event existingEvent : eventsOnDate) {
				if (existingEvent.getTimeInterval().overlaps(timeInterval)) {
					isConflicted = true;
					System.out.println("Time conflict detected with event: " + existingEvent.getName() + " ("
							+ existingEvent.getTimeInterval().getStartTime() + " - "
							+ existingEvent.getTimeInterval().getEndTime() + ")");
					break;
				}
			}

			// If no conflicts, create and add the event
			if (!isConflicted) {
				Event newEvent = new Event(name, timeInterval);
				calendar.addEvent(newEvent);
				System.out.println("Event created successfully.");
			} else {
				System.out.println("Please enter a different time that doesn't conflict.");
			}
		} catch (Exception e) {
			System.out.println("Invalid input. Please enter the correct date and time format.");
		}
	}

	/**
	 * Option to go to a specific date and view events on that date.
	 * 
	 * @param calendar: MyCalendar instance
	 * @param scanner:  Scanner for user input
	 */
	private static void goToOption(MyCalendar calendar, Scanner scanner) {
		System.out.println("Enter the date (MM/DD/YYYY): ");
		String dateStr = scanner.nextLine();
		LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("M/d/yyyy"));

		// Show events on the specified date
		showDayView(calendar, date, scanner);
	}

	/**
	 * Prompting the user to select the event deletion type.
	 * 
	 * @param calendar: MyCalendar instance
	 * @param scanner:  Scanner for user input
	 */
	private static void deleteEvent(MyCalendar calendar, Scanner scanner) {
		calendar.deleteEvent(scanner);
	}

	/**
	 * Displays the current month calendar, highlighting today's date with brackets.
	 * This is the extra method (I programmed it here in case need it in the future)
	 * 
	 * @param today: the today's date
	 */
	public static void displayMonthCalendar(LocalDate today) {
		LocalDate date = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
		System.out.println(date.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + date.getYear());
		System.out.println("Su Mo Tu We Th Fr Sa");

		int startDay = date.getDayOfWeek().getValue();
		int daysInMonth = date.lengthOfMonth();

		// Adjust start day index to match Java's start (1 = Monday) to our needs (0 =
		// Sunday)
		startDay = (startDay == 7) ? 0 : startDay;

		for (int i = 0; i < startDay; i++) {
			System.out.print("   ");
		}

		for (int day = 1; day <= daysInMonth; day++) {
			String dayString = String.format("%2d", day);
			if (day == today.getDayOfMonth()) {
				dayString = "[" + dayString + "]";
			}
			System.out.print(dayString + " ");

			if ((day + startDay) % 7 == 0) {
				System.out.println();
			}
		}
		System.out.println();
	}
}
