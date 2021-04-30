/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.payment.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.vinicioflamini.omt.payment.domain.PaymentFacade;

@Service
public class PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	private Long paymentId = null;

	@Autowired
	private PaymentFacade paymentFacade;

	@Transactional
	public Long makePayment(Long orderId, Long itemId, Long customerId) throws JsonProcessingException {
		if (processPayment()) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Payment completed successfully for order %d", orderId));
				logger.info(String.format("Going to send a \"PaymentReceivedEvent\" for order %d", orderId));
			}
			paymentFacade.completePayment(orderId, itemId, paymentId, customerId);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Payment failed for order id: %d", orderId));
				logger.info(String.format("Going to send a \"PaymentNotReceivedEvent\" for order %d", orderId));
			}
			paymentFacade.rejectPayment(paymentId);
		}
		
		return paymentId;
	}

	/**/

	private boolean processPayment() {
		/* TODO: this can be any payment gateway interface call */
		if (Math.random() < 0.5) {
			paymentId = 6754L;
			return true;
		}

		return false;
	}

}
