package it.vinicioflamini.omt.shipment.integration.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.entity.Shipment;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.shipping.kafka.channel.ShippingChannel;
import it.vinicioflamini.omt.shipping.kafka.source.ShipmentEventSource;

@RunWith(SpringRunner.class)
public class ShipmentEventSourceIntegrationTest {
	@TestConfiguration
	static class ItemFetchedEventSourceIntegrationTestContextConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public ShipmentEventSource shipmentEventSource() {
			return new ShipmentEventSource();
		}
	}

	@Autowired
	private ShipmentEventSource shipmentEventSource;

	@MockBean
	private ShippingChannel shipmentChannel;

	@Mock
	private MessageChannel messageChannel;
	
	private static Message<?> message = null;
	
	@Before
	public void setUp() {
		when(shipmentChannel.outboundShipping()).thenReturn(messageChannel);
	}

	@Test
	public void publishItemFetchedEvent() {
		Shipment payment = new Shipment(10L, 10L, 10L, 10L);
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(10L);
		orderEvent.setItemId(10L);
		orderEvent.setAction(Action.ITEMFETCHED);
		
		message = MessageBuilder.withPayload(orderEvent)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build();
		when(messageChannel.send(message)).thenReturn(true);	

		shipmentEventSource.publishEvent(payment, orderEvent);
		verify(shipmentChannel, times(1)).outboundShipping();
		assertTrue(messageChannel.send(message));
	}
}
