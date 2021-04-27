/**
*
* Vinicio Flamini (io@vinicioflamini.it)
*
*/
package it.vinicioflamini.omt.orchestrator.kafka.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import it.vinicioflamini.omt.common.message.ItemEvent;
import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;

@Component
public class ItemNotCompensatedEventSource {

	@Autowired
	private OrchestratorChannel orchestratorChannel;

	public void publishItemNotCompensatedEvent(Long orderId, Long itemId) {

		ItemEvent message = new ItemEvent();
		message.setOrderId(orderId);
		message.setItemId(itemId);
		message.setAction(ItemEvent.Action.ITEMNOTCOMPENSATED);
		
		MessageChannel messageChannel = orchestratorChannel.outboundInventory();
		messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build());
	}

}