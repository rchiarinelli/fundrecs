/**
 * 
 */
package com.fundrecs.assigment.api;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fundrecs.assigment.domain.Transaction;
import com.fundrecs.assigment.domain.TransactionType;
import com.fundrecs.assigment.services.TransactionService;
import com.fundrecs.assigment.utils.DateUtils;

/**
 * @author rchiarinelli
 *
 */
@RestController
public class TransactionApi {
	
	@Autowired
	private TransactionService transactionService;
	
	/**
	 * 
	 * [
	*{
	*    "date": "11-12-2018",
	*    "type": "credit",
	*    "amount": "9898.36"
	*},
	*{
	*    "date": "11-12-2019",
	*    "type": "credit",
	*    "amount": "98.36"
	*}
	*]
	 * 
	 * @param transation
	 * @return
	 */
	@PostMapping("/transaction")
	public ResponseEntity<List<Transaction>> add(@Valid @RequestBody final List<Transaction> transations) {
		ResponseEntity<List<Transaction>> response = ResponseEntity.badRequest().build();
		
		Optional<List<Transaction>> savedTransactions = transactionService.register(transations);
		
		if (savedTransactions.isPresent()) {
			response = ResponseEntity.ok(savedTransactions.get());
		}
		
		return response;
	}

	@GetMapping("/transaction/{date}/{type}")
	public ResponseEntity<Transaction> getByDateAndType(@NotEmpty @PathVariable final String date, @NotEmpty @PathVariable final String type) {
		ResponseEntity<Transaction> response = ResponseEntity.notFound().build();
		final Optional<Transaction> tx = transactionService.getTransaction(DateUtils.parseDate(date), TransactionType.valueOf(type.toUpperCase()));
		if (tx.isPresent()) {
			response = ResponseEntity.ok(tx.get());
		} 
		
		return response;
	}
	
	@GetMapping("/transaction/{date}")
	public ResponseEntity<List<Transaction>> getByDate(@NotEmpty @PathVariable final String date) {
		ResponseEntity<List<Transaction>> response = ResponseEntity.notFound().build();
		final Optional<List<Transaction>> tx = transactionService.getTransactionByDate(DateUtils.parseDate(date));
		if (tx.isPresent()) {
			response = ResponseEntity.ok(tx.get());
		} 
		return response;
	}
	
}
