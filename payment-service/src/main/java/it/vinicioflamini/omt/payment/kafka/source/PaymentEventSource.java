/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.payment.kafka.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;

import it.vinicioflamini.omt.common.domain.EventSource;
import it.vinicioflamini.omt.common.entity.Payment;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.payment.kafka.channel.PaymentChannel;

@Component
public class PaymentEventSource implements EventSource<Payment> {

	@Autowired
	private PaymentChannel paymentChannel;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentEventSource.class);

	public boolean publishEvent(Payment payment, OrderEvent orderEvent) {
		Assert.notNull(payment, "PAYMENT IS NULL");
		Assert.notNull(orderEvent, "ORDER EVENT IS NULL");
		Assert.notNull(orderEvent.getAction(), "ACTION IS NULL");

		orderEvent.setOrderId(payment.getOrderId());
		orderEvent.setItemId(payment.getItemId());
		orderEvent.setPaymentId(payment.getId());
		orderEvent.setCustomerId(payment.getCustomerId());
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Going to send an %s for order %d", orderEvent.getAction().getName(), payment.getOrderId()));
		}

		MessageChannel messageChannel = paymentChannel.outboundPayment();
		return messageChannel.send(MessageBuilder.withPayload(orderEvent)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build());
	}

}
