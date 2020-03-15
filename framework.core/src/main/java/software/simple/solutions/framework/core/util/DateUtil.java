package software.simple.solutions.framework.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import software.simple.solutions.framework.core.constants.Constants;

public class DateUtil {

	private static final Logger logger = LogManager.getLogger(DateUtil.class);

	public static Date calculateEndTime(BigDecimal value, Date startTime) {
		BigDecimal[] decimals = value.divideAndRemainder(new BigDecimal(1));
		if ((decimals != null) && (decimals.length == 2) && (startTime != null)) {
			Calendar start = Calendar.getInstance();
			start.setTime(startTime);
			start.add(10, decimals[0].intValue());
			int minutes = decimals[1].multiply(new BigDecimal(60)).intValue();
			start.add(12, minutes);
			return start.getTime();
		}
		return null;
	}

	public static BigDecimal getTimeIndecimalFormat(String startHours, String startMinutes, String endHours,
			String endMinutes) {
		BigDecimal start = new BigDecimal(startHours);
		BigDecimal end = new BigDecimal(endHours);
		BigDecimal decimalStartMinutes = new BigDecimal(startMinutes).divide(new BigDecimal(60), 2,
				RoundingMode.HALF_UP);

		BigDecimal decimalEndMinutes = new BigDecimal(endMinutes).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);

		BigDecimal decimalHours = null;
		if (start.compareTo(end) > 0) {
			decimalHours = new BigDecimal(endHours).add(new BigDecimal(24)).add(decimalEndMinutes)
					.subtract(new BigDecimal(startHours).add(decimalStartMinutes));
		} else if ((start.compareTo(end) == 0) && (decimalStartMinutes.compareTo(decimalEndMinutes) > 0)) {
			decimalHours = new BigDecimal(endHours).add(new BigDecimal(24)).add(decimalEndMinutes)
					.subtract(new BigDecimal(startHours).add(decimalStartMinutes));
		} else {
			decimalHours = new BigDecimal(endHours).add(decimalEndMinutes)
					.subtract(new BigDecimal(startHours).add(decimalStartMinutes));
		}
		return decimalHours;
	}

	public static boolean checkStartDateAfterEndDate(Date startDate, Date endDate) {
		boolean isAfter = true;
		if ((startDate != null) && (endDate != null) && (endDate.compareTo(startDate) < 0)) {
			isAfter = false;
		}
		return isAfter;
	}

	public static enum DateOperator {
		ADD, SUBTRACT, SET;

		private DateOperator() {
		}
	}

	public static Date getDateValue(String value) {
		return getDateValue(value, null, null);
	}

	public static Date getDateValue(String value, Date date, DateOperator dateOperator) {
		if ((value != null) && (value.matches("[0-9]+:+([0-9]+)"))) {
			String[] split = value.split(":");
			Calendar calendar = Calendar.getInstance();
			if (date != null) {
				calendar.setTime(date);
			}
			if (dateOperator == null) {
				dateOperator = DateOperator.SET;
			}
			switch (dateOperator) {
			case ADD:
				calendar.add(11, Integer.parseInt(split[0]));
				calendar.add(12, Integer.parseInt(split[1]));
				break;
			case SUBTRACT:
				calendar.add(11, Integer.parseInt(split[0]) * -1);

				calendar.add(12, Integer.parseInt(split[1]) * -1);
				break;
			case SET:
				calendar.set(11, Integer.parseInt(split[0]));
				calendar.set(12, Integer.parseInt(split[1]));
				break;
			}
			return calendar.getTime();
		}
		return null;
	}

	public static String dateToTimeStringValue(Date date) {
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int hour = calendar.get(11);
			int minute = calendar.get(12);
			return Constants.TIME_FORMAT.format(hour) + ":" + Constants.TIME_FORMAT.format(minute);
		}
		return null;
	}

	/**
	 * Adds time to the date value. The date defaulted to 00:00:00 (HH:mm:ss).
	 * The timeValue is of the format HH:mm.
	 * 
	 * @param date
	 *            Date to be used
	 * @param timeValue
	 *            Time to be added
	 * @param addDay
	 *            Should the date be tomorrow.
	 * @return
	 */
	public static Date addTimeToDate(Date date, String timeValue, boolean addDay) {
		SimpleDateFormat SIMPLE_DATE_TIME_FORMAT_24H = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (date != null && timeValue != null && !timeValue.isEmpty()) {
			if (addDay) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				date = calendar.getTime();
			}
			String format = Constants.SIMPLE_DATE_FORMAT.format(date);
			format = format + " " + timeValue;
			Date parsedDate;
			try {
				parsedDate = SIMPLE_DATE_TIME_FORMAT_24H.parse(format);
				return parsedDate;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean isNextDay(String start, String end) {
		if (start != null & end != null) {
			String st = start.replaceAll(":", "");
			String en = end.replaceAll(":", "");
			if (NumberUtil.getLong(st).compareTo(NumberUtil.getLong(en)) < 0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public static String dateToString(Date date, SimpleDateFormat formatter) {
		return formatter.format(date);
	}
	
	public static String dateToString(LocalDateTime date, SimpleDateFormat formatter) {
		return formatter.format(date);
	}
	
	public static String dateToString(LocalTime date, SimpleDateFormat formatter) {
		DateTimeFormatter format = DateTimeFormatter.ISO_TIME; 
		return format.format(date);
	}

	public static Date truncDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Long getAge(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		Long between = ChronoUnit.YEARS.between(dateTime, LocalDateTime.now());
		return between;
	}

	public static Long getAge(LocalDate dateTime) {
		if (dateTime == null) {
			return null;
		}
		Long between = ChronoUnit.YEARS.between(dateTime, LocalDateTime.now());
		return between;
	}

}
