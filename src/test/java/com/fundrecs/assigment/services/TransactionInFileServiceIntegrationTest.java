package com.fundrecs.assigment.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.fundrecs.assigment.domain.Transaction;
import com.fundrecs.assigment.domain.TransactionType;
import com.fundrecs.assigment.store.TransactionStore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@SpringJUnitConfig
@ContextConfiguration(classes = {TransactionInFileService.class, TransactionStore.class})
class TransactionInFileServiceIntegrationTest {
	
	@Autowired
	private TransactionService transactionService;
	
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
			+"},"
			+"{"
			+"\"date\":\"11-12-2011\","
			+"\"type\":\"DEBIT\","
			+"\"amount\":\"50\""
			+"}"

			+"]";
	
	@BeforeEach
	void setup() throws IOException {
		JsonElement json = JsonParser.parseString(JSON_SAMPLE);
		store.persist(json);		
	}
	
	@Test 
	void test_register_allNewTransactions() throws JsonSyntaxException, IOException {
		final String expectedResult = "["
				+ "{\"date\":\"11-12-2011\",\"type\":\"DEBIT\",\"amount\":\"50\"},"
				+ "{\"date\":\"11-12-2018\",\"type\":\"CREDIT\",\"amount\":\"9898.36\"},"
				+ "{\"date\":\"11-12-2019\",\"type\":\"CREDIT\",\"amount\":\"98.36\"},"
				+ "{\"amount\":\"10.0\",\"type\":\"CREDIT\",\"date\":\"13-01-2022\"},"
				+ "{\"amount\":\"12.0\",\"type\":\"CREDIT\",\"date\":\"01-01-2021\"}]";
		
		transactionService.register(Arrays.asList(Transaction.builder().amount(10).date(LocalDate.now()).type(TransactionType.CREDIT).build()
				,Transaction.builder().amount(12).date(LocalDate.parse("01-01-2021",DateTimeFormatter.ofPattern("dd-MM-yyyy"))).type(TransactionType.CREDIT).build()));
		
		final Gson gson = new Gson();
		assertEquals(expectedResult,gson.toJson(store.loadJsonFromFile()));
	}

	@Test
	void test_register_oneNewAndOneExisting() throws JsonSyntaxException, IOException {

		final String expectedResult = "["
				+ "{\"date\":\"11-12-2011\",\"type\":\"DEBIT\",\"amount\":\"50\"},"
				+ "{\"date\":\"11-12-2018\",\"type\":\"CREDIT\",\"amount\":\"9898.36\"},{\"date\":\"11-12-2019\",\"type\":\"CREDIT\",\"amount\":\"198.36\"},{\"amount\":\"10.0\",\"type\":\"CREDIT\",\"date\":\"13-01-2022\"}]";

		transactionService.register(Arrays.asList(
				Transaction.builder().amount(10).date(LocalDate.now()).type(TransactionType.CREDIT).build(),
				Transaction.builder().amount(100)
						.date(LocalDate.parse("11-12-2019", DateTimeFormatter.ofPattern("dd-MM-yyyy")))
						.type(TransactionType.CREDIT).build()));
		
		final Gson gson = new Gson();
		assertEquals(expectedResult, gson.toJson(store.loadJsonFromFile()));
	}
	
	
	@Test
	void test_register_oneNewAndTwoExisting() throws JsonSyntaxException, IOException {

		final String expectedResult = "["
				+ "{\"date\":\"11-12-2011\",\"type\":\"DEBIT\",\"amount\":\"50\"},"
				+ "{\"date\":\"11-12-2018\",\"type\":\"CREDIT\",\"amount\":\"19898.36\"},"
				+ "{\"date\":\"11-12-2019\",\"type\":\"CREDIT\",\"amount\":\"198.36\"},"
				+ "{\"amount\":\"10.0\",\"type\":\"CREDIT\",\"date\":\"13-01-2022\"}]";
		
		transactionService.register(Arrays.asList(
				Transaction.builder().amount(10).date(LocalDate.now()).type(TransactionType.CREDIT).build(),
				
				Transaction.builder().amount(10000)
						.date(LocalDate.parse("11-12-2018", DateTimeFormatter.ofPattern("dd-MM-yyyy")))
						.type(TransactionType.CREDIT).build(),
				Transaction.builder().amount(100)
						.date(LocalDate.parse("11-12-2019", DateTimeFormatter.ofPattern("dd-MM-yyyy")))
						.type(TransactionType.CREDIT).build()));
		
		final Gson gson = new Gson();
		assertEquals(expectedResult, gson.toJson(store.loadJsonFromFile()));
	}
	
	@Test 
	void test_getTransaction_transactionExists() throws JsonSyntaxException, IOException {

		final Transaction expectedResult = Transaction.builder().amount(98.36)
				.date(LocalDate.parse("11-12-2019", DateTimeFormatter.ofPattern("dd-MM-yyyy")))
				.type(TransactionType.CREDIT).build();
		
		Optional<Transaction> actual = transactionService.getTransaction(LocalDate.parse("11-12-2019", DateTimeFormatter.ofPattern("dd-MM-yyyy")), TransactionType.CREDIT);

		assertEquals(expectedResult,actual.get());
	}
}
