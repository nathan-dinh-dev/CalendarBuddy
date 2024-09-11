package calendar;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class MyCalendarTester {

	public static void main(String[] args) {

		// Display today on calendar view
		LocalDate today = LocalDate.now();
		displayMonthCalendar(today);

		// Create calendar instance
		MyCalendar calendar = new MyCalendar();

		// Load calendar
		calendar.loadEvents("C:\\Users\\nghia\\git\\CalendarBuddy\\CS151\\src\\calendar\\sample_events.txt");

		// Print out string indicates loading successfully
		System.out.println("Loading is done!");

		// Print out all events
//		calendar.showAllEvents();

	}

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
