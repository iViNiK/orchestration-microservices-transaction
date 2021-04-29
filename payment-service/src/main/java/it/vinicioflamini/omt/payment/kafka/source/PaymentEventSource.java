/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.payment.kafka.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import it.vinicioflamini.omt.common.domain.EventSource;
import it.vinicioflamini.omt.common.entity.Payment;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.payment.kafka.channel.PaymentChannel;

@Component
public class PaymentEventSource implements EventSource<Payment> {

	@Autowired
	private PaymentChannel paymentChannel;

	public boolean publishEvent(Payment payment, OrderEvent orderEvent) {
		OrderEvent message = new OrderEvent();
		message.setOrderId(payment.getOrderId());
		message.setItemId(payment.getItemId());
		message.setPaymentId(payment.getPaymentId());
		message.setCustomerId(payment.getCustomerId());

		MessageChannel messageChannel = paymentChannel.outboundPayment();
		return messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build());
	}

}
