/**
 * 
 */
package com.fundrecs.assigment.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;

import org.apache.commons.io.FileUtils;

/**
 * Logs system events into file system. The log event files per day, meaning that all events registered withing 24 hour are at a single file.
 * 
 * @author rchiarinelli
 *
 */
public class SimpleEventLogs {


	private static final String EVENTS_DIRECTORY = "/events";
	
	private SimpleEventLogs() {
		
	}
	
	/**
	 * Adds the provided message into event file, creating if it does not exists yet.
	 * 
	 * @param message
	 * @throws IOException
	 */
	public static void logEvent(final String message) throws IOException {
		final String fileName = EVENTS_DIRECTORY + File.separator + LocalDate.now().toString();		
		FileUtils.write(FileUtils.getFile(fileName), message,Charset.defaultCharset());
	}
	
}
