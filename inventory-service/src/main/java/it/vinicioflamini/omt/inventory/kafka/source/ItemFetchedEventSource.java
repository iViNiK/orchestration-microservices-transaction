/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.inventory.kafka.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import it.vinicioflamini.omt.common.message.ItemEvent;
import it.vinicioflamini.omt.inventory.kafka.channel.InventoryChannel;

@Component
public class ItemFetchedEventSource {

	@Autowired
	private InventoryChannel inventoryChannel;

	public void publishItemFetchedEvent(Long orderId, Long itemId) {
		MessageChannel messageChannel = inventoryChannel.outboundInventory();
		messageChannel.send(MessageBuilder.withPayload(getEvent(orderId, itemId))
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build());
	}
	
	public ItemEvent getEvent(Long orderId, Long itemId) {
		ItemEvent message = new ItemEvent();
		message.setItemId(itemId);
		message.setOrderId(orderId);
		message.setAction(ItemEvent.Action.ITEMFETCHED);
		return message;
	}

}
