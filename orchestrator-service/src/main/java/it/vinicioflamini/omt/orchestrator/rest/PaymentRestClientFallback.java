package it.vinicioflamini.omt.orchestrator.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import it.vinicioflamini.omt.common.rest.payload.OrderRequest;
import it.vinicioflamini.omt.orchestrator.kafka.source.PaymentFailedEventSource;

@Component
public class PaymentRestClientFallback implements PaymentRestClient {

	@Autowired
	PaymentFailedEventSource paymentNotReceivedEventSource;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentRestClientFallback.class);
	
	@Override
	public ResponseEntity<String> doPayment(OrderRequest req) {
		/*
		 * If you have an error in the payment process, or if the payment was declined,
		 * you need to realign inventory by posting a PAYMENTNOTRECEIVED event
		 */
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Executing Fallback during payment for order %d", req.getOrderId()));
		}
		
		paymentNotReceivedEventSource.publishPaymentNotReceivedEvent(req.getOrderId(), req.getItemId());
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
