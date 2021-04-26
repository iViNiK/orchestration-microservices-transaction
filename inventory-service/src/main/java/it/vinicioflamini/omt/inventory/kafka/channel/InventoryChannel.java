/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.inventory.kafka.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface InventoryChannel {

	final String OUTPUT_INVENTORY = "inventory-out";

	@Output(OUTPUT_INVENTORY)
	MessageChannel outboundInventory();

}
