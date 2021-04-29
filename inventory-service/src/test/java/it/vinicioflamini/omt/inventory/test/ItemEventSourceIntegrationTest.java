package it.vinicioflamini.omt.inventory.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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
import it.vinicioflamini.omt.common.entity.Item;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.inventory.kafka.channel.InventoryChannel;
import it.vinicioflamini.omt.inventory.kafka.source.InventoryEventSource;

@RunWith(SpringRunner.class)
public class ItemEventSourceIntegrationTest {
	@TestConfiguration
	static class ItemFetchedEventSourceIntegrationTestContextConfiguration {

		@Bean
		public InventoryEventSource itemEventSource() {
			return new InventoryEventSource();
		}
	}

	@Autowired
	private InventoryEventSource itemEventSource;

	@MockBean
	private InventoryChannel inventoryChannel;

	@Mock
	private MessageChannel messageChannel;
	
	private static Message<?> message = null;
	
	@Before
	public void setUp() {
		when(inventoryChannel.outboundInventory()).thenReturn(messageChannel);
	}

	@Test
	public void publishItemFetchedEvent() {
		Item item = new Item(10L, "Item Name");
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setOrderId(10L);
		orderEvent.setItemId(10L);
		orderEvent.setAction(Action.ITEMFETCHED);
		
		message = MessageBuilder.withPayload(orderEvent)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build();
		when(messageChannel.send(message)).thenReturn(true);	

		itemEventSource.publishEvent(item, orderEvent);
		verify(inventoryChannel, times(1)).outboundInventory();
		assertTrue(messageChannel.send(message));
	}
}
