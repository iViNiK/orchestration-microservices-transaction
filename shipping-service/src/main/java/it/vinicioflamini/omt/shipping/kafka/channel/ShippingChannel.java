/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.shipping.kafka.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ShippingChannel {

	final String OUTPUT = "shipping-out";

	@Output(OUTPUT)
	MessageChannel outboundShipping();

}
