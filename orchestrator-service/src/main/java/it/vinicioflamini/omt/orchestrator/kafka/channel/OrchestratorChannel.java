/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.orchestrator.kafka.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface OrchestratorChannel {

	final String INPUT_ORDER = "order-in";
	final String INPUT_INVENTORY = "inventory-in";
	final String INPUT_PAYMENT = "payment-in";
	final String INPUT_SHIPMENT = "shipping-in";

	final String OUTPUT_ORDER = "order-out";
	final String OUTPUT_INVENTORY = "inventory-out";
	final String OUTPUT_PAYMENT = "payment-out";
	final String OUTPUT_SHIPMENT = "shipping-out";

	@Input(INPUT_ORDER)
	SubscribableChannel inboundOrder();
	
	@Input(INPUT_INVENTORY)
	SubscribableChannel inboundInventory();
	
	@Input(INPUT_PAYMENT)
	SubscribableChannel inboundPayment();
	
	@Input(INPUT_SHIPMENT)
	SubscribableChannel inboundShipment();

	@Output(OUTPUT_ORDER)
	MessageChannel outboundOrder();
	
	@Output(OUTPUT_INVENTORY)
	MessageChannel outboundInventory();
	
	@Output(OUTPUT_PAYMENT)
	MessageChannel outboundPayment();
	
	@Output(OUTPUT_SHIPMENT)
	MessageChannel outboundShipment();

}
