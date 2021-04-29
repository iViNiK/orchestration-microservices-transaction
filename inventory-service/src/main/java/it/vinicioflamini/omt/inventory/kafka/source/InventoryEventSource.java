package it.vinicioflamini.omt.inventory.kafka.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import it.vinicioflamini.omt.common.domain.EventSource;
import it.vinicioflamini.omt.common.entity.Item;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.inventory.kafka.channel.InventoryChannel;

@Component
public class InventoryEventSource implements EventSource<Item> {

	@Autowired
	private InventoryChannel inventoryChannel;

	private static final Logger logger = LoggerFactory.getLogger(InventoryEventSource.class);
	
	@Override
	public boolean publishEvent(Item item, OrderEvent orderEvent) {
		OrderEvent message = new OrderEvent();
		message.setOrderId(item.getOrderId());
		message.setItemId(item.getItemId());

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Going to send an %s for item %d", orderEvent.getAction().getName(), item.getItemId()));
		}

		MessageChannel messageChannel = inventoryChannel.outboundInventory();
		return messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build());

	}

}
