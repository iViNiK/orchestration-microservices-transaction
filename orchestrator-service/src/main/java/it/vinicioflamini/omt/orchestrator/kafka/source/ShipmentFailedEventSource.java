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

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;

@Component
public class ShipmentFailedEventSource {

	@Autowired
	private OrchestratorChannel orchestratorChannel;

	public void publishShipmentFailedEvent(Long orderId) {

		OrderEvent message = new OrderEvent();
		message.setOrderId(orderId);
		message.setAction(Action.SHIPMENTFAILED);
		
		MessageChannel messageChannel = orchestratorChannel.outboundShipment();
		messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build());
	}

}
