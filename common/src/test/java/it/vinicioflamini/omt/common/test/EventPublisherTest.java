package it.vinicioflamini.omt.common.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.vinicioflamini.omt.common.domain.Action;
import it.vinicioflamini.omt.common.domain.EventPublisher;
import it.vinicioflamini.omt.common.domain.EventSource;
import it.vinicioflamini.omt.common.entity.Outbox;
import it.vinicioflamini.omt.common.message.OrderEvent;
import it.vinicioflamini.omt.common.repository.OutboxRepository;

@RunWith(SpringRunner.class)
public class EventPublisherTest {
	public class TestEntity {
		private Long id;

		public TestEntity(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
	}

	@MockBean
	private EventSource<TestEntity> eventSource;

	@MockBean
	private JpaRepository<Outbox, Long> outboxBaseRepository;

	@MockBean
	private OutboxRepository outboxRepository;

	@MockBean
	private JpaRepository<TestEntity, Long> domainObjectRepository;

	private EventPublisher<TestEntity> eventPublisher;
	
	private Outbox outbox;

	@Before
	public void setup() {
		ObjectMapper objectMapper = new ObjectMapper();
		
		OrderEvent orderEvent = new OrderEvent();
		orderEvent.setAction(Action.ITEMFETCHED);
		
		outbox = new Outbox();
		outbox.setId(100L);
		outbox.setDomainObjectId(1L);
		try {
			outbox.setOrderEvent(objectMapper.writeValueAsString(orderEvent));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNoOutboxTransactionPending() {
		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository,
				domainObjectRepository);
		when(outboxRepository.pop()).thenReturn(null);
		eventPublisher.publish();
		verify(domainObjectRepository, times(0)).getOne(10L);
		verify(eventSource, times(0)).publishEvent(null, null);
	}

	@Test
	public void testGetObjectOkPublishEventOk() {
		TestEntity testEntity = new TestEntity(1L);
		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository,
				domainObjectRepository);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(outboxRepository.save(outbox)).thenReturn(outbox);
		when(domainObjectRepository.getOne(1L)).thenReturn(testEntity);
		when(eventSource.publishEvent(testEntity, getOrderEvent(outbox))).thenReturn(true);
		eventPublisher.publish();
		verify(outboxRepository, times(1)).save(outbox);
		assertTrue("Processing is FALSE", outbox.isProcessing());
		verify(outboxRepository, times(1)).delete(outbox);
	}

	@Test
	public void testGetObjectOkPublishEventKo() {
		TestEntity testEntity = new TestEntity(1L);
		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository, domainObjectRepository);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(outboxRepository.save(outbox)).thenReturn(outbox);
		when(domainObjectRepository.getOne(1L)).thenReturn(testEntity);
		when(eventSource.publishEvent(testEntity, getOrderEvent(outbox))).thenReturn(false);
		eventPublisher.publish();
		verify(outboxRepository, times(2)).save(outbox);
		assertFalse("Processing is TRUE", outbox.isProcessing());
	}

	@Test
	public void testWhenGetObjectExceptionThenDeleteOutboxTransaction() {
		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository, domainObjectRepository);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(outboxRepository.save(outbox)).thenReturn(outbox);
		when(domainObjectRepository.getOne(1L)).thenThrow(new EntityNotFoundException());
		eventPublisher.publish();
		verify(outboxRepository, times(1)).delete(outbox);
		assertTrue("Processing is FALSE", outbox.isProcessing());
		verify(eventSource, times(0)).publishEvent(null, null);
	}

	@Test
	public void testWhenNoDomainObjectAndNoDomainRepositoryThenRetryOutboxTransaction() {
		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(outboxRepository.save(outbox)).thenReturn(outbox);
		eventPublisher.publish();
		verify(eventSource, times(0)).publishEvent(null, null);
		assertFalse("Processing is TRUE", outbox.isProcessing());
		verify(eventSource, times(0)).publishEvent(null, null);
	}

	@Test
	public void testWhenDomainObjectAndNoDomainRepositoryThenPublishEvent() {
		TestEntity testEntity = new TestEntity(1L);
		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository);
		eventPublisher.setDomainObject(testEntity);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(outboxRepository.save(outbox)).thenReturn(outbox);
		when(eventSource.publishEvent(testEntity, getOrderEvent(outbox))).thenReturn(true);
		eventPublisher.publish();
		verify(outboxRepository, times(1)).save(outbox);
		assertTrue("Processing is FALSE", outbox.isProcessing());
		verify(outboxRepository, times(1)).delete(outbox);
	}

	
	@Test
	public void testGetObjectOkPublishEventExceptionWhenUpdatingOutbox() {
		eventPublisher = new EventPublisher<TestEntity>(eventSource, outboxRepository,
				domainObjectRepository);
		when(outboxRepository.pop()).thenReturn(outbox);
		when(outboxRepository.save(outbox)).thenThrow(new PersistenceException());
		eventPublisher.publish();
		verify(eventSource, times(0)).publishEvent(null, null);
		assertFalse("Processing is TRUE", outbox.isProcessing());
		verify(eventSource, times(0)).publishEvent(null, null);
	}

	/**/
	
	@SuppressWarnings("finally")
	private OrderEvent getOrderEvent(Outbox o) {
		ObjectMapper objectMapper = new ObjectMapper();
		OrderEvent orderEvent = null;
		try {
			orderEvent = objectMapper.readValue(o.getOrderEvent(), OrderEvent.class);
		} catch (Exception e) {
			fail("Cannot parse OrderEvent");
		} finally {
			return orderEvent;
		}
	}
}
