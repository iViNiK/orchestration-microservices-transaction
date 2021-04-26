/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.order.kafka.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OrderChannel {

	final String OUTPUT_ORDER = "order-out";

	@Output(OUTPUT_ORDER)
	MessageChannel outboundOrder();

}
