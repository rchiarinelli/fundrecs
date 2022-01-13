/**
 * 
 */
package com.fundrecs.assigment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author rchiarinelli
 *
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class FundRecsAssigmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundRecsAssigmentApplication.class, args);
	}

}
