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

import it.vinicioflamini.omt.common.message.PaymentEvent;
import it.vinicioflamini.omt.payment.kafka.channel.PaymentChannel;

@Component
public class PaymentEventSource {

	@Autowired
	private PaymentChannel paymentChannel;

	public void publishPaymentEvent(Long orderId, Long itemId, Long paymentId, boolean received) {

		PaymentEvent message = new PaymentEvent();
		message.setOrderId(orderId);
		message.setItemId(itemId);
		message.setPaymentId(paymentId);
		message.setAction(received ? PaymentEvent.Action.PAYMENTRECEIVED : PaymentEvent.Action.PAYMENTFAILED);

		MessageChannel messageChannel = paymentChannel.outboundPayment();
		messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build());
	}

}
