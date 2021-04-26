/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.vinicioflamini.omt.payment.kafka.source.PaymentEventSource;

@Service
public class PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	private Long paymentId = null;

	@Autowired
	private PaymentEventSource paymentEventSource;

	public void makePayment(Long orderId, Long itemId) {
		if (processPayment()) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Payment completed successfully for order %d", orderId));
				logger.info(String.format("Going to send a \"PaymentReceivedEvent\" for order %d", orderId));
			}
			paymentEventSource.publishPaymentEvent(orderId, itemId, paymentId, true);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Payment failed for order id: %d", orderId));
				logger.info(String.format("Going to send a \"PaymentNotReceivedEvent\" for order %d", orderId));
			}
			paymentEventSource.publishPaymentEvent(orderId, itemId, paymentId, false);
		}
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
