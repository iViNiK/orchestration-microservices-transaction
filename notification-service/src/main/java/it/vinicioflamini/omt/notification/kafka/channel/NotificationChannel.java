/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.notification.kafka.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface NotificationChannel {

	final String INPUT_ORDER = "notification-in-order";
	final String INPUT_SHIPPING = "notification-in-shipping";
	
	@Input(INPUT_ORDER)
	SubscribableChannel inboundOrder();
	
	@Input(INPUT_SHIPPING)
	SubscribableChannel inboundShipping();

}
