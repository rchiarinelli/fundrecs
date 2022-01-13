package com.fundrecs.assigment.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 
 * @author rchiarinelli
 *
 */
public class DateUtils {

	public static final String DEFAULT_DATE_PATTERN = "dd-MM-yyyy";
	
	public static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);
	
	private DateUtils() {}
	
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(final LocalDate date) {
		return date.format(DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * 
	 * @param dateString
	 * @return
	 */
	public static LocalDate parseDate(final String dateString) {
		return LocalDate.parse(dateString, DEFAULT_DATE_FORMAT);
	}
	
	
}
