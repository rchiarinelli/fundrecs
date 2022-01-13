/**
 * 
 */
package com.fundrecs.assigment.store;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author rchiarinelli
 *
 */
@SpringJUnitConfig
@ContextConfiguration(classes = {TransactionStore.class})
class TransactionStoreTest {
	
	@Autowired
	private TransactionStore store;
	
	private static final String JSON_SAMPLE="["
			+"{"
			+"\"date\":\"11-12-2018\","
			+"\"type\":\"CREDIT\","
			+"\"amount\":\"9898.36\""
			+"},"
			+"{"
			+"\"date\":\"11-12-2019\","
			+"\"type\":\"CREDIT\","
			+"\"amount\":\"98.36\""
			+"}"
			+"]";
	
	
	@BeforeEach
	void setup() {
		try {
			store.deleteFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void test_persistFile() throws IOException {
		JsonElement json = JsonParser.parseString(JSON_SAMPLE);
		store.persist(json);		
	}
	
	@Test
	void test_loadFile() throws IOException {
		JsonElement json = JsonParser.parseString(JSON_SAMPLE);
		store.persist(json);		
		assertEquals(JSON_SAMPLE, store.loadFile());
	}
	
	@Test
	void test_loadJsonFromFile() throws IOException {
		JsonElement json = JsonParser.parseString(JSON_SAMPLE);
		store.persist(json);
		assertEquals(json, store.loadJsonFromFile());
	}
}
