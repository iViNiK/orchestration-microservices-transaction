package it.vinicioflamini.omt.orchestrator.kafka.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import it.vinicioflamini.omt.orchestrator.kafka.channel.OrchestratorChannel;

@Component
public class MessageChannelFactory {

	@Autowired
	private OrchestratorChannel orchestratorChannel;

	public MessageChannel getMessageChannel(String channelId) {
		MessageChannel messageChannel = null;
		switch (channelId) {
			case OrchestratorChannel.OUTPUT_INVENTORY:
				messageChannel = orchestratorChannel.outboundInventory();
				break;
			case OrchestratorChannel.OUTPUT_ORDER:
				messageChannel = orchestratorChannel.outboundOrder();
				break;
			case OrchestratorChannel.OUTPUT_PAYMENT:
				messageChannel = orchestratorChannel.outboundPayment();
				break;
			case OrchestratorChannel.OUTPUT_SHIPMENT:
				messageChannel = orchestratorChannel.outboundShipment();
				break;
			default:
				break;
		}
		
		return messageChannel;
	}
	
}
