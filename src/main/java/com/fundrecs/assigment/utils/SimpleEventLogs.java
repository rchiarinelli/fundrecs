/**
 * 
 */
package com.fundrecs.assigment.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

/**
 * Logs system events into file system. The log event files per day, meaning that all events registered withing 24 hour are at a single file.
 * 
 * @author rchiarinelli
 *
 */
public class SimpleEventLogs {


	private static final String EVENTS_DIRECTORY = "/events";
	
	
	private static final String EVENTS_FILENAME = "events.log";
	
	private SimpleEventLogs() {
			
	}
	
	/**
	 * Adds the provided message into event file, creating if it does not exists yet.
	 * 
	 * @param message
	 * @throws IOException
	 */
	public static void logEvent(final String message) throws IOException {
		final File eventDirectory = FileUtils.getFile(FileUtils.getUserDirectory().getCanonicalFile().toString() + File.separator + EVENTS_DIRECTORY);
		
		if (!eventDirectory.exists()) {
			FileUtils.forceMkdir(eventDirectory);
		}
		
		final String fileName = eventDirectory.getCanonicalPath() + File.separator + EVENTS_FILENAME ;		
		FileUtils.write(FileUtils.getFile(fileName), message + "\n", Charset.defaultCharset(),true);
	}
	
}
