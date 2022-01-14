/**
 * 
 */
package com.fundrecs.assigment.domain;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fundrecs.assigment.utils.DateUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author rchiarinelli
 *
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Transaction {

	@NotEmpty
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
	private LocalDate date;
	
	@NotEmpty
	private TransactionType type;
	
	@NotEmpty
	private double amount;

	public Transaction() {
		super();
	}

	public Transaction(@NotEmpty LocalDate date, @NotEmpty TransactionType type, @NotEmpty double amount) {
		super();
		this.date = date;
		this.type = type;
		this.amount = amount;
	}
	
	public static Transaction fromJson(final JsonElement jsonElement) {
		final JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		return Transaction.builder().amount(Double.valueOf(jsonObject.get("amount").getAsString()))
				.type(TransactionType.valueOf(jsonObject.get("type").getAsString().toUpperCase()))
				.date(DateUtils.parseDate(jsonObject.get("date").getAsString())).build();
	}
}
