package it.vinicioflamini.omt.orchestration.test;

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


//	@Test
//	public void testNoOutboxTransactionPending() {
//		when(outboxRepository.pop()).thenReturn(null);
//
//		message = MessageBuilder.withPayload(orderEvent)
//				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build();
//		when(messageChannel.send(message)).thenReturn(true);	
//
//		fallbackEventSource.publish();
//		verify(orchestratorChannel, times(1)).outboundInventory();
//		assertTrue(messageChannel.send(message));
//	}

//	@Test
//	public void testGetObjectOkPublishEventOk() {
//		TestEntity testEntity = new TestEntity(1L);
//		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository,
//				domainObjectRepository);
//		when(outboxRepository.pop()).thenReturn(outbox);
//		when(outboxRepository.save(outbox)).thenReturn(outbox);
//		when(domainObjectRepository.getOne(1L)).thenReturn(testEntity);
//		when(eventSource.publishEvent(testEntity, getOrderEvent(outbox))).thenReturn(true);
//		eventPublisher.publish();
//		verify(outboxRepository, times(1)).save(outbox);
//		assertTrue("Processing is FALSE", outbox.isProcessing());
//		verify(outboxRepository, times(1)).delete(outbox);
//	}
//
//	@Test
//	public void testGetObjectOkPublishEventKo() {
//		TestEntity testEntity = new TestEntity(1L);
//		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository, domainObjectRepository);
//		when(outboxRepository.pop()).thenReturn(outbox);
//		when(outboxRepository.save(outbox)).thenReturn(outbox);
//		when(domainObjectRepository.getOne(1L)).thenReturn(testEntity);
//		when(eventSource.publishEvent(testEntity, getOrderEvent(outbox))).thenReturn(false);
//		eventPublisher.publish();
//		verify(outboxRepository, times(2)).save(outbox);
//		assertFalse("Processing is TRUE", outbox.isProcessing());
//	}
//
//	@Test
//	public void testWhenGetObjectExceptionThenDeleteOutboxTransaction() {
//		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository, domainObjectRepository);
//		when(outboxRepository.pop()).thenReturn(outbox);
//		when(outboxRepository.save(outbox)).thenReturn(outbox);
//		when(domainObjectRepository.getOne(1L)).thenThrow(new EntityNotFoundException());
//		eventPublisher.publish();
//		verify(outboxRepository, times(1)).delete(outbox);
//		assertTrue("Processing is FALSE", outbox.isProcessing());
//		verify(eventSource, times(0)).publishEvent(null, null);
//	}
//
//	@Test
//	public void testWhenNoDomainObjectAndNoDomainRepositoryThenRetryOutboxTransaction() {
//		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository);
//		when(outboxRepository.pop()).thenReturn(outbox);
//		when(outboxRepository.save(outbox)).thenReturn(outbox);
//		eventPublisher.publish();
//		verify(eventSource, times(0)).publishEvent(null, null);
//		assertFalse("Processing is TRUE", outbox.isProcessing());
//		verify(eventSource, times(0)).publishEvent(null, null);
//	}
//
//	@Test
//	public void testWhenDomainObjectAndNoDomainRepositoryThenPublishEvent() {
//		TestEntity testEntity = new TestEntity(1L);
//		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository);
//		eventPublisher.setDomainObject(testEntity);
//		when(outboxRepository.pop()).thenReturn(outbox);
//		when(outboxRepository.save(outbox)).thenReturn(outbox);
//		when(eventSource.publishEvent(testEntity, getOrderEvent(outbox))).thenReturn(true);
//		eventPublisher.publish();
//		verify(outboxRepository, times(1)).save(outbox);
//		assertTrue("Processing is FALSE", outbox.isProcessing());
//		verify(outboxRepository, times(1)).delete(outbox);
//	}
//
//	
//	@Test
//	public void testGetObjectOkPublishEventExceptionWhenUpdatingOutbox() {
//		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository,
//				domainObjectRepository);
//		when(outboxRepository.pop()).thenReturn(outbox);
//		when(outboxRepository.save(outbox)).thenThrow(new PersistenceException());
//		eventPublisher.publish();
//		verify(eventSource, times(0)).publishEvent(null, null);
//		assertFalse("Processing is TRUE", outbox.isProcessing());
//		verify(eventSource, times(0)).publishEvent(null, null);
//	}

}
