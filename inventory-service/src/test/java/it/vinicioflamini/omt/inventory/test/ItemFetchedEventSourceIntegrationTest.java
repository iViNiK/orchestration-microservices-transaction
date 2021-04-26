package it.vinicioflamini.omt.inventory.test;

import static org.junit.Assert.assertEquals;
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

import it.vinicioflamini.omt.common.message.ItemEvent;
import it.vinicioflamini.omt.inventory.kafka.channel.InventoryChannel;
import it.vinicioflamini.omt.inventory.kafka.source.ItemFetchedEventSource;

@RunWith(SpringRunner.class)
public class ItemFetchedEventSourceIntegrationTest {
	@TestConfiguration
	static class ItemFetchedEventSourceIntegrationTestContextConfiguration {

		@Bean
		public ItemFetchedEventSource itemFetchedEventSource() {
			return new ItemFetchedEventSource();
		}
	}

	@Autowired
	private ItemFetchedEventSource itemFetchedEventSource;

	@MockBean
	private InventoryChannel inventoryChannel;

	@Mock
	private MessageChannel messageChannel;
	
	private static Message<?> message = null;
	
	@Before
	public void setUp() {
		message = MessageBuilder.withPayload(new ItemEvent())
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build();

		when(inventoryChannel.outboundInventory()).thenReturn(messageChannel);
		when(messageChannel.send(message)).thenReturn(true);	
	}

	@Test
	public void publishItemFetchedEvent() {
		itemFetchedEventSource.publishItemFetchedEvent(10L, 10L);
		verify(inventoryChannel, times(1)).outboundInventory();
		assertEquals(ItemEvent.Action.ITEMFETCHED, itemFetchedEventSource.getEvent(10L, 10L).getAction());
		assertTrue(messageChannel.send(message));
	}
}
