/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.payment.kafka.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PaymentChannel {

	final String OUTPUT = "payment-out";

	@Output(OUTPUT)
	MessageChannel outboundPayment();

}
