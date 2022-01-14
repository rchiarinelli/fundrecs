/**
 * 
 */
package com.fundrecs.assigment.store;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.LinkOption;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


/**
 * Stores the transaction data.
 * 
 * @author rchiarinelli
 *
 */
@Service
@Scope("singleton")
public class TransactionStore {

	protected static final String TRANSACTIONS_DIRECTORY = File.separator + "transactions";

	protected static final String TRANSACTIONS_FILE = TRANSACTIONS_DIRECTORY + File.separator + "store";

	protected static final String TRANSACTIONS_CACHE = "transactions";
	
	/**
	 * 
	 * @throws IOException
	 */
	void deleteFile() throws IOException {
		final File storageFile = FileUtils.getFile(
				FileUtils.getUserDirectory().getCanonicalFile().toString() + File.separator + TRANSACTIONS_FILE); 
		if (storageFile.exists()) {
			FileUtils.delete(storageFile);
			
		}
	}

	/**
	 * 
	 * @param json
	 * @throws IOException
	 */
	public void persist(final JsonElement json) throws IOException {
		deleteFile();
		final Gson gson = new Gson();
		if (!FileUtils.isDirectory(FileUtils.getFile(
				FileUtils.getUserDirectory().getCanonicalFile().toString() + File.separator + TRANSACTIONS_DIRECTORY),
				LinkOption.NOFOLLOW_LINKS)) {
			FileUtils.forceMkdir(FileUtils.getFile(FileUtils.getUserDirectory().getCanonicalFile().toString()
					+ File.separator + TRANSACTIONS_DIRECTORY));
		}
		FileUtils.write(FileUtils.getFile(
				FileUtils.getUserDirectory().getCanonicalFile().toString() + File.separator + TRANSACTIONS_FILE),
				gson.toJson(json), Charset.defaultCharset());
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	String loadFile() throws IOException {
		final File storageFile = FileUtils.getFile(
				FileUtils.getUserDirectory().getCanonicalFile().toString() + File.separator + TRANSACTIONS_FILE); 
		return storageFile.exists() ? FileUtils.readFileToString(FileUtils.getFile(
				FileUtils.getUserDirectory().getCanonicalFile().toString() + File.separator + TRANSACTIONS_FILE),
				Charset.defaultCharset()) : "[]";
	}
	
	/**
	 * 
	 * @return
	 * @throws JsonSyntaxException
	 * @throws IOException
	 */
	public JsonElement loadJsonFromFile() throws JsonSyntaxException, IOException {
		return JsonParser.parseString(loadFile());
	}
	
	

}
