package it.vinicioflamini.omt.orchestrator.integration.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.PersistenceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.entity.Outbox;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.repository.OutboxRepository;
import it.vinicioflamini.omt.orchestrator.kafka.source.FallbackEventSource;
import it.vinicioflamini.omt.orchestrator.kafka.source.MessageChannelFactory;

@RunWith(SpringRunner.class)
public class FallbackEventSourceTest {

	@TestConfiguration
	static class ItemFetchedEventSourceIntegrationTestContextConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public FallbackEventSource fallbackEventSource() {
			return new FallbackEventSource();
		}
	}

	@Autowired
	private FallbackEventSource fallbackEventSource;

	@MockBean
	private MessageChannelFactory messageChannelFactory;

	@MockBean
	private MessageChannel messageChannel;

	@MockBean
	private OutboxRepository outboxRepository;

	private static Message<?> message = null;
	
	private Outbox outbox;
	
	private OrderEvent orderEvent;

	@Before
	public void setup() {
		ObjectMapper objectMapper = new ObjectMapper();
		
		orderEvent = new OrderEvent();
		orderEvent.setOrderId(10L);
		orderEvent.setItemId(10L);
		orderEvent.setAction(Action.ITEMFETCHED);
		
		outbox = new Outbox();
		outbox.setId(100L);
		outbox.setChannelId("channelId");
		outbox.setDomainObjectId(1L);
		try {
			outbox.setOrderEvent(objectMapper.writeValueAsString(orderEvent));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testWhenFoundOutboxTransactionPendingThenPublishEventOk() {
		when(outboxRepository.pop()).thenReturn(outbox);
		when(outboxRepository.save(outbox)).thenReturn(outbox);
		when(messageChannelFactory.getMessageChannel("channelId")).thenReturn(messageChannel);
		when(messageChannel.send(ArgumentMatchers.<Message<?>>any())).thenReturn(true);	

		message = MessageBuilder.withPayload(orderEvent)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build();

		fallbackEventSource.publish();
		
		assertTrue(messageChannel.send(message));
		assertTrue(outbox.isProcessing());
		verify(outboxRepository, times(1)).save(outbox);
		verify(outboxRepository, times(1)).delete(outbox);
	}

	@Test
	public void testWhenFoundOutboxTransactionPendingThenPublishEventKo() {
		when(outboxRepository.pop()).thenReturn(outbox);
		when(outboxRepository.save(outbox)).thenReturn(outbox);
		when(messageChannelFactory.getMessageChannel("channelId")).thenReturn(messageChannel);
		when(messageChannel.send(ArgumentMatchers.<Message<?>>any())).thenReturn(false);	

		message = MessageBuilder.withPayload(orderEvent)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build();

		fallbackEventSource.publish();
		
		assertFalse(messageChannel.send(message));
		assertFalse(outbox.isProcessing());
		verify(outboxRepository, times(2)).save(outbox);
		verify(outboxRepository, times(0)).delete(outbox);
	}

	@Test
	public void testWhenNotFoundOutboxTransactionPendingThenNoEventPublished() {
		when(outboxRepository.pop()).thenReturn(null);
		
		verify(outboxRepository, times(0)).save(outbox);
		verify(outboxRepository, times(0)).delete(outbox);
		verify(messageChannel, times(0)).send(ArgumentMatchers.<Message<?>>any());
		
	}

	@Test
	public void testWhenGetObjectExceptionThenDeleteOutboxTransaction() {
		when(outboxRepository.pop()).thenReturn(outbox);
		when(outboxRepository.save(outbox)).thenThrow(new PersistenceException());

		assertFalse(outbox.isProcessing());
		verify(outboxRepository, times(0)).save(outbox);
		verify(outboxRepository, times(0)).delete(outbox);
		verify(messageChannel, times(0)).send(ArgumentMatchers.<Message<?>>any());
	}


}
