/**
 * 
 */
package com.fundrecs.assigment.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fundrecs.assigment.domain.Transaction;
import com.fundrecs.assigment.domain.TransactionType;
import com.fundrecs.assigment.store.TransactionStore;
import com.fundrecs.assigment.utils.DateUtils;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import lombok.extern.log4j.Log4j2;

/**
 * {@link TransactionService} implementation to save data into filesystem.
 * 
 * @author rchiarinelli
 *
 */
@Service
@Log4j2
public class TransactionInFileService implements TransactionService {

	
	@Autowired
	private TransactionStore store;
	
	@Override
	public Optional<List<Transaction>> register(final List<Transaction> transactions) {
		try {
			
			List<JsonElement> transactionsStore = Lists.newArrayList(store.loadJsonFromFile().getAsJsonArray().iterator());
			
			Collections.sort(transactionsStore, new Comparator<JsonElement>() {
				@Override
				public int compare(JsonElement jsonElement, JsonElement otherJsonElement) {
					final LocalDate dateJsonElement = LocalDate.parse(jsonElement.getAsJsonObject().get("date").getAsString(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
					final LocalDate dateOtherJsonElement = LocalDate.parse(otherJsonElement.getAsJsonObject().get("date").getAsString(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
					return dateJsonElement.compareTo(dateOtherJsonElement);
				}
			});
			
			List<Transaction> repeatedItensFromInput = new ArrayList<>();
			Set<JsonElement> existingFromStore = new HashSet<>();
			List<Transaction> newTransactions = new ArrayList<>();
			
			for (final Transaction transaction : transactions) {
				//check if jsonStore has this transaction
				final Pair<Transaction, JsonElement> existingItems = find(transaction, transactionsStore);
				if (existingItems == null) {
					newTransactions.add(transaction);
				} else {
					final JsonElement element = existingItems.getRight();
					repeatedItensFromInput.add(transaction);
					existingFromStore.add(element);
				}
			}

			for (final Transaction repeated : repeatedItensFromInput) {
				final JsonElement existingElement = existingFromStore.stream()
						.filter(jsonElement -> jsonElement.getAsJsonObject().get("date").getAsString()
								.equals(repeated.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
								&& jsonElement.getAsJsonObject().get("type").getAsString()
										.equals(repeated.getType().name()))
						.findFirst().get();
				
				double elementAmount = Double.valueOf(existingElement.getAsJsonObject().get("amount").getAsString()) + repeated.getAmount();
				existingElement.getAsJsonObject().remove("amount");
				existingElement.getAsJsonObject().addProperty("amount", Double.toString(elementAmount));
			}
			
			for (final Transaction newTransaction : newTransactions) {
				final JsonObject transactionJson = new JsonObject();
				transactionJson.addProperty("amount", Double.toString(newTransaction.getAmount()));
				transactionJson.addProperty("type", newTransaction.getType().name());
				transactionJson.addProperty("date", newTransaction.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
				transactionsStore.add(transactionJson);
			}
			
			store.persist(new GsonBuilder().create().toJsonTree(transactionsStore));
		} catch (JsonSyntaxException | IOException e) {
			log.error(e);
			return Optional.empty();
		}
		return Optional.of(transactions);
	}
	
	@Override
	public Optional<Transaction> getTransaction(final LocalDate date, TransactionType type) {
		Transaction transaction = null;
		try {
			final JsonArray transactionsStore = store.loadJsonFromFile().getAsJsonArray();
			
			final JsonElement jsonElement = Lists.newArrayList(transactionsStore.iterator()).stream().collect(Collectors.toList()).stream().filter(txJson -> 
				txJson.getAsJsonObject().get("date").getAsString().equals(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
				&& txJson.getAsJsonObject().get("type").getAsString().equals(type.name())).findFirst().orElse(null);	
			
			if (jsonElement != null) {
				final double amount = Double.valueOf(jsonElement.getAsJsonObject().get("amount").getAsString());
				final LocalDate txDate =  LocalDate.parse(jsonElement.getAsJsonObject().get("date").getAsString(),DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				final TransactionType txType = TransactionType.valueOf(jsonElement.getAsJsonObject().get("type").getAsString());
				
				transaction = Transaction.builder().amount(amount).date(txDate).type(txType).build();
			}
			
		} catch (JsonSyntaxException | IOException e) {
			log.error(e);
		}		
		return Optional.ofNullable(transaction);
	}
	
	@Override
	public Optional<List<Transaction>> getTransactionByDate(final LocalDate date) {
		List<Transaction> transactions = new ArrayList<>();
		try {
			final JsonArray transactionsStore = store.loadJsonFromFile().getAsJsonArray();
			
			final List<JsonElement> jsonElements = Lists.newArrayList(transactionsStore.iterator()).stream().collect(Collectors.toList()).stream().filter(txJson -> 
				txJson.getAsJsonObject().get("date").getAsString().equals(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))).collect(Collectors.toList());	
			
			for (JsonElement jsonElement : jsonElements) {
				final double amount = Double.valueOf(jsonElement.getAsJsonObject().get("amount").getAsString());
				final LocalDate txDate =  LocalDate.parse(jsonElement.getAsJsonObject().get("date").getAsString(),DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				final TransactionType txType = TransactionType.valueOf(jsonElement.getAsJsonObject().get("type").getAsString());
				
				transactions.add(Transaction.builder().amount(amount).date(txDate).type(txType).build());
			}
			
		} catch (JsonSyntaxException | IOException e) {
			log.error(e);
			return Optional.empty();
		}		
		return Optional.of(transactions);
	}
	
	
	/**
	 * Finds a given transaction into the provided jsonElements.
	 * 
	 * @param transaction
	 * @param jsonElements
	 * @return
	 */
	Pair<Transaction, JsonElement> find(final Transaction transaction, final List<JsonElement> jsonElements) {
		if (jsonElements.size() == 1 && checkTransactionAndJson(transaction, jsonElements.get(0))) {
			return Pair.of(transaction, jsonElements.get(0));
		} else if (jsonElements.size() == 0 || jsonElements.size() == 1 && !checkTransactionAndJson(transaction, jsonElements.get(0))) {
			return null;
		}
		
		int halfPos = jsonElements.size() / 2;

		final List<JsonElement> leftSide = jsonElements.subList(0, halfPos);
		final List<JsonElement> rightSide = jsonElements.subList(halfPos, jsonElements.size());
		
		final LocalDate lastItemDateLeft = DateUtils.parseDate(leftSide.get(leftSide.size() -1).getAsJsonObject().get("date").getAsString());
		final LocalDate firstItemDateRight = DateUtils.parseDate(rightSide.get(0).getAsJsonObject().get("date").getAsString());
		
		Pair<Transaction, JsonElement> result = null;
		
		if (transaction.getDate().isBefore(lastItemDateLeft) || transaction.getDate().equals(lastItemDateLeft)) {
			result = find(transaction, leftSide);
		}  
		if (result == null && transaction.getDate().isAfter(firstItemDateRight) || transaction.getDate().equals(firstItemDateRight)) {
			result = find(transaction, rightSide);
		}
		
		return result;
	}
	
	/**
	 * Compare a transaction with a json element by date and type 
	 * 
	 * @param transaction
	 * @param transactionJson
	 * @return
	 */
	boolean checkTransactionAndJson(final Transaction transaction, final JsonElement transactionJson) {
		final String txType = transactionJson.getAsJsonObject().get("type").getAsString();
		final String txDate = transactionJson.getAsJsonObject().get("date").getAsString();
		return transaction.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).equals(txDate)
				&& transaction.getType().toString().equals(txType);
	}
	
}
