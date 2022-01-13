/**
 * 
 */
package com.fundrecs.assigment.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fundrecs.assigment.domain.Transaction;
import com.fundrecs.assigment.domain.TransactionType;

/**
 * Defines the operations supported for transaction. 
 * 
 * @author rchiarinelli
 *
 */
public interface TransactionService {

	/**
	 * Register the provided transaction returning the new state.
	 * 
	 * @param transactions the provided transaction
	 * @return the resulting transaction state
	 */
	Optional<List<Transaction>> register(final List<Transaction> transactions);
	
	/**
	 * Retrive a specific transaction by date and type.
	 * 
	 * @param date the provided date
	 * @param type the transaction type
	 * @return optional with the related transaction or null 
	 */
	Optional<Transaction> getTransaction(final LocalDate date, final TransactionType type);
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	Optional<List<Transaction>> getTransactionByDate(final LocalDate date);
	
	
}
